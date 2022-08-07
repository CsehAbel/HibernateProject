package chapter03.hibernate;

import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import java.util.List;

@Entity
public class IP_Unique_G {
    @Id
    private int id;

    @NaturalId
    private String dst_ip;

    //@OneToMany(mappedBy="group")
    //it will persist the IP_Unique_G's PK as a FK in "ip_unique"

    //it will persist the IP_Unique_G's PK as a FK in "ip_unique_g_jt"
    @OneToMany
    @JoinTable(name="ip_unique_g_jt",
            joinColumns={@JoinColumn(name = "iug_dip", referencedColumnName = "dst_ip")},
            inverseJoinColumns={@JoinColumn(name = "iu_dip", referencedColumnName = "dst_ip")})
    private List<IP_Unique> sources;
}
