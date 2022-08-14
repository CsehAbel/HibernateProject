package chapter03.hibernate;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

@Entity
public class ST_Ports_G {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String dest_ip;
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> port;
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> name;
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> number;

    public ST_Ports_G() {
    }

    public ST_Ports_G(String dest_ip, Set<String> port, Set<String> name, Set<String> number) {
        this.dest_ip = dest_ip;
        this.port = port;
        this.name = name;
        this.number = number;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ST_Ports_G that = (ST_Ports_G) o;
        return id == that.id && Objects.equals(dest_ip, that.dest_ip) && Objects.equals(port, that.port) && Objects.equals(name, that.name) && Objects.equals(number, that.number);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, dest_ip, port, name, number);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDest_ip() {
        return dest_ip;
    }

    public void setDest_ip(String dest_ip) {
        this.dest_ip = dest_ip;
    }

    public Set<String> getPort() {
        return port;
    }

    public void setPort(Set<String> port) {
        this.port = port;
    }

    public Set<String> getName() {
        return name;
    }

    public void setName(Set<String> name) {
        this.name = name;
    }

    public Set<String> getNumber() {
        return number;
    }

    public void setNumber(Set<String> number) {
        this.number = number;
    }
}
