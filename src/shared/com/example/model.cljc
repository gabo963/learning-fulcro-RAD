(ns com.example.model
  (:require
    [com.example.model.timezone :as timezone]
    [com.example.model.category :as category]
    [com.example.model.todo :as todo]
    [com.fulcrologic.rad.attributes :as attr]))

(def all-attributes (vec (concat
                           category/attributes
                           timezone/attributes
                           todo/attributes)))

(def all-attribute-validator (attr/make-attribute-validator all-attributes))
