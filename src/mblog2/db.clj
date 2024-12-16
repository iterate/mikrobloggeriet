(ns mblog2.db
  "Load what we know about cohorts and docs."
  (:require
   [babashka.fs :as fs]
   [clojure.edn :as edn]
   [datomic.api :as d]
   [mblog2.schema]))

;; Cohorts and authors are added by hand!

(def cohorts
  {:cohort/iterate
   {:cohort/root "text/iterate",
    :cohort/slug "iterate",
    :cohort/type :cohort.type/markdown,
    :cohort/name "ITERATE",
    :cohort/description "Mikrobloggen ITERATE skrives av folk fra Iterate."},
   :cohort/jals
   {:cohort/root "j",
    :cohort/slug "jals",
    :cohort/type :cohort.type/markdown,
    :cohort/name "JALS"
    :cohort/description "Mikrobloggen JALS skrives av Adrian, Lars og Sindre. Jørgen har skrevet tidligere."},
   :cohort/kiel
   {:cohort/root "text/kiel",
    :cohort/slug "kiel",
    :cohort/type :cohort.type/markdown,
    :cohort/name "Kunstig Intelligens—Ekte Læring"
    :cohort/description "Designer og teknologiformidler, Julian Hallen Eriksen, utforsker muligheter og utfordringer knytta til AI i norske skoler."},
   :cohort/luke
   {:cohort/root "text/luke",
    :cohort/slug "luke",
    :cohort/type :cohort.type/markdown,
    :cohort/name "Mikrobloggeriets Julekalender 2023"
    :cohort/description "Mikrobloggen LUKE ble skrevet av Iterate-ansatte gjennom adventstida 2023."},
   :cohort/oj
   {:cohort/root "text/oj",
    :cohort/slug "oj",
    :cohort/type :cohort.type/markdown,
    :cohort/name "OJ"
    :cohort/description "Mikrobloggen OJ skrives av Olav og Johan."},
   :cohort/olorm
   {:cohort/root "o",
    :cohort/slug "olorm",
    :cohort/type :cohort.type/markdown,
    :cohort/name "OLORM",
    :cohort/description
    "Mikrobloggen OLORM skrives av Oddmund, Lars, Richard og Teodor."},
   :cohort/urlog
   {:cohort/root "text/urlog",
    :cohort/slug "urlog",
    :cohort/type :cohort.type/urlog,
    :cohort/name "URLOG"
    :cohort/description "Tilfeldige dører til internettsteder som kan være morsomme og/eller interessante å besøke en eller annen gang."},
   :cohort/vakt
   {:cohort/root "text/vakt",
    :cohort/slug "vakt",
    :cohort/type :cohort.type/markdown,
    :cohort/name "VAKT"
    :cohort/description "Fra oss som lager Mikrobloggeriet."}})

(def authors
  [{:author/email "aaberg89@gmail.com", :author/first-name "Jørgen"}
   {:author/email "adrian.tofting@iterate.no", :author/first-name "Adrian"}
   {:author/email "brunstad@iterate.no", :author/first-name "Ole Jacob"}
   {:author/email "camilla@iterate.no", :author/first-name "Camilla"}
   {:author/email "ella.swan@iterate.no", :author/first-name "Ella"}
   {:author/email "finn@iterate.no", :author/first-name "Finn"}
   {:author/email "git@teod.eu", :author/first-name "Teodor"}
   {:author/email "haavard@vaage.com", :author/first-name "Håvard"}
   {:author/email "haugeto@iterate.no", :author/first-name "Anders"}
   {:author/email "jomarn@me.com", :author/first-name "Johan"}
   {:author/email "julian.hallen.eriksen@iterate.no", :author/first-name "Julian"}
   {:author/email "kjersti@iterate.no", :author/first-name "Kjersti"}
   {:author/email "lars.barlindhaug@iterate.no", :author/first-name "Lars"}
   {:author/email "larshbj@gmail.com", :author/first-name "Lars"}
   {:author/email "neno.mindjek@iterate.no", :author/first-name "Neno"}
   {:author/email "oddmunds@iterate.no", :author/first-name "Oddmund"}
   {:author/email "olav.moseng@iterate.no", :author/first-name "Olav"}
   {:author/email "pernille@iterate.no", :author/first-name "Pernille"}
   {:author/email "rasmus.stride@iterate.no", :author/first-name "Rasmus"}
   {:author/email "richard.tingstad@iterate.no", :author/first-name "Richard"}
   {:author/email "rune@iterate.no", :author/first-name "Rune"}
   {:author/email "sindre@iterate.no", :author/first-name "Sindre"}
   {:author/email "thusan@iterate.no", :author/first-name "Thusan"}])

(defn find-cohort-docs [cohort]
  (->> (fs/list-dir (:cohort/root cohort))
       (filter (fn [dir]
                 (and (fs/exists? (fs/file dir "index.md"))
                      (fs/exists? (fs/file dir "meta.edn")))))
       (map (fn [dir]
              (assoc (select-keys (edn/read-string (slurp (fs/file dir "meta.edn")))
                                  [:doc/created :doc/uuid :git.user/email])
                     :doc/markdown (slurp (fs/file dir "index.md"))
                     :doc/slug (fs/file-name dir))))))

(defn loaddb [cohorts authors]
  (let [uri (str "datomic:mem://" (random-uuid))
        _ (d/create-database uri)
        conn (d/connect uri)]
    @(d/transact conn mblog2.schema/schema)
    @(d/transact conn
                 (for [[cohort-id cohort] cohorts]
                   (assoc cohort :cohort/id cohort-id)))
    @(d/transact conn authors)
    (doseq [cohort-internal-id (d/q '[:find [?e ...] :where [?e :cohort/id]] (d/db conn))]
      (let [cohort (d/entity (d/db conn) cohort-internal-id)]
        @(d/transact conn
                     [{:cohort/id (:cohort/id cohort)
                       :cohort/docs (find-cohort-docs cohort)}])))
    ;; Create relation :doc/cohort by inverting :cohort/docs
    (doseq [doc-slug (d/q '[:find [?slug ...] :where [_ :doc/slug ?slug]] (d/db conn))]
      (let [doc (d/entity (d/db conn) [:doc/slug doc-slug])]
        @(d/transact conn
                     [{:doc/slug doc-slug
                       :doc/cohort [:cohort/id (:cohort/id (first (:cohort/_docs doc)))]}])))
    (d/db conn)))

(comment
  (set! *print-namespace-maps* false)
  (def db (loaddb cohorts authors))
  (def olorm (d/entity db [:cohort/id :cohort/olorm]))

  (:cohort/docs olorm)

  (def olorm-7 (d/entity db [:doc/slug "olorm-7"]))
  (into {} olorm-7)

  (:cohort/_docs olorm-7)
  )
