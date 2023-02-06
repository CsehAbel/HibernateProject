package chapter03.application;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.testng.annotations.Test;

import chapter03.hibernate.Fwpolicy;
import chapter03.hibernate.Rlst;


@lombok.Data
public class InnerJoin_Map_Example {
        
        protected Map<Fwpolicy,Set<Rlst>> resource_innerJoin;
        protected Map<Union_Alternative,Map<Long,Set<Long>>> result_innerJoinMap;
        protected Map<Long, Set<Long>> resource_source_groups;

        //assign report and resource_innerJoin in the constructor
        public InnerJoin_Map_Example() {
            this.resource_source_groups = new IP_Map_Example().getMap();
            this.resource_innerJoin = new Report_Alternative_Example().getInnerJoin();
            this.result_innerJoinMap = this.provideInnerJoinMap();
        }

        @Test
        public void testInnerJoin_Map_Example() {
            System.out.println("map");
        }

        public String intToIp(long ipv4address) {
            long ip = ipv4address;
            //return String.format("%d.%d.%d.%d", (ip & 0xff), (ip >> 8 & 0xff), (ip >> 16 & 0xff), (ip >> 24 & 0xff));
            //do the same but with endianess little endian
            return String.format("%d.%d.%d.%d", (ip >> 24 & 0xff), (ip >> 16 & 0xff), (ip >> 8 & 0xff), (ip & 0xff));
        }

        public Map<Union_Alternative,Map<Long,Set<Long>>> provideInnerJoinMap() {
            return this.provideInnerJoinMap(this.resource_innerJoin);
        }


        public Map<Union_Alternative,Map<Long,Set<Long>>> provideInnerJoinMap(Map<Fwpolicy,Set<Rlst>> resource_innerJoin){
            Map<Union_Alternative,Map<Long,Set<Long>>> resource_innerJoinMap = new HashMap<>();
            
            //iterate over the resource_innerJoin map
            Iterator<Map.Entry<Fwpolicy, Set<Rlst>>> it = resource_innerJoin.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<Fwpolicy, Set<Rlst>> pair = it.next();
                Fwpolicy fwpolicy = pair.getKey();
                Set<Rlst> rlstSet = pair.getValue();

                long start_int = fwpolicy.getDest_ip_start_int();
                long end_int = fwpolicy.getDest_ip_end_int();

                Map<Long, Set<Long>> srcIpList = this.collectToExploded(start_int, end_int);
                
                    if (!srcIpList.isEmpty()) {
                        //place u and srcIpList first in a map and then add the map to the resource_innerJoinMap
                        Map<Union_Alternative,Map<Long,Set<Long>>> map = new HashMap<>();
                        map.put(new Union_Alternative(fwpolicy,rlstSet),srcIpList);
                        resource_innerJoinMap.putAll(map);
                    }
                
            }
            return resource_innerJoinMap;
        }


        private Map<Long, Set<Long>> collectToExploded(long start_int, long end_int) {
            Map<Long,Set<Long>> srcIpList = new HashMap<>();
            //iterate over the ip range, starting from the star_int to the end_int
            for (long j = start_int; j <= end_int; j++) {
                //convert the int to an ip address
                String string_ip = this.intToIp(j);
                //check if the ip is already in the map
                Set<Long> srcIp = this.resource_source_groups.get(j);
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
