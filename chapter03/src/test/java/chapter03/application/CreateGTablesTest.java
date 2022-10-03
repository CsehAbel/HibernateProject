package chapter03.application;

import chapter03.hibernate.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CreateGTablesTest {
    QueryService service = new ImplementationQueryService();
    public enum STPortsGKeys{
        ports(1), rule_name(2),rule_number(3);

        int property;
        STPortsGKeys(int property) {
            this.property=property;
        }
    }

    public class RlstKey {
        String dst_ip;
        String app_id;

        public RlstKey() {
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            RlstKey rlstKey = (RlstKey) o;
            return dst_ip.equals(rlstKey.dst_ip) && app_id.equals(rlstKey.app_id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(dst_ip, app_id);
        }

        public String getDst_ip() {
            return dst_ip;
        }

        public void setDst_ip(String dst_ip) {
            this.dst_ip = dst_ip;
        }

        public String getApp_id() {
            return app_id;
        }

        public void setApp_id(String app_id) {
            this.app_id = app_id;
        }

        public RlstKey(String dst_ip, String app_id) {
            this.dst_ip = dst_ip;
            this.app_id = app_id;
        }
    }

    //Query using IP_Unique.class and create two new entities from Map<String, List<String>>
    //create new objects: new IP_Unique_G() and persist them and associate them to the IP_Unique.class

    public List<ST_Ports_G> get_stports_map(String param) {
        List<ST_Ports_G> list_stports_g=new ArrayList<>();
        //ToDO: generate Maps from NativeQuery
        var res_list = service.querySTPortsWhere(ST_Ports.class,param);
        Map<String, Set<String>> mapPorts = new HashMap<>();
        Map<String, Set<String>> mapRuleName = new HashMap<>();
        Map<String, Set<String>> mapRuleNumber = new HashMap<>();
        for (int i = 0; i < res_list.size(); i++) {
            ST_Ports stp = res_list.get(i);
            String key = stp.getSt_dest_ip();
            fillMap(mapPorts, key, stp.getSt_port());
            fillMap(mapRuleName, key, stp.getRule_name());
            fillMap(mapRuleNumber, key, stp.getRule_number());
        }
        Iterator<Map.Entry<String,Set<String>>> it=mapPorts.entrySet().iterator();
        while(it.hasNext()) {
            var dest_ip=it.next().getKey();
            ST_Ports_G stpg = new ST_Ports_G(dest_ip,
                    mapPorts.get(dest_ip),
                    mapRuleName.get(dest_ip),
                    mapRuleNumber.get(dest_ip));
            list_stports_g.add(stpg);
        }
        return list_stports_g;
    }

    //fill Map with key and value


    private <T> void fillMap(Map<T, Set<String>> map, T key, String value) {
        if (value == null) {
            value = "null";
        }
        String finalValue = value;
        if (map.get(key) != null) {
            //if key exists, add value to the set
            map.compute(key, (k, v) -> Stream.concat(v.stream(), Set.of(finalValue).stream()).collect(Collectors.toSet()));
        } else {
            var set = new HashSet<String>();
            set.add(value);
            map.put(key, set);
        }
    }

    public Map<RlstKey,Rlst_G> get_rlst_gList(String param) {
        Map<RlstKey,Rlst_G> rlst_gList=new HashMap<>();

        var res_list = service.queryRlstWhere(Rlst.class,param);
        Map<RlstKey, Set<String>> mapAppName = new HashMap<>();
        Map<RlstKey, Set<String>> mapPorts = new HashMap<>();
        Map<RlstKey, Set<String>> mapComment = new HashMap<>();
        Map<RlstKey, Set<String>> mapFqdn = new HashMap<>();
        Map<RlstKey, Set<String>> mapTSA = new HashMap<>();
        for (int i = 0; i < res_list.size(); i++) {
            res_list.get(i);
            var rlst = res_list.get(i);
            if (rlst.getChange_type() == "deleted") {
                continue;
            }
            var key1 = rlst.getIps();
            var key2 = rlst.getApp_id();
            var compoundkey = new RlstKey(key1, key2);
            String app_name = rlst.getApplication_name();
            String truncatedAppName = (app_name != null ? app_name.substring(0, Math.min(254, app_name.length())) : null);
            fillMap(mapAppName, compoundkey, truncatedAppName);
            fillMap(mapComment, compoundkey, rlst.getComment());
            fillMap(mapFqdn, compoundkey, rlst.getFqdns());
            fillMap(mapTSA, compoundkey, rlst.getTsa_expiration_date());
            fillMap(mapPorts, compoundkey, rlst.getProtocol_type_port());
        }
        //
        Iterator<Map.Entry<RlstKey,Set<String>>> it=mapComment.entrySet().iterator();
        while (it.hasNext()){
            var key=it.next().getKey();
            var key_app_id = key.getApp_id();
            var key_dest_ip = key.getDst_ip();
            Rlst_G rlst_g = new Rlst_G(key_dest_ip, key_app_id,
                    mapPorts.get(key),
                    mapTSA.get(key),
                    mapFqdn.get(key),
                    mapAppName.get(key),
                    mapComment.get(key)
                    );
            rlst_gList.put(new RlstKey(key_dest_ip,key_app_id),rlst_g);
        }

        return rlst_gList;
    }
}
