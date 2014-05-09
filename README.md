nytt-folkeregister-atom
=======================

Tenkt implementasjon av nytt folkeregister med bruk av Atom-feeds for publisering. Modernisering av Folkeregisteret er på trappene (kan komme i 2015, avhengig av prioritering på statsbudsjettet), dette kan være et innspill til tilnærming og plattform.

[Hva som lagres i folkeregisteret (PDF)] (http://www.regjeringen.no/upload/FIN/Vedlegg/sl/Rapporter/Vedlegg%202%20Hva%20som%20registreres%20i%20Folkeregisteret.pdf)

Hovedområder/Epics/Målsetninger med PoC:
* Vurdere immutable datamodell for lagring, oppdatering, spørring, ytelse, konsistens, itegritet.
* Vurdere Atom feeds som mekanisme for publisering av data
 * Konfidensialitet og integritet med kryptering og signering av content, se [Securing Atom Documents] (http://tools.ietf.org/html/rfc4287#page-26)
 * Benytte standard HTTP/Atom-egenskaper som caching, komprimering
* Teste ytelse, robusthet og skalerbarhet med realistiske volumer (7 millioner personer)

Foreløpige tanker:
* [Datomic] (http://www.datomic.com/) og [EventStore] (http://geteventstore.com/) som datalager/modell
* Vurder [AtomHopper] (http://atomhopper.org/) for Atom Feeds (Eventstore tilbyr Atom feeds som standard)
* Kjør i AWS eller liknende med fulle volumer
