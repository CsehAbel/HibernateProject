package chapter03.hibernate;

import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDst_ip() {
        return dst_ip;
    }

    public void setDst_ip(String dst_ip) {
        this.dst_ip = dst_ip;
    }

    public List<IP_Unique> getSources() {
        return sources;
    }

    public void setSources(List<IP_Unique> sources) {
        this.sources = sources;
    }

    public IP_Unique_G(String dst_ip) {
        this.dst_ip = dst_ip;
    }

    public IP_Unique_G() {

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IP_Unique_G that = (IP_Unique_G) o;
        return id == that.id && dst_ip.equals(that.dst_ip) && Objects.equals(sources, that.sources);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, dst_ip, sources);
    }

    @Override
    public String toString() {
        return "IP_Unique_G{" +
                "id=" + id +
                ", dst_ip='" + dst_ip + '\'' +
                '}';
    }
}
