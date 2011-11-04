# fughetta

A [JFugue](http://www.jfugue.org/) wrapper for Clojure

### Notes, durations and chords as functions

* Defualts

```clj
  (cs) => "[61]" or "C#"   
  (q-) => "Rq."
```

* Octave 
      

```clj
  (c 6) => "[72]"
```

* Durations 

```clj
  (c 6 q i) = (i (q (c 6))) => "[72]qi"
```

* Chords

```clj
  (maj (q (c))) => "[60]majq"
  (min* (d 6 w)) => "[74]minw"
```

### Instruments and rhythms

* Instruments

```clj
  (inst :distortion-guitar (c) (d) (e)) => "I[DISTORTION_GUITAR] [60] [62] [64]"
```

* Rhythms

```clj
  (let [x "q"
        - "Rq"]
    (rhythm
      :bass-drum      [x - - - x - - - x - - - x - - -]
      :electric-snare [- - x - - - x - - - x - - - x -]
      :closed-hi-hat  [x x x x x x x x x x x x x x x x]))
```

### Patterns
Use the pattern function to join notes as well as patterns

```clj
  (pattern 
    (inst :distortion-guitar (c) (d) (e))
    (pattern (c) (d) (e)))
  => " I[DISTORTION_GUITAR] [60] [62] [64]   [60] [62] [64]  "
```

### Tempo

```clj
  (tempo 150 (pattern (c) (d) (e)))
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

More information on JFugue can be found [here] (http://www.cs.cofc.edu/~manaris/spring04/cs220-handouts/JFugue/JFugue-UserGuide.html)

## License

Copyright (C) 2011 Sebastian Rojas

Distributed under the GNU Lesser General Public License, the same as JFugue.
