package chapter03.application;

import chapter03.hibernate.Fwpolicy;
import chapter03.hibernate.Rlst;

import java.util.List;

@lombok.Data
public class Report{
    private List<Rlst> notInPolicy;
    private List<Fwpolicy> notInRlst;
    private List<Union> inBoth;

    public Report(List<Rlst> notInPolicy, List<Fwpolicy> notInRlst, List<Union> inBoth) {
        this.notInPolicy = notInPolicy;
        this.notInRlst = notInRlst;
        this.inBoth = inBoth;
    }
}
