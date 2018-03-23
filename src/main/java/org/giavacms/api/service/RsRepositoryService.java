package org.giavacms.api.service;

import org.giavacms.api.repository.Repository;
import org.giavacms.api.repository.Search;
import org.giavacms.api.util.RepositoryUtils;
import org.jboss.logging.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.net.URLDecoder;
import java.util.List;

public abstract class RsRepositoryService<T> extends RsResponseService implements Serializable {

    private static final long serialVersionUID = 1L;

    protected final Logger logger = Logger.getLogger(getClass());

    private Repository<T> repository;

    public RsRepositoryService() {

    }

    public RsRepositoryService(Repository<T> repository) {
        this.repository = repository;
    }

    protected void prePersist(T object) throws Exception {
    }

    @POST
//   @RolesAllowed({ "Admin" })
    public Response persist(T object) throws Exception {
        try {
            prePersist(object);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return jsonMessageResponse(Status.BAD_REQUEST, e);
        }
        try {
            T persisted = doPersist(object);
            if (persisted == null || getId(persisted) == null) {
                logger.error("persist error: Failed to create resource: " + object);
                return jsonErrorMessageResponse(object);
            } else {
                return Response.status(Status.OK).entity(persisted).build();
            }
        } catch (Exception e) {
            logger.error(" persist error: " + e.getMessage(), e);
            return jsonErrorMessageResponse(object);
        } finally {
            try {
                postPersist(object);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    protected T doPersist(T object) throws Exception {
        return repository.persist(object);
    }

    protected void postPersist(T object) throws Exception {
    }

    protected void postFetch(T object) throws Exception {
    }

    /*
     * R
     */

    @GET
    @Path("/{id}")
//   @RolesAllowed({ "Admin" })
    public Response fetch(@PathParam("id") String id) {
        logger.info("@GET :" + id);
        try {
            T t = repository.fetch(repository.castId(id));
            if (t == null) {
                logger.error("fetch error: NOT_FOUND ");
                return jsonMessageResponse(Status.NOT_FOUND, id);
            } else {
                try {
                    postFetch(t);
                } catch (Exception e) {
                    logger.error("fetch error: " + e.getMessage(), e);
                }
                return Response.status(Status.OK).entity(t).build();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return jsonErrorMessageResponse(e);
        }
    }

    /*
     * U
     */

    protected T preUpdate(T object) throws Exception {
        return object;
    }

    protected void doUpdate(T object) throws Exception {
        repository.update(object);
    }

    @PUT
    @Path("/{id}")
//   @RolesAllowed({ "Admin" })
    public Response update(@PathParam("id") String id, T object) throws Exception {
        logger.info("@PUT update:" + object.toString());
        try {
            object = preUpdate(object);
        } catch (Exception e) {
            logger.error("update error: " + e.getMessage());
            return jsonMessageResponse(Status.BAD_REQUEST, e);
        }
        try {
            doUpdate(object);
            return Response.status(Status.OK).entity(object).build();
        } catch (Exception e) {
            logger.error("update error: " + e.getMessage(), e);
            return jsonErrorMessageResponse(object);
        } finally {
            try {
                postUpdate(object);
            } catch (Exception e) {
                logger.error("update error: " + e.getMessage(), e);
            }
        }
    }

    /**
     * concepita per chiamare robe async dopo l'update (o cmq robe fuori dalla tx principale che non rollbacka se erorri qui)
     *
     * @param object
     * @throws Exception
     */
    protected void postUpdate(T object) throws Exception {
    }

    /*
     * D
     */

    protected void preDelete(String id) throws Exception {
    }

    @DELETE
    @Path("/{id}")
//   @RolesAllowed({ "Admin" })
    public Response delete(@PathParam("id") String id) throws Exception {
        logger.info("@DELETE:" + id);
        try {
            preDelete(id);
        } catch (Exception e) {
            return jsonMessageResponse(Status.BAD_REQUEST, e);
        }
        try {
            repository.delete(repository.castId(id));
            postDelete(id);
            return jsonMessageResponse(Status.NO_CONTENT, id);
        } catch (Exception e) {
            logger.error("delete error: " + e.getMessage(), e);
            return jsonErrorMessageResponse(e);
        }
    }

    protected void postDelete(String id) throws Exception {
    }

    /*
     * E
     */

    @GET
    @Path("/{id}/exist")
//   @RolesAllowed({ "Admin" })
    public Response exist(@PathParam("id") String id) {
        logger.info("@GET exist:" + id);
        try {
            boolean exist = repository.exist(repository.castId(id));
            if (!exist) {
                return jsonMessageResponse(Status.NOT_FOUND, id);
            } else {
                return jsonMessageResponse(Status.OK, id);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return jsonErrorMessageResponse(e);
        }
    }

    @GET
    @Path("/listSize")
//   @RolesAllowed({ "Admin" })
    public Response getListSize(@Context UriInfo ui) {
        // logger.info("@GET list:" + ctx.getCallerPrincipal().getName());
        // logger.info("@GET list:" +
        // SecurityContextAssociation.getSecurityContext().getSubjectInfo().getAuthenticatedSubject().getPrincipals());
        try {
            Search<T> search = getSearch(ui, null);
            int listSize = repository.getListSize(search);
            return Response.status(Status.OK).entity(listSize)
                    .header("Access-Control-Expose-Headers", "listSize")
                    .header("listSize", listSize).build();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return jsonErrorMessageResponse(e);
        }
    }

    /*
     * Q
     */

    @GET
//   @RolesAllowed({ "Admin" })
    public Response getList(
            @DefaultValue("0") @QueryParam("startRow") Integer startRow,
            @DefaultValue("10") @QueryParam("pageSize") Integer pageSize,
            @QueryParam("orderBy") String orderBy, @Context UriInfo ui) {
        // logger.info("@GET list:" + ctx.getCallerPrincipal().getName());
        // logger.info("@GET list:" +
        // SecurityContextAssociation.getSecurityContext().getSubjectInfo().getAuthenticatedSubject().getPrincipals());
        try {
            Search<T> search = getSearch(ui, orderBy);
            int listSize = repository.getListSize(search);
            List<T> list = repository.getList(search, startRow, pageSize);
            postList(list);
            // PaginatedListWrapper<T> wrapper = new PaginatedListWrapper<>();
            // wrapper.setList(list);
            // wrapper.setListSize(listSize);
            // wrapper.setStartRow(startRow);
            return Response
                    .status(Status.OK)
                    .entity(list)
                    .header("Access-Control-Expose-Headers",
                            "startRow, pageSize, listSize").header("startRow", startRow)
                    .header("pageSize", pageSize).header("listSize", listSize).build();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return jsonErrorMessageResponse(e);
        }
    }

    protected void postList(List<T> list) throws Exception {
    }

    protected Search<T> getSearch(UriInfo ui, String orderBy) throws Exception {
        Search<T> s = new Search<T>(getClassType());
        if (orderBy != null && !orderBy.trim().isEmpty()) {
            s.setOrder(orderBy);
        }
        if (ui != null && ui.getQueryParameters() != null
                && !ui.getQueryParameters().isEmpty()) {
            MultivaluedMap<String, String> queryParams = ui.getQueryParameters();
            // TODO - DA TESTARE:
            // tutte le prop su search.getObj() possono essere in "chiaro" - senza
            // prefissi
            // tutte le prop su oggetti search.getFrom() - search.getTo() sono con
            // prefisso: es.from.id - from.dataInit

            makeSearch(queryParams, s);

        }

        return s;
    }

    <T> void makeSearch(MultivaluedMap<String, String> queryParams, Search<T> s) {
        for (String key : queryParams.keySet()) {
            try {
                T instance = s.getObj();
                String value = queryParams.getFirst(key);
                value = URLDecoder.decode(value, "UTF-8");

                String fieldName = key;
                if (key.startsWith("obj.")) {
                    instance = s.getObj();
                    fieldName = key.substring(4);
                } else if (key.startsWith("from.")) {
                    instance = s.getFrom();
                    fieldName = key.substring(5);
                } else if (key.startsWith("to.")) {
                    instance = s.getTo();
                    fieldName = key.substring(3);
                } else if (key.startsWith("like.")) {
                    instance = s.getLike();
                    fieldName = key.substring(5);
                } else if (key.startsWith("not.")) {
                    instance = s.getNot();
                    fieldName = key.substring(4);
                } else if (key.startsWith("nil.")) {
                    instance = s.getNil();
                    fieldName = key.substring(4);
                } else if (key.startsWith("notNil.")) {
                    instance = s.getNotNil();
                    fieldName = key.substring(7);
                }
                RepositoryUtils.setFieldByName(instance.getClass(), instance,
                        fieldName, value);

            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }

    }

    protected Repository<T> getRepository() {
        return repository;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private Class<T> getClassType() {
        Class clazz = getClass();
        while (!(clazz.getGenericSuperclass() instanceof ParameterizedType)) {
            clazz = clazz.getSuperclass();
        }
        ParameterizedType parameterizedType = (ParameterizedType) clazz
                .getGenericSuperclass();
        return (Class<T>) parameterizedType.getActualTypeArguments()[0];
    }

    /**
     * Override this is needed
     *
     * @param t
     * @return
     */

    protected abstract Object getId(T t);

}
