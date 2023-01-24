package chapter03.application;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;

@lombok.Data
public class Union_Exists_Map_Example {

    protected List<Union> resource_innerJoin;
    protected Map<Union,Map<Long,ExistsAsHost>> result_unionExistsMap;
    protected Map<Long, ExistsAsHost> resource_dst_ip_existsMap;

    public Union_Exists_Map_Example() {
        this.resource_innerJoin = new Report_Example().getReport().getInBoth();
        this.resource_dst_ip_existsMap = new DstIp_Exists_Map_Example().getResult_dstIp_exists();
        this.result_unionExistsMap = this.provideUnionExistsMap();
    }

    public Map<Union,Map<Long,ExistsAsHost>> provideUnionExistsMap() {
        return this.provideUnionExistsMap(this.resource_innerJoin);
    }   

    public String intToIp(long ipv4address) {
        long ip = ipv4address;
        // return String.format("%d.%d.%d.%d", (ip & 0xff), (ip >> 8 & 0xff), (ip >> 16
        // & 0xff), (ip >> 24 & 0xff));
        // do the same but with endianess little endian
        return String.format("%d.%d.%d.%d", (ip >> 24 & 0xff), (ip >> 16 & 0xff), (ip >> 8 & 0xff), (ip & 0xff));
    }

    public Map<Union,Map<Long,ExistsAsHost>> provideUnionExistsMap(List<Union> resource_innerJoin){
        Map<Union,Map<Long,ExistsAsHost>> result_unionExistsMap = new HashMap<>();
        
        for (int i = 0; i < this.resource_innerJoin.size(); i++) {
            Union u = this.resource_innerJoin.get(i);
            //List of source ips
            long start_int = u.getFwpolicy().getDest_ip_start_int();
            long end_int = u.getFwpolicy().getDest_ip_end_int();
            String start_ip = u.getFwpolicy().getDest_ip_start();
            String end_ip = u.getFwpolicy().getDest_ip_end();
            
            
            Map<Long, ExistsAsHost> srcIpList = this.collectToExploded(start_int, end_int);
            
            if (!srcIpList.isEmpty()) {
                
                Map<Union, Map<Long, ExistsAsHost>> map = new HashMap<>();
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
