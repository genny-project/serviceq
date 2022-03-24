package life.genny.serviceq.endpoints;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.vertx.core.json.JsonObject;

/**
 * DummyVersion --- An endpoint normally used by public facing clients
 * such as alyson to retrieve information about this project. 
 *
 * @author    hello@gada.io
 *
 */
@Path("/api/version")
public class Version {

	@ConfigProperty(name = "genny.version", defaultValue = "Not supported")
	String version;
	
    /**
     * The endpoint returns information specific to this project 
     * information such as commit hash, last user who commit and 
     * version number of this project are included
     *
     * @return GitApplicationProperties 
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject version() {
        return new JsonObject().put("version", version);
    }
}
