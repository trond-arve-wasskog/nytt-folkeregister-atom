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
        _ (change-name-and-status @db-atom ssn (str name " foo") :person.sivilstatus/skilt)
        [new-name new-status] (select-k-vec (find-by-ssn @db-atom ssn) [:person/name :person/sivilstatus])]
    status =not=> :person.sivilstatus/skilt
    name =not=> new-name
    new-name => (has-suffix #" foo")
    new-status => :person.sivilstatus/skilt))

(fact "Updating a name that does not exist, must fail"
  (change-name-and-status @db-atom "-111" "alf" nil) => (throws IllegalStateException))

(fact "Updating anything about person should work"
  (save-or-update-entity @db-atom
    {:person/ssn  "19107612345"
     :person/name "Eivind Waaler 2"})
  (:person/name (find-by-ssn @db-atom "19107612345")) => "Eivind Waaler 2")

(fact "Changing address should work"
  (move-to-new-address @db-atom "23013454321" "Henrik Ibsens gate" "1" "0010")
  (merge {} (d/touch (:person/address (find-by-ssn @db-atom "23013454321")))) =>
  {:address/street "Henrik Ibsens gate", :address/streetnumber "1", :address/postnumber "0010"})

(fact "Getting history for one person, without change"
  (let [dbid (id-by-ssn @db-atom "23013454321")
        changes (changes-for-person @db-atom dbid)]
    (count changes) => 1
    (dissoc (first changes) :timestamp)
    => {:changes
         [{:person/ssn
            {:new "23013454321", :old nil}}
          {:person/birthplace
            {:new "Trondheim", :old nil}}
          {:person/name {:new "Ola Nordmann", :old nil}}]}))

(fact "Getting history for one person, with one"
  (change-name-and-status @db-atom "23013454321" "Alf" nil)
  (let [dbid (id-by-ssn @db-atom "23013454321")
        changes (changes-for-person @db-atom dbid)]
    (count changes) => 2
    (dissoc (second changes) :timestamp) =>
    {:changes [{:person/name {:old "Ola Nordmann", :new "Alf"}}]}
    [(:timestamp (first changes)) (:timestamp (second changes))]
    => (chatty-checker [[ts-first ts-second]]
         (= -1 (compare ts-first ts-second)))))


(fact "Deep walking should result in a fully realized object"
  (let [eivind (find-by-ssn @db-atom "19107612345")
        realized (deep-touch! @db-atom eivind)]
    (dissoc realized :db/id :person/address) =>
    {:person/name        "Eivind Waaler"
     :person/birthplace  "Oslo"
     :person/ssn         "19107612345"
     :person/sivilstatus :person.sivilstatus/ugift}
    (dissoc (:person/address realized) :db/id) =>
    {:address/street       "Majorstuveien"
     :address/streetnumber "16"
     :address/postnumber   "0367"}
    (:db/id realized) => identity
    (get-in realized [:person/address :db/id]) => identity))
