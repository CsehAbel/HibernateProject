package chapter03.application;

import chapter03.hibernate.IP_Unique_G;
import chapter03.hibernate.Rlst_G;
import chapter03.hibernate.ST_Ports_G;
import chapter03.hibernate.util.SessionUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.List;

public class ImplementationQueryService implements QueryService {


    @Override
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

    @Override
    public <T> void remove(T t) {
        try (Session session = SessionUtil.getSession()) {
            Transaction tx = session.beginTransaction();
            session.remove(t);
            tx.commit();
        }
    }
    @Override
    public <T> void remove(List<T> list) {
        try (Session session = SessionUtil.getSession()) {
            Transaction tx = session.beginTransaction();
            for (int i = 0; i < list.size(); i++) {
                //T t= (T) session.merge(list.get(i));
                T t=list.get(i);
                session.refresh(t);
                session.remove(t);
            }
            tx.commit();
        }
    }


    @Override
    public List<Rlst_G> selectRlstG(String dst_ip) {
        List<Rlst_G> list=new ArrayList<>();
        try (Session session = SessionUtil.getSession()) {
            Transaction tx = session.beginTransaction();
            Query<Rlst_G> query = session.createQuery(
                    "from Rlst_G a where a.dst_ip=:param");
            query.setParameter("param",dst_ip);
            list = query.getResultList();
            tx.commit();
        }
        return list;
    }

    @Override
    public List<String> selectEagleIP() {
        List<String> list=new ArrayList<>();
        try (Session session = SessionUtil.getSession()) {
            Transaction tx = session.beginTransaction();
            Query<String> query = session.createQuery(
                    "select a.ip from Eagle a");
            list = query.getResultList();
            tx.commit();
        }
        return list;
    }

   @Override
    public List<ST_Ports_G> selectSTPortsG(String dest_ip){
        List<ST_Ports_G> list=new ArrayList<>();
        try (Session session = SessionUtil.getSession()) {
            Transaction tx = session.beginTransaction();
            Query<ST_Ports_G> query = session.createQuery(
                    "from ST_Ports_G a where a.dest_ip=:param");
            query.setParameter("param",dest_ip);
            list = query.getResultList();
            tx.commit();
        }
        return list;
    }

    @Override
    public List<String> selectIUGPK(){
        List<String> list=new ArrayList<>();
        try(Session session=SessionUtil.getSession()){
            Transaction tx=session.beginTransaction();
            Query<String> query2=session.createQuery( "select iug.dst_ip from "+
                    IP_Unique_G.class.getName() + " iug");
            list=query2.getResultList();
            tx.commit();
        }
        return list;
    }

    @Override
    public IP_Unique_G selectIUG(String dst_ip){
        IP_Unique_G iug=null;
        try(Session session=SessionUtil.getSession()){
            Transaction tx=session.beginTransaction();
            Query<IP_Unique_G> query2=session.createQuery( "from "+
                    IP_Unique_G.class.getName() + " iug " + "where iug.dst_ip=:param");
            query2.setParameter("param",dst_ip);
            iug=query2.getSingleResult();
            tx.commit();
        }
        return iug;
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

