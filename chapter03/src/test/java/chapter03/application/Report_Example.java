package chapter03.application;

import chapter03.hibernate.Fwpolicy;
import chapter03.hibernate.Rlst;

import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

@lombok.Data
public class Report_Example {

    private Report report;

    private QueryService service;

    //assign service in the constructor
    public Report_Example() {
        this.service = new QueryService();
        report = supplyReport();
    }

    @Test
    public void testSupplyReport() {
        System.out.println("report");
    }

    private Report supplyReport() {

        List<Rlst> rlstList = service.selectAll(Rlst.class);
        int rlstSize = rlstList.size();
        List<Fwpolicy> fwpolicyList = service.selectAll(Fwpolicy.class);
        int fwpolicySize = fwpolicyList.size();
        List<Rlst> notInPolicy = new ArrayList<>();
        List<Fwpolicy> notInRuleset = new ArrayList<>();
        //fill readyToExport with inner join
        //SELECT * FROM rlstList INNER JOIN fwpolicyList
        //ON rlstList.start_int=fwpolicyList.dest_ip_start_int AND r.end_int=f.dest_ip_end_int;
        List<Union> innerJoin1 = new ArrayList<>();
        for (int i = 0; i < rlstList.size(); i++) {
            Rlst r = rlstList.get(i);
            List<Union> unionList1 = new ArrayList<>();
            //if there is a match, add to matchesForRuleset
            //if there is no match, add to not_in_policy
            for (int j = 0; j < fwpolicyList.size(); j++) {
                Fwpolicy f = fwpolicyList.get(j);
                if (r.getStart_int() == f.getDest_ip_start_int() && r.getEnd_int() == f.getDest_ip_end_int()) {
                    Union u = new Union(r,f);
                    unionList1.add(u);
                }
            }
            if (unionList1.size() == 0) {
                //place r in notInPolicy
                notInPolicy.add(r);
            } else {
                innerJoin1.addAll(unionList1);
            }
        }
        int notInPolicySize = notInPolicy.size();
        List<Union> innerJoin2 = new ArrayList<>();
        for(int l=0; l<fwpolicyList.size(); l++){
            Fwpolicy f = fwpolicyList.get(l);
            List<Union> unionList2 = new ArrayList<>();
            for(int m=0; m<rlstList.size(); m++){
                Rlst r = rlstList.get(m);
                if (r.getStart_int() == f.getDest_ip_start_int() && r.getEnd_int() == f.getDest_ip_end_int()) {
                    Union u = new Union(r,f);
                    unionList2.add(u);
                }
            }
            if(unionList2.size()==0){
                //place f in notInRuleset
                notInRuleset.add(f);
            } else {
                innerJoin2.addAll(unionList2);
            }
        }
        int notInRulesetSize = notInRuleset.size();
        int innerJoin1Size = innerJoin1.size();
        int innerJoin2Size = innerJoin2.size();
        boolean twoListsAreEqual = innerJoin1.equals(innerJoin2);
        return new Report(notInPolicy, notInRuleset, innerJoin1);
    }
}
