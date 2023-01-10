package chapter03.application;

import java.util.HashMap;
import java.util.Map;

import chapter03.hibernate.Eagle;

@lombok.Data
public class Eagle_Map_Example {
    
    private Map<String, String> map;

    private QueryService service;

    public Eagle_Map_Example() {
        service = new QueryService();
        map = get_eagle_map();
    }

    public Map<String, String> get_eagle_map() {
        var res_list = this.service.selectAll(Eagle.class);
        //Map contains key - ip, value - base
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < res_list.size(); i++) {
            Eagle iu = res_list.get(i);
            map.put(iu.getIp(), iu.getBase());
        }
        return map;
    }
}
