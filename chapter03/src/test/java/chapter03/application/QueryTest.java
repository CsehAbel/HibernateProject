package chapter03.application;

import chapter03.hibernate.*;
import org.testng.annotations.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class QueryTest {
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

    private Map<String, Set<String>> get_ipunique_map() {
        String parameter = "";
        var res_list = service.selectAll(IP_Unique.class);
        Map<String, Set<String>> map = new HashMap<>();
        for (int i = 0; i < res_list.size(); i++) {
            IP_Unique iu = res_list.get(i);
            var key = iu.getDst_ip();
            var value = iu.getSrc_ip();
            fillMap(map, key, value);
        }
        return map;
    }

    public void createTableIpUniqueG(){
        var map = get_ipunique_map();
        Iterator<Map.Entry<String,Set<String>>> it=map.entrySet().iterator();
        while(it.hasNext()){
            var entry=it.next();
            var key=entry.getKey();
            var val=entry.getValue();
            IP_Unique_G iug=new IP_Unique_G(key,val);
            service.save(iug);
        }
    }

    //@Test
    public void selectAllIPUniqueG(){
        var list=service.selectAll(IP_Unique_G.class);
        "".isEmpty();
    }
    private EnumMap<STPortsGKeys, Map<String, Set<String>>> get_stports_map() {
        var res_list = service.selectAll(ST_Ports.class);
        Map<String, Set<String>> mapPorts = new HashMap<>();
        Map<String, Set<String>> mapRuleName = new HashMap<>();
        Map<String, Set<String>> mapRuleNumber = new HashMap<>();
        for (int i = 0; i < res_list.size(); i++) {
            var stp = res_list.get(i);
            var key = stp.getSt_dest_ip();
            fillMap(mapPorts, key, stp.getSt_port());
            fillMap(mapRuleName, key, stp.getRule_name());
            fillMap(mapRuleNumber, key, stp.getRule_number());
        }
        EnumMap<STPortsGKeys, Map<String, Set<String>>> newm = new EnumMap<>(STPortsGKeys.class);
        newm.put(STPortsGKeys.ports, mapPorts);
        newm.put(STPortsGKeys.rule_name, mapRuleName);
        newm.put(STPortsGKeys.rule_number, mapRuleNumber);
        return newm;
    }


    public void createTableSTPortsG(){
        var map=get_stports_map();
        Iterator<Map.Entry<String,Set<String>>> it=map.get(STPortsGKeys.ports).entrySet().iterator();
        while(it.hasNext()) {
            var dest_ip=it.next().getKey();
            ST_Ports_G stpg = new ST_Ports_G(dest_ip,
                    map.get(STPortsGKeys.ports).get(dest_ip),
                    map.get(STPortsGKeys.rule_name).get(dest_ip),
                    map.get(STPortsGKeys.rule_number).get(dest_ip));
            service.save(stpg);
        }
    }
    //@Test
    public void selectAllSTPortsG(){
        var list=service.selectAll(ST_Ports_G.class);
        "".isEmpty();
    }

    private <T> void fillMap(Map<T, Set<String>> map, T key, String value) {
        if (value == null) {
            value = "null";
        }
        String finalValue = value;
        if (map.get(key) != null) {
            map.compute(key, (k, v) -> Stream.concat(v.stream(), Set.of(finalValue).stream()).collect(Collectors.toSet()));
        } else {
            var set = new HashSet<String>();
            set.add(value);
            map.put(key, set);
        }
    }

    private Map<String, Map<RlstKey, Set<String>>> get_rlst_map() {
        var res_list = service.selectAll(Rlst.class);
        Map<RlstKey, Set<String>> mapAppName = new HashMap<>();
        Map<RlstKey, Set<String>> mapComment = new HashMap<>();
        Map<RlstKey, Set<String>> mapFqdn = new HashMap<>();
        Map<RlstKey, Set<String>> mapTSA = new HashMap<>();
        Map<RlstKey, Set<String>> mapAppRequestor = new HashMap<>();
        for (int i = 0; i < res_list.size(); i++) {
            res_list.get(i);
            var rlst = res_list.get(i);
            if (rlst.getChange_type() == "deleted") {
                continue;
            }
            var key1 = rlst.getIps();
            var key2 = rlst.getApp_id();
            var compoundkey = new RlstKey(key1, key2);
            String app_name=rlst.getApplication_name();
            String truncatedAppName=(app_name!=null?app_name.substring(0,Math.min(254,app_name.length())):null);
            fillMap(mapAppName, compoundkey, truncatedAppName);
            fillMap(mapComment, compoundkey, rlst.getComment());
            fillMap(mapFqdn, compoundkey, rlst.getFqdn());
            fillMap(mapTSA, compoundkey, rlst.getTsa_expiration_date());
            fillMap(mapAppRequestor, compoundkey, rlst.getApplication_requester());
        }
        Map<String, Map<RlstKey, Set<String>>> newm = new HashMap<>();
        newm.put("ports", mapComment);
        newm.put("app_name", mapAppName);
        newm.put("fqdn", mapFqdn);
        newm.put("tsa", mapTSA);
        newm.put("app_requestor", mapAppRequestor);
        return newm;
    }

    public void createTableRlst_G(){
        var newm= get_rlst_map();
        var map1 = newm.get("ports");
        var map2 = newm.get("app_requestor");
        var map3 = newm.get("tsa");
        var map4 = newm.get("fqdn");
        var map5 = newm.get("app_name");
        Iterator<Map.Entry<RlstKey, Set<String>>> it = map1.entrySet().iterator();
        while(it.hasNext()){
            var entry= it.next();
            var key = entry.getKey();
            var key_app_id=entry.getKey().getApp_id();
            var key_dest_ip = entry.getKey().getDst_ip();
            Rlst_G rlst_g=new Rlst_G(key_dest_ip,key_app_id,
                    map1.get(key),map2.get(key),map3.get(key),map4.get(key),map5.get(key));
            service.save(rlst_g);
        }
        System.out.println("Table and linking tables created");
    }

    //

}
