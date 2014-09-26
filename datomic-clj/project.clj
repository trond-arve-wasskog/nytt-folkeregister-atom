(defproject datomic-clj "0.1.0-SNAPSHOT"
  :description "Clojure/Datomic impl av folkeregister backend"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :plugins [[lein-ring "0.8.11" :exclusions [org.clojure/clojure]]]
  :ring {:handler datomic-clj.core/handler}
  :repositories [["Datomic" "http://files.datomic.com/maven"]]
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [liberator "0.12.0"]
                 [compojure "1.1.9"]
                 [ring/ring-core "1.3.1"]
                 [com.stuartsierra/component "0.2.2"]
                 [com.datomic/datomic-free "0.9.4766"]]
  :profiles {:dev {:dependencies [[midje "1.6.3"]]
                   :plugins [[lein-midje "3.1.3"]]
                   :resource-paths ["test-resources"]}})
