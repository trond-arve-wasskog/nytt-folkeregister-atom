package ske.folkeregister.datomic.feed;

import datomic.Connection;
import datomic.Peer;
import datomic.db.Datum;
import org.apache.abdera.Abdera;
import org.apache.abdera.model.Entry;

import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Collectors;

import static datomic.Connection.*;

@SuppressWarnings("unchecked")
public class FeedGenerator implements Runnable {

   private static final Abdera abdera = Abdera.getInstance();
   private static final String datum_query =
      "[:find ?attrname" +
         " :in $ ?attr" +
         " :where [?attr :db/ident ?attrname]]";
   public static final String ssn_query =
      "[:find ?ssn :in $ ?entity :where [?entity :person/ssn ?ssn]]";

   private final BlockingQueue<Map> txReportQueue;

   public FeedGenerator(Connection conn) {
      txReportQueue = conn.txReportQueue();
   }

   @Override
   public void run() {
      while (true) {
         try {
            final Map tx = txReportQueue.take();
            final List<Datum> txData = (List<Datum>) tx.get(TX_DATA);

            System.out.println("Create feed entries tx: " + txData.get(0).v());

            txData
               .stream()
               .substream(1)
               .collect(Collectors.groupingBy(Datum::e))
               .entrySet()
               .stream()
               .map(e -> {
                  Entry entry = abdera.newEntry();

                  String ssn = queryForSSN(tx, e.getKey());
                  entry.addLink(entry.addLink("/person/" + ssn, "person"));

                  e.getValue().forEach(datum -> {
                     if (datum.added()) {
                        entry.addCategory(
                           "added",
                           queryForAttrName(tx.get(DB_AFTER), datum.a()),
                           datum.v().toString()
                        );
                     } else {
                        entry.addCategory(
                           "removed",
                           queryForAttrName(tx.get(DB_BEFORE), datum.a()),
                           datum.v().toString()
                        );
                     }
                  });

                  return entry;
               })
               .forEach(entry -> {
                  try {
                     // TODO - post to Atom Hopper?
                     // curl -H "Content-Type: application/atom+xml" -X POST -d @entry.xml localhost:8080/namespace/feed/
                     System.out.println(Abdera.getNewWriter().write(entry));
                  } catch (Exception e) {
                     System.err.println("Får ikke skrevet entry: " + e.getMessage());
                  }
               });
         } catch (Exception e) {
            System.err.println("### Yikes: " + e.getMessage());
            e.printStackTrace();
         }
      }
   }

   private String queryForSSN(Map tx, Object entity) {
      return Peer.q(ssn_query, tx.get(DB_AFTER), entity).iterator().next().get(0).toString();
   }

   private String queryForAttrName(Object db, Object attr) {
      return Peer.q(datum_query, db, attr).iterator().next().get(0).toString();
   }
}
