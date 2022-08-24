package chapter03.application;

import chapter03.hibernate.util.SessionUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class NativeQueryTest {

    static String db_name="FOKUS_DB";


    public static String getLatestHistoryTableName() {
        var list = showTables(String.class);
        Pattern pattern = Pattern.compile("^ip_(\\d+)$");

        int max=0;
        String name_of_max="";
        for (String line :
                list) {
            Matcher matcher = pattern.matcher(line);
            if(matcher.matches()){
                int current = Integer.parseInt(matcher.group(1));
                if (max<current){ max=current; name_of_max=line;}
            }
        }
        return name_of_max;
        //Assert.assertNotEquals(max,0);
        //Assert.assertNotEquals(list.size(),0);
    }
    public static <T> List<T> showTables(Class<T> clazz){

            List<T> list=new ArrayList<>();
            try (Session session = SessionUtil.getSession()) {
                Transaction tx = session.beginTransaction();
                Query<T> query = session.createNativeQuery("SHOW TABLES FROM "+db_name+" LIKE \"ip%\";");
                list = query.getResultList();
                tx.commit();
            }
            return list;

    }

    //select1() returns the latest ip_%Y%m%d table
    //select * from select1()
    //create Map<String,Set<String>>
    public static Map<String, Set<String>> createMap(String table){
            List<Object> list=new ArrayList<>();
            try (Session session = SessionUtil.getSession()) {
                Query<Object> query = session.createNativeQuery(
                        "SELECT source_ip,dest_ip FROM "+db_name+"."+table);
                list = query.getResultList();
            }

            Map<String, Set<String>> map = new HashMap<>();
            for (int i = 0; i < list.size(); i++) {
                var key = ((Object[])list.get(i))[1].toString();
                var value = ((Object[])list.get(i))[0].toString();
                fillMap(map, key, value);
            }
            return map;
    }

    private static <T> void fillMap(Map<T, Set<String>> map, T key, String value) {
        if (value == null) {
            value = "null";
        }
        String finalValue = value;
        if (map.get(key) != null) {
            map.compute(key, (k, v) -> Stream.concat(v.stream(), Set.of(finalValue).stream()).collect(Collectors.toSet()));
        } else {
            var set = new HashSet<String>();
            set.add(value);
            map.put(key, set);
        }
    }

    public static Map<String, Set<String>> createMapFromHistory(){
        String table= getLatestHistoryTableName();
        var map= createMap(table);
        return map;
    }
}
