package chapter03.hibernate;

import org.hibernate.annotations.GeneratorType;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
public class IP_Unique implements Serializable {

    @Id
    private int id;

    @Column
    private String src_ip;

    @Column
    private String dst_ip;

    public IP_Unique() {
    }

    public IP_Unique(String src_ip, String dst_ip) {
        this.src_ip = src_ip;
        this.dst_ip = dst_ip;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSrc_ip() {
        return src_ip;
    }

    public void setSrc_ip(String src_ip) {
        this.src_ip = src_ip;
    }

    public String getDst_ip() {
        return dst_ip;
    }

    public void setDst_ip(String dst_ip) {
        this.dst_ip = dst_ip;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IP_Unique ip_unique = (IP_Unique) o;
        return id == ip_unique.id && src_ip.equals(ip_unique.src_ip) && dst_ip.equals(ip_unique.dst_ip);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, src_ip, dst_ip);
    }

    @Override
    public String toString() {
        return "IP_Unique{" +
                "id=" + id +
                ", src_ip='" + src_ip + '\'' +
                ", dst_ip='" + dst_ip + '\'' +
                '}';
    }
}
