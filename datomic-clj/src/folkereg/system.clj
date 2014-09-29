(ns folkereg.system
  (:import (folkereg.db Db))
  (:require [com.stuartsierra.component :as component]
            [folkereg.db :refer [new-db]]))

(defn system [config]
  (component/start
    (component/system-map
      :db (new-db (:db-url config)))))
