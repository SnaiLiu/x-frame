(ns demo.engine
  (:require [cljs.core.async :as async]
            [net.cgrand.xforms :as x]
            [rum.core :as rum]))

(defn app-ch
  "返回程序通道: 它将使用 x-form 这个 transform 函数将每个事件 event
  转化为 view (rum 组件)，并将其渲染到 elem (HTMLElement) 上"
  [x-form elem]
  (let [input-ch (async/chan 1 x-form)]
    (async/go-loop []
                   (when-let [view (async/<! input-ch)]
                     (rum/mount view elem)
                     (recur)))
    input-ch))

(defn mk-executor
  "构建执行器"
  [event-fxs effect-fxs]
  (fn [app-state [event-id & event-args]]
    (let [effects   (apply (event-fxs event-id) app-state event-args)
          new-state (cond-> effects
                            (:dispatch app-state)
                            (assoc :dispatch (:dispatch app-state))
                            (not (:model effects))
                            (assoc :model (:model app-state)))]
      (doseq [[k fx] effect-fxs]
        (when (contains? new-state k)
          (fx (:dispatch new-state) (new-state k))))
      (apply dissoc new-state (keys effect-fxs)))))

(defn build-channel
  "构建一个生产流水线：
   handlers-map：处理器映射表
   {:db #(db-handler ...)
    :request #(request-handler ...)
    :once-timer #(once-timer-handler ...)
    .. ;; other side-effect-handlers}"
  [elem {:keys [init-model event-fxs effect-fxs of-model]}]
  (let [f-boot #(assoc %1 :dispatch %2)
        event-fxs (assoc event-fxs ::boot f-boot)
        x-form (comp (x/reductions
                       (mk-executor event-fxs effect-fxs)
                       {:model init-model})
                     (map of-model))
        ch       (app-ch x-form elem)
        dispatch (partial async/put! ch)]
    (dispatch [::boot dispatch])
    ch))