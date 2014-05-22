package ske.folkeregister.datomic.util;

import datomic.Database;
import datomic.Peer;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static datomic.Util.map;

@SuppressWarnings("unchecked")
public class Changes {

   /**
    * Fra August Lilleaas: http://dbs-are-fn.com/2013/datomic_history_of_an_entity/
    */
   public static List<Map> changeOverTimeForEntity(Database db, Object entityId) {
      return Peer
         .q("[:find ?tx ?a :in $ ?e :where [?e ?a _ ?tx]]", db.history(), entityId)
         .stream()
         .collect(Collectors.groupingBy(list -> list.get(0)))
         .values()
         .stream()
         .sorted((l1, l2) -> ((Comparable) l1.get(0)).compareTo(l2.get(0)))
         .map(changes -> {
            final Object ffirst = changes.get(0).get(0);
            return map(
               ":changes", mapChangesForEntity(db, entityId, changes),
               ":timestamp", db.asOf(ffirst).entity(ffirst).get(":db/txInstant")
            );
         })
         .collect(Collectors.toList());
   }

   private static List<Map> mapChangesForEntity(
      Database db,
      Object entityId,
      List<List<Object>> changes
   ) {
      return changes
         .stream()
         .map(list -> {
            final Long tx = (Long) list.get(0);
            final Object attr = list.get(1);

            final Database dbBefore = db.asOf(tx - 1);
            final Database dbAfter = db.asOf(tx);

            final Object attrBefore = dbBefore.entity(attr).get(":db/ident");
            final Object attrAfter = dbAfter.entity(attr).get(":db/ident");

            final Object oldVal = IO.attrToValue(dbBefore.entity(entityId), attrBefore);
            final Object newVal = IO.attrToValue(dbAfter.entity(entityId), attrAfter);

            return map(
               attrAfter, map(
                  ":old", oldVal,
                  ":new", newVal
               )
            );
         })
         .collect(Collectors.toList());
   }
}
