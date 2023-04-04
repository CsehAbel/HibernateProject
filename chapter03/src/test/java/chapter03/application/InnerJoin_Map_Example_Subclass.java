package chapter03.application;

import chapter03.hibernate.Fwpolicy;
import chapter03.hibernate.Rlst;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.testng.annotations.Test;



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
    public Map<Union_Alternative,Map<Long,Set<Long>>> provideInnerJoinMap(){
        this.eagle_map = new Eagle_Map_Example().getMap();
        Map<Fwpolicy,Set<Rlst>> newInnerJoin = this.filteredNewInnerJoin();
        Map<Union_Alternative,Map<Long,Set<Long>>> innerJoinMap = this.provideInnerJoinMap(newInnerJoin);
        return innerJoinMap;
    }

    public Map<Fwpolicy, Set<Rlst>> filteredNewInnerJoin() {

        Map<Fwpolicy,Set<Rlst>> toFilter = this.resource_innerJoin;

        //iterate through the entries of toFilter
        //and check if entry keys fwpolicy has an ip_start_int that is
        //greater than or equal to a eagle_map entry's key and an ip_end_int that is less than or equal to the same eagle_map entry's key
        //if so, filter out the entry from toFilter
        Map<Fwpolicy,Set<Rlst>> newInnerJoin = toFilter.entrySet().stream().filter(e -> {
            long ip_start_int = e.getKey().getDest_ip_start_int();
            long ip_end_int = e.getKey().getDest_ip_end_int();
            return !this.eagle_map.entrySet().stream().anyMatch(e2 -> {
                long key = e2.getKey();
                long value = e2.getValue();
                return ip_start_int >= key && ip_end_int <= value;
            });
        }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        return newInnerJoin;
    
    }
}
