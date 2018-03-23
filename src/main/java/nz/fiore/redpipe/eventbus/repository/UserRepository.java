package nz.fiore.redpipe.eventbus.repository;

import nz.fiore.redpipe.eventbus.model.User;
import org.giavacms.api.repository.Repository;
import org.giavacms.api.repository.Search;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserRepository implements Repository<User> {
    @Override
    public List<User> getList(Search<User> search, int startRow, int pageSize) throws Exception {
        List<User> users = new ArrayList<>();
        return users;
    }

    @Override
    public int getListSize(Search<User> search) throws Exception {
        return 0;
    }

    @Override
    public User find(Object key) throws Exception {
        User user = new User();
        user.uuid = UUID.randomUUID().toString();
        user.name = "fiorenzo";
        user.surname = "pizza";
        return user;
    }

    @Override
    public User fetch(Object key) throws Exception {
        User user = new User();
        user.uuid = key.toString();
        user.name = "fiorenzo";
        user.surname = "pizza";
        return user;
    }

    @Override
    public User create(Class<User> domainClass) throws Exception {
        User user = new User();
        user.uuid = UUID.randomUUID().toString();
        user.name = "fiorenzo";
        user.surname = "pizza";
        return user;
    }

    @Override
    public User persist(User object) throws Exception {
        User user = new User();
        user.uuid = UUID.randomUUID().toString();
        user.name = "fiorenzo";
        user.surname = "pizza";
        return user;
    }

    @Override
    public User update(User user) throws Exception {
        return user;
    }

    @Override
    public void delete(Object key) throws Exception {

    }

    @Override
    public boolean exist(Object key) throws Exception {
        return false;
    }

    @Override
    public Object castId(String key) throws Exception {
        return key;
    }

    @Override
    public void detach(User object) {

    }
}
