package chapter03.hibernate;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data
public class Eagle {
    @Id
    int id;
    String ip;
    String base;
    String cidr;
}
