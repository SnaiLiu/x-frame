(ns demo.core
  "核心业务逻辑模块
  * 本模块负责处理各种事件，完全由纯函数实现：输入为整个app的数据model和各事件传递过来的数据，输出为新的数据model。

  * cljc文件用于clojure和clojurescript公用代码。
    当我们的web开发和server开发在同一个工程，且双方都有一些相同的处理逻辑时，就可以将其放在.cljc文件中。

    理论上讲，在cljc文件中实现的代码都应该同时支持clojure和clojurescript。

    建议在该逻辑单元中，尽量不要写js代码。")

;; =========================
;; 组装请求数据
;; =========================

#?(:cljs
   (defn encode-uri-component
     [uri-component]
     (js/encodeURIComponent uri-component)))


#?(:clj
   (defn encode-uri-component
     ;; FIXME 仅用于展示说明，非正确的实现
     [uri-component]
     uri-component))

(defn user-info-request
  "用户详细信息请求数据"
  [username]
  {:method :get
   :uri    (str "/hjd/" (encode-uri-component username))
   :resp-event :user-info-resp})


(defn user-info
  "查询用户详细信息"
  [{:keys [model] :as app-state} username]
  {:request (user-info-request username)})

(defn user-info-resp
  "处理用户详细信息的查询结果"
  [{:keys [model] :as app-state} {:keys [user-info] :as resp}]
  {:model (assoc model :user-info user-info)})

(defn rm-user-info
  "删除用户详情"
  [{:keys [model] :as app-state}]
  {:model (assoc model :user-info nil)})