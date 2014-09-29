(ns datomic-clj.core
  (:gen-class)
  (:import (java.io PushbackReader))
  (:require [liberator.core :refer [resource defresource]]
            [liberator.dev :refer [wrap-trace]]
            [ring.middleware.params :refer [wrap-params]]
            [compojure.core :refer [defroutes ANY GET POST context]]
            [ring.adapter.jetty :as jetty]
            [nomad :refer [defconfig]]
            [clojure.java.io :as io]
            [clojure.edn :as edn]
            [schema.core :as s]))

(defn- person-get [ctx ssn]
  {:ssn ssn})

(defn- person-get-changes [ctx ssn]
  {:changed false :ssn ssn})

(defn- person-find-all [ctx]
  [{:foo "bar"}])

(defn person-update [ctx map]
  [:updated true])

(defroutes persons
  (context "/persons" []
    (GET "/:ssn" [ssn :as r]
      (resource
        :available-media-types ["application/json"]
        :handle-ok #(person-get % ssn))
      (GET "/:ssn/changes" [ssn :as r]
        (resource
          :available-media-types ["application/json"]
          :handle-ok #(person-get-changes % ssn)))
      (GET "/" request
        (resource
          :available-media-types ["application/json"]
          :handle-ok #(person-find-all %)))
      (POST "/" [person-map :as r]
        (resource
          :available-media-types ["application/json"]
          :handle-ok #(person-update % person-map))))))

(defroutes default
  (GET "/" request
    {:status  200
     :headers {"Content-Type" "text/html"}
     :body    "Hello World"}))

(defroutes main
  default
  persons)

(defn resource-to-map [r]
  (if-let [res (some-> r io/reader (PushbackReader.) edn/read)]
    res
    {}))

(def config-schema
  {(s/required-key :port) s/Int})

(defn setup-config []
  (let [config (merge (io/resource "folkrereg-config-default.edn")
                 (io/resource "folkrereg-config-test.edn")
                 (io/file "folkrereg-config.edn"))]
    (s/validate config-schema config)
    (println config)
    config))

(defn add-system [handler system]
  (fn [request]
    (handler (assoc request :folkereg/system system))))

(def handler
  (-> main
    (wrap-trace :header :ui)
    ;(add-system nil)
    wrap-params))
#_
(defn -main [& args]
  (jetty/run-jetty handler {:port 3000}))
