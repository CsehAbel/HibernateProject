package chapter03.application;

import chapter03.hibernate.*;

import java.util.List;

public interface QueryService {

    <T> List<T> selectAll(Class<T> clazz);
    List<Rlst_G> selectRlstG(String dst_ip);
    List<String> selectEagleIP();

    List<ST_Ports_G> selectSTPortsG(String dest_ip);

    List<String> selectIUGPK();

    IP_Unique_G selectIUG(String dst_ip);

    <T> void save(T t);

}
