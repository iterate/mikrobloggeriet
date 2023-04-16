;; OLORM DEVUI

(ns olorm.devui
  (:require
   [nextjournal.clerk :as clerk]
   [olorm.lib :as olorm]
   [iki.api :as iki]
   [babashka.fs :as fs]))

;; ## Session 1

(defonce xx (atom nil))

@xx

(clerk/table
 (for [[h v] (:headers @xx)]
   {:header h :value v}))

123123123

(let [repo-path  ".."]
  (->> (fs/list-dir (fs/canonicalize (str repo-path "/p")))
       (map fs/file-name)
       (map str)
       (map olorm/slug->olorm)
       (filter :olorm)))

#_
(let [repo-path  ".."]
  (fs/canonicalize
   (str repo-path "/p"))
  #_#_#_
  (map str)
  (map olorm/slug->olorm)
  (filter :olorm))

;; ## Session 2

(defn olorm->html [& _args])

(olorm->html {:slug "olorm-1" :repo-path ".."})

;; ... or do I want hiccup?
;; not sure.
;; how do I want this to integrate with the rest of the system?

;; Q: what am I doing (really) in OGGPOW?
;;
;; A: I just generate an HTML string and slap it into the Hiccup tree.
;;
;;     (defn page [req]
;;       (let [{:keys [slug]} (:params req)
;;             page {:slug slug}
;;             time-before-convert (now-ms)
;;             page-html (->html page)
;;             time-after-convert (now-ms)]
;;         (page/html5
;;          [:head (hiccup.page/include-css "/style.css")]
;;          [:body
;;           [:div {:class style/content-container}
;;            [:a {:href "/"} "home"]
;;            [:h2 slug]
;;            [:pre (with-out-str (pprint/pprint (->meta page)))]
;;            [:p "Inferred builder: " [:code  (pr-str (infer-builder page))]]
;;            [:p "Page created in " [:em  (- time-after-convert time-before-convert)] " ms."]
;;            [:div page-html]]])))
;;
;; Okay, let's to the same here.

(let [repo-path ".." slug "olorm-1"]
  [(str (fs/canonicalize repo-path) "/p/" slug)
   (str
    (fs/canonicalize
     (fs/file repo-path "p" slug)))]
  )

(def markdown->html
  (iki/cache-fn-by iki/markdown->html identity))

(defn olorm->html [{:keys [repo-path slug]}]
  ;; canonicalize
  (let [olorm-path (fs/path repo-path "p" slug)
        index-md-file (fs/file olorm-path "index.md")]
    ;; validate
    (when (and (fs/directory? olorm-path)
             (fs/exists? index-md-file))
      (markdown->html (slurp index-md-file)))))

;; infer builder
;;
;; no!
;;
;; we just support markdown for now.

(clerk/html
 (olorm->html {:slug "olorm-1" :repo-path ".."}))






^
{:nextjournal.clerk/visibility {:code :hide}}
(clerk/html [:div {:style {:height "50vh"}}])
