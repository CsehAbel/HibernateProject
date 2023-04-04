package chapter03.application;

import java.util.HashMap;
import java.util.Map;

import chapter03.hibernate.Host;

@lombok.Data
public class Host_Map_Example {

    private QueryService service;
    private Map<String, String> map;

    public Host_Map_Example() {
        service = new QueryService();
        map = get_host_map();
    }

    public Map<String, String> get_host_map() {
        var res_list = this.service.selectAll(Host.class);
        //Map contains key - ip, value - name of host
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < res_list.size(); i++) {
            Host iu = res_list.get(i);
            map.put(iu.getIp(), iu.getName());
        }
        return map;
    }
}
