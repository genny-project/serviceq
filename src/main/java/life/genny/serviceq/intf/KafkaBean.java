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

		JsonObject payloadObj = null;
		GennyToken userToken = null;

		try {
			payloadObj = jsonb.fromJson(payload, JsonObject.class);
			userToken = new GennyToken(payloadObj.getString("token"));
		} catch (Exception e) {
			log.debug("Message could not be deserialized to a JsonObject.");
		}

		if ("data".equals(channel)) {
			producer.getToData().send(payload);

		} else if ("valid_data".equals(channel)) {
			producer.getToValidData().send(payload);

		} else if ("search_events".equals(channel)) {
			producer.getToSearchEvents().send(payload);

		} else if ("search_Data".equals(channel)) {
			producer.getToSearchData().send(payload);

		} else if ("messages".equals(channel)) {
			producer.getToMessages().send(payload);

		} else if ("schedule".equals(channel)) {
			producer.getToSchedule().send(payload);

		} else if ("blacklist".equals(channel)) {
			producer.getToBlacklist().send(payload);

		} else if ("webcmds".equals(channel)) {

			// String bridgeId = BridgeSwitch.mappings.get(userToken.getUniqueId());

			// OutgoingKafkaRecordMetadata<String> metadata = OutgoingKafkaRecordMetadata.<String>builder()
			// 	.withTopic(bridgeId + "-" + channel)
			// 	.build();

			// producer.getToWebCmds().send(Message.of(payloadObj.toString()).addMetadata(metadata));
			producer.getToWebCmds().send(payload);
		} else if ("webdata".equals(channel)) {

			// String bridgeId = BridgeSwitch.bridges.get(userToken.getUniqueId());

			// OutgoingKafkaRecordMetadata<String> metadata = OutgoingKafkaRecordMetadata.<String>builder()
			// 	.withTopic(bridgeId + "-" + channel)
			// 	.build();

			// producer.getToWebData().send(Message.of(payloadObj.toString()).addMetadata(metadata));
			producer.getToWebData().send(payload);
		} else {
			log.error("Producer unable to write to channel " + channel);
		}
	}
}
