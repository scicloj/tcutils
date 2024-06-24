(ns scicloj.tcutils.api
  ^{:doc "Public interface for tcutils library."
    :author "Kira McLean"}
  (:require [tech.v3.dataset.rolling :as rolling]
            [scicloj.tcutils.strings :as strings]
            [tablecloth.api :as tc]))

(defn lag
  "Compute previous (lagged) values from one column in a new column, can be used e.g. to compare
    values behind the current value.

  ## Usage

  `lag(ds column-selector lag-size)`
  `lag(ds new-column-name column-selector lag-size)`

  ## Arguments

  - `ds` - A `tech.ml.dataset` (i.e a `tablecloth` dataset)
  - `new-column-name` - __optional__ Name for the column where newly computed values will go.
    When ommitted new column name defaults to the keyword `<old-column-name>-lag-<lag-size>`
  - `column-selector` - Name of the column to use to compute the lagged values
  - `lag-size` - positive integer indicating how many rows to skip over to compute the lag

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
  "Compute next (lead) values from one column in a new column, can be used e.g. to compare values ahead of the current value.

  ## Usage

  `lead(ds column-selector lead-size)`
  `lead(ds new-column-name column-selector lead-size)`

  ## Arguments

  - `ds` - A `tech.ml.dataset` (i.e a `tablecloth` dataset)
  - `new-column-name` - __optional__ Name for the column where newly computed values will go.
    When ommitted new column name defaults to the keyword `<old-column-name>-lag-<lag-size>`
  - `column-selector` - Name of the column to use to compute the lead values
  - `lead-size` - positive integer indicating how many rows to skip over to compute the lead

  ## Returns

  A dataset with the column populated with the lagged values.
  "
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
  "Compute the cumulative sum of a column

  ## Usage

  `cumsum(ds column-name)`
  `cumsum(ds new-column-name column-name)`

  ## Arguments

  - `ds` - A `tech.ml.dataset` (i.e a `tablecloth` dataset)
  - `new-column-name` - __optional__ Name for the column where newly computed values will go.
    When ommitted new column name defaults to the keyword `<old-column-name>-cumulative-sum`
  - `column-selector` - Name of the column to use to compute the cumulative sum

  ## Returns

  A dataset with the additional column containing the cumulative sum."
  ([ds column-name]
   (let [new-column-name (-> column-name
                             name
                             (str "-cumulative-sum")
                             keyword)]
     (cumsum ds new-column-name column-name)))
  ([ds new-column-name column-name]
   (rolling/expanding ds {new-column-name (rolling/sum column-name)})))

(defn clean-column-names
  "Convert column names of a dataset into ASCII-only, kebab-cased keywords.

  ## Usage

  `clean-column-names(ds)`

  ## Arguments

  - `ds` - A `tech.ml.dataset` (i.e a `tablecloth` dataset)

  ## Returns

  A dataset with the column names converted to kebab-cased keywords."
  [ds]
  (tc/rename-columns ds strings/to-clean-keyword))

;; (defn value-counts [ds]
;;   )
