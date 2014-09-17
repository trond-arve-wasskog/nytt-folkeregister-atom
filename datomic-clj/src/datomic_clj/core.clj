(ns datomic-clj.core
  (:require [liberator.core :refer [resource defresource]]
            [liberator.dev :refer [wrap-trace]]
            [ring.middleware.params :refer [wrap-params]]
            [compojure.core :refer [defroutes ANY GET POST context]]))

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
    (GET "/:ssn" [ssn]
      (resource
        :available-media-types ["application/json"]
        :handle-ok #(person-get % ssn))
      (GET "/:ssn/changes" [ssn]
        (resource
          :available-media-types ["application/json"]
          :handle-ok #(person-get-changes % ssn)))
      (GET "/" []
        (resource
          :available-media-types ["application/json"]
          :handle-ok #(person-find-all %)))
      (POST "/" [person-map]
        (resource
          :available-media-types ["application/json"]
          :handle-ok #(person-update % person-map))))))

(defroutes main
  persons)

(def handler
  (-> main
    (wrap-trace :header :ui)
    wrap-params))
