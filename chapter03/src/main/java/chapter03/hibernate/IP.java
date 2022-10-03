package chapter03.hibernate;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
public class IP implements Serializable {

    @Id
    private int id;

    @Column
    private String src_ip;

    @Column
    private String dst_ip;

    public IP() {
    }

    public IP(String src_ip, String dst_ip) {
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
        IP ip = (IP) o;
        return src_ip.equals(ip.src_ip) && dst_ip.equals(ip.dst_ip);
    }

    @Override
    public int hashCode() {
        return Objects.hash(src_ip, dst_ip);
    }

    @Override
    public String toString() {
        return "IP{" +
                "src_ip='" + src_ip + '\'' +
                ", dst_ip='" + dst_ip + '\'' +
                '}';
    }
}
