package chapter03.application;

import chapter03.hibernate.Rlst;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class Union_Exists_Map_Example_Subclass_3 extends Union_Exists_Map_Example_Subclass_2{

    public List<App_Exists> result_map_2;

    public Union_Exists_Map_Example_Subclass_3(){
        super();
        this.result_map_2 = this.provideUnionExistsMapReduced_2(this.getResult_map());
    }

    //a Record that has app_id,app_name,ExistsAsHost.exists,ExistsAsHost.notexists
    public record App_Exists(String dst_ip, String exists, String notexists, String app_name, String app_id){
    }

    private List<App_Exists> provideUnionExistsMapReduced_2(Map<Union_Alternative, ExistsAsHost> resultUnionExistsMap) {
        //for each Union_Alternative in resultUnionExistsMap, reduce it to a single App_Exists and return a List of App_Exists
        //the App_Exists will have a list of all the src_ip's that exist and a list of all the src_ip's that do not exist
        return resultUnionExistsMap.entrySet().stream()
                .map(entry -> {
                    Union_Alternative unionAlternative = entry.getKey();
                    ExistsAsHost existsAsHost = entry.getValue();
                    List<String> exists = existsAsHost.getExists();
                    List<String> notexists = existsAsHost.getNotexists();

                    String app_id = concatenateRlstAppIds(unionAlternative.getRlstSet());
                    String app_name = concatenateRlstAppNames(unionAlternative.getRlstSet());
                    String exists_concatenated = concatenateExists(exists);
                    String notexists_concatenated = concatenateExists(notexists);
                    String ip_range = unionAlternative.getFwpolicy().getDest_ip_start()+"_"+unionAlternative.getFwpolicy().getDest_ip_end();
                    String ip_range_with_cidr = unionAlternative.getFwpolicy().getDest_ip_start()+"_"+unionAlternative.getFwpolicy().getDest_ip_end()+"/"+unionAlternative.getFwpolicy().getDest_ip_cidr();

                    return new App_Exists(ip_range, exists_concatenated, notexists_concatenated, app_name, app_id);
                })
                .collect(Collectors.toList());

    }

    public String concatenateRlstAppIds(Set<Rlst> rlsts){
        return rlsts.stream()
                .map(Rlst::getApp_id)
                .distinct()
                .collect(Collectors.joining("_"));
    }

    public String concatenateRlstAppNames(Set<Rlst> rlsts){
        return "&&"+rlsts.stream()
                .map(Rlst::getApp_name)
                .distinct()
                .collect(Collectors.joining("&&"));
    }

    public String concatenateExists(List<String> exists){
        return exists.stream()
                .distinct()
                .collect(Collectors.joining(","));
    }
}
