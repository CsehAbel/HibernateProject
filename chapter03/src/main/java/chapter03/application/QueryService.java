package chapter03.application;

import chapter03.hibernate.IP_Unique_G;
import chapter03.hibernate.Rlst_G;
import chapter03.hibernate.ST_Ports;
import chapter03.hibernate.ST_Ports_G;

import java.util.List;

public interface QueryService {

    <T> List<T> selectAll(Class<T> clazz);
    List<Rlst_G> selectRlstG(String dst_ip);

    List<ST_Ports_G> selectSTPortsG(String dest_ip);

    List<String> selectIUGPK();

    List<IP_Unique_G> selectIUG(String dst_ip);

    <T> void save(T t);

}
