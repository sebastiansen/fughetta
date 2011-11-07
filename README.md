# fughetta

A [JFugue](http://www.jfugue.org/) wrapper for Clojure with some ideas from [Haskore] (http://www.haskell.org/haskellwiki/Haskore)

### Notes, durations and chords as functions

* Defaults

```clj
  (cs) => "[61]" = "C#"   
  (q-) => "Rq."
```

* Octave       

```clj
  (c 6) => "[72]" = "C6"
```

* Durations 

```clj
  (c 6 q i) = (i (q (c 6))) => "[72]qi"
```

* Chords

```clj
  (maj (q (c)))   => "[60]majq"
  (minor (d 6 w)) => "[74]minw"
```

### Patterns

Use the pattern function to join notes (collections will be flattened!)

```clj
  (pattern 
    (c) (d) (e)
    [(f) (g)]
    (repeat 2 [(c) (d) (e)]))
  => " [60] [62] [64] [65] [67] [60] [62] [64] [60] [62] [64] "
```

### Instruments and rhythms

* Instruments

```clj
  (inst :distortion-guitar (c) (d) (e)) => "I[DISTORTION_GUITAR] [60] [62] [64]"
```

* Rhythms (subject to change)

```clj
  (let [x "q"
        - (q)]
    (rhythm
      :bass-drum      [x - - - x - - - x - - - x - - -]
      :electric-snare [- - x - - - x - - - x - - - x -]
      :closed-hi-hat  [x x x x x x x x x x x x x x x x]))
```

### Combining Notes

```clj
  (++ (c) (d) (e))      => "[60]+[62]+[64]"
  (++ (c) (-- (d) (e))) => "[60]+[62]_[64]"
```

### Tuplets

* Triplets

```clj
  (*** (c 5 q) (d 5 q) (e 5 q)) => " [60]q* [62]q* [64]q* "
```

* Other tuplets

```clj
  (** 2 (c 5 q) (d 5 q) (e 5 q)) => " [60]q*3:2 [62]q*3:2 [64]q*3:2 "
  (** 4 (map q (c) (d) (e) (f) (g))) => " [60]q*5:4 [62]q*5:4 [64]q*5:4 [65]q*5:4 [67]q*5:4 "
```

### Moving through semi-tones

```clj
  (>> (c))   => "[61]"
  (>> (c) 4) => "[65]"
  (<< (c))   => "[59]"
  (<< (c) 4) => "[56]"
```

### Voices, tempo

* Voices

```clj
  (voice 1 (c) (d) (e))
```

* Tempo

```clj
  (tempo 150 (c) (d) (e))
```

### Player

* Play

```clj
  (play! (c) (d 5) (e 5 q))
```

* Stop

```clj
  (stop!)
```

* Save to file

```clj
  (save! "filename" (c) (d 5) (e 5 q))
```

### More to come...

## JFugue

More information on JFugue can be found [here] (http://www.jfugue.org/jfugue-chapter2.pdf)

## License

Copyright (C) 2011 Sebastian Rojas

Distributed under the GNU Lesser General Public License, the same as JFugue.
