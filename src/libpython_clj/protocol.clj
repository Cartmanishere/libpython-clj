(ns libpython-clj.protocol)


(defprotocol Datafiable
  :extend-via-metadata true

  (datafy [o] "return a representation of o as data (default identity)"))

(extend-protocol Datafiable
  nil
  (datafy [_] nil)

  Object
  (datafy [x] x))

(defprotocol Navigable
  :extend-via-metadata true

  (nav [coll k v] "return (possibly transformed) v in the context of coll and k (a key/index or nil),
defaults to returning v."))

(extend-protocol Navigable
  Object
  (nav [_ _ x] x))
