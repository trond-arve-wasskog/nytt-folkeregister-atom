package ske.folkeregister.datomic;

import datomic.Connection;
import datomic.Database;
import datomic.Entity;
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
   public static List<Map> changeOverTimeForObj(Connection conn, Object entityId) {
      return Peer
         .q("[:find ?tx ?a :in $ ?e :where [?e ?a _ ?tx]]", conn.db().history(), entityId)
         .stream()
         .collect(Collectors.groupingBy(list -> list.get(0)))
         .values()
         .stream()
         .sorted((l1, l2) -> ((Comparable) l1.get(0)).compareTo(l2.get(0)))
         .map(changes -> {
            final Object ffirst = changes.get(0).get(0);
            return map(
               ":changes", changeListForObj(conn, entityId, changes),
               ":timestamp", conn.db().asOf(ffirst).entity(ffirst).get(":db/txInstant")
            );
         })
         .collect(Collectors.toList());
   }

   private static List<Map> changeListForObj(
      Connection conn,
      Object entityId,
      List<List<Object>> changes
   ) {
      return changes
         .stream()
         .map(list -> {
            final Long tx = (Long) list.get(0);
            final Object attr = list.get(1);
            final Database db = conn.db();
            final Database dbBefore = db.asOf(tx - 1);
            final Database dbAfter = db.asOf(tx);
            final Object attrBefore = dbBefore.entity(attr).get(":db/ident");
            final Object attrAfter = dbAfter.entity(attr).get(":db/ident");

            Object oldVal = dbBefore.entity(entityId).get(attrBefore);
            Object newVal = dbAfter.entity(entityId).get(attrAfter);

            if (oldVal instanceof Entity) {
               ((Entity) oldVal).touch();
            }
            if (newVal instanceof Entity) {
               ((Entity) newVal).touch();
            }

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
