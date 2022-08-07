package chapter03.application;

import java.util.List;

public interface QueryService {

    <T> List<T> getList(Class<T> clazz);

    <T> void save(T t);

}
