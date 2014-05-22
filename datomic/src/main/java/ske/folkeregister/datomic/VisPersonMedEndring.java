package ske.folkeregister.datomic;

import datomic.Connection;
import datomic.Database;
import datomic.Peer;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static datomic.Connection.DB_AFTER;
import static datomic.Connection.TEMPIDS;
import static datomic.Peer.tempid;
import static datomic.Util.*;

@SuppressWarnings("unchecked")
public class VisPersonMedEndring {

   public static void main(String[] args) throws ExecutionException, InterruptedException {
      // Opprett ny inmemory database
      final Connection conn = IO.newMemConnection();

      // Legg inn db-skjema
      IO.transactAllFromFile(conn, "schema.edn");

      // Legg inn en person
      System.out.println("Legger inn ny person");
      final Object personId = leggInnEivindUgiftMajorstua(conn);

      sleepPrint(2);

      // Endre sivilstand til gift med nytt navn
      System.out.println("Person gifter seg og skifter navn");
      endreNavnOgSivilstatus(conn, personId);

      sleepPrint(2);

      // Flytt til større hus
      System.out.println("Person flytter fra leilighet til hus");
      flyttTilJar(conn, personId);

      // Grav litt i historikken til person
      System.out.println("\n### Endringer for person: ###");
      Changes.changeOverTimeForEntity(conn.db(), personId).forEach(changeset -> {
         System.out.println("\nTidspunkt: " + changeset.get(":timestamp"));
         System.out.println("Endringer:");
         ((List)changeset.get(":changes")).forEach(obj -> System.out.println("\t" + obj));
      });

      // For å få Datomic til å stenge
      System.exit(0);
   }

   private static void flyttTilJar(Connection conn, Object personId) throws InterruptedException, ExecutionException {
      Object newTmpAddressId = tempid("db.part/user");
      conn.transact(list(
         address(newTmpAddressId, "Alfheimveien", "9A", "1358"),
         map(
            "db/id", personId,
            "person/address", newTmpAddressId
         )
      )).get();
   }

   private static Map endreNavnOgSivilstatus(Connection conn, Object personId) throws InterruptedException, ExecutionException {
      return conn.transact(list(map(
         "db/id", personId,
         "person/sivilstatus", read(":person.sivilstatus/gift"),
         "person/name", "Eivind Barstad Waaler"
      ))).get();
   }

   private static Object leggInnEivindUgiftMajorstua(Connection conn) throws InterruptedException, ExecutionException {
      Object tmpPersonId = tempid("db.part/user");
      Object tmpAddressId = tempid("db.part/user");
      Map txResult = conn.transact(list(
         address(tmpAddressId, "Majorstuveien", "16", "0367"),
         map(
            "db/id", tmpPersonId,
            "person/name", "Eivind Waaler",
            "person/birthplace", "Oslo",
            "person/sivilstatus", read(":person.sivilstatus/ugift"),
            "person/address", tmpAddressId
         )
      )).get();
      return Peer.resolveTempid((Database) txResult.get(DB_AFTER), txResult.get(TEMPIDS), tmpPersonId);
   }

   private static Map address(
      Object tmpAddressId, String street, String number, String postnumber
   ) {
      return map(
         "db/id", tmpAddressId,
         "address/street", street,
         "address/streetnumber", number,
         "address/postnumber", postnumber
      );
   }

   private static void sleepPrint(int seconds) throws InterruptedException {
      System.out.print("Sover i " + seconds + " sek.");
      for (int i = 0; i < seconds; i++) {
         Thread.sleep(1_000);
         System.out.print(".");
      }
      System.out.println();
   }
}
