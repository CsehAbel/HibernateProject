package chapter03.application;

import chapter03.hibernate.IP_Unique;
import org.testng.annotations.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class QueryTest {
    QueryService service=new ImplementationQueryService();

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
