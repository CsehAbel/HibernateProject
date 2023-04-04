package chapter03.application;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.Iterator;

import org.testng.annotations.Test;

import chapter03.hibernate.Fwpolicy;
import chapter03.hibernate.Rlst;


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
    public Map<Union_Alternative,Map<Long,Set<String>>> provideUnionExistsMap(){
        this.eagle_map = new Eagle_Map_Example().getMap();
        Map<Fwpolicy, Set<Rlst>> newInnerJoin = this.filteredNewInnerJoin();
        Map<Union_Alternative,Map<Long,Set<String>>> unionExistMap = this.provideUnionExistsMap(newInnerJoin);
        return unionExistMap;
    }

    public Map<Fwpolicy, Set<Rlst>> filteredNewInnerJoin() {

        Map<Fwpolicy, Set<Rlst>> tobekept = new HashMap<>();
        List<Fwpolicy> tobeexcluded = new ArrayList<Fwpolicy>();

        Iterator<Map.Entry<Fwpolicy,Set<Rlst>>> it = this.resource_innerJoin.entrySet().iterator();
        while(it.hasNext()){
            Fwpolicy fwpolicy = it.next().getKey();
            long ip_start_int = fwpolicy.getDest_ip_start_int();
            long ip_end_int = fwpolicy.getDest_ip_end_int();

            if(this.eagle_map.entrySet().stream().anyMatch(e2 -> {
                long key = e2.getKey();
                long value = e2.getValue();
                return ip_start_int >= key && ip_end_int <= value;
            })){
                tobeexcluded.add(fwpolicy);
            }
        }

        //fill tobekept with all entries from this.resource_innerJoin that are not in tobeexcluded
        tobekept = this.resource_innerJoin.entrySet().stream().filter(e -> !tobeexcluded.contains(e.getKey())).collect(Collectors.toMap(Map.Entry<Fwpolicy, Set<Rlst>>::getKey, Map.Entry<Fwpolicy, Set<Rlst>>::getValue));

        return tobekept;
    }
    
}
