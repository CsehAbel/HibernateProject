package chapter03.application;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class Union_Exists_Map_Example_Subclass_2 extends Union_Exists_Map_Example_Subclass{

    public Map<Union_Alternative,ExistsAsHost> result_map;

    public Union_Exists_Map_Example_Subclass_2() {
        super();
        this.result_map = this.provideUnionExistsMapReduced(this.getResult_unionExistsMap());
    }

    private Map<Union_Alternative, ExistsAsHost> provideUnionExistsMapReduced(Map<Union_Alternative, Map<Long, ExistsAsHost>> resourceDstIpExistsMap) {
        //for each Union_Alternative in resourceDstIpExistsMap, reduce the Map<Long, ExistsAsHost> to a single ExistsAsHost
        //the ExistsAsHost will have a list of all the src_ip's that exist and a list of all the src_ip's that do not exist
        Map<Union_Alternative, ExistsAsHost> unionExistsMapReduced = new HashMap<>();

        for (Map.Entry<Union_Alternative, Map<Long, ExistsAsHost>> entry : resourceDstIpExistsMap.entrySet()) {
            Union_Alternative unionAlternative = entry.getKey();
            Map<Long, ExistsAsHost> existsAsHostMap = entry.getValue();

            List<String> exists = new ArrayList<>();
            List<String> notexists = new ArrayList<>();

            for (Map.Entry<Long, ExistsAsHost> existsAsHostEntry : existsAsHostMap.entrySet()) {
                ExistsAsHost existsAsHost = existsAsHostEntry.getValue();
                exists.addAll(existsAsHost.getExists());
                notexists.addAll(existsAsHost.getNotexists());
            }

            ExistsAsHost reducedExistsAsHost = new ExistsAsHost(exists, notexists);
            unionExistsMapReduced.put(unionAlternative, reducedExistsAsHost);
        }

        return unionExistsMapReduced;
    }

    //
}
