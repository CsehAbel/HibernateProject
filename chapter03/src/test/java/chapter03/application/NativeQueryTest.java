package chapter03.application;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class NativeQueryTest {

    static QueryService qs=new ImplementationQueryService();


    public static String getLatestHistoryTableName(String db_name) {
        var list = qs.showTables(String.class,db_name);
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

    public static List<String> createListFromHistory(String param){
        String db_name="CSV_DB";
        String table= getLatestHistoryTableName(db_name);
        List<String> list=qs.queryIPTableWhere(db_name,table,param);
        return list;
    }

    public static List<String> createListFromCurrent(String param){
        String db_name="CSV_DB";
        String table= "ip";
        List<String> list=qs.queryIPTableWhere(db_name,table,param);
        return list;
    }
}
