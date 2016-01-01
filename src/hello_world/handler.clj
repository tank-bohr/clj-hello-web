(ns hello-world.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [hiccup.page :as page]
            [hiccup.form :as form]
            [hiccup.element :as elem]
            [ring.util.response :as resp]
            [ring.util.anti-forgery :as af]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]))

(defn index
  []
  (page/html5
    [:head
      [:meta {:http-equiv "Content-Type" :content "text/html;charset=utf-8"}]
      [:title "Welcome"]]
    [:body
      (elem/link-to "/hello", "Hello")]))

(defn hello
  [{{{name-from-cookies :value}, "hw_name"} :cookies}]
  (let [name (if (nil? name-from-cookies) "мир" name-from-cookies)]
    (page/html5
      [:head
        [:meta {:http-equiv "Content-Type" :content "text/html;charset=utf-8"}]
        [:title "Hello world"]]
      [:body
        [:div {:id "content"} (str "Привет, " name "!")]
        (form/form-to ["POST" "create"]
          (form/text-field {:placeholder "Enter name"} "name")
          (af/anti-forgery-field)
          (form/submit-button "Отправить"))
      ])))

(defn create
  [name]
  (assoc
    (resp/redirect (str "/hello"))
    :cookies {"hw_name" {:value name :http-only true}} ))

(defroutes app-routes
  (GET "/" [] (index))
  (GET "/hello" request (hello request))
  (POST "/create" [name] (create name))
  (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes site-defaults))
