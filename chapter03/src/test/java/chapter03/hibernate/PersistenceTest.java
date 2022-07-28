package chapter03.hibernate;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

public class PersistenceTest {
    private SessionFactory factory = null;

    @BeforeClass
    public void setup() {
        StandardServiceRegistry registry = new StandardServiceRegistryBuilder().
                configure().build();
        factory = new MetadataSources(registry).buildMetadata().
                buildSessionFactory();
    }

    @Test
    public void readIP_Unique() {

        List<IP_Unique> list;
        try (Session session = factory.openSession()) {
            list = session.
                    createQuery("from IP_Unique", IP_Unique.class).list();
        }
        Assert.assertNotEquals(list.size(), 1);
        for (int i = 0; i < 2; i++) {
            IP_Unique ip_unique = list.get(i);
            System.out.println(ip_unique);
        }
    }
}

