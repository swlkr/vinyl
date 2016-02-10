(defproject vinyl "0.1.0"
  :description "Overengineered blog for my lovely wife"
  :url ""
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.8.0"]
                 [ring-server "0.4.0"]
                 [reagent "0.5.1" :exclusions [org.clojure/tools.reader]]
                 [reagent-forms "0.5.15"]
                 [reagent-utils "0.1.7"]
                 [compojure "1.4.0"]
                 [hiccup "1.0.5"]
                 [org.clojure/clojurescript "1.7.228" :scope "provided"]
                 [secretary "1.2.3"]
                 [venantius/accountant "0.1.6" :exclusions [org.clojure/tools.reader]]
                 [cljs-http "0.1.39"]
                 [http-kit "2.1.18"]
                 [cheshire "5.5.0"]
                 [ragtime "0.5.2"]
                 [org.postgresql/postgresql "9.4-1201-jdbc41"]
                 [yesql "0.5.2"]
                 [clj-jwt "0.1.1"]
                 [crypto-password "0.1.3"]
                 [bunyan "0.1.1"]
                 [ring "1.4.0"]
                 [ring/ring-defaults "0.1.5"]
                 [ring/ring-core "1.4.0"]
                 [ring/ring-devel "1.4.0"]
                 [ring/ring-json "0.4.0"]
                 [markdown-clj "0.9.85"]
                 [envvar "1.1.0"]
                 [clj-aws-s3 "0.3.10" :exclusions [joda-time]]]

  :aliases {"migrate"  ["run" "-m" "vinyl.handler/migrate"]
            "rollback" ["run" "-m" "vinyl.handler/rollback"]}

  :plugins [[lein-cljsbuild "1.1.1"]
            [lein-asset-minifier "0.2.4"
             :exclusions [org.clojure/clojure]]]

  :ring {:handler vinyl.handler/app
         :uberwar-name "vinyl.war"}

  :min-lein-version "2.5.0"

  :uberjar-name "vinyl.jar"

  :main vinyl.server

  :clean-targets ^{:protect false} [:target-path
                                    [:cljsbuild :builds :app :compiler :output-dir]
                                    [:cljsbuild :builds :app :compiler :output-to]]

  :source-paths ["src/clj" "src/cljc"]
  :resource-paths ["resources" "target/cljsbuild"]

  :minify-assets
  {:assets
   {"resources/public/css/site.min.css" "resources/public/css/site.css"}}

  :cljsbuild {:builds {:app {:source-paths ["src/cljs" "src/cljc"]
                             :compiler {:output-to "target/cljsbuild/public/js/app.js"
                                        :output-dir "target/cljsbuild/public/js/out"
                                        :asset-path   "/js/out"
                                        :optimizations :none
                                        :pretty-print  true}}}}


  :profiles {:dev {:repl-options {:init-ns vinyl.repl}

                   :dependencies [[ring/ring-mock "0.3.0"]
                                  [ring/ring-devel "1.4.0"]
                                  [prone "1.0.1"]
                                  [lein-figwheel "0.5.0-6"
                                   :exclusions [org.clojure/core.memoize
                                                ring/ring-core
                                                org.clojure/clojure
                                                org.ow2.asm/asm-all
                                                org.clojure/data.priority-map
                                                org.clojure/tools.reader
                                                org.clojure/clojurescript
                                                org.clojure/core.async
                                                org.clojure/tools.analyzer.jvm]]
                                  [org.clojure/clojurescript "1.7.170"
                                   :exclusions [org.clojure/clojure
                                                org.clojure/tools.reader]]
                                  [org.clojure/tools.nrepl "0.2.12"]
                                  [com.cemerick/piggieback "0.2.1"]
                                  [pjstadig/humane-test-output "0.7.1"]]


                   :source-paths ["env/dev/clj"]
                   :plugins [[lein-figwheel "0.5.0-6"
                              :exclusions [org.clojure/core.memoize
                                           ring/ring-core
                                           org.clojure/clojure
                                           org.ow2.asm/asm-all
                                           org.clojure/data.priority-map
                                           org.clojure/tools.reader
                                           org.clojure/clojurescript
                                           org.clojure/core.async
                                           org.clojure/tools.analyzer.jvm]]
                             [org.clojure/clojurescript "1.7.228"]]


                   :injections [(require 'pjstadig.humane-test-output)
                                (pjstadig.humane-test-output/activate!)]

                   :figwheel {:http-server-root "public"
                              :server-port 3449
                              :nrepl-port 7002
                              :nrepl-middleware ["cemerick.piggieback/wrap-cljs-repl"]

                              :css-dirs ["resources/public/css"]
                              :ring-handler vinyl.handler/app}

                   :cljsbuild {:builds {:app {:source-paths ["env/dev/cljs"]
                                              :compiler {:main "vinyl.dev"
                                                         :source-map true}}}}}






             :uberjar {:hooks [minify-assets.plugin/hooks]
                       :source-paths ["env/prod/clj"]
                       :prep-tasks ["compile" ["cljsbuild" "once"]]
                       :env {:production true}
                       :aot :all
                       :omit-source true
                       :cljsbuild {:jar true
                                   :builds {:app
                                            {:source-paths ["env/prod/cljs"]
                                             :compiler
                                             {:optimizations :advanced
                                              :pretty-print false}}}}}})
