package ske.folkeregister.dw.conf;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.client.JerseyClientConfiguration;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class ServerConfig extends Configuration {

   @NotNull
   @JsonProperty
   private String feedServerUrl;

   @Valid
   @NotNull
   @JsonProperty
   private JerseyClientConfiguration jerseyClient = new JerseyClientConfiguration();

   public String getFeedServerUrl() {
      return feedServerUrl;
   }

   public JerseyClientConfiguration getJerseyClientConfig() {
      return jerseyClient;
   }
}
