package ske.folkeregister.dw.managed;

import io.dropwizard.lifecycle.Managed;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

public class ThreadManager implements Managed {

   private final Runnable[] runnables;

   private ExecutorService executorService;

   public ThreadManager(Runnable... runnables) {
      this.runnables = runnables;
   }

   @Override
   public void start() throws Exception {
      executorService = Executors.newFixedThreadPool(runnables.length);
      Stream.of(runnables).forEach(executorService::execute);
   }

   @Override
   public void stop() throws Exception {
      executorService.shutdown();
   }
}
