package chapter03.application;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.List;
import chapter03.hibernate.IP;
import chapter03.hibernate.Systems;

//import Stream, HashMap
import java.util.stream.Stream;

import org.testng.annotations.Test;

import java.util.stream.Collectors;
import java.util.HashSet;


@lombok.Data
public class IP_SrcBuckets_Map_Example {
    
    protected List<Systems> resource_systems;
    
    protected List<IP> resource_ips;

    protected Map<Long, Set<String>> result_map;

    protected QueryService service;

    public IP_SrcBuckets_Map_Example() {
        this.service = new QueryService();
        this.resource_systems = this.get_systems();
        this.resource_ips = this.get_ips();
        this.result_map = this.get_ip_bucket();
    }

    private List<Systems> get_systems() {  
        List<Systems> res_list = this.service.selectAll(Systems.class);
        return res_list;
    }

    private List<IP> get_ips() {
        List<IP> res_list = this.service.selectAll(IP.class);
        return res_list;
    }

    public Map<Long, Set<String>> get_ip_bucket() {  
        Map<Long, Set<String>> map = new HashMap<>();
        for (int i = 0; i < this.resource_ips.size(); i++) {
            IP iu = this.resource_ips.get(i);
            var dst_ip = iu.getDst_ip_int();
            var src_ip = iu.getSrc_ip_int();
            for(int j = 0; j < this.resource_systems.size(); j++) {
                
                try{
                    Systems sys = this.resource_systems.get(j);
                    var sys_start = sys.getStart_int();
                    var sys_end = sys.getEnd_int();
                    if (sys_start <= src_ip && src_ip <= sys_end) {
                        fillMap(map, dst_ip, sys.getName());
                    }
                } catch (Exception e) {
                    // TODO: handle exception
                    System.out.println("Exception: " + e.getMessage());
                }
            }
        }
        // instead of above, if sys.
        return map;
    }

    public <T> void fillMap(Map<T, Set<String>> map, T key, String value) {
        String finalValue = value;
        if (map.get(key) != null) {
            map.compute(key, (k, v) -> Stream.concat(v.stream(), Set.of(finalValue).stream()).collect(Collectors.toSet()));
        } else {
            var set = new HashSet<String>();
            set.add(value);
            map.put(key, set);
        }
    }

    @Test
    public void testMePlease() {
        "".isEmpty();
        System.out.println("this.result_map");
    }






}
