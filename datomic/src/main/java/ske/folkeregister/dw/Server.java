package ske.folkeregister.dw;

import datomic.Connection;
import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import ske.folkeregister.datomic.person.DatomicPersonApi;
import ske.folkeregister.datomic.person.PersonApi;
import ske.folkeregister.datomic.util.IO;
import ske.folkeregister.dw.api.PersonResource;
import ske.folkeregister.dw.managed.DatomicConnectionManager;

public class Server extends Application<Configuration> {

   public static void main(String[] args) throws Exception {
      new Server().run(new String[]{"server"});
   }

   @Override
   public void initialize(Bootstrap<Configuration> bootstrap) {
      bootstrap.addBundle(new AssetsBundle("/web/", "/web", "index.html"));
   }

   @Override
   public void run(Configuration conf, Environment env) throws Exception {
      final Connection conn = IO.newMemConnection();
      final PersonApi personApi = new DatomicPersonApi(conn);

      IO.transactAllFromFile(conn, "datomic/schema.edn");
      IO.transactAllFromFile(conn, "datomic/data.edn");

      env.jersey().register(new PersonResource(personApi));

      env.lifecycle().manage(new DatomicConnectionManager(conn));
   }
}
