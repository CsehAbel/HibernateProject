package chapter03.application;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

import chapter03.hibernate.Eagle;

@lombok.Data
public class Eagle_Map_Example {

    private Map<Long, Long> map;

    private QueryService service;

    public Eagle_Map_Example() {
        service = new QueryService();
        map = get_eagle_map();
    }

    // get_eagle_map containing ip_start_int and ip_end_int
    private Map<Long, Long> get_eagle_map() {
        List<Eagle> res_list = this.service.selectAll(Eagle.class);
        Map<Long, Long> map = new HashMap<>();
        for (int i = 0; i < res_list.size(); i++) {
            Eagle iu = res_list.get(i);
            var key = iu.getIp_start_int();
            var value = iu.getIp_end_int();
            map.put(key, value);
        }
        return map;
    }
}
