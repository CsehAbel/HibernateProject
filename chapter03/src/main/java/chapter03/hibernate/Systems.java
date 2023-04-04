package chapter03.hibernate;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Data;

//create a class to represent the table
// CREATE TABLE `systems` (
//   `index` bigint DEFAULT NULL,
//   `name` text,
//   `start` text,
//   `end` text,
//   `cidr` bigint DEFAULT NULL,
//   `type` text,
//   `start_int` bigint DEFAULT NULL,
//   `end_int` bigint DEFAULT NULL,
//   KEY `ix_systems_index` (`index`)
// ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci

@Entity @Data
public class Systems {
    
    @Id
    private Long index;
    private String name;
    private String start;
    private String end;
    private Long cidr;
    private String type;
    private Long start_int;
    private Long end_int;
}
