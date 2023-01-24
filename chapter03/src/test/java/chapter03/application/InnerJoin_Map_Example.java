package chapter03.application;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


@lombok.Data
public class InnerJoin_Map_Example {
        
        protected List<Union> resource_innerJoin;
        protected Map<Union,Map<Long,Set<Long>>> result_innerJoinMap;
        protected Map<Long, Set<Long>> resource_source_groups;

        //assign report and resource_innerJoin in the constructor
        public InnerJoin_Map_Example() {
            this.resource_source_groups = new IP_Map_Example().getMap();
            this.resource_innerJoin = new Report_Example().getReport().getInBoth();
            this.result_innerJoinMap = this.provideInnerJoinMap();
        }

        public String intToIp(long ipv4address) {
            long ip = ipv4address;
            //return String.format("%d.%d.%d.%d", (ip & 0xff), (ip >> 8 & 0xff), (ip >> 16 & 0xff), (ip >> 24 & 0xff));
            //do the same but with endianess little endian
            return String.format("%d.%d.%d.%d", (ip >> 24 & 0xff), (ip >> 16 & 0xff), (ip >> 8 & 0xff), (ip & 0xff));
        }

        public Map<Union,Map<Long,Set<Long>>> provideInnerJoinMap() {
            return this.provideInnerJoinMap(this.resource_innerJoin);
        }


        public Map<Union,Map<Long,Set<Long>>> provideInnerJoinMap(List<Union> resource_innerJoin){
            Map<Union,Map<Long,Set<Long>>> resource_innerJoinMap = new HashMap<>();
            
            for (int i = 0; i < this.resource_innerJoin.size(); i++) {
                Union u = this.resource_innerJoin.get(i);
                //List of source ips
                long start_int = u.getFwpolicy().getDest_ip_start_int();
                long end_int = u.getFwpolicy().getDest_ip_end_int();
                String start_ip = u.getFwpolicy().getDest_ip_start();
                String end_ip = u.getFwpolicy().getDest_ip_end();
                
                //create a Map which contains the destination ip and the List of source ips
                Map<Long, Set<Long>> srcIpList = this.collectToExploded(start_int, end_int);
                //if the srcIpList is not empty
                //add the destination ip and the List of source ips to the resource_innerJoinMap
                if (!srcIpList.isEmpty()) {
                    //place u and srcIpList first in a map and then add the map to the resource_innerJoinMap
                    Map<Union,Map<Long,Set<Long>>> map = new HashMap<>();
                    map.put(u,srcIpList);
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
