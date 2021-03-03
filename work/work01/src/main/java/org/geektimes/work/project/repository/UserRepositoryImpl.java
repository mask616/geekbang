package org.geektimes.work.project.repository;

import org.geektimes.work.project.bean.User;

import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;

public class UserRepositoryImpl extends CommonRepository<User> implements UserRepository {

    @Override
    public boolean save(User user) throws SQLException, IllegalAccessException, IntrospectionException, InvocationTargetException {
        return sava(user);
    }

    @Override
    public boolean deleteById(Long userId) {
        return false;
    }

    @Override
    public boolean update(User user) {
        return false;
    }

    @Override
    public User getById(Long userId) {
        return null;
    }

    @Override
    public User getByNameAndPassword(String userName, String password) {
        return null;
    }

    @Override
    public Collection<User> getAll() {
        return null;
    }
}
