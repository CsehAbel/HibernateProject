package chapter03.application;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

import org.testng.annotations.Test;

@lombok.Data
public class DstIp_Exists_Map_Example {

    protected Map<Long, ExistsAsHost> result_dstIp_exists;
    protected Map<String, String> resource_host;
    protected Map<Long, Set<Long>> resource_source_groups;

    public DstIp_Exists_Map_Example() {
        this.resource_source_groups = new IP_Map_Example().getMap();
        this.resource_host = new Host_Map_Example().getMap();
        this.result_dstIp_exists = getReport2s();
    }

    @Test
    public void testSupplyInnerJoin_Map_Example() {
        System.out.println("subclass");
    }

    public String intToIp(long ipv4address) {
        long ip = ipv4address;
        // return String.format("%d.%d.%d.%d", (ip & 0xff), (ip >> 8 & 0xff), (ip >> 16
        // & 0xff), (ip >> 24 & 0xff));
        // do the same but with endianess little endian
        return String.format("%d.%d.%d.%d", (ip >> 24 & 0xff), (ip >> 16 & 0xff), (ip >> 8 & 0xff), (ip & 0xff));
    }

    private Map<Long,ExistsAsHost> getReport2s() {
        Map<Long,ExistsAsHost> report2s = new HashMap<>();

        Iterator<Map.Entry<Long, Set<Long>>> it2 = this.resource_source_groups.entrySet().iterator();
        while (it2.hasNext()) {
            Map.Entry<Long, Set<Long>> pair2 = it2.next();
            Long dstIp = pair2.getKey();
            Set<Long> srcIpSet = pair2.getValue();
            // for each element in the srcIpSet
            Iterator<Long> it3 = srcIpSet.iterator();
            List<String> exists = new ArrayList<>();
            List<String> notexists = new ArrayList<>();
            while (it3.hasNext()) {
                Long srcIp = it3.next();
                // if the srcIp is in Map<String,String> host
                if (resource_host.containsKey(intToIp(srcIp))) {
                    // add the srcIp to the exists list
                    exists.add(resource_host.get(intToIp(srcIp)));
                } else {
                    // add the srcIp to the notexists list
                    notexists.add(intToIp(srcIp));
                }
            }
            // add the exists, notexists and union to the report2 list
            report2s.put(dstIp,new ExistsAsHost(exists, notexists));
        }
        return report2s;
    }
}
