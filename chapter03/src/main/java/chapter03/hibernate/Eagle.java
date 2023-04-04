package chapter03.hibernate;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

//create a class to represent the table
// CREATE TABLE `eagle` (
//   `id` int NOT NULL AUTO_INCREMENT,
//   `ip_start` varchar(15) NOT NULL,
//   `ip_end` varchar(15) NOT NULL,
//   `cidr` int NOT NULL,
//   `ip_start_int` int unsigned NOT NULL,
//   `ip_end_int` int unsigned NOT NULL,
//   PRIMARY KEY (`id`)
// ) ENGINE=InnoDB AUTO_INCREMENT=2515 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci

@Entity
@Data
public class Eagle {
    @Id
    int id;
    String ip_start;
    String ip_end;
    int cidr;
    long ip_start_int;
    long ip_end_int;
}
