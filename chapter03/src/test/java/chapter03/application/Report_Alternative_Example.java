package chapter03.application;

import chapter03.hibernate.Fwpolicy;
import chapter03.hibernate.Rlst;

import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.HashMap;

@lombok.Data
public class Report_Alternative_Example {

    private Map<Fwpolicy,Set<Rlst>> innerJoin;

    private QueryService service;

    // assign service in the constructor
    public Report_Alternative_Example() {
        this.service = new QueryService();
        innerJoin = supplyReport();
    }

    @Test
    public void testSupplyReport() {
        System.out.println("map");
    }

    private Map<Fwpolicy, Set<Rlst>> supplyReport() {

        List<Rlst> rlstAll = service.selectAll(Rlst.class);
        int rlstSize = rlstAll.size();
        List<Fwpolicy> fwpolicyAll = service.selectAll(Fwpolicy.class);
        int fwpolicySize = fwpolicyAll.size();
        Map<Fwpolicy,Set<Rlst>> innerJoin = new HashMap<>();
 
        for (Fwpolicy f : fwpolicyAll) {
            Set<Rlst> rlstSet = new HashSet<>();
            for (Rlst r : rlstAll) {
                if (r.getStart_int() == f.getDest_ip_start_int() && r.getEnd_int() == f.getDest_ip_end_int()) {
                    rlstSet.add(r);
                }
            }
            if (rlstSet.size() > 0){
                innerJoin.put(f,rlstSet);
            }
        }
        return innerJoin;
    }
}
