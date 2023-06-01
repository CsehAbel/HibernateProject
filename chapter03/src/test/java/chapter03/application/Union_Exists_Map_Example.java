package chapter03.application;

import java.util.Map;
import java.util.Set;

import org.testng.annotations.Test;

import chapter03.hibernate.Fwpolicy;
import chapter03.hibernate.Rlst;

import java.util.HashMap;
import java.util.Iterator;

@lombok.Data
public class Union_Exists_Map_Example {

    public Map<Fwpolicy, Set<Rlst>> resource_innerJoin;
    public Map<Union_Alternative,Map<Long,ExistsAsHost>> result_unionExistsMap;
    public Map<Long, ExistsAsHost> resource_dst_ip_existsMap;

    public Union_Exists_Map_Example() {
        this.resource_innerJoin = new Report_Alternative_Example().getInnerJoin();
        this.resource_dst_ip_existsMap = new DstIp_Exists_Map_Example().getResult_dstIp_exists();
        this.result_unionExistsMap = this.provideUnionExistsMap();
    }

    @Test
    public void testProvideUnionExistsMap() {
        var r = this.provideUnionExistsMap();
    }

    public Map<Union_Alternative,Map<Long,ExistsAsHost>> provideUnionExistsMap() {
        return this.provideUnionExistsMap(this.resource_innerJoin);
    }   

    public String intToIp(long ipv4address) {
        long ip = ipv4address;
        // return String.format("%d.%d.%d.%d", (ip & 0xff), (ip >> 8 & 0xff), (ip >> 16
        // & 0xff), (ip >> 24 & 0xff));
        // do the same but with endianess little endian
        return String.format("%d.%d.%d.%d", (ip >> 24 & 0xff), (ip >> 16 & 0xff), (ip >> 8 & 0xff), (ip & 0xff));
    }

    public Map<Union_Alternative,Map<Long,ExistsAsHost>> provideUnionExistsMap(Map<Fwpolicy, Set<Rlst>> resource_innerJoin){
        Map<Union_Alternative,Map<Long,ExistsAsHost>> result_unionExistsMap = new HashMap<>();
        
        Iterator<Fwpolicy> it = resource_innerJoin.keySet().iterator();
        
        while (it.hasNext()) {
            Fwpolicy fwpolicy = it.next();
            Set<Rlst> rlstSet = resource_innerJoin.get(fwpolicy);
            Union_Alternative u = new Union_Alternative(fwpolicy, rlstSet);
            //List of source ips
            long start_int = fwpolicy.getDest_ip_start_int();
            long end_int = fwpolicy.getDest_ip_end_int();
            
            Map<Long, ExistsAsHost> srcIpList = this.collectToExploded(start_int, end_int);
            
            if (!srcIpList.isEmpty()) {
                
                Map<Union_Alternative, Map<Long, ExistsAsHost>> map = new HashMap<>();
                map.put(u,srcIpList);
                result_unionExistsMap.putAll(map);
            }
        }
        return result_unionExistsMap;
    }


    private Map<Long, ExistsAsHost> collectToExploded(long start_int, long end_int) {
        Map<Long,ExistsAsHost> srcIpList = new HashMap<>();
        //iterate over the ip range, starting from the star_int to the end_int
        for (long j = start_int; j <= end_int; j++) {
            //check if the ip is already in the map
            ExistsAsHost srcIp = this.resource_dst_ip_existsMap.get(j);
            //if srcIp is not null, then the ip is in the map
            //if so srcIpList.put(j,srcIp);  
            //otherwise don't put it in the map
            if (srcIp!=null) {
                srcIpList.put(j,srcIp);
            }
        }
        return srcIpList;
    }
    
}
