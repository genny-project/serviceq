package life.genny.serviceq;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.persistence.EntityManager;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import org.jboss.logging.Logger;

import io.quarkus.runtime.annotations.RegisterForReflection;
import life.genny.qwandaq.data.GennyCache;
import life.genny.qwandaq.models.GennyToken;
import life.genny.qwandaq.utils.BaseEntityUtils;
import life.genny.qwandaq.utils.CacheUtils;
import life.genny.qwandaq.utils.DatabaseUtils;
import life.genny.qwandaq.utils.KafkaUtils;
import life.genny.qwandaq.utils.KeycloakUtils;
import life.genny.qwandaq.utils.QwandaUtils;
import life.genny.serviceq.intf.KafkaBean;
import life.genny.serviceq.live.data.InternalProducer;

@RegisterForReflection
@ApplicationScoped
public class Service {

	static final Logger log = Logger.getLogger(Service.class);

    static Jsonb jsonb = JsonbBuilder.create();

	@ConfigProperty(name = "genny.keycloak.url", defaultValue = "https://keycloak.gada.io")
	String baseKeycloakUrl;

	@ConfigProperty(name = "genny.keycloak.realm", defaultValue = "genny")
	String keycloakRealm;

	@ConfigProperty(name = "genny.service.username", defaultValue = "service")
	String serviceUsername;

	@ConfigProperty(name = "genny.service.password", defaultValue = "password")
	String servicePassword;

	@ConfigProperty(name = "genny.oidc.client-id", defaultValue = "backend")
	String clientId;

	@ConfigProperty(name = "genny.oidc.credentials.secret", defaultValue = "secret")
	String secret;

	@Inject
	EntityManager entityManager;

	@Inject
	InternalProducer producer;

	@Inject 
	GennyCache cache;

	@Inject
	KafkaBean kafkaBean;

	GennyToken serviceToken;

	BaseEntityUtils beUtils;

	public BaseEntityUtils getBeUtils() {
		return beUtils;
	}

	public void setBeUtils(BaseEntityUtils beUtils) {
		this.beUtils = beUtils;
	}

	public GennyToken getServiceToken() {
		return serviceToken;
	}

	public void setServiceToken(GennyToken serviceToken) {
		this.serviceToken = serviceToken;
	}

	public void initToken() {
		// fetch token and init entity utility
		serviceToken = KeycloakUtils.getToken(baseKeycloakUrl, keycloakRealm, clientId, secret, serviceUsername, servicePassword);
		beUtils = new BaseEntityUtils(serviceToken);
	}

	public void initDatabase() {
		DatabaseUtils.init(entityManager);
	}

	public void initCache() {
		CacheUtils.init(cache);
	}

	public void initKafka() {
		KafkaUtils.init(kafkaBean);
	}

	public void initAttributes() {
		QwandaUtils.init(serviceToken);
	}
	
	public void fullServiceInit() {
		initToken();
		initDatabase();
		initCache();
		initKafka();
		initAttributes();
	}
}
