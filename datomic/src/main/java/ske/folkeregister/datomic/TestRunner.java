package ske.folkeregister.datomic;

import datomic.Connection;
import datomic.Database;
import datomic.Peer;
import datomic.Util;

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

/**
 * Example inspired by https://github.com/Datomic/datomic-java-examples just to get started..
 */
@SuppressWarnings("unchecked")
public class TestRunner {

   public static void main(String[] args) throws ExecutionException, InterruptedException {
      final Connection conn = newMemConnection();

      transactAllFromFile(conn, "accounts.edn");

      Object account = tempid("db.part/user");
      Map txResult = conn.transact(list(map("db/id", account, "account/balance", 100))).get();
      account = Peer.resolveTempid((Database)txResult.get(DB_AFTER), txResult.get(TEMPIDS),  account);

      System.out.println("CAS from 100->110 should succeed");
      conn.transact(list(list("db.fn/cas", account, "account/balance", 100, 110))).get();

      System.out.println("CAS from 100->120 should fail");
      try {
         conn.transact(list(list("db.fn/cas", account, "account/balance", 100, 120))).get();
      } catch (Throwable t) {
         System.out.println("Failed with " + t.getMessage());
      }

      System.out.println("Balance is " + conn.db().entity(account).get("account/balance"));

      System.exit(0);
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
