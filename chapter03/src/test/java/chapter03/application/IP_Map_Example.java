package chapter03.application;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import chapter03.hibernate.IP;
import lombok.Data;

@Data
public class IP_Map_Example {

    private Map<Long, Set<Long>> map;

    private QueryService service;

    //assign map in the constructor
    public IP_Map_Example() {
        this.service = new QueryService();
        this.map = get_ip_map();
    }
    
    
    private Map<Long, Set<Long>> get_ip_map() {  
        List<IP> res_list = this.service.selectAll(IP.class);
        Map<Long, Set<Long>> map = new HashMap<>();
        for (int i = 0; i < res_list.size(); i++) {
            IP iu = res_list.get(i);
            var key = iu.getDst_ip_int();
            var value = iu.getSrc_ip_int();
            fillMap(map, key, value);
        }
        return map;
    }

    private <T> void fillMap(Map<T, Set<Long>> map, T key, Long value) {
        if (value == null) {
            value = 0L;
        }
        long finalValue = value;
        if (map.get(key) != null) {
            map.compute(key, (k, v) -> Stream.concat(v.stream(), Set.of(finalValue).stream()).collect(Collectors.toSet()));
        } else {
            var set = new HashSet<Long>();
            set.add(value);
            map.put(key, set);
        }
    }
}
