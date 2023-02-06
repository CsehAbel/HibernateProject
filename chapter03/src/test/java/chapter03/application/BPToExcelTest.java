package chapter03.application;

import org.testng.annotations.Test;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
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

public class BPToExcelTest {

    // Union_Exists_Map_Example_Subclass union_Exists_Map_Example_Subclass;
    protected Union_Exists_Map_Example union_Exists_Map_Example;
    protected String path = "C:\\Users\\z004a6nh\\IdeaProjects\\HibernateProject\\chapter03\\reports";

    public void writeUnionToJsonFile(
            Map<Union_Alternative, Map<Long, ExistsAsHost>> result_unionExistsMap, String filename, String path)
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {

        // create an iterator of map entries
        Iterator<Map.Entry<Union_Alternative, Map<Long, ExistsAsHost>>> it = result_unionExistsMap.entrySet()
                .iterator();

        com.google.gson.JsonObject jsonObject = new com.google.gson.JsonObject();
        com.google.gson.JsonArray array = new com.google.gson.JsonArray();
        jsonObject.add("inBoth", array);

        while (it.hasNext()) {
            Map.Entry<Union_Alternative, Map<Long, ExistsAsHost>> entry = it.next();
            com.google.gson.JsonObject innerJsonObject = new com.google.gson.JsonObject();
            array.add(innerJsonObject);

            String keys_ip = entry.getKey().getFwpolicy().getDest_ip_start() + "_"
                    + entry.getKey().getFwpolicy().getDest_ip_end();
            String keys_appid = entry.getKey().getRlstSet().stream().map(Rlst::getApp_id).reduce("", (a, b) -> a + "_" + b);
            String key = keys_ip + "_" + keys_appid;
            com.google.gson.JsonArray arrayOfTwo = new com.google.gson.JsonArray();

            innerJsonObject.add(key, arrayOfTwo);

            com.google.gson.JsonObject union_aObj = create_union_aObj(entry);
            arrayOfTwo.add(union_aObj);

            com.google.gson.JsonObject existsObj = create_existsObj(entry);
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

    private com.google.gson.JsonObject create_union_aObj(Map.Entry<Union_Alternative, Map<Long, ExistsAsHost>> entry) {
        com.google.gson.JsonObject union_aObj = new com.google.gson.JsonObject();
        Set<Rlst> rlst = entry.getKey().getRlstSet();
        com.google.gson.JsonArray arrayForRlst = create_rlsts_array(rlst);
        union_aObj.add("rlsts", arrayForRlst);

        Fwpolicy fwpolicy = entry.getKey().getFwpolicy();
        com.google.gson.JsonObject fwpolicyJsonObject = create_fwpolicy_object(fwpolicy);
        union_aObj.add("fwpolicy", fwpolicyJsonObject);
        return union_aObj;
    }

    private com.google.gson.JsonObject create_existsObj(Map.Entry<Union_Alternative, Map<Long, ExistsAsHost>> entry) {
        com.google.gson.JsonObject existsObj = new com.google.gson.JsonObject();
        Map<Long, ExistsAsHost> map = entry.getValue();
        Iterator<Map.Entry<Long, ExistsAsHost>> it2 = map.entrySet().iterator();
        while (it2.hasNext()) {

            Map.Entry<Long, ExistsAsHost> entry2 = it2.next();

            com.google.gson.JsonArray array_exists_notexists = new com.google.gson.JsonArray();
            String ip = intToIp(entry2.getKey());
            existsObj.add(ip, array_exists_notexists);

            ExistsAsHost existsAsHost = entry2.getValue();

            com.google.gson.JsonArray array_notexists = new com.google.gson.JsonArray();
            Iterator<String> it3 = existsAsHost.getNotexists().iterator();
            while (it3.hasNext()) {
                String notexists = it3.next();
                array_notexists.add(notexists);
            }
            String keyForNotExists = existsAsHost.getNotexists().size() + "_existsNot";
            com.google.gson.JsonObject notexistsObj = new com.google.gson.JsonObject();
            notexistsObj.add(keyForNotExists, array_notexists);
            array_exists_notexists.add(notexistsObj);

            com.google.gson.JsonArray array_exists = new com.google.gson.JsonArray();
            Iterator<String> it4 = existsAsHost.getExists().iterator();
            while (it4.hasNext()) {
                String exists = it4.next();
                array_exists.add(exists);
            }
            String keyForExists = existsAsHost.getExists().size() + "_existsYes";
            com.google.gson.JsonObject existsYesObj = new com.google.gson.JsonObject();
            existsYesObj.add(keyForExists, array_exists);
            array_exists_notexists.add(existsYesObj);

        }
        return existsObj;
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
                        throw new NullPointerException();
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
            writeUnionToJsonFile(union_Exists_Map_Example.result_unionExistsMap, "energyx_bp_%d.json", path);
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
