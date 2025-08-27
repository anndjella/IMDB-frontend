(ns app.core)

(defn q [sel] (.querySelector js/document sel))
(defn qe [el sel] (.querySelector el sel))

(defn to-int ^number [^js el]
  (let [v (.-value el)
        n (js/Number v)]
    (when-not (js/isNaN n) (js/Math.floor n))))

(defn to-binary ^number [^js el] (if (.-checked el) 1 0))

(defn form-input ^js [^js form]
  (let [el (fn [n] (qe form (str "[name='" n "']")))
        num (fn [n] (or (to-int (el n)) 0))
        bin (fn [n] (to-binary (el n)))]
    #js {:runtime         (num "runtime")
         :num_of_ratings  (num "num_of_ratings")
         :release_year    (num "release_year")
         :drama (bin "drama") :biography (bin "biography") 
         :documentary (bin "documentary") :animation (bin "animation")
         :action (bin "action") :comedy (bin "comedy") :horror (bin "horror")}))


(defn form-valid? [^js form]
  (.checkValidity form))

(defn handle-submit! [^js e]
  (.preventDefault e)
  (let [form (.-currentTarget e)]
    (if-not (form-valid? form)
      (js/console.warn "Not valid form")
      (js/console.log "Input: " (form-input form)))))

(defn ^:export init []
  (when-let [form (q "#predict-form")]
    (.addEventListener form "submit" handle-submit!)))
