(ns sugbi.auth.routes
  (:require
   [sugbi.auth.google.handlers :as google.handlers]))


(defn routes []
  ["/auth"
   ["/google" {:swagger {:tags ["Auth with Google"]}}
    ["/login" {:get {:summary   "authorize redirection"
                     :responses {302 {:body some?}}
                     :handler   (google.handlers/authorize-handler "http://localhost:3000/auth/google/callback")}}]
    ["/callback" {:get {:summary   "OAuth callback"
                        :responses {302 {:body some?}}
                        :handler   (google.handlers/callback-handler "http://localhost:3000/auth/google/callback")}}]]])
