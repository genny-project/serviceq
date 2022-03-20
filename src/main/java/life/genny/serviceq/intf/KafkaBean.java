package life.genny.serviceq.intf;

import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.JsonObject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;

import io.smallrye.reactive.messaging.kafka.OutgoingKafkaRecordMetadata;
import org.eclipse.microprofile.reactive.messaging.Message;

import life.genny.qwandaq.data.BridgeSwitch;
import life.genny.qwandaq.intf.KafkaInterface;
import life.genny.qwandaq.models.GennyToken;
import life.genny.serviceq.live.data.InternalProducer;

@ApplicationScoped
public class KafkaBean implements KafkaInterface {

	@Inject 
	InternalProducer producer;

	static final Logger log = Logger.getLogger(KafkaBean.class);

	static Jsonb jsonb = JsonbBuilder.create();

	/**
	* Write a string payload to a kafka channel.
	*
	* @param channel
	* @param payload
	 */
	public void write(String channel, String payload) { 

		if (channel == null) {
			log.error("Channel must not be null!");
			return;
		}

		if (payload == null) {
			log.error("Payload must not be null!");
			return;
		}

		// find GennyToken from payload contents
		JsonObject payloadObj = null;
		GennyToken gennyToken = null;

		try {
			payloadObj = jsonb.fromJson(payload, JsonObject.class);
			gennyToken = new GennyToken(payloadObj.getString("token"));
		} catch (Exception e) {
			log.debug("Message could not be deserialized to a JsonObject.");
		}

		// create metadata for correct bridge if outgoing
		OutgoingKafkaRecordMetadata<String> metadata = null;

		if ("webcmds".equals(channel) || "webdata".equals(channel)) {

			String bridgeId = BridgeSwitch.get(gennyToken);

			if (bridgeId == null) {
				log.warn("No Bridge ID found for " + gennyToken.getUserCode() + " : " + gennyToken.getJTI());

				bridgeId = BridgeSwitch.activeBridgeIds.iterator().next();
				log.warn("Sending to " + bridgeId + " instead!");
			}

			metadata = OutgoingKafkaRecordMetadata.<String>builder()
				.withTopic(bridgeId + "-" + channel)
				.build();
		}

		// channel switch
		switch (channel) {
			case "data":
				producer.getToData().send(payload);
			case "valid_data":
				producer.getToValidData().send(payload);
			case "search_events":
				producer.getToSearchEvents().send(payload);
			case "search_data":
				producer.getToSearchData().send(payload);
			case "messages":
				producer.getToMessages().send(payload);
			case "schedule":
				producer.getToSchedule().send(payload);
			case "blacklist":
				producer.getToBlacklist().send(payload);
			case "webcmds":
				// producer.getToWebCmds().send(Message.of(payloadObj.toString()).addMetadata(metadata));
				producer.getToWebCmds().send(payload);
			case "webdata":
				// producer.getToWebData().send(Message.of(payloadObj.toString()).addMetadata(metadata));
				producer.getToWebData().send(payload);
			default:
				log.error("Producer unable to write to channel " + channel);
		}
	}
}
