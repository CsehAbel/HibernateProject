package chapter03.application;

import chapter03.hibernate.Fwpolicy;
import chapter03.hibernate.Rlst;

import java.util.Set;

@lombok.Data
public class Union_Alternative {

    private Fwpolicy fwpolicy;
    private Set<Rlst> rlstSet;

    public Union_Alternative(Fwpolicy fwpolicy, Set<Rlst> rlstSet) {
        this.fwpolicy = fwpolicy;
        this.rlstSet = rlstSet;
    }
    
}
