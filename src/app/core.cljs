(ns app.core)

(defn q [sel] (.querySelector js/document sel))

(defn form-valid? [^js form]
  (.checkValidity form))

(defn handle-submit! [^js e]
  (.preventDefault e)
  (let [form (.-currentTarget e)]
    (if (form-valid? form)
      (js/console.log "Valid form!")
      (js/console.warn "Not valid form"))))

(defn ^:export init []
  (when-let [form (q "#predict-form")]
    (.addEventListener form "submit" handle-submit!)))
