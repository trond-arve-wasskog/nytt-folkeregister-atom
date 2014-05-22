package ske.folkeregister.datomic;

import datomic.Connection;
import datomic.Peer;
import ske.folkeregister.datomic.person.DatomicPersonApi;
import ske.folkeregister.datomic.person.PersonApi;
import ske.folkeregister.datomic.util.IO;

import java.util.List;

@SuppressWarnings("unchecked")
public class TestPersonWithChanges {

   private static final String TEST_SSN = "19107612345";

   public static void main(String[] args) throws Exception {
      Connection conn = IO.newMemConnection();

      IO.transactAllFromFile(conn, "datomic/schema.edn");
      IO.transactAllFromFile(conn, "datomic/data.edn");

      PersonApi personApi = new DatomicPersonApi(conn);

      System.out.println(personApi.findAllPersons());

      // Legger inn noen tenkte endringer
      addData(personApi);

      // Grav litt i historikken til person
      System.out.println("\n### Endringer for person: ###");
      personApi.changesForPerson(TEST_SSN).forEach(changeset -> {
         System.out.println("\nTidspunkt: " + changeset.get(":timestamp"));
         System.out.println("Endringer:");
         ((List) changeset.get(":changes")).forEach(obj -> System.out.println("\t" + obj));
      });

      // For å få Datomic til å stenge
      Peer.shutdown(true);
   }

   public static void addData(PersonApi personApi) throws Exception {
      sleepPrint(2);

      // Endre sivilstand til gift med nytt navn
      personApi.changeNameAndStatus(TEST_SSN, "Eivind Barstad Waaler", ":person.sivilstatus/gift");

      sleepPrint(2);

      // Flytt til større hus
      personApi.moveToNewAddress(TEST_SSN, "Alfheimveien", "9A", "1358");
   }

   private static void sleepPrint(int seconds) throws Exception {
      System.out.print("Sover i " + seconds + " sek.");
      for (int i = 0; i < seconds; i++) {
         Thread.sleep(1_000);
         System.out.print(".");
      }
      System.out.println();
   }
}
