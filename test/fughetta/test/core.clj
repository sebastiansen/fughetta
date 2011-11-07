(ns fughetta.test.core
  (:use [fughetta.core])
  (:use [clojure.test]))

;; Part 1 in New Millenium Cyanide Christ by Meshuggah
(let [x "s"
      - (s)
      patt1 #(->> (cycle (concat %3 %2 %1 %2))
                  (drop 3)
                  (take 128))
      b1 [x x x x]
      b2 [- - x - x -]
      b3 [x x x x x x x]
      snare (take 128 (cycle [- - - - - - - - x - - - - - - -]))
      cymbal (take 128 (cycle [x - - -]))
      g1 [0 1 0 2]
      g2 [- - 0 - 0 -]
      g3 [0 1 0 0 1 0 2]
      g (apply patt1
               (map #(map (fn [n] (if (= n -) - (>> (bf 2 s) n)))
                          %)
                    [g1 g2 g3]))]
  (play!
   (tempo 150
          (repeat 2 (inst :distortion-guitar g))
          (repeat 2 (rhythm
                     :bass-drum (patt1 b1 b2 b3)
                     :electric-snare snare
                     :crash-cymbal-1 cymbal)))))
