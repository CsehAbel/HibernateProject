package chapter03.hibernate;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(uniqueConstraints = {@UniqueConstraint(name = "UniqueDstipAndAppid",
        columnNames = {"dst_ip", "app_id"})})
public class Rlst_G {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String dst_ip;

    private String app_id;

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> ports;
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> app_requestor;
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> tsa;
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> fqdn;
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> app_name;

    public Rlst_G() {
    }

    @Override
    public String toString() {
        return "Rlst_G{" +
                "id=" + id +
                ", dst_ip='" + dst_ip + '\'' +
                ", app_id='" + app_id + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rlst_G rlst_g = (Rlst_G) o;
        return id == rlst_g.id && dst_ip.equals(rlst_g.dst_ip) && app_id.equals(rlst_g.app_id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, dst_ip, app_id);
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

    public String getApp_id() {
        return app_id;
    }

    public void setApp_id(String app_id) {
        this.app_id = app_id;
    }

    public Set<String> getPorts() {
        return ports;
    }

    public void setPorts(Set<String> ports) {
        this.ports = ports;
    }

    public Set<String> getApp_requestor() {
        return app_requestor;
    }

    public void setApp_requestor(Set<String> app_requestor) {
        this.app_requestor = app_requestor;
    }

    public Set<String> getTsa() {
        return tsa;
    }

    public void setTsa(Set<String> tsa) {
        this.tsa = tsa;
    }

    public Set<String> getFqdn() {
        return fqdn;
    }

    public void setFqdn(Set<String> fqdn) {
        this.fqdn = fqdn;
    }

    public Set<String> getApp_name() {
        return app_name;
    }

    public void setApp_name(Set<String> app_name) {
        this.app_name = app_name;
    }

    public Rlst_G(String dst_ip, String app_id, Set<String> ports, Set<String> app_requestor, Set<String> tsa, Set<String> fqdn, Set<String> app_name) {
        this.dst_ip = dst_ip;
        this.app_id = app_id;
        this.ports = ports;
        this.app_requestor = app_requestor;
        this.tsa = tsa;
        this.fqdn = fqdn;
        this.app_name = app_name;
    }
}
