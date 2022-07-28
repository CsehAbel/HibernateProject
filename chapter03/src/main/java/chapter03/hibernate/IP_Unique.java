package chapter03.hibernate;

import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import java.io.Serializable;
import java.util.Objects;

@Entity
public class IP_Unique implements Serializable {
    @EmbeddedId
    private CompositePK compositePK;
    private String src_ip;
    private String dst_ip;
    public IP_Unique() {
    }

    public IP_Unique(String src_ip, String dst_ip) {
        this();
        this.src_ip = src_ip;
        this.dst_ip = dst_ip;
    }

    public CompositePK getCompositePK() {
        return compositePK;
    }

    public void setCompositePK(CompositePK compositePK) {
        this.compositePK = compositePK;
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
        return compositePK.equals(ip_unique.compositePK) && src_ip.equals(ip_unique.src_ip) && dst_ip.equals(ip_unique.dst_ip);
    }

    @Override
    public int hashCode() {
        return Objects.hash(compositePK, src_ip, dst_ip);
    }

    @Override
    public String toString() {
        return "IP_Unique{" +
                "compositePK=" + compositePK +
                ", src_ip='" + src_ip + '\'' +
                ", dst_ip='" + dst_ip + '\'' +
                '}';
    }

    @Embeddable
    public static class CompositePK implements Serializable {
        protected String src_ip;
        protected String dst_ip;

        public CompositePK() {
        }

        public CompositePK(String src_ip, String dst_ip) {
            this.src_ip = src_ip;
            this.dst_ip = dst_ip;
        }
        // equals, hashCode


        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CompositePK that = (CompositePK) o;
            return src_ip.equals(that.src_ip) && dst_ip.equals(that.dst_ip);
        }

        @Override
        public String toString() {
            return "CompositePK{" +
                    "src_ip='" + src_ip + '\'' +
                    ", dst_ip='" + dst_ip + '\'' +
                    '}';
        }

        @Override
        public int hashCode() {
            return Objects.hash(src_ip, dst_ip);
        }
    }
}
