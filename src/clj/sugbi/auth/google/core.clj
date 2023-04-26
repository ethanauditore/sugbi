(ns sugbi.auth.google.core
  (:require
   [buddy.sign.jwt :as jwt]
   [camel-snake-kebab.core :refer [->kebab-case]]
   [clj-http.client :as http]
   [clojure.string :as str]
   [medley.core :as medley]
   [mount.core :refer [defstate]]
   [muuntaja.core :as muuntaja]))


(def discovery-endpoint-uri
  "https://accounts.google.com/.well-known/openid-configuration")

(defstate openid-configuration
  :start (->> discovery-endpoint-uri
              http/get
              muuntaja/decode-response-body
              (medley/map-keys ->kebab-case)))


(defn scopes-str
  [scopes]
  (str/join " " (map name scopes)))


(defn oauth-authorize-uri
  [client-id redirect-uri scopes]
  (str (:authorization-endpoint openid-configuration)
       "?"
       (http/generate-query-string
        {:client_id     client-id
         :response_type "code"
         :redirect_uri  redirect-uri
         :scope         (scopes-str scopes)})))


(defn oauth-access-token
  [client-id client-secret code redirect-uri scopes]
  (->> {:form-params {:client_id     client-id
                      :client_secret client-secret
                      :grant_type    "authorization_code"
                      :code          code
                      :redirect_uri  redirect-uri
                      :scope         (scopes-str scopes)}}
       (http/post (:token-endpoint openid-configuration))
       (muuntaja/decode-response-body)
       (medley/map-keys ->kebab-case)
       :access-token))


(defn user-info
  [access-token]
  (->> {:content-type "application/x-www-form-urlencoded"
        :query-params {"access_token" access-token}}
       (http/get (:userinfo-endpoint openid-configuration))
       (muuntaja/decode-response-body)
       (medley/map-keys ->kebab-case)))
