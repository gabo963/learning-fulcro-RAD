(ns com.example.components.database-queries
  (:require
    [com.fulcrologic.rad.database-adapters.datomic-options :as do]
    [datomic.client.api :as d]
    [taoensso.timbre :as log]
    [taoensso.encore :as enc]))

(defn- env->db [env]
  (some-> env (get-in [do/databases :production]) (deref)))

(defn get-all-todos
  [env {:category/keys [id]}]
  (if-let [db (env->db env)]
    (let [ids (if id
                (d/q '[:find ?uuid
                       :in $ ?catid
                       :where
                       [?c :category/id ?catid]
                       [?t :item/category ?c]
                       [?t :todo/id ?uuid]] db id)
                (d/q '[:find ?uuid
                       :where
                       [_ :todo/id ?uuid]] db))]
      (mapv (fn [[id]] {:todo/id id}) ids))
    (log/error "No database atom for production schema!"))
  )

(defn get-all-categories
  [env query-params]
  (if-let [db (env->db env)]
    (let [ids (d/q '[:find ?id
                     :where
                     [?e :category/label]
                     [?e :category/id ?id]] db)]
      (mapv (fn [[id]] {:category/id id}) ids))
    (log/error "No database atom for production schema!")))

(defn get-login-info
  "Get the account name, time zone, and password info via a username (email)."
  [env username]
  (enc/if-let [db (log/spy :info (env->db env))]
    (d/pull db [:account/name
                {:time-zone/zone-id [:db/ident]}
                :password/hashed-value
                :password/salt
                :password/iterations]
      [:account/email username])))
