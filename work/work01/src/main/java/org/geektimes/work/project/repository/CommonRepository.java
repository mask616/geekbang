package org.geektimes.work.project.repository;

import org.geektimes.work.orm.ID;
import org.geektimes.work.orm.TableName;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;

public abstract class CommonRepository<T> {

    protected static DataSource dataSource;

    String databaseURL = "jdbc:derby:/db/user-platform;create=true";

    static {
        try {
            Context initCtx = new InitialContext();
            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            dataSource = (DataSource)envCtx.lookup("UserPlatformDB");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    protected Connection getConnection() throws SQLException {
        Connection connection = null;
        if (dataSource != null){
            connection =  dataSource.getConnection();
        } else {
            connection = DriverManager.getConnection(databaseURL);
        }
        return connection;
    }

    protected boolean sava(T t) throws IntrospectionException, InvocationTargetException, IllegalAccessException, SQLException {
        String sql = getInsertSql(t);
        Connection connection = getConnection();
        PreparedStatement statement = connection.prepareStatement(sql);
        int row = statement.executeUpdate();
        if (row > 0){
            return true;
        }
        return false;
    }

    private String getInsertSql(T t) throws IntrospectionException, InvocationTargetException, IllegalAccessException {
        TableName tableName = t.getClass().getAnnotation(TableName.class);
        StringBuilder sql = new StringBuilder("insert into ")
                .append(" ").append(tableName.value()).append("(");
        BeanInfo beanInfo = Introspector.getBeanInfo(t.getClass(), Object.class);

        for (PropertyDescriptor propertyDescriptor : beanInfo.getPropertyDescriptors()){

            Class<?> fieldType = propertyDescriptor.getPropertyType();
            // 如果是ID则忽略， 因为这里使用了数据库的主键自增
            if (propertyDescriptor.isBound()){
                continue;
            }
            sql.append(propertyDescriptor.getName()).append(",");
        }

        sql.deleteCharAt(sql.length() - 1);

        sql.append(") values (");
        for (PropertyDescriptor propertyDescriptor : beanInfo.getPropertyDescriptors()){
            Method readMethod = propertyDescriptor.getReadMethod();
            Class<?> fieldType = propertyDescriptor.getPropertyType();
            Object value = readMethod.invoke(t);

            // 如果是ID则忽略， 因为这里使用了数据库的主键自增
            if (fieldType.getAnnotation(ID.class) != null){
                continue;
            }
            if (fieldType.equals(String.class)){
                if (value != null){
                    sql.append("\'").append(String.valueOf(value)).append("\',");
                } else {
                    sql.append("\'\',");
                }

            } else if (fieldType.equals(Long.class)){
                sql.append(Long.valueOf(String.valueOf(value))).append(",");
            }
        }
        sql.deleteCharAt(sql.length() - 1);
        sql.append(")");


        return sql.toString();
    }

}
