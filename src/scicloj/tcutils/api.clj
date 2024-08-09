(ns scicloj.tcutils.api
  ^{:doc "Public interface for tcutils library."
    :author "Kira McLean"}
  (:require [tech.v3.dataset :as ds]
            [tech.v3.dataset.base :as ds-base]
            [tablecloth.api :as tc]
            [tech.v3.dataset.rolling :as rolling]
            [scicloj.tcutils.strings :as strings]))

(defn lag
  "Compute previous (lagged) values from one column in a new column, can be used e.g. to compare values behind the current value.

  ## Usage

  `(lag ds column-name lag-size)`

  `(lag ds new-column-name column-name lag-size)`

  ## Arguments

  - `ds` - A `tech.ml.dataset` (i.e a `tablecloth` dataset)
  - `new-column-name` - __optional__ Name for the column where newly computed values will go.
    When ommitted new column name defaults to the keyword `<old-column-name>-lag-<lag-size>`
  - `column-name` - Name of the column to use to compute the lagged values
  - `lag-size` - positive integer indicating how many rows to skip over to compute the lag

  ## Returns

  A dataset with the new column populated with the lagged values.
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

  `(lead ds column-name lead-size)`

  `(lead ds new-column-name column-name lead-size)`

  ## Arguments

  - `ds` - A `tech.ml.dataset` (i.e a `tablecloth` dataset)
  - `new-column-name` - __optional__ Name for the column where newly computed values will go.
    When ommitted new column name defaults to the keyword `<old-column-name>-lead-<lead-size>`
  - `column-name` - Name of the column to use to compute the lead values
  - `lead-size` - positive integer indicating how many rows to skip over to compute the lead

  ## Returns

  A dataset with the column populated with the lead values.
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

  `(cumsum ds column-name)`

  `(cumsum ds new-column-name column-name)`

  ## Arguments

  - `ds` - A `tech.ml.dataset` (i.e a `tablecloth` dataset)
  - `new-column-name` - __optional__ Name for the column where newly computed values will go.
    When ommitted new column name defaults to the keyword `<old-column-name>-cumulative-sum`
  - `column-name` - Name of the column to use to compute the cumulative sum

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
  "Convert column names of a dataset into ASCII-only, kebab-cased keywords. Throws an error if any column would be left with no name, e.g. one that was an all non-ASCII string.

  ## Usage

  `clean-column-names(ds)`

  ## Arguments

  - `ds` - A `tech.ml.dataset` (i.e a `tablecloth` dataset)

  ## Returns

  A dataset with the column names converted to ASCII-only, kebab-cased keywords."
  [ds]
  (tc/rename-columns ds strings/to-clean-keyword))

(defn between
  "Detect where values fall in a specified range in a numeric column. This is a shortcut for `(< low x high)`.

  ## Usage

  `(between ds col-name low high)`

  `(between ds col-name low high {:missing-default val})`

  ## Arguments

  - `ds` - A `tech.ml.dataset` (i.e a `tablecloth` dataset)
  - `column-name` - Name of the column to use in the comparison
  - `low` - Lower bound for values of `column-name`
  - `high` - Upper bound for values of `column-name`
  - `options` - __optional__ Options map containing the key `missing-default` to specify what value to use in the case that the value of (col-name row) is `nil`. Throws an error if there are any missing values in the column and this option is not provided.

  ## Returns

  A dataset with only rows that contain values between `low` and `high` in column `col-name`"
  ([ds col-name low high]
   (between ds col-name low high {}))
  ([ds col-selector low high {:keys [missing-default]}]
   (tc/select-rows ds #(< low (% col-selector missing-default) high))))

(defn duplicate-rows
  "Filter a dataset for only duplicated rows.

  ## Usage

  `(duplicate-rows ds)`

  ## Arguments

  - `ds` - A `tech.ml.dataset` (i.e a `tablecloth` dataset)

  ## Returns

  A dataset containing only rows that are exact duplicates."
  [ds]
  (->> (ds/group-by->indexes ds vals)
       (mapcat (fn [[_ v]] (when (second v) v)))
       (#'ds-base/sorted-int32-sequence)
       (ds/select-rows ds)))
