package ske.folkeregister.datomic;

import datomic.*;

import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static datomic.Connection.DB_AFTER;
import static datomic.Connection.TEMPIDS;
import static datomic.Peer.tempid;
import static datomic.Util.list;
import static datomic.Util.map;
import static datomic.Util.read;

@SuppressWarnings("unchecked")
public class TestRunner {

   public static void main(String[] args) throws ExecutionException, InterruptedException {
      // Opprett ny inmemory database
      final Connection conn = newMemConnection();

      // Legg inn db-skjema
      transactAllFromFile(conn, "schema.edn");

      // Legg inn en person
      Object tmpId = tempid("db.part/user");
      Map txResult = conn.transact(list(map(
         "db/id", tmpId,
         "person/name", "Eivind Barstad Waaler",
         "person/birthplace", "Oslo",
         "person/sivilstatus", read(":person.sivilstatus/ugift")
      ))).get();
      final Object personId = Peer.resolveTempid((Database) txResult.get(DB_AFTER), txResult.get(TEMPIDS), tmpId);

      // Skriv ut navn + sivilstand
      Entity entity = conn.db().entity(personId);
      System.out.println(entity.get(":person/name") + " " + entity.get(":person/sivilstatus"));

      sleepPrint(5);

      // Endre sivilstand til gift
      conn.transact(list(map(
         "db/id", personId,
         "person/sivilstatus", read(":person.sivilstatus/gift")
      ))).get();

      // Skriv ut navn + sivilstand igjen
      entity = conn.db().entity(personId);
      System.out.println(entity.get(":person/name") + " " + entity.get(":person/sivilstatus"));

      // Grav litt i historikken til person
      // Fra August Lilleaas: http://dbs-are-fn.com/2013/datomic_history_of_an_entity/
      System.out.println("\n### Endringer i sivilstand for person: ###");
      Peer
         .q("[:find ?tx ?a :in $ ?e :where [?e ?a _ ?tx]]", conn.db().history(), personId)
         .stream()
         .map(list -> conn.db().entity(list.get(0)))
         .distinct()
         .sorted((e1, e2) -> ((Comparable) e1.get(":db/txInstant")).compareTo(e2.get(":db/txInstant")))
         .map(tx -> {
            final Database db = conn.db();
            return map(
               "timestamp", tx.get(":db/txInstant"),
               "before", db.asOf(((long)tx.get(":db/id")) - 1).entity(personId).get(":person/sivilstatus"),
               "after", db.asOf(tx.get(":db/id")).entity(personId).get(":person/sivilstatus")
            );
         })
         .forEach(System.out::println);

      System.exit(0);
   }

   private static void sleepPrint(int seconds) throws InterruptedException {
      System.out.print("Sover i " + seconds + " sek.");
      for(int i = 0; i < seconds; i++) {
         Thread.sleep(1_000);
         System.out.print(".");
      }
      System.out.println();
   }

   private static void transactAllFromFile(Connection conn, String filename) throws InterruptedException, ExecutionException {
      for (List tx : readData(filename)) {
         conn.transact(tx).get();
      }
   }

   private static List<List> readData(String filename) {
      return Util.readAll(new InputStreamReader(
         Thread.currentThread().getContextClassLoader().getResourceAsStream(filename)));
   }

   private static Connection newMemConnection() {
      String uri = "datomic:mem://" + UUID.randomUUID();
      Peer.createDatabase(uri);
      return Peer.connect(uri);
   }

}
