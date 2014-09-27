(ns datomic-clj.db
  (:require [com.stuartsierra.component :as component]
            [datomic.api :as d]))

(defn conn [db]
  (d/db db))

(defn find-all-persons [db]
  (let [conn (conn db)]
    (map #(->> % first (d/entity conn) d/touch)
      (d/q '[:find ?e :where [?e :person/name]] conn))))

(defn id-by-ssn [db ssn]
  (as-> (conn db) x
    (d/q '[:find ?e :in $ ?ssn :where [?e :person/ssn ?ssn]] x ssn)
    (ffirst x)))

(defn find-by-ssn [db ssn]
  (d/entity (conn db) (id-by-ssn db ssn)))

(defn change-name-and-status [db ssn new-name new-status]
  (if-let [id (id-by-ssn db ssn)]
    @(d/transact db
       [(conj {:db/id      id
               :person/ssn ssn}
          (when new-name [:person/name new-name])
          (when new-status [:person/sivilstatus new-status]))])
    (throw (IllegalStateException. (str "Could not find ssn " ssn)))))

(defn save-or-update-entity [db & maps]
  (->> maps
    (map (fn [m] (merge {:db/id (d/tempid :db.part/user)} m)))
    (d/transact db)
    deref))

(defn move-to-new-address [db ssn street number postnumber]
  (let [adr-id (d/tempid :db.part/user)]
    (save-or-update-entity db
      {:db/id                adr-id
       :address/postnumber   postnumber
       :address/street       street
       :address/streetnumber number}
      {:person/ssn ssn
       :person/address adr-id})))

(defn entity-by-id [db id]
  (d/entity (conn db) id))

(defn transaction-changes [db dbid [tx ids]]
  (let [conn (conn db)
        db-before (d/as-of conn (dec tx))
        db-after (d/as-of conn tx)
        ts (:db/txInstant (entity-by-id db tx))]
    (->> ids
      (map (fn [id]
             (let [before-e (d/entity db-before id)
                   after-e (d/entity db-after id)]
               {(:db/ident after-e)
                 {:old (get (d/entity db-before dbid) (:db/ident before-e))
                  :new (get (d/entity db-after dbid) (:db/ident after-e))}})))
      (assoc {:timestamp ts} :changes))))

(defn changes-for-person [db dbid]
  (let [conn (conn db)
        h (d/history conn)
        txs (d/q '[:find ?tx ?a :in $ ?e :where [?e ?a _ ?tx]] h dbid)]
    (->>
      (group-by first txs)
      (map (fn [[tx atts]]
             [tx (map second atts)]))
      (sort-by first)
      (map (partial transaction-changes db dbid)))))

(defrecord db [url connection]
  component/Lifecycle
  (start [this]
    (let [conn (d/connect url)]
      (assoc this :connection conn)))
  (stop [this]
    (dissoc this :connection)))


