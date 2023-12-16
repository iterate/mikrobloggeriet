(ns
    ^{:doc "https://mikrobloggeriet.no/urlog/

Neno deler lenker --- og ønsker mer kontroll på utseende enn det han får ut
av boksen med mikrobloggeriet.

Trekker dette ut i et eget navnerom for å gjøre det lettere for Neno å
fokusere på det som er relevant for Neno, og gjøre koden i
`mikrobloggeriet.serve` mer lesbar. Det reduserer også risiko for at
urlog-eksperimentering brekker resten av mikrobloggeriet.
"}
    mikrobloggeriet.urlog
  (:require
   [hiccup.page :as page]
   [mikrobloggeriet.cache :as cache]
   [mikrobloggeriet.cohort :as cohort]
   [mikrobloggeriet.doc :as doc]
   [mikrobloggeriet.doc-meta :as doc-meta]
   [mikrobloggeriet.pandoc :as pandoc]
   [mikrobloggeriet.store :as store]
   [ring.middleware.cookies :as cookies]))

(defn feeling-lucky [content]
  [:a {:href "/random-doc" :class :feeling-lucky} content])

(defn urlogs
  "Display urlogs to Neno's liking (hopefully)"
  [_req]
  (page/html5
   [:head (page/include-css "/urlog.css")]
   [:body
    [:p [:a {:href "/random-doc" :class :feeling-lucky} "🎄"]]
    [:main
     [:p
      (let [cohort store/urlog]
        (interpose " · "
                   (for [doc (->> (store/docs cohort)
                                  (map (fn [doc] (store/load-meta cohort doc)))
                                  (remove doc-meta/draft?))]
                     [:a {:href (store/doc-href cohort doc)} "🚪"])))]]]))

(defn html-header
  "Shared HTML, including CSS.
  Handles CSS theming system with cookies."
  [req]
  [[:meta {:charset "utf-8"}]
   [:meta {:name "viewport" :content "width=device-width,initial-scale=1"}]
   (hiccup.page/include-css "/vanilla.css")
   (hiccup.page/include-css "/mikrobloggeriet.css")
   (let [theme (get-in (cookies/cookies-request req)
                       [:cookies "theme" :value]
                       "christmas")]
     (hiccup.page/include-css (str "/theme/" theme ".css")))
   (let [theme (get-in (cookies/cookies-request req) [:cookies "theme" :value])
         number (rand-nth (range 4))]
     (when (= theme "iterate")
       [:style {:type "text/css"}
        (str ":root{ --text-color: var(--iterate-base0" number ")}")]))])

(def markdown->html+info
  (cache/cache-fn-by (fn markdown->html+info [markdown]
                       (let [pandoc (pandoc/from-markdown markdown)]
                         {:doc-html (pandoc/to-html pandoc)
                          :title (pandoc/infer-title pandoc)}))
                     identity))

(defn doc
  [req cohort]
  (when (:slug (:route-params req))
    (let [doc (doc/from-slug (:slug (:route-params req)))
          {:keys [title doc-html]}
          (when (store/doc-exists? cohort doc)
            (markdown->html+info (slurp (store/doc-md-path cohort doc))))]
      {:status 200
       :body
       (page/html5
        (into [:head] (concat (when title [[:title title]])
                              (html-header req)))
        [:body
         [:p (feeling-lucky "🎄")
          " — "
          [:a {:href "/"} "mikrobloggeriet"]
          " "
          [:a {:href (str "/" (cohort/slug cohort) "/")}
           (cohort/slug cohort)]
          " — "
          [:span (let [
                       previouse-number (dec (doc/number doc))
                       prev (doc/from-slug (str (cohort/slug cohort) "-" previouse-number))]
                   (when (store/doc-exists? cohort prev)
                     [:span [:a {:href (str (store/doc-href cohort prev))} (doc/slug prev)] " · "]))]
          [:span (:doc/slug doc) ]
          [:span (let [previouse-number (inc (doc/number doc))
                       prev (doc/from-slug (str (cohort/slug cohort) "-" previouse-number))]
                   (when (store/doc-exists? cohort prev)
                     [:span " · " [:a {:href (str (store/doc-href cohort prev))}  (doc/slug prev)]]
                     ))]]
         doc-html])})))

(defn index-section [_req]
  [:section
   [:h2 "URLOG"]
   [:p "Tilfeldige dører til internettsteder som kan være morsomme og/eller interessante å besøke en eller annen gang."]
   [:p
    (let [cohort store/urlog]
      (interpose " · "
                 (for [doc (->> (store/docs cohort)
                                (map (fn [doc] (store/load-meta cohort doc)))
                                (remove doc-meta/draft?))]
                   [:a {:href (store/doc-href cohort doc)} "🚪"])))]])
