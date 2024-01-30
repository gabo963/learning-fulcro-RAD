(ns com.example.ui.todo-forms
  (:require
    [com.example.model.todo :as todo]
    [com.fulcrologic.rad.form-options :as fo]
    [com.fulcrologic.rad.form :as form]
    [com.fulcrologic.rad.report :as report]
    [com.fulcrologic.rad.report-options :as ro]
    [com.example.model.category :as category]))

(form/defsc-form TodoForm [this props]
  {fo/id           todo/id
   fo/attributes   [todo/text todo/done todo/due todo/status todo/category]
   fo/cancel-route ["landing-page"]
   fo/route-prefix "todos"
   fo/title        "Edit To-do"
   fo/layout       [[:todo/text]
                    [:todo/due :todo/done]
                    [:todo/status :todo/category]]})

(report/defsc-report TodoReport [this props]
  {ro/title            "To-Do List"
   ro/source-attribute :todo/all-todos
   ro/row-pk           todo/id
   ro/columns          [todo/text todo/due todo/status todo/done category/label]
   ro/run-on-mount?    true
   ro/form-links       {todo/id TodoForm}
   ro/route            "todo-report"})
