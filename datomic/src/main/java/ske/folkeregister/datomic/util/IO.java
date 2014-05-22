package ske.folkeregister.datomic.util;

import clojure.lang.Keyword;
import datomic.Connection;
import datomic.Entity;
import datomic.Peer;
import datomic.Util;

import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

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

   public static Map<String, Object> entityToMap(Entity entity) {
      return entity
         .keySet()
         .stream()
         .collect(Collectors.toMap(key -> key, key -> attrToValue(entity, key)));
   }

   public static Object attrToValue(Entity entity, Object key) {
      Object value = entity.get(key);
      if (value instanceof Entity) {
         value = entityToMap((Entity) value);
      } else if (value instanceof Keyword) {
         value = ((Keyword) value).getName();
      }
      return value;
   }

   private static List<List> readData(String filename) {
      return Util.readAll(new InputStreamReader(
         Thread.currentThread().getContextClassLoader().getResourceAsStream(filename)));
   }
}
