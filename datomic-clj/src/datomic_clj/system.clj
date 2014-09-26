(ns datomic-clj.system
  (:require [com.stuartsierra.component :as component]))


(defrecord Database [host port uname pwd connection]
  component/Lifecycle
  (start [this])
  (stop [this]))
