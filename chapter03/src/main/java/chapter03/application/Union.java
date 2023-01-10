package chapter03.application;

import chapter03.hibernate.Fwpolicy;
import chapter03.hibernate.Rlst;

@lombok.Data
public class Union{
    private Rlst rlst;
    private Fwpolicy fwpolicy;

    public Union(Rlst rlst, Fwpolicy fwpolicy) {
        this.rlst = rlst;
        this.fwpolicy = fwpolicy;
    }
}
