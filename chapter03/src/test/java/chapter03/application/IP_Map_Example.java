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

    protected Map<Long, Set<Long>> map;
    protected Map<Long, Set<String>> anotherMap;

    protected QueryService service;

    //assign map in the constructor
    public IP_Map_Example() {
        this.service = new QueryService();
        this.map = get_ip_map();
        this.anotherMap = convertMap(this.map);
    }
    
    //convert Map<Long, Set<Long>> to Map<Long, Set<String>> by converting the values from ip_int to ip
    public Map<Long, Set<String>> convertMap(Map<Long, Set<Long>> local_map) {
        Map<Long, Set<String>> result = new HashMap<>();
        for (Long key : local_map.keySet()) {
            Set<Long> value = local_map.get(key);
            Set<String> converted_value = new HashSet<>();
            for (Long ip_int : value) {
                String ip = this.intToIp(ip_int);
                converted_value.add(ip);
            }
            result.put(key, converted_value);
        }
        return result;
    }

    public String intToIp(long ipv4address) {
        long ip = ipv4address;
        // return String.format("%d.%d.%d.%d", (ip & 0xff), (ip >> 8 & 0xff), (ip >> 16
        // & 0xff), (ip >> 24 & 0xff));
        // do the same but with endianess little endian
        return String.format("%d.%d.%d.%d", (ip >> 24 & 0xff), (ip >> 16 & 0xff), (ip >> 8 & 0xff), (ip & 0xff));
    }

    
    public Map<Long, Set<Long>> get_ip_map() {  
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

    public <T> void fillMap(Map<T, Set<Long>> map, T key, Long value) {
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
