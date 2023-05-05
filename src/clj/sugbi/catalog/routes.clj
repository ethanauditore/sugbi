(ns sugbi.catalog.routes
  (:require
   [spec-tools.data-spec :as ds]
   [sugbi.catalog.handlers :as catalog.handlers]))


(def basic-book-info-spec
  {:isbn  string?
   :title string?})


(def book-info-spec
  {:isbn                         string?
   :copies                       int?
   :available                    boolean?
   (ds/opt :title)               string?
   (ds/opt :full-title)          string?
   (ds/opt :subtitle)            string?
   (ds/opt :publishers)          [string?]
   (ds/opt :publish-date)        string?
   (ds/opt :weight)              string?
   (ds/opt :physical-dimensions) string?
   (ds/opt :genre)               string?
   (ds/opt :subjects)            [string?]
   (ds/opt :number-of-pages)     int?})

(defn date?
  [o]
  (instance? java.time.LocalDate o))

(def loan-spec
  {:lending-id     int?
   :lending-date date?
   :due-date date?
   :return-date (ds/maybe date?)})

(def routes
  ["/catalog" {:swagger {:tags ["Catalog"]}}
   ["/books"
    ["" {:get  {:summary    "gets the catalog. Optionally, accepts a search criteria"
                :parameters {:query {(ds/opt :q) string?}}
                :responses  {200 {:body [book-info-spec]}}
                :handler    catalog.handlers/search-books}
         :post {:summary    "add a book title to the catalog"
                :parameters {:header {:cookie string?}
                             :body   basic-book-info-spec}
                :responses  {200 {:body basic-book-info-spec}
                             405 {:body {:message string?}}}
                :handler    catalog.handlers/insert-book!}}]
    ["/:isbn" {:parameters {:path {:isbn string?}}}
     ["" {:get    {:summary    "get a book info by its isbn"
                   :responses  {200 {:body book-info-spec}
                                404 {:body {:isbn string?}}}
                   :handler    catalog.handlers/get-book}
          :delete {:summary    "delete a book title of the catalog"
                   :parameters {:header {:cookie string?}}
                   :responses  {200 {:body {:deleted int?}}
                                405 {:body {:message string?}}}
                   :handler    catalog.handlers/delete-book!}}]
     ["/item"
      ["/:book-item-id" {:parameters {:path {:book-item-id int?}}}
       ["/checkout" {:post {:summary "creates a lending as the current user"
                            :responses {200 {:body loan-spec}
                                        403 {:body {:message string?}}
                                        404 {:body {:message string?}}
                                        409 {:body {:message string?}}}
                            :handler catalog.handlers/checkout-lending!}}]
       ["/return" {:post {:summary "creates a lending as the current user"
                          :responses {200 {:body {:updated int?}}
                                      403 {:body {:message string?}}
                                      404 {:body {:message string?}}
                                      409 {:body {:message string?}}}
                          :handler catalog.handlers/return-book!}}]]]]]])

(def lending-routes
  ["/user" {:swagger {:tags ["User"]}}
   ["/lendings" {:get {:summary    (str "returns all the loans of the user. "
                                        "Optionally accepts a search "
                                        "criteria if you are a librarian")
                       :parameters {:query {(ds/opt :user-id) string?}}
                       :responses  {200 {:body [loan-spec]}
                                    403 {:body {:message string?}}
                                    404 {:body {:message string?}}}
                       :handler    catalog.handlers/get-book-lendings}}]])
