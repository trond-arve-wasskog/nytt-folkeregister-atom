nytt-folkeregister-atom
=======================

Tenkt implementasjon av nytt folkeregister med bruk av Atom-feeds for publisering. Modernisering av Folkeregisteret er på trappene (kan komme i 2015, avhengig av prioritering på statsbudsjettet), dette kan være et innspill til tilnærming og plattform:
* [Modernisering av folkeregisteret Forstudierapport (PDF)] (http://www.skatteetaten.no/upload/PDFer/Rapport_Modernisering_av_Folkeregisteret.pdf)
* [Dokument fra dialogkonferansen (PDF)] (http://www.regjeringen.no/upload/subnettsteder/framtidens_byer/Forbruk%20og%20avfall/2014/InvitasjonDialogkonferanse.pdf) viser overordnede behov og krav til nytt folkeregister.
* [Hva som lagres i folkeregisteret (PDF)] (http://www.regjeringen.no/upload/FIN/Vedlegg/sl/Rapporter/Vedlegg%202%20Hva%20som%20registreres%20i%20Folkeregisteret.pdf)

Folkeregisteret inneholder i dag informasjon om over 7 millioner personer med fødselsnummer (F-nr) som er eller har vært bosatt i Norge. I tillegg kommer ca. 1,3 millioner personer som har fått D-nummer og som har en midlertidig tilknytning til Norge. Det innrulleres årlig mer enn 220.000 personer, hvorav 60.000 fødte i Norge, ca. 80.000 innvandrere og ca. 80.000 med midlertidig tilknytning. Det foretas årlig nærmere 2 millioner endringer av opplysningene i databasen. 

Målsetninger med PoC:
* Vurdere immutable datamodell for lagring, oppdatering, spørring, ytelse, konsistens, itegritet.
* Vurdere Atom feeds som mekanisme for publisering av data
 * Konfidensialitet og integritet med kryptering og signering av content, se [Securing Atom Documents] (http://tools.ietf.org/html/rfc4287#page-26)
 * Benytte standard HTTP/Atom-egenskaper som caching, komprimering
* Teste ytelse, robusthet og skalerbarhet med realistiske volumer (7 millioner personer)

Foreløpige tanker:
* [Datomic] (http://www.datomic.com/) og [EventStore] (http://geteventstore.com/) som datalager/modell
* Vurder [AtomHopper] (http://atomhopper.org/) for Atom Feeds (Eventstore tilbyr Atom feeds som standard)
* Kjør i AWS eller liknende med fulle volumer
