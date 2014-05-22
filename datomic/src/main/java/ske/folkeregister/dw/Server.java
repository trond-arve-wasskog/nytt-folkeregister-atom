package ske.folkeregister.dw;

import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import ske.folkeregister.dw.api.TestResource;

public class Server extends Application<Configuration> {

   public static void main(String[] args) throws Exception {
      new Server().run(new String[]{"server"});
   }

   @Override
   public void initialize(Bootstrap<Configuration> bootstrap) {
   }

   @Override
   public void run(Configuration conf, Environment env) throws Exception {
      env.jersey().register(TestResource.class);
   }
}
