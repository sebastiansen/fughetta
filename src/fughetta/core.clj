(ns fughetta.core
  (:import [org.jfugue Player])
  (:require [clojure.string :as st]))

(def ^{:private true} player (Player.))

(defrecord Note [pitch durations chord]
  Object
  (toString [this] (str pitch chord (apply str durations))))

(defn pattern
  [& xs]
  (str " " (st/join " " xs) " "))

(defn play!
  [& patts]
  (.play player (apply pattern patts)))

(defn stop!
  []
  (.stop player))

(defn save!
  [file-name & patts]
  (.save player (apply pattern patts) file-name))

(defn ++
  [& notes]
  (reduce #(str %1 "+" %2) notes))

(defn --
  [& notes]
  (reduce #(str %1 "_" %2) notes))

(def notes
  {:cf -1 :c  0 :cs  1
   :df  1 :d  2 :ds  3
   :ef  3 :e  4 :es  5
   :ff  4 :f  5 :fs  6
   :gf  6 :g  7 :gs  8
   :af  8 :a  9 :as 10
   :bf 10 :b 11 :bs 12})

(for [[n v] notes]
  (eval `(defn ~(symbol (name n))
           ([]
              (Note. [(+ 60 ~v)] nil nil))
           ([octave#]
              (Note. [(+ (* octave# 12) ~v)] nil nil))
           ([octave# & durations#]
              (reduce
               #(%2 %1)
               (Note. [(+ (* octave# 12) ~v)] nil nil)
               durations#)))))

(for [d (mapcat (fn [s] [(str s) (str s "-")]) "whqistxn")]
  (eval `(defn ~(symbol d)
           ([]
              (Note. "R" nil ~d))
           ([note#]
              (assoc note# :durations (if-let [durs# (:durations note#)]
                                        (conj durs# ~d)
                                        [~d]))))))

(def chords [:maj :min* :maj7 :min7 :dim :aug :aug7 :sus :add9])

(for [c chords]
  (let [c (name c)]
    (eval `(defn ~(symbol c)
             [note#]
             (assoc note# :chord ~c)))))

(defn- key->inst
  [k]
  (str  [(st/upper-case (st/replace (name k) \- \_ ))]))

(defn rhythm
  [& layers]
  (pattern
   "V9"
   (apply str
          (mapcat
           (fn [[i [k v]]]
             (let [k (key->inst k)]
               (str "L" [(inc i)]
                    (apply pattern (map #(if (= (first %) \R)
                                           %
                                           (str k %)) v)))))
           (map-indexed vector (partition 2 layers))))))

(defn tempo
  [n & patt]
  (str "T" n (apply pattern patt)))

(defn vol
  [n & patt]
  (str "X[Volume]=" n (apply pattern patt)))

(defn inst
  [i & patt]
  (str "I" (key->inst i) (apply pattern patt)))

(comment (play! (tempo 150
                       (inst :piano (e 4) (c 5) (ff 4) (q))
                       (let [x "q"
                             - "Rq"]
                         (rhythm
                          :bass-drum      [x - - - x - - - x - - - x - - -]
                          :electric-snare [- - x - - - x - - - x - - - x -]
                          :closed-hi-hat  [x x x x x x x x x x x x x x x x])))))
