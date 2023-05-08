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

    protected Map<Fwpolicy, Set<Rlst>> resource_innerJoin;
    protected Map<Fwpolicy,Map<Long,Set<String>>> result_unionExistsMap;
    protected Map<Long, Set<String>> resource_dst_ip_bucketsMap;

    public Union_Exists_Map_Example() {
        this.resource_innerJoin = new Report_Alternative_Example().getInnerJoin();
        this.resource_dst_ip_bucketsMap = new IP_SrcBuckets_Map_Example().getResult_map();
        this.result_unionExistsMap = this.provideUnionExistsMap();
    }

    @Test
    public void testProvideUnionExistsMap() {
        var r = this.provideUnionExistsMap();
    }

    public Map<Fwpolicy,Map<Long,Set<String>>> provideUnionExistsMap() {
        return this.provideUnionExistsMap(this.resource_innerJoin);
    }   

    public String intToIp(long ipv4address) {
        long ip = ipv4address;
        // return String.format("%d.%d.%d.%d", (ip & 0xff), (ip >> 8 & 0xff), (ip >> 16
        // & 0xff), (ip >> 24 & 0xff));
        // do the same but with endianess little endian
        return String.format("%d.%d.%d.%d", (ip >> 24 & 0xff), (ip >> 16 & 0xff), (ip >> 8 & 0xff), (ip & 0xff));
    }

    public Map<Fwpolicy,Map<Long,Set<String>>> provideUnionExistsMap(Map<Fwpolicy, Set<Rlst>> resource_innerJoin){
        Map<Fwpolicy,Map<Long,Set<String>>> result_unionExistsMap = new HashMap<>();
        
        Iterator<Fwpolicy> it = resource_innerJoin.keySet().iterator();
        
        while (it.hasNext()) {
            Fwpolicy fwpolicy = it.next();
            //List of source ips
            long start_int = fwpolicy.getDest_ip_start_int();
            long end_int = fwpolicy.getDest_ip_end_int();
            
            Map<Long, Set<String>> srcIpList = this.collectToExploded(start_int, end_int);
            
            if (!srcIpList.isEmpty()) {
                
                Map<Fwpolicy, Map<Long, Set<String>>> map = new HashMap<>();
                map.put(fwpolicy,srcIpList);
                result_unionExistsMap.putAll(map);
            }
        }
        return result_unionExistsMap;
    }


    private Map<Long, Set<String>> collectToExploded(long start_int, long end_int) {
        Map<Long,Set<String>> srcBucketMap = new HashMap<>();
        //iterate over the ip range, starting from the star_int to the end_int
        for (long j = start_int; j <= end_int; j++) {
            //check if the ip is already in the map
            Set<String> srcBucket = this.resource_dst_ip_bucketsMap.get(j);
            //if srcIp is not null, then the ip is in the map
            //if so srcIpList.put(j,srcIp);  
            //otherwise don't put it in the map
            if (srcBucket!=null) {
                srcBucketMap.put(j,srcBucket);
            }
        }
        return srcBucketMap;
    }
    
}
