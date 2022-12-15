package chapter03.hibernate;

import javax.persistence.*;

import lombok.Data;

//create a class to represent the table
// CREATE TABLE `ip` (
//   `id` int NOT NULL AUTO_INCREMENT,
//   `src_ip` varchar(20) NOT NULL,
//   `dst_ip` varchar(20) NOT NULL,
//   `src_ip_int` int unsigned NOT NULL,
//   `dst_ip_int` int unsigned NOT NULL,
//   PRIMARY KEY (`id`),
//   UNIQUE KEY `my_uniq_id` (`src_ip`,`dst_ip`)
// ) ENGINE=InnoDB AUTO_INCREMENT=355228 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci


@Entity
@Table(name="ip")
@Data
public class IP{

    @Id
    private int id;
    @Column(name = "src_ip" )
    private String src_ip;
    @Column(name = "dst_ip" )
    private String dst_ip;
    @Column(name = "src_ip_int" )
    private long src_ip_int;
    @Column(name = "dst_ip_int" )
    private long dst_ip_int;
}