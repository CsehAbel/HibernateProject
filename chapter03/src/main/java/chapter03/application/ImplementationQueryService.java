package chapter03.application;

import chapter03.hibernate.util.SessionUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.List;

public class ImplementationQueryService implements QueryService {

    @Override
    public List<String> queryIPTableWhere(String db_name, String table, String param){
        List<String> list=new ArrayList<>();
        try (Session session = SessionUtil.getSession()) {
            Query<String> query = session.createNativeQuery(
                    "select src_ip FROM (select src_ip from "+db_name+"."+table+" WHERE dst_ip=:param) as i" +
                            " LEFT JOIN (select `0` as sip from "+db_name+".systems) as o \n" +
                            "ON i.src_ip=o.sip WHERE o.sip IS NOT NULL");
            query.setParameter("param",param);
            list = query.getResultList();
        }
        return list;
    }

    @Override
    public <T> List<T> querySTPortsWhere(Class<T> clazz, String param) {
        List<T> list;
        try (Session session = SessionUtil.getSession()) {
            Transaction tx = session.beginTransaction();
            List<T> l = new ArrayList<>();
            Query<T> query = session.createQuery(
                    "from "+ clazz.getName()+" c WHERE c.st_dest_ip=:param",
                    clazz);
            query.setParameter("param",param);
            list = query.getResultList();
            tx.commit();
        }
        return list;
    }

    @Override
    public <T> List<T> queryRlstWhere(Class<T> clazz, String param) {
        List<T> list;
        try (Session session = SessionUtil.getSession()) {
            Transaction tx = session.beginTransaction();
            List<T> l = new ArrayList<>();
            Query<T> query = session.createQuery(
                    "from "+ clazz.getName()+" c WHERE c.ips=:param AND change_type!='deleted'",
                    clazz);
            query.setParameter("param",param);
            list = query.getResultList();
            tx.commit();
        }
        return list;
    }

    @Override
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

    @Override
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

    @Override
    public <T> void save( T t) {
        try (Session session = SessionUtil.getSession()) {
            Transaction tx = session.beginTransaction();
            session.save(t);
            tx.commit();
        }
    }

}

