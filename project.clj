(defproject demo "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://localhost:/FIXME"
  :dependencies [
                 [org.clojure/clojure "1.9.0-alpha16"]
                 [org.clojure/clojurescript "1.9.229"]

                 [org.clojure/core.async "0.3.465"]        ; 异步通道
                 ;
                 [net.cgrand/xforms "0.15.0"]              ; 更多的 transducer 函数，引擎需要使用到
                 [rum "0.10.8"]                            ; rum库提供页面渲染功能
                 ]

  :plugins [[lein-cljsbuild "1.1.6" :exclusions [[org.clojure/clojure]]] ;; clojurescript编译打包需要的插件
            [lein-figwheel "0.5.10"]] ;; figwheel插件

  :min-lein-version "2.7.1"

  :cljsbuild {:builds
              [{:id           "dev" ;; 开发时的编译、打包配置
                :source-paths ["src"]
                :figwheel     {:open-urls ["http://localhost:3449/demo.html"] ;; figwheel配置
                               :on-jsload            "demo.main/-main"
                               }
                :compiler     {:main                 demo.main
                               :asset-path           "js/compiled/out"
                               :output-to            "resources/public/js/compiled/demo.js" ;; 指定编译的js文件存放的目录
                               :output-dir           "resources/public/js/compiled/out"
                               :source-map-timestamp true
                               :preloads             [devtools.preload]
                               }}
               {:id           "min" ;; 发布时的编译、打包配置
                :source-paths ["src"]
                :compiler     {:output-to     "resources/public/js/compiled/demo.js"
                               :main          demo.main
                               :optimizations :advanced ;; 优化、压缩生成的js
                               :pretty-print  false}}]}

  :figwheel {:css-dirs ["resources/public/css"]}

  :profiles {:dev {;;这里的依赖库主要是开发过程中figwheel-repl 和 cljs-repl所需库
                   :dependencies  [[binaryage/devtools "0.9.2"]
                                   [figwheel-sidecar "0.5.10"]
                                   [com.cemerick/piggieback "0.2.1"]]
                   :source-paths  ["src"]
                   :repl-options  {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}
                   ;; 执行 lein clean时会情况的目录
                   :clean-targets ^{:protect false} ["resources/public/js/compiled"
                                                     :target-path]}}

  ;; repl中启动figwheel
  ;; (require '[figwheel-sidecar.repl-api :as f])
  ;; (f/start-figwheel!)
  ;; repl中启动cljs-repl
  ;; (f/cljs-repl)

  )