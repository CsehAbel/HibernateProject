package chapter03.application;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.stream.Collectors;

import org.testng.annotations.Test;

@lombok.Data
public class Union_Exists_Map_Example_Subclass extends Union_Exists_Map_Example {

    private Map<Long, Long> eagle_map;

    public Union_Exists_Map_Example_Subclass() {
        super();
    }

    @Test
    public void testUnion_Exist_Map_Example_Subclass() {
        System.out.println("map");
    }

    @Override
    public Map<Union,Map<Long,ExistsAsHost>> provideUnionExistsMap(){
        this.eagle_map = new Eagle_Map_Example().getMap();
        List<Union> newInnerJoin = this.filteredNewInnerJoin();
        Map<Union,Map<Long,ExistsAsHost>> unionExistMap = this.provideUnionExistsMap(newInnerJoin);
        return unionExistMap;
    }

    public List<Union> filteredNewInnerJoin() {

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
