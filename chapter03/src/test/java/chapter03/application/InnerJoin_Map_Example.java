package chapter03.application;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


@lombok.Data
public class InnerJoin_Map_Example {
        
        protected Report_Example report_example;
        protected Report report;
        protected List<Union> innerJoin;
        protected Map<Union,Map<Long,Set<Long>>> innerJoinMap;
        protected Map<Long, Set<Long>> source_groups;

        //assignt report in the getter lazy
        public Report getReport() {
            if (this.report == null) {
                this.report = this.report_example.getReport();
            }
            return this.report;
        }

        //assign report and innerJoin in the constructor
        public InnerJoin_Map_Example() {
            this.source_groups = new IP_Map_Example().getMap();
            this.report_example = new Report_Example();
            this.innerJoin = report_example.getReport().getInBoth();
            this.innerJoinMap = this.provideInnerJoinMap();
        }

        public String intToIp(long ipv4address) {
            long ip = ipv4address;
            //return String.format("%d.%d.%d.%d", (ip & 0xff), (ip >> 8 & 0xff), (ip >> 16 & 0xff), (ip >> 24 & 0xff));
            //do the same but with endianess little endian
            return String.format("%d.%d.%d.%d", (ip >> 24 & 0xff), (ip >> 16 & 0xff), (ip >> 8 & 0xff), (ip & 0xff));
        }

        public Map<Union,Map<Long,Set<Long>>> provideInnerJoinMap() {
            return this.provideInnerJoinMap(this.innerJoin);
        }


        public Map<Union,Map<Long,Set<Long>>> provideInnerJoinMap(List<Union> innerJoin){
            Map<Union,Map<Long,Set<Long>>> innerJoinMap = new HashMap<>();
            
            for (int i = 0; i < this.innerJoin.size(); i++) {
                Union u = this.innerJoin.get(i);
                //List of source ips
                long start_int = u.getFwpolicy().getDest_ip_start_int();
                long end_int = u.getFwpolicy().getDest_ip_end_int();
                String start_ip = u.getFwpolicy().getDest_ip_start();
                String end_ip = u.getFwpolicy().getDest_ip_end();
                
                //create a Map which contains the destination ip and the List of source ips
                Map<Long, Set<Long>> srcIpList = this.collectToExploded(start_int, end_int);
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
                String string_ip = this.intToIp(j);
                //check if the ip is already in the map
                Set<Long> srcIp = this.source_groups.get(j);
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
