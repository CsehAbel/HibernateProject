package chapter03.application;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.testng.annotations.Test;

import java.util.List;

@lombok.Data
public class InnerJoin_Map_Example_Subclass extends InnerJoin_Map_Example {

    private Map<Long, Long> eagle_map;

    public InnerJoin_Map_Example_Subclass() {
        super();
    }

    @Test
    public void testInnerJoin_Map_Example_Subclass() {
        System.out.println("map");
    }

    @Override
    public Map<Union,Map<Long,Set<Long>>> provideInnerJoinMap(){
        this.eagle_map = new Eagle_Map_Example().getMap();
        List<Union> newInnerJoin = this.filteredNewInnerJoin();
        Map<Union,Map<Long,Set<Long>>> innerJoinMap = this.provideInnerJoinMap(newInnerJoin);
        return innerJoinMap;
    }

    public List<Union> filteredNewInnerJoin() {

        //iterate through innerJoin
        //and check if innerJoin contains a union with a fwpolicy with an ip_start_int that is
        //greater than or equal to a eagle_map entry's key and an ip_end_int that is less than or equal to the same eagle_map entry's key
        //if so, add the union to the tobeexcluded list
        List<Union> tobeexcluded = this.resource_innerJoin.stream().filter(u -> {
            long ip_start_int = u.getFwpolicy().getDest_ip_start_int();
            long ip_end_int = u.getFwpolicy().getDest_ip_end_int();
            return this.eagle_map.entrySet().stream().anyMatch(e -> {
                long key = e.getKey();
                long value = e.getValue();
                return ip_start_int >= key && ip_end_int <= value;
            });
        }).collect(Collectors.toList());

        //remove all unions from innerJoin that are in tobeexcluded, in an immutable way, creating a new list
        List<Union> newInnerJoin = this.resource_innerJoin.stream().filter(u -> !tobeexcluded.contains(u)).collect(Collectors.toList());
        return newInnerJoin;
    
    }
}