(ns mblog2.schema)

(def schema
  [;; Cohorts
   {:db/ident :cohort/id
    :db/valueType :db.type/keyword
    :db/cardinality :db.cardinality/one
    :db/unique :db.unique/identity}

   {:db/ident :cohort/root
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/unique :db.unique/identity}

   {:db/ident :cohort/slug
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/unique :db.unique/identity}

   {:db/ident :cohort/type
    :db/valueType :db.type/keyword
    :db/cardinality :db.cardinality/one}

   {:db/ident :cohort/name
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one}

   {:db/ident :cohort/description
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one}

   ;; Authors
   {:db/ident :author/email
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/unique :db.unique/identity}

   {:db/ident :author/first-name
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one}

   ;; Docs
   {:db/ident :doc/slug
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/unique :db.unique/identity}

   {:db/ident :doc/created
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one}

   {:db/ident :doc/uuid
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/unique :db.unique/identity}

   {:db/ident :git.user/email
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one}

   {:db/ident :doc/markdown
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one}

   {:db/ident :doc/html
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one}

   {:db/ident :doc/cohort
    :db/valueType :db.type/ref
    :db/cardinality :db.cardinality/one}

   ;; Doc author
   {:db/ident :doc/primary-author
    :db/valueType :db.type/ref
    :db/cardinality :db.cardinality/one}

   ;; Cohort docs
   {:db/ident :cohort/docs
    :db/valueType :db.type/ref
    :db/cardinality :db.cardinality/many}
   ])
