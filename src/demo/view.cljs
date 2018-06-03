(ns demo.view
  "视图展示层
  * 该单元的所有组件完全由纯函数实现，即给什么数据，就生成一个什么视图，它不关心数据的外部存储形式。
    另外，它要求使用者提供一个方法，将交互事件传递出去，事件结构为[event-type & 事件其他数据]，
    因此要求该方法可以接收一个该事件结构作为参数。
  * 该单元没有引入任何其他依赖，即便后续要引入依赖，规定只能引入作用于该单元内部的依赖。

  这样设计，大大增强了该视图组件的可重用性。")

(defn user-list
  "用户列表"
  [users dispatch]
  [:ul.user-list
   (doall
     (for [username users]
       [:li.username {:key      username
                      :on-click #(dispatch [:user-info username])}
        username]))])

(defn waiting-list
  "正等待的用户列表"
  [waiting-users dispatch]
  [:div.waiting-users
   [:div.title
    [:span "=====正排队等待的用户列表====="]]
   (user-list waiting-users dispatch)])

(defn passed-list
  "已通过的用户列表"
  [passed-users dispatch]
  [:div.passed-users
   [:div.title
    [:span "=====已通过的用户列表====="]]
   (user-list passed-users dispatch)])

(defn user-detail
  "用户详细信息"
  [{:keys [username] :as user-info} dispatch]
  [:div.user-info
   [:button.close {:on-click #(dispatch [:rm-user-info])} "关闭"]
   [:div.title "======用户详情======"]
   [:span.label "姓名："] [:span.username username]])

(defn of-model
  "根据业务数据模型生成视图"
  [{:keys [model dispatch] :as app-state}]
  (let [{:keys [waiting-users passed-users user-info]} model]
    [:div
     [:div.list {:style {:display (if user-info "none" "block")}}
      (waiting-list waiting-users dispatch)
      (passed-list passed-users dispatch)]
     (when user-info
       (user-detail user-info dispatch))]))
