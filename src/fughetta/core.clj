;  Copyright (c) Sebastian Rojas, 2011. All rights reserved.

;This program is free software: you can redistribute it and/or modify
;it under the terms of the GNU General Public License as published by
;the Free Software Foundation, either version 3 of the License, or
;(at your option) any later version.

;This program is distributed in the hope that it will be useful,
;but WITHOUT ANY WARRANTY; without even the implied warranty of
;MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
;GNU General Public License for more details.

;You should have received a copy of the GNU General Public License
;along with this program.  If not, see <http://www.gnu.org/licenses/>.

(ns fughetta.core
  (:import [org.jfugue Player])
  (:use [clojure.contrib macro-utils])
  (:require [clojure.string :as st]))

;; Patterns

(defn pattern
  [& xs]
  (str " " (st/join " " (flatten xs)) " "))

;; Notes, durations and chords

(defrecord Note [pitch durations chord]
  Object
  (toString [this] (str [pitch] chord (apply str durations))))

(defrecord Rest [durations]
  Object
  (toString [this] (str "R" (apply str durations))))

(macrolet
    [(defnotes [notes]
       `(do
          ~@(for [[n v] notes]
              `(defn ~n
                 ([]
                    (Note. (+ 60 ~v) nil nil))
                 ([octave#]
                    (Note. (+ (* octave# 12) ~v) nil nil))
                 ([octave# & durations#]
                    (reduce
                     #(%2 %1)
                     (Note. (+ (* octave# 12) ~v) nil nil)
                     durations#))))))]
  (defnotes {cf -1 c  0 cs  1
             df  1 d  2 ds  3
             ef  3 e  4 es  5
             ff  4 f  5 fs  6
             gf  6 g  7 gs  8
             af  8 a  9 as 10
             bf 10 b 11 bs 12}))

(macrolet
   [(defdurations []
      `(do
         ~@(for [d (butlast (mapcat (fn [s] [(str s) (str s "-")]) "whqistxo"))]
             (let [d- (if (= (last d) \-) (str (first d) ".") d)]
              `(defn ~(symbol d)
                 ([]
                    (Rest. [~d-]))
                 ([note#]
                    (assoc note# :durations (if-let [durs# (:durations note#)]
                                              (conj durs# ~d-)
                                              [~d-]))))))))]
   (defdurations))

(macrolet
   [(defchords [chords]
      `(do
         ~@(for [c chords]
             `(defn ~c
                [note#]
                (assoc note# :chord ~(if (= c 'min*) "min" (str c)))))))]
   (defchords [maj minor aug dim dom7 maj7 min7 sus4 sus2 maj6
               min6 dom9 maj9 min9 dim7 add9 min11 dom11 dom13
               min13 maj13 dom7<5 dom7>5 maj7<5 maj7>5 minmaj7
               dom7<5<9 dom7<5>9 dom7>5<9 dom7>5>9]))

(defn ++
  [& notes]
  (reduce #(str %1 "+" %2) notes))

(defn --
  [& notes]
  (reduce #(str %1 "_" %2) notes))

(defn tie
  [& notes]
  (let [[follow & continues] (flatten notes)
        middle (butlast continues)
        final (last continues)]
    (pattern (str follow \-)
             (when middle
               (map (fn [{:keys [pitch chord durations]}]
                      (str [pitch] chord \- (apply str durations) \-))
                    middle))
             (when final
               (let [{:keys [pitch chord durations]} final]
                 (str [pitch] chord \- (apply str durations)))))))

(defn **
  [n & notes]
  (let [notes (flatten notes)
        total (count notes)
        ratio (str "*" total ":" n)]
    (pattern (map #(str % ratio) notes))))

(defn ***
  [n1 n2 n3]
  (pattern (map #(str % "*") [n1 n2 n3])))

(defn >>
  ([note] (>> note 1))
  ([note n] (assoc note :pitch (+ (:pitch note) n))))

(defn <<
  ([note] (<< note 1))
  ([note n] (assoc note :pitch (- (:pitch note) n))))

;; Voices and controllers

(defn voice
  [n & xs]
  (str "V" n (pattern xs)))

(defn tempo
  [n & xs]
  (str "T" n (pattern xs)))

(defn vol
  [n & xs]
  (str "X[Volume]=" n (pattern xs)))

;; Instruments, rhythms

(defn- key->inst
  [k]
  (str  "[" (st/upper-case (st/replace (name k) \- \_ )) "]"))

(defn inst
  [i & xs]
  (str "I" (key->inst i) (pattern xs)))

(defn rhythm
  [& layers]
  (voice 9
         (map
          (fn [[i [k v]]]
            (let [k (key->inst k)]
              (pattern "L" [(inc i)]
                       (map #(condp = (type %)
                                 Rest
                               (str %)
                               String
                               (str k %)) v))))
          (map-indexed vector (partition 2 layers)))))

;; Player

(def ^{:private true} player (Player.))

(defn play!
  [& xs]
  (let [x (pattern xs)]
    (.play player x)
    (.close player)
    x))

(defn stop!
  []
  (.pause player))

(defn pause!
  []
  (.pause player))

(defn resume!
  []
  (.stop player))

(defn save!
  [file-name & xs]
  (.save player (pattern xs) file-name))
