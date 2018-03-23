package nz.fiore.redpipe.eventbus.service.rs;

import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.core.eventbus.EventBus;
import net.redpipe.engine.core.AppGlobals;
import nz.fiore.redpipe.eventbus.model.User;
import nz.fiore.redpipe.eventbus.repository.UserRepository;
import org.giavacms.api.service.RsRepositoryService;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserServiceRs extends RsRepositoryService<User> {


    @Inject
    EventBus eventBus;

    @Inject
    Vertx vertx;

    @Inject
    JsonObject config;

    public UserServiceRs() {
        super();
    }


    @Inject
    public UserServiceRs(UserRepository repository) {
        super(repository);
    }

    @Override
    protected Object getId(User user) {
        return user.uuid;
    }


    @GET
    @Path("/test-queue")
    public Response get(@Context Vertx vertx) {
        System.out.println(eventBus);
        System.out.println(vertx);
        System.out.println(config);
        eventBus.send("test-queue", new JsonObject().put("msg", "hello"));
        return Response.ok().build();
    }
}
