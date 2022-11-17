package chapter03.application;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NativeQueryTest {

    static QueryService qs=new QueryService();


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

    public static List<String> createListFromHistory(String param){
        String db_name="DARWIN_DB";
        String table= getLatestHistoryTableName(db_name);
        List<String> list=qs.queryIPTableWhere(db_name,table,param);
        return list;
    }

    public static List<String> createListFromCurrent(String param){
        String db_name="DARWIN_DB";
        String table= "ip";
        List<String> list=qs.queryIPTableWhere(db_name,table,param);
        return list;
    }
}
