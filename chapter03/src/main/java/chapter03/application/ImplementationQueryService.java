package chapter03.application;

import chapter03.hibernate.util.SessionUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.List;

public class ImplementationQueryService implements QueryService {
    public <T> List<T> selectAll(Session session, Class<T> clazz) {
        List<T> l = new ArrayList<>();
        Query<T> query = session.createQuery(
                "from "+clazz.getName()+" c",
                clazz);
        return  query.getResultList();
    }

    @Override
    public <T> List<T> selectAll(Class<T> clazz) {
        List<T> list;
        try (Session session = SessionUtil.getSession()) {
            Transaction tx = session.beginTransaction();
            list = selectAll(session,clazz);
            tx.commit();
        }
        return list;
    }

    @Override
    public <T> void save( T t) {
        try (Session session = SessionUtil.getSession()) {
            Transaction tx = session.beginTransaction();
            save(session,t);
            tx.commit();
        }
    }

    public <T> void save(Session session, T t){
        session.save(t);
    }

}

