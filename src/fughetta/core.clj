(ns fughetta.core
  (:import [org.jfugue Player]))

(def ^{:private true} player (Player.))

(defn play!
  [& patts]
  (.play player (apply mix patts)))

(defn stop!
  []
  (.stop player))

(defn- key->channel
  [k]
  (str "[" (st/upper-case (st/replace-char \- \_ (name k))) "]"))

(defn drum
  [& layers]
  (apply str "V9 "
         (mapcat
          (fn [[i [k v]]]
            (let [k (key->channel k)]
              (str " L" (inc i)
                   " "
                   (st/join " " (map #(let [t (name %)]
                                    (if (= (first t) \R)
                                      t
                                      (str k t))) v))
                   " ")))
          (map-indexed vector (partition 2 layers)))))

(defn bpm
  "doc-string"
  [n patt]
  (str "T" n " " patt))

(defn vol
  [n patt]
  (str "X[Volume]=" n " " patt))

(defn guitar-tab
  [inst strings]
  (apply str "V9 "
         (mapcat

          (map-indexed vector (partition 2 strings)))))

(defn inst
  [i patt]
  (str "I" (key->channel i) " " patt))
