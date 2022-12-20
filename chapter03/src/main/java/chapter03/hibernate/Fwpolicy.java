package chapter03.hibernate;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

//create a class to represent the table
//CREATE TABLE `fwpolicy` (
//        `id` int NOT NULL AUTO_INCREMENT,
//        `dest_ip_start` varchar(15) NOT NULL,
//        `dest_ip_end` varchar(15) NOT NULL,
//        `dest_ip_cidr` int NOT NULL,
//        `dest_ip_type` varchar(15) NOT NULL,
//        `dest_ip_start_int` int unsigned NOT NULL,
//        `dest_ip_end_int` int unsigned NOT NULL,
//        `json_services` varchar(591) NOT NULL,
//        `rule_name` varchar(80) NOT NULL,
//        `rule_number` varchar(15) NOT NULL,
//        PRIMARY KEY (`id`)
//        ) ENGINE=InnoDB AUTO_INCREMENT=2176 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
@Entity
@Table(name="fwpolicy")
@Data
public class Fwpolicy{
    @Id
    private int id;
    @Column(name = "dest_ip_start" )
    private String dest_ip_start;
    @Column(name = "dest_ip_end" )
    private String dest_ip_end;
    @Column(name = "dest_ip_cidr" )
    private int dest_ip_cidr;
    @Column(name = "dest_ip_type" )
    private String dest_ip_type;
    @Column(name = "dest_ip_start_int" )
    private long dest_ip_start_int;
    @Column(name = "dest_ip_end_int" )
    private long dest_ip_end_int;
    @Column(name = "json_services" )
    private String json_services;
    @Column(name = "rule_name" )
    private String rule_name;
    @Column(name = "rule_number" )
    private String rule_number;
}
