(ns datomic-clj.db-test
  (:import (java.io PushbackReader)
           (java.util UUID))
  (:require
    [clojure.java.io :as io]
    [datomic-clj.db :refer :all]
    [com.stuartsierra.component :as c]
    [datomic.api :as d])
  (:use midje.sweet))

(def db-atom (atom nil))

(defn db-name []
  (str "datomic:mem://" (str (UUID/randomUUID))))

(defn read-from-cp [f-name]
  (->> f-name
    (.getResourceAsStream (ClassLoader/getSystemClassLoader))
    io/reader
    PushbackReader.
    read))

(def schema (read-from-cp "schema.edn"))
(def data (read-from-cp "data.edn"))

(defn db-init! []
  (let [db-name (db-name)
        _ (d/create-database db-name)
        db (d/connect db-name)]
    (d/transact db schema)
    (d/transact db data)
    db))

(defn select-k-vec [m key-seq]
  (map #(get m %) key-seq))

(namespace-state-changes (before :facts (reset! db-atom (db-init!))))

(fact "Should get at database!"
  (->> @db-atom find-all-persons (map :person/name) set) => #{"Eivind Waaler" "Ola Nordmann" "Kari Nordmann"})

(fact "Should find person by ssn"
  (:person/name (find-by-ssn @db-atom "19107612345")) => "Eivind Waaler")

(fact "Updates of name and status should work"
  (let [ssn "23013454321"
        [name status] (select-k-vec (find-by-ssn @db-atom ssn) [:person/name :person/sivilstatus])
        _ (change-name-and-status @db-atom ssn (str name  " foo") :person.sivilstatus/skilt)
        [new-name new-status] (select-k-vec (find-by-ssn @db-atom ssn) [:person/name :person/sivilstatus])]
    status =not=> :person.sivilstatus/skilt
    name =not=> new-name
    new-name => (has-suffix #" foo")
    new-status => :person.sivilstatus/skilt))

(fact "Updating a name that does not exist, must fail"
  (change-name-and-status @db-atom "-111" "alf" nil) => (throws IllegalStateException))
