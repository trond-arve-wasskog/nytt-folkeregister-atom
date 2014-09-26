(ns datomic-clj.db
  (:require [com.stuartsierra.component :as component]
            [datomic.api :as d]))

(defn conn [database]
  (d/db database))

(defn find-all-persons [database]
  (let [conn (conn database)]
    (map #(->> % first (d/entity conn) d/touch)
      (d/q '[:find ?e :where [?e :person/name]] conn))))

(defn id-by-ssn [database ssn]
  (as-> (conn database) x
    (d/q '[:find ?e :in $ ?ssn :where [?e :person/ssn ?ssn]] x ssn)
    (ffirst x)))

(defn find-by-ssn [database ssn]
  (d/entity (conn database) (id-by-ssn database ssn)))

(defn change-name-and-status [database ssn new-name new-status]
  (if-let [id (id-by-ssn database ssn)]
    (d/transact database
      [(conj {:db/id      id
              :person/ssn ssn}
         (when new-name [:person/name new-name])
         (when new-status [:person/sivilstatus new-status]))])
    (throw (IllegalStateException. (str "Could not find ssn " ssn)))))

(defn- connect-to-db [url]
  (d/connect url))

(defrecord Database [url connection]
  component/Lifecycle
  (start [this]
    (let [conn (connect-to-db url)]
      (assoc this :connection conn)))
  (stop [this]
    (dissoc this :connection)))
