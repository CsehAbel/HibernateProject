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


    public void writeUnionToJsonFile(Map<Union, Map<Long, ExistsAsHost>> result_unionExistsMap, String filename, String path)
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {// ,
                                                                                                // Map<String,Set<String>>
                                                                                                // history_iug_map) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("first");

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

        //create an iterator of map entries
        Iterator<Map.Entry<Union, Map<Long, ExistsAsHost>>> it = result_unionExistsMap.entrySet().iterator();

        com.google.gson.JsonObject jsonObject = new com.google.gson.JsonObject();
        com.google.gson.JsonArray array = new com.google.gson.JsonArray();
        jsonObject.add("inBoth", array);

        while(it.hasNext()) {
            Map.Entry<Union, Map<Long, ExistsAsHost>> entry = it.next();
            com.google.gson.JsonObject innerJsonObject = new com.google.gson.JsonObject();
            array.add(innerJsonObject);

            String key = entry.getKey().getRlst().getStart() + "-" + entry.getKey().getRlst().getEnd() + "_"
                    + entry.getKey().getRlst().getApp_id();
            com.google.gson.JsonArray arrayOfTwo = new com.google.gson.JsonArray();
            innerJsonObject.add(key, arrayOfTwo);
            
            com.google.gson.JsonObject innerInnerJsonObject = new com.google.gson.JsonObject();
            com.google.gson.JsonObject sourcesExistsJsonObject = new com.google.gson.JsonObject();
            arrayOfTwo.add(innerInnerJsonObject);
            arrayOfTwo.add(sourcesExistsJsonObject);

            

            

            Rlst rlst = entry.getKey().getRlst();
            Fwpolicy fwpolicy = entry.getKey().getFwpolicy();

            for (int j = 0; j < methodsForRlst.size(); j++) {
                Method method = methodsForRlst.get(j);
                String name = method.getName();
                name = name.substring(3);
                name = name.substring(0, 1).toLowerCase() + name.substring(1);
                var value = method.invoke(rlst);
                
                innerInnerJsonObject.addProperty(name, value == null ? "null" : value.toString());

            }
            for (int j = 0; j < methodsForFwpolicy.size(); j++) {
                Method method = methodsForFwpolicy.get(j);
                String name = method.getName();
                name = name.substring(3);
                name = name.substring(0, 1).toLowerCase() + name.substring(1);
                var value = method.invoke(fwpolicy);

                innerInnerJsonObject.addProperty(name, value.toString());
            }

            Map<Long, ExistsAsHost> map = entry.getValue();
            Iterator<Map.Entry<Long, ExistsAsHost>> it2 = map.entrySet().iterator();
            while(it2.hasNext()) {
                
                Map.Entry<Long, ExistsAsHost> entry2 = it2.next();

                com.google.gson.JsonArray array_exists_notexists = new com.google.gson.JsonArray();
                String ip = intToIp(entry2.getKey());
                sourcesExistsJsonObject.add(ip, array_exists_notexists);
                
                com.google.gson.JsonArray array_notexists = new com.google.gson.JsonArray();
                com.google.gson.JsonArray array_exists = new com.google.gson.JsonArray();
                array_exists_notexists.add(array_notexists);
                array_exists_notexists.add(array_exists);
                
                Iterator<String> it3 = entry2.getValue().getNotexists().iterator();
                
                while(it3.hasNext()) {
                    String notexists = it3.next();
                    array_notexists.add(notexists);
                }

                Iterator<String> it4 = entry2.getValue().getExists().iterator();
                
                while(it4.hasNext()) {
                    String exists = it4.next();
                    array_exists.add(exists);
                }

            }
            
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
