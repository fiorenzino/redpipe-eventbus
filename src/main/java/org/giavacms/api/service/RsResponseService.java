package org.giavacms.api.service;

import io.vertx.core.json.JsonObject;
import org.giavacms.api.management.AppConstants;
import org.jboss.logging.Logger;

import javax.ws.rs.OPTIONS;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.io.Serializable;
import java.util.Map;

public abstract class RsResponseService implements Serializable {

    private static final long serialVersionUID = 1L;

    protected final Logger logger = Logger.getLogger(getClass());

    @OPTIONS
    public Response options() {
        logger.info("@OPTIONS");
        return Response.ok().build();
    }

    @OPTIONS
    @Path("{path:.*}")
    public Response allOptions() {
        logger.info("@OPTIONS ALL");
        return Response.ok().build();
    }

    public static Response jsonResponse(Map<String, String> toJson, Status status) {
        JsonObject jsonObject = new JsonObject();
        for (String key : toJson.keySet()) {
            jsonObject.put(key, toJson.get(key));
        }
        return Response.status(status).entity(jsonObject.toString()).build();
    }

    public static Response jsonResponse(Status status, String key, Object value) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.put(key, value.toString());
        return Response.status(status).entity(jsonObject.toString()).build();
    }

    public static Response jsonMessageResponse(Status status, Object object) {
        if (object instanceof Throwable) {
            Throwable t = (Throwable) object;
            return jsonResponse(
                    status,
                    AppConstants.JSON_ERROR_KEY,
                    t.getMessage() == null ? t.getClass().getCanonicalName() : t
                            .getMessage());
        } else {
            return jsonResponse(status, AppConstants.JSON_ERROR_KEY, "" + object);

        }
    }

    public static Response jsonErrorMessageResponse(Object error) {
        if (error instanceof Throwable) {
            Throwable t = (Throwable) error;
            return jsonResponse(Status.INTERNAL_SERVER_ERROR,
                    AppConstants.JSON_ERROR_KEY, t.getMessage() == null ? t.getClass()
                            .getCanonicalName() : t.getMessage());
        } else {
            return jsonResponse(Status.INTERNAL_SERVER_ERROR,
                    AppConstants.JSON_ERROR_KEY, "" + error);
        }
    }

}
