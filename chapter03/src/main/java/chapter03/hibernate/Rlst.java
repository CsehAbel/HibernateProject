package chapter03.hibernate;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

//create a class to represent the table
//CREATE TABLE `ruleset` (
//        `id` int NOT NULL AUTO_INCREMENT,
//        `start` varchar(15) NOT NULL,
//        `end` varchar(15) NOT NULL,
//        `start_int` int unsigned NOT NULL,
//        `end_int` int unsigned NOT NULL,
//        `cidr` int NOT NULL,
//        `fqdns` varchar(255) DEFAULT NULL,
//        `tsa` date DEFAULT NULL,
//        `app_name` varchar(255) DEFAULT NULL,
//        `app_id` varchar(255) DEFAULT NULL,
//        PRIMARY KEY (`id`)
//        ) ENGINE=InnoDB AUTO_INCREMENT=2409 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
@Entity
@Table(name="ruleset")
@Data
public class Rlst{

    @Id
    private int id;
    @Column(name = "start" )
    private String start;
    @Column(name = "end" )
    private String end;
    @Column(name = "start_int" )
    private double start_int;
    @Column(name = "end_int" )
    private double end_int;
    @Column(name = "cidr" )
    private int cidr;
    @Column(name = "fqdns" )
    private String fqdns;
    @Column(name = "tsa" )
    private String tsa;
    @Column(name = "app_name" )
    private String app_name;
    @Column(name = "app_id" )
    private String app_id;

}