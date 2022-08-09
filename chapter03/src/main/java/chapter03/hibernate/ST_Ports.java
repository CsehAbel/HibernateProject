package chapter03.hibernate;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Objects;

@Entity
public class ST_Ports {
    @Id
    private int index;

    private String st_dest_ip;

    private String st_port;

    private String rule_name;

    private String rule_number;

    public ST_Ports() {
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getSt_dest_ip() {
        return st_dest_ip;
    }

    public void setSt_dest_ip(String st_dest_ip) {
        this.st_dest_ip = st_dest_ip;
    }

    public String getSt_port() {
        return st_port;
    }

    public void setSt_port(String st_port) {
        this.st_port = st_port;
    }

    public String getRule_name() {
        return rule_name;
    }

    public void setRule_name(String rule_name) {
        this.rule_name = rule_name;
    }

    public String getRule_number() {
        return rule_number;
    }

    public void setRule_number(String rule_number) {
        this.rule_number = rule_number;
    }

    public ST_Ports(String st_dest_ip, String st_port, String rule_name, String rule_number) {
        this.st_dest_ip = st_dest_ip;
        this.st_port = st_port;
        this.rule_name = rule_name;
        this.rule_number = rule_number;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ST_Ports st_ports = (ST_Ports) o;
        return index == st_ports.index && st_dest_ip.equals(st_ports.st_dest_ip) && st_port.equals(st_ports.st_port) && rule_name.equals(st_ports.rule_name) && rule_number.equals(st_ports.rule_number);
    }

    @Override
    public String toString() {
        return "ST_Ports{" +
                "id=" + index +
                ", st_dest_ip='" + st_dest_ip + '\'' +
                ", st_port='" + st_port + '\'' +
                ", rule_name='" + rule_name + '\'' +
                ", rule_number='" + rule_number + '\'' +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(index, st_dest_ip, st_port, rule_name, rule_number);
    }
}
