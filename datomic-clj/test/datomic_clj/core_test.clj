(ns datomic-clj.core-test
  (:require [datomic-clj.core :refer :all]
            [clojure.java.io :as io])
  (:use midje.sweet))

(fact "Reading a edn resource file should give a map of values"
  (count (f-to-map (io/resource "folkrereg-config-test.edn"))) =>
  (chatty-checker [num] (< 0 num)))

(fact "When no file is found empty map should be returned"
  (f-to-map (io/resource "foobar.foobar")) => nil
  (f-to-map (io/file "foobar.foobar")) => nil)

(fact "Files not on the classpath should be added"
  (count (f-to-map (io/file "project.clj"))) => (chatty-checker [num] (< 0 num)))
