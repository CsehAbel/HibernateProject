package chapter03.hibernate;

import org.hibernate.annotations.NaturalId;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
public class IP_Unique_G {
    @Id
    private int id;

    @NaturalId
    private String dst_ip;

    //@OneToMany(mappedBy="dst_ip")
    @OneToMany(mappedBy="group")
    private List<IP_Unique> sources;
}
