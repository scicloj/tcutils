# tcutils

[![Clojars Project](https://img.shields.io/clojars/v/org.scicloj/tcutils.svg)](https://clojars.org/org.scicloj/tcutils)

Warning: This is highly experimental and subject to change. Use at your own risk.

A collection of util functions for working with [tablecloth](https://github.com/scicloj/tablecloth)
 datasets.

## Usage

### Install from Clojars

Add the dependency to your `deps.edn` file. The latest version can be [found on Clojars](https://clojars.org/org.scicloj/tcutils/).

### Import into your project or notebook to use

```clojure
(require '[scicloj.tcutils.api :as tcutils])

(-> {"Some data" [1 2 3]
     "Some more data" [4 5 6]}
     tc/dataset
     tcutils/clean-column-names)

```

## Development

Run the project's tests:

    $ clojure -T:build test

Run the project's CI pipeline and build a JAR:

    $ clojure -T:build ci

This will produce an updated `pom.xml` file with synchronized dependencies inside the `META-INF`
directory inside `target/classes` and the JAR in `target`. You can update the version (and SCM tag)
information in generated `pom.xml` by updating `build.clj`.

Install it locally (requires the `ci` task be run first):

    $ clojure -T:build install

## License

Copyright © 2024 Kira McLean

Distributed under the MIT License.
