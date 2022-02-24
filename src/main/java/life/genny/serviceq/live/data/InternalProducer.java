package life.genny.serviceq.live.data;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

/**
 * InternalProducer --- Kafka smallrye producer objects to send to other
 * internal consumers within GENNY.
 */

@ApplicationScoped
public class InternalProducer {

  @Inject @Channel("data") Emitter<String> data;
  public Emitter<String> getToData() {
    return data;
  }

  @Inject @Channel("valid_data") Emitter<String> valid_data;
  public Emitter<String> getToValidData() {
    return valid_data;
  }

  @Inject @Channel("webcmdsout") Emitter<String> webCmds;
  public Emitter<String> getToWebCmds() {
    return webCmds;
  }

  @Inject @Channel("webdataout") Emitter<String> webData;
  public Emitter<String> getToWebData() {
    return webData;
  }

  @Inject @Channel("search_eventsout") Emitter<String> searchEvents;
  public Emitter<String> getToSearchEvents() {
    return searchEvents;
  }

  @Inject @Channel("search_dataout") Emitter<String> searchData;
  public Emitter<String> getToSearchData() {
    return searchData;
  }

  @Inject @Channel("messagesout") Emitter<String> messages;
  public Emitter<String> getToMessages() {
    return messages;
  }

  @Inject @Channel("scheduleout") Emitter<String> schedule;
  public Emitter<String> getToSchedule() {
    return schedule;
  }

}
