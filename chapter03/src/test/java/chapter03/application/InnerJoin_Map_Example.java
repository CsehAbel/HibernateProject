package chapter03.application;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


@lombok.Data
public class InnerJoin_Map_Example {
        
        private Report_Example report_example;
        private Report report;
        private List<Union> innerJoin;
        private Map<Union,Map<Long,Set<Long>>> innerJoinMap;
        private Map<Long, Set<Long>> source_groups;
        private QueryService service;

        //assignt report in the getter lazy
        public Report getReport() {
            if (this.report == null) {
                this.report = this.report_example.getReport();
            }
            return this.report;
        }

        //assign report and innerJoin in the constructor
        public InnerJoin_Map_Example() {
            this.report_example = new Report_Example();
            this.innerJoin = report_example.getReport().getInBoth();
            this.service= new QueryService();
            this.source_groups = new IP_Map_Example().getMap();
            List<Integer> listPKWithoutEagle = this.service.listPKWithoutEagle();

            
            
            //take List<Integer> listPKWithoutEagle and temp_map
            //exclude from temp_map all keys that are in listPKWithoutEagle
            //and put the result in map
            //map = temp_map.entrySet().stream()
            //        .filter(e -> !listPKWithoutEagle.contains(e.getKey()))
            //        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

            //take List<Integer> listPKWithoutEagle and List<Union> innerJoin
            //exclude from innerJoin all unions that have a fwpolicy with an ip range
            //and put the result in innerJoin
            innerJoin = innerJoin.stream()
                    .filter(u -> !listPKWithoutEagle.contains(u.getFwpolicy().))
                    .collect(Collectors.toList());

            this.innerJoinMap = provideInnerJoinMap();
        }


        public String intToIp(long ipv4address) {
            long ip = ipv4address;
            //return String.format("%d.%d.%d.%d", (ip & 0xff), (ip >> 8 & 0xff), (ip >> 16 & 0xff), (ip >> 24 & 0xff));
            //do the same but with endianess little endian
            return String.format("%d.%d.%d.%d", (ip >> 24 & 0xff), (ip >> 16 & 0xff), (ip >> 8 & 0xff), (ip & 0xff));
        }
    

        public Map<Union,Map<Long,Set<Long>>> provideInnerJoinMap() {
            Map<Union,Map<Long,Set<Long>>> innerJoinMap = new HashMap<>();
            
            for (int i = 0; i < this.innerJoin.size(); i++) {
                Union u = this.innerJoin.get(i);
                //List of source ips
                long start_int = u.getFwpolicy().getDest_ip_start_int();
                long end_int = u.getFwpolicy().getDest_ip_end_int();
                String start_ip = u.getFwpolicy().getDest_ip_start();
                String end_ip = u.getFwpolicy().getDest_ip_end();
                
                //create a Map which contains the destination ip and the List of source ips
                Map<Long, Set<Long>> srcIpList = collectToExploded(start_int, end_int);
                //if the srcIpList is not empty
                //add the destination ip and the List of source ips to the innerJoinMap
                if (!srcIpList.isEmpty()) {
                    //place u and srcIpList first in a map and then add the map to the innerJoinMap
                    Map<Union,Map<Long,Set<Long>>> map = new HashMap<>();
                    map.put(u,srcIpList);
                    innerJoinMap.putAll(map);
                }
            }
            return innerJoinMap;
        }


        private Map<Long, Set<Long>> collectToExploded(long start_int, long end_int) {
            Map<Long,Set<Long>> srcIpList = new HashMap<>();
            //iterate over the ip range, starting from the star_int to the end_int
            for (long j = start_int; j <= end_int; j++) {
                //convert the int to an ip address
                String string_ip = intToIp(j);
                //check if the ip is already in the map
                Set<Long> srcIp = source_groups.get(j);
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
