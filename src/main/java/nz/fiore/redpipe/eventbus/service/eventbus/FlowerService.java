package nz.fiore.redpipe.eventbus.service.eventbus;

import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.eventbus.EventBus;
import io.vertx.rxjava.core.eventbus.Message;
import net.redpipe.engine.core.AppGlobals;
import org.jboss.weld.vertx.VertxConsumer;
import org.jboss.weld.vertx.VertxEvent;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.io.Serializable;

public class FlowerService implements Serializable {

    @Inject
    EventBus eventBus;

    public FlowerService() {
        System.out.println("FlowerService start");
        AppGlobals.get().getVertx().eventBus().consumer("test-queue", this::consume);
    }

    @PostConstruct
    public void postConstruct() {
        System.out.println("FlowerService postConstruct");
        try {
            eventBus.consumer("test-queue", this::consume);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void consume(Message<JsonObject> message) {
        System.out.println("MANUAL: " + message);
    }

    void echoConsumer(@Observes @VertxConsumer("test-queue") VertxEvent event) {
        System.out.println("received new event");
        System.out.println(event.getMessageBody());
    }
}
