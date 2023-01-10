package chapter03.application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import chapter03.hibernate.Fwpolicy;
import chapter03.hibernate.Rlst;
import chapter03.hibernate.util.SessionUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

@lombok.Data
public class Report2_Example {

    private InnerJoin_Map_Example innerJoin_Map_Example = new InnerJoin_Map_Example();

    private Map<Union, Map<Long, Set<Long>>> innerJoinMap = new InnerJoin_Map_Example().getInnerJoinMap();

    private List<Report2> cReport2s;

    private Host_Map_Example host_Map_Example;

    private Map<String, String> host;

    public Report2_Example() {
        innerJoin_Map_Example = new InnerJoin_Map_Example();
        innerJoinMap = innerJoin_Map_Example.getInnerJoinMap();
        host_Map_Example = new Host_Map_Example();
        host = host_Map_Example.getMap();
        cReport2s = getReport2s();
    }

    public String intToIp(long ipv4address) {
        long ip = ipv4address;
        // return String.format("%d.%d.%d.%d", (ip & 0xff), (ip >> 8 & 0xff), (ip >> 16
        // & 0xff), (ip >> 24 & 0xff));
        // do the same but with endianess little endian
        return String.format("%d.%d.%d.%d", (ip >> 24 & 0xff), (ip >> 16 & 0xff), (ip >> 8 & 0xff), (ip & 0xff));
    }

    private List<Report2> getReport2s() {
        Iterator<Map.Entry<Union, Map<Long, Set<Long>>>> it = this.innerJoinMap.entrySet().iterator();
        List<Report2> report2s = new ArrayList<>();
        while (it.hasNext()) {
            Map.Entry<Union, Map<Long, Set<Long>>> pair = it.next();
            Union union = pair.getKey();
            Map<Long, Set<Long>> srcIpsMap = pair.getValue();
            // union contains a destination ip range
            // srcIpMap contains a set of source ip ranges for each ip in the destination ip
            // range
            // for each element in the srcIpList
            // we should add an item to report2
            Iterator<Map.Entry<Long, Set<Long>>> it2 = srcIpsMap.entrySet().iterator();
            while (it2.hasNext()) {
                Map.Entry<Long, Set<Long>> pair2 = it2.next();
                Long dstIp = pair2.getKey();
                String dstIpString = intToIp(dstIp);
                Set<Long> srcIpSet = pair2.getValue();
                // for each element in the srcIpSet
                Iterator<Long> it3 = srcIpSet.iterator();
                List<String> exists = new ArrayList<>();
                List<String> notexists = new ArrayList<>();
                while (it3.hasNext()) {
                    Long srcIp = it3.next();
                    // if the srcIp is in Map<String,String> host
                    if (host.containsKey(intToIp(srcIp))) {
                        // add the srcIp to the exists list
                        exists.add(host.get(intToIp(srcIp)));
                    } else {
                        // add the srcIp to the notexists list
                        notexists.add(intToIp(srcIp));
                    }
                }
                // add the exists, notexists and union to the report2 list
                report2s.add(new Report2(dstIpString, exists, notexists, union));
            }

        }
        return report2s;
    }
}
