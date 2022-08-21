package chapter03.hibernate;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data
public class Eagle {
    @Id
    int index;
    String ip;
    String base;
    String cidr;
    String ussm;
    String vpn;
}
