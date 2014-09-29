# folkereg

Eksempel applikasjon for foleregister bygget med Clojure og Datomic.

Foreløpig kun backend, som har omtrent tilsvarende funksjonlitet som Java Datomic eksempelet.

## Bygging og oppstart

Bygges med [Leiningen](http://leiningen.org/).

```
lein do clean, midje, uberjar
```

Kan kjøres via leiningen

```
lein ring server
```

Eller som standalone

```
java -jar target/folkereg.jar
```

Appen starter default på [http://localhost:3000](http://localhost:3000).

Konfigurasjon gjøres via ./folkrereg-config.edn, ellers er det innebygd default config.

Se src/folkereg/core.clj#config-schema for hva tillatt config er.

## TODO

* ClojureScript frontend
* Deployment (Heroku?)
* Database migrering
* Logge oppsett
* Mer konfigurasjon
* Tilgangskontroll
* Osv...
