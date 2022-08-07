package chapter03.application;

import chapter03.hibernate.IP_Unique;
import chapter03.hibernate.IP_Unique_G;

import java.util.List;

public interface QueryService {

    <T> List<T> selectAll(Class<T> clazz);
    IP_Unique_G saveIP_Unique_G(String dst_ip);

    IP_Unique updateIP_Unique(String iug_dst_ip, String dst_ip, String src_ip);
}
