(ns folkereg.core
  (:gen-class)
  (:import (java.io PushbackReader IOException))
  (:require [liberator.core :refer [resource defresource]]
            [liberator.dev :refer [wrap-trace]]
            [ring.middleware.params :refer [wrap-params]]
            [compojure.core :refer [defroutes ANY GET POST context]]
            [ring.adapter.jetty :as jetty]
            [clojure.java.io :as io]
            [clojure.edn :as edn]
            [schema.core :as s]
            [clojure.tools.logging :as log]
            [folkereg.db :as db]
            [prone.middleware :as prone]
            [folkereg.system :as sys]
            [ring.middleware.json :refer [wrap-json-response]]
            [ring.util.response :refer [response]]
            [hiccup.core :as h]))

(defn get-system [ctx kw]
  (get-in ctx [:folkereg/system kw]))

(defn- person-get [ctx ssn]
  (or (db/find-by-ssn (get-system ctx :db) ssn) {}))

(defn- person-get-changes [ctx ssn]
  (let [db (get-system ctx :db)]
    (or (some->> ssn
          (db/id-by-ssn db)
          (db/changes-for-person db))
      [])))

(defn- person-find-all [ctx]
  (or (db/find-all-persons (get-system ctx :db)) []))

(defn person-update [ctx map]
  (db/save-or-update-entity (get-system ctx :db) map))

(defroutes persons
  (context "/persons" []
    (GET "/" request
      (response (person-find-all request)))
    (GET ["/:ssn", :id #"[0-9]+"] [ssn :as r]
      (response (person-get r ssn)))
    (GET "/:ssn/changes" [ssn :as r]
      (response (person-get-changes r ssn)))
    (POST "/:ssn" [person-map :as r]
      (response (person-update r person-map)))))

(defroutes default
  (GET "/" request
    (response
      (h/html [:html
               [:body [:h1 "Velkommen til foleregister appen"]
                [:div [:a {:href "persons"} "Vis alle registrerte brukere"]]
                ]]))))

(defroutes main
  default
  persons)

(defn f-to-map [r]
  (try
    (some-> r io/reader PushbackReader. edn/read)
    (catch IOException ioe)))

(def config-schema
  {(s/required-key :port) s/Int
   (s/optional-key :dev) s/Bool})

(defn setup-config []
  (let [config (merge {}
                 (f-to-map (io/resource "folkrereg-config-default.edn"))
                 (f-to-map (io/resource "folkrereg-config-test.edn"))
                 (f-to-map (io/file "folkrereg-config.edn")))]
    (s/check config-schema config)
    (log/info "App set up with config " config)
    config))

(defn add-system [system handler]
  (fn [request]
    (handler (assoc request :folkereg/system system))))

(defn setup-system [config]
  (sys/system config))

(defn handler
  ([request]
   (let [config (setup-config)]
     (handler
       (setup-system config) config
       request)))
  ([system config request]
   (log/error "Dev mode endabled?" (:dev config))
   (let [add-sys-middle (partial add-system system)]
     ((cond-> main
        true wrap-json-response
        true wrap-params
        true add-sys-middle
        (:dev config) prone/wrap-exceptions)
      request))))

(defn -main [& args]
  (let [config (setup-config)]
    (jetty/run-jetty
      (partial handler
        (setup-system config) config)
      {:port (:port config)})))
