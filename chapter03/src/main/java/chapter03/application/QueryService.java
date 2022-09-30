package chapter03.application;

import chapter03.hibernate.IP;

import java.util.List;

public interface QueryService {

    List<String> queryIPTableWhere(String db_name, String table, String dst_ip);

    <T> List<T> querySTPortsWhere(Class<T> clazz, String param);

    <T> List<T> queryRlstWhere(Class<T> clazz, String param);

    <T> List<T> showTables(Class<T> clazz, String db_name);

    <T> List<T> listPKWithoutEagle(Class<T> stringClass);

    <T> void save(T t);

}
