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

;;Notes, durations and chords

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
         ~@(for [d (mapcat (fn [s] [(str s) (str s "-")]) "whqistxn")]
             `(defn ~(symbol d)
                ([]
                   (Rest. [~d]))
                ([note#]
                   (assoc note# :durations (if-let [durs# (:durations note#)]
                                             (conj durs# ~d)
                                             [~d])))))))]
   (defdurations))

(macrolet
   [(defchords [chords]
      `(do
         ~@(for [c chords]
             `(defn ~c
                [note#]
                (assoc note# :chord ~(if (= c 'min*) "min" (str c)))))))]
   (defchords [maj min* maj7 min7 dim aug aug7 sus add9]))

(defn ++
  [& notes]
  (reduce #(str %1 "+" %2) notes))

(defn --
  [& notes]
  (reduce #(str %1 "_" %2) notes))

(defn >>
  ([note] (>> note 1))
  ([note n] (assoc note :pitch (+ (:pitch note) n))))

(defn <<
  ([note] (<< note 1))
  ([note n] (assoc note :pitch (- (:pitch note) n))))

;;Instruments, rhythms and controllers

(defn- key->inst
  [k]
  (str  "[" (st/upper-case (st/replace (name k) \- \_ )) "]"))

(defn inst
  [i & patt]
  (str "I" (key->inst i) (apply pattern patt)))

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

;;Player

(def ^{:private true} player (Player.))

(defn pattern
  [& xs]
  (str " " (st/join " " xs) " "))

(defn play!
  [& patts]
  (let [patt (apply pattern patts)]
    (.play player patt)
    patt))

(defn stop!
  []
  (.stop player))

(defn save!
  [file-name & patts]
  (.save player (apply pattern patts) file-name))
