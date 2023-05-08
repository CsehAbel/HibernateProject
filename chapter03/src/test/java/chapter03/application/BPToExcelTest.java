package chapter03.application;

import org.testng.annotations.Test;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
//ArrayList
import java.util.ArrayList;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
//reflections
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

//GSON
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
//JsonParser
import com.google.gson.JsonParser;

import chapter03.hibernate.Fwpolicy;
import chapter03.hibernate.Rlst;

//Apache POI
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFCell;

import chapter03.util.LoggingUtil;
import java.util.logging.Logger;
import java.util.logging.Level;

public class BPToExcelTest {

    // Union_Exists_Map_Example_Subclass union_Exists_Map_Example_Subclass;
    protected Union_Exists_Map_Example union_Exists_Map_Example;
    protected String path = "C:\\Users\\z004a6nh\\IdeaProjects\\HibernateProject\\chapter03\\reports";

    public void writeUnionToJsonFile(
            Map<Fwpolicy, Map<Long, Set<String>>> result_unionExistsMap, String filename, String path)
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {

        // create an iterator of map entries
        Iterator<Map.Entry<Fwpolicy, Map<Long, Set<String>>>> it = result_unionExistsMap.entrySet()
                .iterator();

        com.google.gson.JsonObject jsonObject = new com.google.gson.JsonObject();
        com.google.gson.JsonArray array = new com.google.gson.JsonArray();
        jsonObject.add("inBoth", array);

        while (it.hasNext()) {
            Map.Entry<Fwpolicy, Map<Long, Set<String>>> entry = it.next();
            com.google.gson.JsonObject innerJsonObject = new com.google.gson.JsonObject();
            array.add(innerJsonObject);

            String key = concatStartEndIP(entry);
            
            com.google.gson.JsonArray arrayOfTwo = new com.google.gson.JsonArray();

            innerJsonObject.add(key, arrayOfTwo);

            com.google.gson.JsonObject firewall_object = create_fwpolicy_object(entry.getKey());
            arrayOfTwo.add(firewall_object);

            com.google.gson.JsonObject existsObj = this.create_existsObj(entry);
            arrayOfTwo.add(existsObj);
        }

        String uglyJsonString = jsonObject.toString();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonElement je = JsonParser.parseString(uglyJsonString);
        String prettyJsonString = gson.toJson(je);
        String file_name = String.format(filename, result_unionExistsMap.size());
        try {
            FileWriter writer = new FileWriter(path + "\\" + file_name);
            writer.write(prettyJsonString);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Done writing to file: " + file_name);
    }

    private String concatStartEndIP(Map.Entry<Fwpolicy, Map<Long, Set<String>>> entry) {
        String keys_ip = entry.getKey().getDest_ip_start() + "_"
                + entry.getKey().getDest_ip_end();
        //collect the app_id's into a Set
        //concatenate the app_id's into a String
        // Set<String> app_id_set = entry.getKey().getRlstSet().stream().map(Rlst::getApp_id).collect(HashSet::new, HashSet::add, HashSet::addAll);
        // String keys_appid = app_id_set.stream().reduce("", (a, b) -> a + "_" + b);
        // String key = keys_ip + "_" + keys_appid;
        // return key;
        String key = keys_ip;
        return key;
    }

    private com.google.gson.JsonObject create_existsObj(Map.Entry<Fwpolicy, Map<Long, Set<String>>> entry) {
        com.google.gson.JsonObject bucketsObj = new com.google.gson.JsonObject();
        Map<Long, Set<String>> map = entry.getValue();
        
        com.google.gson.JsonArray buckets = new com.google.gson.JsonArray();
        

        Iterator<Map.Entry<Long, Set<String>>> it2 = map.entrySet().iterator();
        Set<String> mergedBuckets = new HashSet<>();

        while (it2.hasNext()) {

            Map.Entry<Long, Set<String>> entry2 = it2.next();

            Set<String> setOfBuckets = entry2.getValue();
            
            mergedBuckets.addAll(setOfBuckets);
        }

        for (String each : mergedBuckets) {
            buckets.add(each);
        }

        bucketsObj.add(String.format("%d_buckets",mergedBuckets.size()), buckets);

        return bucketsObj;
    }

    private com.google.gson.JsonObject create_fwpolicy_object(Fwpolicy fwpolicy) {

        // get each field of the object fwpolicy using reflection
        List<Method> methodsForFwpolicy = new ArrayList<>();
        for (Method method : Fwpolicy.class.getDeclaredMethods()) {
            if (Modifier.isPublic(method.getModifiers())
                    && method.getParameterTypes().length == 0
                    && method.getReturnType() != void.class
                    && method.getName().startsWith("get")) {
                methodsForFwpolicy.add(method);
            }
        }

        com.google.gson.JsonObject fwpolicyJsonObject = new com.google.gson.JsonObject();
        for (Method method : methodsForFwpolicy) {
            String name = method.getName();
            name = name.substring(3);
            name = name.substring(0, 1).toLowerCase() + name.substring(1);
            Object value=null;
            try {
                value = method.invoke(fwpolicy);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            fwpolicyJsonObject.addProperty(name, value.toString());
        }
        return fwpolicyJsonObject;
    }

    private com.google.gson.JsonArray create_rlsts_array(Set<Rlst> rlstSet) {

        // get each field of the object rlst using reflection
        List<Method> methodsForRlst = new ArrayList<>();
        /// get each field of the object rlst using reflection
        for (Method method : Rlst.class.getDeclaredMethods()) {
            if (Modifier.isPublic(method.getModifiers())
                    && method.getParameterTypes().length == 0
                    && method.getReturnType() != void.class
                    && method.getName().startsWith("get")) {
                methodsForRlst.add(method);
            }
        }
        com.google.gson.JsonArray arrayForRlst = new com.google.gson.JsonArray();
        for (Rlst aRlst : rlstSet) {

            // new com.google.gson.JsonObject() for each rlst
            com.google.gson.JsonObject rlstJsonObject = new com.google.gson.JsonObject();
            arrayForRlst.add(rlstJsonObject);
            for (Method method : methodsForRlst) {
                String name = method.getName();
                name = name.substring(3);
                name = name.substring(0, 1).toLowerCase() + name.substring(1);
                try {
                    var value = method.invoke(aRlst);
                    if (value == null){
                        Logger logger2 = LoggingUtil.initLogger("npe2", LoggingUtil.path + "\\" + "rlstnullvalues.xml");
                        //throw new NullPointerException();
                        //log the null value'‚ reason, the object, and which getter resulted in null
                        String message = String.format("null value for %s in %s", name, aRlst.toString());
                        logger2.log(Level.WARNING, message);
                    }
                    rlstJsonObject.addProperty(name, value == null ? "null" : value.toString());
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return arrayForRlst;
    }

    public String intToIp(long ipv4address) {
        long ip = ipv4address;
        // return String.format("%d.%d.%d.%d", (ip & 0xff), (ip >> 8 & 0xff), (ip >> 16
        // & 0xff), (ip >> 24 & 0xff));
        // do the same but with endianess little endian
        return String.format("%d.%d.%d.%d", (ip >> 24 & 0xff), (ip >> 16 & 0xff), (ip >> 8 & 0xff), (ip & 0xff));
    }

    @Test
    public void createBPXlsx() throws SecurityException, IOException {

        union_Exists_Map_Example = new Union_Exists_Map_Example_Subclass();
        // write the report2 list to an excel file
        try {
            writeUnionToJsonFile(union_Exists_Map_Example.result_unionExistsMap, "energyy_bp_%d.json", path);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // list_of_list_obj = list_of_list_obj.stream()
        // .filter(x -> x.rlst().stream().filter(y ->
        // y.getIp().equals("139.23.230.92")).findFirst()
        // .orElse(null)==null).collect(Collectors.toList());
    }
}
