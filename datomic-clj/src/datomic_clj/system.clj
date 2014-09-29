(ns datomic-clj.system
  (:import (datomic_clj.db Db))
  (:require [com.stuartsierra.component :as component]
            [datomic-clj.db :refer [new-db]]))

(defn system [config]
  (component/start
    (component/system-map
      :db (new-db (:db-url config)))))
