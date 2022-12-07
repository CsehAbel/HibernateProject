package chapter03.application;

import chapter03.hibernate.Fwpolicy;
import chapter03.hibernate.IP;
import chapter03.hibernate.Rlst;
import chapter03.hibernate.util.SessionUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class QueryService {

    public List<String> queryIPTableWhere(String db_name, String table, long param){
        List<String> list=new ArrayList<>();
        try (Session session = SessionUtil.getSession()) {
            Query<String> query = session.createNativeQuery(
                    "select src_ip FROM (select src_ip from "+db_name+"."+table+" WHERE dst_ip_int=:param) as i" +
                            " LEFT JOIN (select `0` as sip from "+db_name+".systems) as o \n" +
                            "ON i.src_ip=o.sip WHERE o.sip IS NOT NULL");
            query.setParameter("param",param);
            list = query.getResultList();
        }
        return list;
    }

    private <T> void fillMap(Map<T, Set<Long>> map, T key, Long value) {
        if (value == null) {
            value = 0L;
        }
        long finalValue = value;
        if (map.get(key) != null) {
            map.compute(key, (k, v) -> Stream.concat(v.stream(), Set.of(finalValue).stream()).collect(Collectors.toSet()));
        } else {
            var set = new HashSet<Long>();
            set.add(value);
            map.put(key, set);
        }
    }

    public <T> List<T> selectAll(Class<T> clazz) {
        List<T> list;
        try (Session session = SessionUtil.getSession()) {
            Transaction tx = session.beginTransaction();
            List<T> l = new ArrayList<>();
            Query<T> query = session.createQuery(
                    "from "+ clazz.getName()+" c",
                    clazz);
            list = query.getResultList();
            tx.commit();
        }
        return list;
    }

    //Query using IP_Unique.class and create two new entities from Map<String, List<String>>
    //create new objects: new IP_Unique_G() and persist them and associate them to the IP_Unique.class

    public Map<Long, Set<Long>> get_ip_map() {
        String parameter = "";
        var res_list = selectAll(IP.class);
        Map<Long, Set<Long>> map = new HashMap<>();
        for (int i = 0; i < res_list.size(); i++) {
            IP iu = res_list.get(i);
            var key = iu.getDst_ip_int();
            var value = iu.getSrc_ip_int();
            fillMap(map, key, value);
        }
        return map;
    }

    //queryRlstWhere() but instead of generics it uses Rlst.class
    public List<Rlst> queryRlst() {
        List<Rlst> list;
        try (Session session = SessionUtil.getSession()) {
            Query<Rlst> query = session.createQuery("from Rlst", Rlst.class);
            list = query.getResultList();
        }
        return list;
    }

    public List<Fwpolicy> queryFwpolicy() {
        List<Fwpolicy> list;
        try (Session session = SessionUtil.getSession()) {
            Query<Fwpolicy> query = session.createQuery("from Fwpolicy", Fwpolicy.class);
            list = query.getResultList();
        }
        return list;
    }

    //query for Rlst table, session.createQuery(from Rlst,Rlst.class)
    public <T> List<T> queryClass(Class<T> clazz) {
        List<T> list;
        try (Session session = SessionUtil.getSession()) {
            Transaction tx = session.beginTransaction();
            List<T> l = new ArrayList<>();
            Query<T> query = session.createQuery(
                    "from "+ clazz.getName(),
                    clazz);
            list = query.getResultList();
            tx.commit();
        }
        return list;
    }

    public <T> List<T> showTables(Class<T> clazz, String db_name){

        List<T> list=new ArrayList<>();
        try (Session session = SessionUtil.getSession()) {
            Transaction tx = session.beginTransaction();
            Query<T> query = session.createNativeQuery("SHOW TABLES FROM "+db_name+" LIKE \"ip%\";");
            list = query.getResultList();
            tx.commit();
        }
        return list;
    }

    public <T> List<T> listPKWithoutEagle(Class<T> stringClass) {

        List<T> list = new ArrayList<>();
        try (Session session = SessionUtil.getSession()) {
            Transaction tx = session.beginTransaction();
            Query<T> query = session.createNativeQuery("SELECT i.dst_ip FROM " +
                    "(SELECT dst_ip FROM csv_db.ip) as i " +
                    "LEFT JOIN (SELECT ip FROM csv_db.eagle) as e " +
                    "ON i.dst_ip=e.ip " +
                    "WHERE e.ip IS NULL " +
                    "GROUP BY dst_ip");
            list = query.getResultList();
            tx.commit();
        }
        return list;
    }

    public <T> void save( T t) {
        try (Session session = SessionUtil.getSession()) {
            Transaction tx = session.beginTransaction();
            session.save(t);
            tx.commit();
        }
    }

}

