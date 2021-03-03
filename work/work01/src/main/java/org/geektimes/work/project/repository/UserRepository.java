package org.geektimes.work.project.repository;


import org.geektimes.work.project.bean.User;

import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.Collection;

/**
 * User repository
 *
 * @since 1.0
 */
public interface UserRepository {

    boolean save(User user) throws SQLException, IllegalAccessException, IntrospectionException, InvocationTargetException;

    boolean deleteById(Long userId);

    boolean update(User user);

    User getById(Long userId);

    User getByNameAndPassword(String userName, String password);

    Collection<User> getAll();
}
