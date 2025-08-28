(ns app.core)

(def backend-url "http://localhost:3000/api/predict")

(defn q [sel] (.querySelector js/document sel))
(defn qe [el sel] (.querySelector el sel))

(defn to-int ^number [^js el]
  (let [v (.-value el)
        n (js/Number v)]
    (when-not (js/isNaN n) (js/Math.floor n))))

(defn to-binary ^number [^js el] (if (.-checked el) 1 0))

(defn set-text! [sel s] (set! (.-textContent (q sel)) (or s "")))

(defn form-input ^js [^js form]
  (let [el (fn [n] (qe form (str "[name='" n "']")))
        num (fn [n] (or (to-int (el n)) 0))
        bin (fn [n] (to-binary (el n)))]
    #js {:runtime_cleaned         (num "runtime")
         :num_of_ratings_cleaned  (num "num_of_ratings")
         :release_year    (num "release_year")
         :drama (bin "drama") :biography (bin "biography") 
         :documentary (bin "documentary") :animation (bin "animation")
         :action (bin "action") :comedy (bin "comedy") :horror (bin "horror")}))


(defn form-valid? [^js form]
  (.reportValidity form))

(defn handle-submit [^js e]
  (.preventDefault e)
  (let [form (.-currentTarget e)
        input (form-input form)
        json    (.stringify js/JSON input nil 2)] 
    (js/console.log "Input (JSON):" json)
    (set-text! "#errors" "")
    (set-text! "#prediction" "")
    (if-not (form-valid? form)
      (set-text! "#errors" "Please fix the highlighted fields.")
      (-> (js/fetch backend-url
                    #js {:method "POST"
                         :headers #js {"Content-Type" "application/json"}
                         :body (.stringify js/JSON (form-input form))}) 
          (.then (fn [resp]
                   (if (.-ok resp) (.json resp)
                       (js/Promise.reject (str "HTTP " (.-status resp))))))
          (.then (fn [data]
                   (let [m (js->clj data :keywordize-keys true)
                         p (:prediction m)
                         pstr (when p (.toFixed (js/Number p) 1))]
                     (set-text! "#prediction" (if pstr (str "Predicted rating: " pstr) "No prediction")))))
          (.catch (fn [err] (set-text! "#errors" (str err))))))))

(defn ^:export init []
  (when-let [form (q "#predict-form")]
    (.addEventListener form "submit" handle-submit)))
