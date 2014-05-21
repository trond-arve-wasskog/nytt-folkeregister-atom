package ske.folkeregister.datomic;

import datomic.Connection;
import datomic.Peer;
import datomic.Util;

import java.io.InputStreamReader;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@SuppressWarnings("unchecked")
public class IO {

   public static void transactAllFromFile(Connection conn, String filename) throws InterruptedException, ExecutionException {
      for (List tx : readData(filename)) {
         conn.transact(tx).get();
      }
   }

   public static Connection newMemConnection() {
      String uri = "datomic:mem://" + UUID.randomUUID();
      Peer.createDatabase(uri);
      return Peer.connect(uri);
   }

   private static List<List> readData(String filename) {
      return Util.readAll(new InputStreamReader(
         Thread.currentThread().getContextClassLoader().getResourceAsStream(filename)));
   }
}
