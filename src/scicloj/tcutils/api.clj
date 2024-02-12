(ns scicloj.tcutils.api
  (:require [tech.v3.dataset.rolling :as rolling]))

(defn lag
  "Compute previous (lagged) values from one column in a new column, used to compare values ahead of the current value.

  ## Usage

  `lag(ds new-column-name column-selector lag-size)`

  ## Arguments

  | ds | A `tech.ml.dataset` (i.e a `tablecloth` dataset) |
  | new-column-name | Name for the column where newly computed values will go |
  | column-selector | Name of the column to use to compute the lagged values |
  | lag-size | positive integer indicating how many rows to skip over to compute the lag |

  ## Returns

  A dataset with the column populated with the lagged values.
  "
  [ds new-column-name column-selector lag-size]
  (rolling/rolling ds
                   {:window-type :fixed
                    :window-size (inc lag-size)
                    :relative-window-position :left
                    :edge-mode :zero}
                   {new-column-name (rolling/first column-selector)}))
