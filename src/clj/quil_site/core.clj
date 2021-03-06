(ns quil-site.core
  (:require [compojure.core :refer [defroutes GET]]
            [compojure.handler :refer [site]]
            [compojure.route :refer [files]]
            [ring.util.response :as resp]
            [ring.middleware.json :as json]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.stacktrace :as stacktrace]
            [quil-site.controllers.sketches :as sketches]
            [quil-site.controllers.api :as api]
            [quil-site.views.about :refer [about-page]]))

(defroutes app
  (GET "/" [] (about-page))
  sketches/routes
  api/routes
  (files "/"))

(defn dump-request [handler]
  (fn [req]
    (clojure.pprint/pprint req)
    (handler req)))

(def handler
  (-> #'app
;      dump-request
      site
      (json/wrap-json-body {:keywords? true})
      json/wrap-json-response
      stacktrace/wrap-stacktrace))

(defn run [port]
  (run-jetty handler {:port (Integer/parseInt port)}))

(comment

 (def server (run-jetty #(handler %) {:port 8080 :join? false}))

 (.stop server)

)
