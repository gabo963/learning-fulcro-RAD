(ns com.example.model.todo
  (:require
    #?(:clj [com.example.components.database-queries :as queries])
    [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
    [com.wsscode.pathom.connect :as pc]
    [com.fulcrologic.rad.attributes-options :as ao]))

(defattr id :todo/id :uuid
  {ao/identity? true
   ao/schema    :production})

(defattr text :todo/text :string
  {ao/schema     :production
   ao/identities #{:todo/id}})

(defattr done :todo/done :boolean
  {ao/schema     :production
   ao/identities #{:todo/id}})

(defattr due :todo/due :instant
  {ao/schema     :production
   ao/identities #{:todo/id}})

(def statuses #:todo.status {:DONE   "Done"
                             :WIP    "In progress"
                             :LATE   "Late"
                             :CLOSED "Closed"
                             :PLAN   "Planned"})

(defattr status :todo/status :enum
  {ao/enumerated-values (set (keys statuses))
   ao/identities        #{:todo/id}
   ao/schema            :production
   ao/enumerated-labels statuses})

(defattr category :todo/category :ref
  {ao/target      :category/id
   ao/cardinality :many
   ao/schema      :production
   ao/identities  #{:todo/id}})

(defattr all-todos :todo/all-todos :ref
  {ao/target    :todo/id
   ::pc/output  [{:todo/all-todos [:todo/id]}]
   ::pc/resolve (fn [{:keys [query-params] :as env} _]
                  #?(:clj
                     {:todo/all-todos (queries/get-all-todos env query-params)}))})
#?(:clj
   (pc/defresolver todo-category-resolver [{:keys [parser] :as env} {:todo/keys [id]}]
     {::pc/input  #{:todo/id}
      ::pc/output [:category/id :category/label]}
     (let [result (parser env [{[:todo/id id] [{:todo/category [:category/id :category/label]}]}])]
       (get-in result [[:todo/id id] :item/category]))))


(def attributes [id text done due status category all-todos])

#?(:clj
   (def resolvers [todo-category-resolver]))
