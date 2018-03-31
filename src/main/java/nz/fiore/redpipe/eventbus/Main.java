package nz.fiore.redpipe.eventbus;

import net.redpipe.engine.core.Server;
import nz.fiore.redpipe.eventbus.service.rs.UserServiceRs;

public class Main {
    public static void main(String[] args) {
        new Server()
                .start(UserServiceRs.class)
                .subscribe(v -> System.err.println("Deploy is completed"),
                        x -> x.printStackTrace());
    }
}
