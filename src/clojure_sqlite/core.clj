(ns clojure-sqlite.core
  (:require [clojure.java.jdbc :refer :all])
  (:gen-class))

(def testdata
  { :url "http://example.com",
   :title "SQLite Example",
   :body "Example using SQLite with Clojure"
   })

(def db
  {:classname   "org.sqlite.JDBC"
   :subprotocol "sqlite"
   :subname     "db/database.db"
   })

(defn create-db
  "create db and table"
  []
  (try (db-do-commands db
                       (create-table-ddl :news
                                         [[:timestamp :datetime :default :current_timestamp ]
                                          [:url :text]
                                          [:title :text]
                                          [:body :text]]))
       (catch Exception e
         (println (.getMessage e)))))

(defn create-db-queue
  "create message table"
  []
  (try (db-do-commands db
                       (create-table-ddl :messages
                                         [[:timestamp :datetime :default :current_timestamp ]
                                          [:to      :text]
                                          [:from    :text]
                                          [:message :text]
                                          [:delay   :text]]
                                         ))
       (catch Exception e
         (println (.getMessage e)))))

(defn print-result-set
  "prints the result set in tabular form"
  [result-set]
  (doseq [row result-set]
    (println row)))

(defn output
  "execute query and return lazy sequence"
  []
  (query db ["select * from news"]))

(defn -main
  "launch!"
  []
  (create-db)
  (create-db-queue)
  (insert! db :news testdata)
  (insert! db :messages {:to "4476"
                         :from "4478"
                         :message "CO2 building up"
                         :delay "7m"})

  (print-result-set (output)))
