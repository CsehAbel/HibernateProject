package chapter03.application;

import chapter03.hibernate.IP_Unique;
import chapter03.hibernate.IP_Unique_G;
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
            list = selectAll(session, clazz);
            tx.commit();
        }
        return list;
    }

    @Override
    public IP_Unique_G saveIP_Unique_G(String dst_ip) {
        IP_Unique_G iug=null;
        try (Session session = SessionUtil.getSession()) {
            Transaction tx = session.beginTransaction();
            iug=saveIP_Unique_G(session,dst_ip);
            tx.commit();
        }
        return iug;
    }

    public IP_Unique_G saveIP_Unique_G(Session session, String dst_ip) {
        IP_Unique_G found = findIP_Unique_G(session,dst_ip);
        if(found==null) {
            found = new IP_Unique_G(dst_ip);
            session.save(found);
        }
        return found;
    }

    public <T> List<T> selectAll(Session session, Class<T> clazz) {
        List<T> l = new ArrayList<>();
        Query<T> query = session.createQuery(
                "from " + clazz.getName() + " c",
                clazz);
        return query.getResultList();
    }

    @Override
    public IP_Unique updateIP_Unique(String iug_dst_ip, String dst_ip, String src_ip) {
        IP_Unique iu=null;
        try (Session session = SessionUtil.getSession()) {
            Transaction tx = session.beginTransaction();
            iu=updateIP_Unique(session,iug_dst_ip,dst_ip, src_ip);
            tx.commit();
        }
        return iu;
    }

    private IP_Unique updateIP_Unique( Session session,String iug_dst_ip,String dst_ip, String src_ip) {
        IP_Unique iu = findIP_Unique(session, dst_ip, src_ip);
        IP_Unique_G iug = findIP_Unique_G(session,iug_dst_ip);
        iu.setGroup(iug);
        return iu;
    }

    private IP_Unique findIP_Unique(Session session,String dst_ip,String src_ip) {
        Query<IP_Unique> query = session.createQuery(
                "from IP_Unique iu "
                        + "where iu.dst_ip=:dip and "
                        + "iu.src_ip=:sip", IP_Unique.class);
        query.setParameter("dip", dst_ip);
        query.setParameter("sip", src_ip);
        IP_Unique iu = query.uniqueResult();
        return iu;
    }

    private IP_Unique_G findIP_Unique_G(Session session,String dst_ip) {
        Query<IP_Unique_G> query = session.createQuery(
                "from IP_Unique_G iug "
                        + "where iug.dst_ip=:dip", IP_Unique_G.class);
        query.setParameter("dip", dst_ip);
        IP_Unique_G iug = query.uniqueResult();
        return iug;
    }
}

