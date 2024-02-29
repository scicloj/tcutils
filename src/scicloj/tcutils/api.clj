(ns scicloj.tcutils.api
  (:require [tech.v3.dataset.rolling :as rolling]
            [tech.v3.datatype.functional :as fun]
            [tablecloth.api :as tc]))

(defn lag
  "Compute previous (lagged) values from one column in a new column, used to compare values ahead of the current value.

  ## Usage

  `lag(ds column-selector lag-size)`
  `lag(ds new-column-name column-selector lag-size)`

  ## Arguments

  | ds | A `tech.ml.dataset` (i.e a `tablecloth` dataset) |
  | new-column-name | __optional__ Name for the column where newly computed values will go. When ommitted new column name defaults to the keyword `<old-column-name>-lag-<lag-size>`|
  | column-selector | Name of the column to use to compute the lagged values |
  | lag-size | positive integer indicating how many rows to skip over to compute the lag |

  ## Returns

  A dataset with the column populated with the lagged values.
  "
  ([ds column-name lag-size]
   (let [new-column-name (-> column-name
                             name
                             (str "-lag-" lag-size)
                             keyword)]
     (lag ds new-column-name column-name lag-size)))
  ([ds new-column-name column-name lag-size]
   (rolling/rolling ds
                    {:window-type :fixed
                     :window-size (inc lag-size)
                     :relative-window-position :left
                     :edge-mode :zero}
                    {new-column-name (rolling/first column-name)})))

(defn lead
  " "
  ([ds column-name lead-size]
   (let [new-column-name (-> column-name
                             name
                             (str "-lead-" lead-size)
                             keyword)]
     (lead ds new-column-name column-name lead-size)))
  ([ds new-column-name column-name lead-size]
   (rolling/rolling ds
                    {:window-type :fixed
                     :window-size (inc lead-size)
                     :relative-window-position :right
                     :edge-mode :zero}
                    {new-column-name (rolling/last column-name)})))

(defn cumsum
  "Compute the cumulative sum of a column"
  ([ds column-name]
   (let [new-column-name (-> column-name
                             name
                             (str "-cumulative-sum")
                             keyword)]
     (cumsum ds new-column-name column-name)))
  ([ds new-column-name column-name]
   (rolling/expanding ds {new-column-name (rolling/sum column-name)})))

(defn substract-columns [ds new-column-name col1 col2]
  (tc/map-columns new-column-name [col1 col2] fun/-))
