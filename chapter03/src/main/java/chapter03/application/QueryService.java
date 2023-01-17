package chapter03.application;

import chapter03.hibernate.Fwpolicy;
import chapter03.hibernate.Host;
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

    public <T> void save( T t) {
        try (Session session = SessionUtil.getSession()) {
            Transaction tx = session.beginTransaction();
            session.save(t);
            tx.commit();
        }
    }

}

