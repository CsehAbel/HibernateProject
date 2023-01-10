package chapter03.hibernate;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
public class Eagle {
    @Id
    int id;
    String ip;
    String base;
    int cidr;
}
