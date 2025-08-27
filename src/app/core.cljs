(ns app.core
  (:require [reagent.core :as r]
            [reagent.dom.client :as rdom]))

(defn App []
  [:div {:style {:maxWidth "860px" :margin "2rem auto" :fontFamily "system-ui, sans-serif"}}
   [:h1 "Movie rating predictor"]])

(defonce root* (atom nil))

(defn mount! []
  (let [container (.getElementById js/document "app")]
    (when-not @root* (reset! root* (rdom/create-root container)))
    (rdom/render @root* (r/as-element [App]))))

(defn init [] (mount!))
