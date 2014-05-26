package ske.folkeregister.datomic.person;

import datomic.Connection;
import org.junit.Test;
import ske.folkeregister.datomic.feed.FeedGenerator;
import ske.folkeregister.datomic.util.IO;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static datomic.Util.list;
import static datomic.Util.map;

public class FeedGeneratorTest {

   @Test
   public void listenForChanges() throws Exception {
      final Connection conn = IO.newMemConnection();
      IO.transactAllFromFile(conn, "datomic/schema.edn");

      final ExecutorService executorService = Executors.newSingleThreadExecutor();
      executorService.execute(new FeedGenerator(conn));

      final PersonApi personApi = new DatomicPersonApi(conn);

      personApi.updatePerson(map(
         ":person/name", "Test Name",
         ":person/ssn", "123"
      ));

      personApi.updatePerson(map(
         ":person/name", "Test Number 2",
         ":person/ssn", "321"
      ));

      personApi.updatePerson(map(
         ":person/name", "Test Updated Name",
         ":person/ssn", "123"
      ));

      personApi.updatePerson(map(
         ":person/name", "Test Newname",
         ":person/birthplace", "Oslo",
         ":person/ssn", "123"
      ));

      conn.transact(list(
         list(":db/retract", list(":person/ssn", "123"), ":person/birthplace", "Oslo"),
         list(":db/retract", list(":person/ssn", "321"), ":person/name", "Test Number 2")
      )).get();

      executorService.awaitTermination(1, TimeUnit.SECONDS);
   }
}
