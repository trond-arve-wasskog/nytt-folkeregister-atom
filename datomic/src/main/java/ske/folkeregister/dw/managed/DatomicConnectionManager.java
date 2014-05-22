package ske.folkeregister.dw.managed;

import datomic.Connection;
import datomic.Peer;
import io.dropwizard.lifecycle.Managed;

public class DatomicConnectionManager implements Managed {

   private final Connection conn;

   public DatomicConnectionManager(Connection conn) {
      this.conn = conn;
   }

   @Override
   public void start() throws Exception {
   }

   @Override
   public void stop() throws Exception {
      conn.release();
      Peer.shutdown(true);
   }
}
