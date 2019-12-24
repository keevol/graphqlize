(ns graphqlize.lacinia.field
  (:require [graphqlize.lacinia.name :as l-name]
            [honeyeql.meta-data :as heql-md]))

(defn lacinia-type [heql-attr-type heql-attr-ref-type]
  (case heql-attr-type
    :attr.type/ref (l-name/object heql-attr-ref-type)
    (:attr.type/big-integer :attr.type/integer) 'Int
    (:attr.type/float :attr.type/double :attr.type/decimal) 'Float
    :attr.type/boolean 'Boolean
    (:attr.type/string :attr.type/Unknown) 'String
    nil))

(defn generate [heql-meta-data attr-ident]
  (let [attr-meta-data                     (heql-md/attr-meta-data heql-meta-data attr-ident)
        {:attr/keys [ident nullable type]} attr-meta-data
        field-type                         (lacinia-type type (:attr.ref/type attr-meta-data))
        gql-field-type                     (cond->> field-type
                                             (= :attr.ref.cardinality/many
                                                (:attr.ref/cardinality attr-meta-data)) (list 'list)
                                             (not nullable) (list 'non-null))]
    (when field-type
      {(l-name/field ident) {:type gql-field-type}})))