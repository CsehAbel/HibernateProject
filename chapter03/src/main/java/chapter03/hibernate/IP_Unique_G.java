package chapter03.hibernate;

import org.hibernate.annotations.Loader;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
public class IP_Unique_G {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String dst_ip;

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> sources;

    public IP_Unique_G() {
    }

    public IP_Unique_G(String dst_ip, Set<String> sources) {
        this.dst_ip = dst_ip;
        this.sources = sources;
    }

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

    public Set<String> getSources() {
        return sources;
    }

    public void setSources(Set<String> sources) {
        this.sources = sources;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IP_Unique_G that = (IP_Unique_G) o;
        return id == that.id && dst_ip.equals(that.dst_ip) && sources.equals(that.sources);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, dst_ip, sources);
    }
}
