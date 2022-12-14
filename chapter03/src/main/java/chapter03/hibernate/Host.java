package chapter03.hibernate;

import javax.persistence.*;

import lombok.Data;

//create a class to represent the table
// CREATE TABLE `hosts` (
//   `id` int NOT NULL AUTO_INCREMENT,
//   `ip` varchar(15) NOT NULL,
//   `name` varchar(80) NOT NULL,
//   PRIMARY KEY (`id`)
// ) ENGINE=InnoDB AUTO_INCREMENT=10837 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci


@Entity
@Table(name="hosts")
@Data
public class Host {
    
    @Id
    private int id;
    @Column(name = "ip" )
    private String ip;
    @Column(name = "name" )
    private String name;
}
