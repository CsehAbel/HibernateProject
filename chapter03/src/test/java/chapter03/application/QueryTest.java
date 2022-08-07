package chapter03.application;

import chapter03.hibernate.IP_Unique;
import org.testng.annotations.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class QueryTest {
    QueryService service=new ImplementationQueryService();

    //Query using IP_Unique.class and create two new entities from Map<String, List<String>>
    //create new objects: new IP_Unique_G() and persist them and associate them to the IP_Unique.class
    @Test
    public void getResult(){
        String parameter="";
        var res_list=service.getList(IP_Unique.class);
        Map<String, List<String>> map = new HashMap<>();
        for (int i = 0; i < res_list.size(); i++) {
            IP_Unique iu = res_list.get(i);
            var key=iu.getDst_ip();
            var value = iu.getSrc_ip();
            if(map.get(key)!=null) {
                map.compute(key,(k,v)-> Stream.concat(v.stream(),List.of(value).stream()).collect(Collectors.toList()));
            } else {
                map.put(key, new ArrayList<>(Arrays.asList(value)));
            }
        }


    }
}
