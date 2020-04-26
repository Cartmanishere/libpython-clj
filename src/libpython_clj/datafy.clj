(ns libpython-clj.datafy
  (:require [libpython-clj.protocol :as p]
            [tech.parallel.utils :refer [requiring-resolve]]))


(set! *warn-on-reflection* true)

(defn datafy
  "Attempts to return x as data.
  datafy will return the value of clojure.core.protocols/datafy. If
  the value has been transformed and the result supports
  metadata, :clojure.datafy/obj will be set on the metadata to the
  original value of x, and :clojure.datafy/class to the name of the
  class of x, as a symbol."
  [x]
  (let [v (p/datafy x)]
    (if (identical? v x)
      v
      (if (instance? clojure.lang.IObj v)
        (vary-meta v assoc ::obj x ::class (-> x class .getName symbol))
        v))))

(defn nav
  "Returns (possibly transformed) v in the context of coll and k (a
  key/index or nil). Callers should attempt to provide the key/index
  context k for Indexed/Associative/ILookup colls if possible, but not
  to fabricate one e.g. for sequences (pass nil). nav returns the
  value of clojure.core.protocols/nav."
  [coll k v]
  (p/nav coll k v))

(defn- sortmap [m]
  (into (sorted-map) m))

(extend-protocol p/Datafiable
  Throwable
  (datafy [x]
          (Throwable->map x))

  clojure.lang.IRef
  (datafy [r]
          (with-meta [(deref r)] (meta r)))

  clojure.lang.Namespace
  (datafy [n]
          (with-meta {:name (.getName n)
                      :publics (-> n ns-publics sortmap)
                      :imports (-> n ns-imports sortmap)
                      :interns (-> n ns-interns sortmap)}
            (meta n)))

  java.lang.Class
  (datafy [c]
    (let [{:keys [members] :as ret} ((requiring-resolve 'clojure.reflect/reflect) c)]
            (assoc ret :name (-> c .getName symbol) :members (->> members (group-by :name) sortmap)))))
