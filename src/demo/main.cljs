(ns demo.main
  (:require
    [demo.view :as v]
    [demo.core :as c]
    [demo.engine :as engine]
    [net.cgrand.xforms :as x]
    [cljs.core.async :as async]
    [rum.core :as rum :include-macros true :refer [defc]]))

(enable-console-print!)

(comment)
;; ============= 业务集成 ================
(def init-model
  "数据模型"
  {:waiting-users ["foo1" "foof2" "foo3"]
   :passed-users  ["userf1" "柳朕zf"]})

(defc of-model
  "使用rum作为渲染工具，需使用defc来生成页面"
  [{:keys [dispatch model] :as app-state}]
  (v/of-model app-state))

(def event-handlers
  "事件类型与事件处理函数映射表"
  {:user-info      c/user-info
   :user-info-resp c/user-info-resp
   :rm-user-info   c/rm-user-info})

(defmulti http-request "http请求"
          (fn [_ request-params]
            (:run-mode request-params)))

(defmethod http-request :default
  [dispatch request-params]
  (print "app-data = " request-params)
  (.log js/console "invalid run-mode! supported mode: :local or :network")
  request-params)

;; 本地模拟网络请求
(defmethod http-request :local
  [dispatch request-params]
  (print "request-params = " request-params)
  (let [local-data {:user-info-resp {:result true :user-info {:username "柳朕" :age 28}}}
        resp-event (:resp-event request-params)]
    (when request-params
      (async/go
        (dispatch [resp-event (local-data resp-event)])))))

(defmethod http-request :network
  [dispatch request-params]
  ;; TODO 真实的网络请求处理
  )

(defn ^:export -main
  "入口函数"
  []
  (let [elem (.getElementById js/document "app")]
    (print "elem ==  " elem)
    (engine/build-channel elem
                          {:init-model init-model
                           :event-fxs event-handlers
                           :effect-fxs {:request #(http-request %1 (assoc %2 :run-mode :local))}
                           :of-model of-model})))
(-main)


#_(defn wrap-app-dispatcher
  "包装app处理函数，对model模块定义的业务处理函数event-handlers进行了一层统一的包装：
  每次调用业务处理前，都将request置为空，避免业务函数每次都做这个操作。"
  [event-id]
  (let [f-boot   #(assoc %1 :dispatch %2)
        handlers (assoc event-handlers ::boot f-boot)]
    (fn [app-data & event-data]
      (apply (handlers event-id) (dissoc app-data :request) event-data))))

#_(defn ^:export -main
  "入口函数。run-mode为:local时，表示使用本地模式，无网络调用。为:network时，则表示有网络调用"
  []
  (let [run-mode :local
        x-form   (comp
                   (engine/map-reductions wrap-app-dispatcher app-data) ; 转换状态
                   (map #(http-request (assoc % :run-mode run-mode)))
                   (map of-model))
        elem     (.getElementById js/document "app")
        ch       (engine/app-ch x-form elem)
        dispatch (partial async/put! ch)]
    (dispatch [::boot dispatch])
    ;; 监听注册页面的注册结果消息
    ch))

;; 若是真实运行环境，则为(-main :network)

#_(-main)

(comment
  ;;; =============== 开发View层 ================
  (defc of-model
    [{:keys [waiting-users passed-users dispatch] :as original-model}]
    (v/of-model original-model))

  (defn -main []
    (rum/mount (of-model {:waiting-users ["foo1" "foo2"]
                          :passed-users  ["foo1" "pass1" "pass2"]
                          ;:user-info     {:username "zl"}
                          :dispatch      #(js/alert (str %))})
               (.getElementById js/document "app")))

  (-main))