package chapter03.application;

import chapter03.hibernate.Fwpolicy;
import chapter03.hibernate.Rlst;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.logging.FileHandler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;


public class ReportToExcelTest {
    QueryService service = new QueryService();

    public record Union(Rlst rlst, Fwpolicy fwpolicy){
    }

    //used for returning the result of supplyInnerJoin()
    //stores  List<Rlst> notInPolicy, List<Fwpolicy> notInRlst, List<Union> inBoth
    public record Report(List<Rlst> notInPolicy, List<Fwpolicy> notInRlst, List<Union> inBoth){
    }

    //stores List<String> exists, List<String> notexists, Union union
    public record Report2(String destIpString, List<String> exists, List<String> notexists, Union union){
    }

    public Report supplyReport() {

        List<Rlst> rlstList = service.queryRlst();
        int rlstSize = rlstList.size();
        List<Fwpolicy> fwpolicyList = service.queryFwpolicy();
        int fwpolicySize = fwpolicyList.size();
        List<Rlst> notInPolicy = new ArrayList<>();
        List<Fwpolicy> notInRuleset = new ArrayList<>();
        //fill readyToExport with inner join
        //SELECT * FROM rlstList INNER JOIN fwpolicyList
        //ON rlstList.start_int=fwpolicyList.dest_ip_start_int AND r.end_int=f.dest_ip_end_int;
        List<Union> innerJoin1 = new ArrayList<>();
        for (int i = 0; i < rlstList.size(); i++) {
            Rlst r = rlstList.get(i);
            List<Union> unionList1 = new ArrayList<>();
            //if there is a match, add to matchesForRuleset
            //if there is no match, add to not_in_policy
            for (int j = 0; j < fwpolicyList.size(); j++) {
                Fwpolicy f = fwpolicyList.get(j);
                if (r.getStart_int() == f.getDest_ip_start_int() && r.getEnd_int() == f.getDest_ip_end_int()) {
                    Union u = new Union(r,f);
                    unionList1.add(u);
                }
            }
            if (unionList1.size() == 0) {
                //place r in notInPolicy
                notInPolicy.add(r);
            } else {
                innerJoin1.addAll(unionList1);
            }
        }
        int notInPolicySize = notInPolicy.size();
        List<Union> innerJoin2 = new ArrayList<>();
        for(int l=0; l<fwpolicyList.size(); l++){
            Fwpolicy f = fwpolicyList.get(l);
            List<Union> unionList2 = new ArrayList<>();
            for(int m=0; m<rlstList.size(); m++){
                Rlst r = rlstList.get(m);
                if (r.getStart_int() == f.getDest_ip_start_int() && r.getEnd_int() == f.getDest_ip_end_int()) {
                    Union u = new Union(r,f);
                    unionList2.add(u);
                }
            }
            if(unionList2.size()==0){
                //place f in notInRuleset
                notInRuleset.add(f);
            } else {
                innerJoin2.addAll(unionList2);
            }
        }
        int notInRulesetSize = notInRuleset.size();
        int innerJoin1Size = innerJoin1.size();
        int innerJoin2Size = innerJoin2.size();
        boolean twoListsAreEqual = innerJoin1.equals(innerJoin2);
        return new Report(notInPolicy, notInRuleset, innerJoin1);
    }

    //write the result of supplyReport() to an excel file
    //write etiher a List<Rlst> notInPolicy, List<Fwpolicy> notInRlst
    public <T> void writeExcelToFile(List<T> oneOfEm, String filename, String path){//, Map<String,Set<String>> history_iug_map) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("first");

        var rlst = oneOfEm.get(0);
        //get each field of the object rlst using reflection
        List<Method> methods = new ArrayList<>();
        for (Method method : rlst.getClass().getDeclaredMethods()) {
            if (
                    Modifier.isPublic(method.getModifiers())
                            && method.getParameterTypes().length == 0
                            && method.getReturnType() != void.class
                            && method.getName().startsWith("get")) {
                methods.add(method);
            }
        }
        //create headers for each object
        //create headers for the fields of a Rlst.java
        String[] headers = new String[methods.size()];
        for (int j = 0; j < methods.size(); j++) {
            Method method = methods.get(j);
            String name = method.getName();
            name = name.substring(3);
            name = name.substring(0, 1).toLowerCase() + name.substring(1);
            headers[j] = name;
        }

        int rowNum = 0;
        Row row;
        //create a row for each header
        row = sheet.createRow(rowNum++);
        //create a cell for each field of the object
        for (int h = 0; h < headers.length; h++) {
            String header = headers[h];
            Cell cell = row.createCell(h);
            cell.setCellValue(header);
        }

        for (int i = 0; i < oneOfEm.size(); i++) {
            //if oneOfEm is a List<Rlst> notInPolicy
            //if oneOfEm is a List<Fwpolicy> notInRlst
            rlst = oneOfEm.get(i);

            row = sheet.createRow(rowNum++);
            //create a cell for each field of the object
            for (int j = 0; j < methods.size(); j++) {
                Method method = methods.get(j);
                Object value = null;
                try {
                    value = method.invoke(rlst);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
                Cell cell = row.createCell(j);
                if (value instanceof String) {
                    cell.setCellValue((String) value);
                } else if (value instanceof Integer) {
                    cell.setCellValue((Integer) value);
                } else if (value instanceof Long) {
                    cell.setCellValue((Long) value);
                } else if (value instanceof Double) {
                    cell.setCellValue((Double) value);
                } else if (value instanceof Boolean) {
                    cell.setCellValue((Boolean) value);
                } else if (value instanceof Date) {
                    cell.setCellValue((Date) value);
                } else if (value instanceof Calendar) {
                    cell.setCellValue((Calendar) value);
                }
            }
        }

        String file_name = String.format(filename,rowNum);
        try {
            FileOutputStream outputStream = new FileOutputStream(path + "\\" + file_name);
            workbook.write(outputStream);
            workbook.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Done writing to file: " + file_name);
    }

    //write the result of supplyReport() to an excel file
    //write List<Union> inBoth
    public void writeUnionToXlsxFile(List<Report2> inBoth, String filename,String path){//, Map<String,Set<String>> history_iug_map) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("first");

        var u = inBoth.get(0).union;
        var rlst = u.rlst;
        var fwpolicy = u.fwpolicy;
        var exists = inBoth.get(0).exists;
        var notexists = inBoth.get(0).notexists;
        //get each field of the object rlst using reflection
        List<Method> methodsForRlst = new ArrayList<>();
        ///get each field of the object rlst using reflection
        for (Method method : rlst.getClass().getDeclaredMethods()) {
            if (
                    Modifier.isPublic(method.getModifiers())
                            && method.getParameterTypes().length == 0
                            && method.getReturnType() != void.class
                            && method.getName().startsWith("get")) {
                methodsForRlst.add(method);
            }
        }
        //get each field of the object fwpolicy using reflection
        List<Method> methodsForFwpolicy = new ArrayList<>();
        for (Method method : fwpolicy.getClass().getDeclaredMethods()) {
            if (
                    Modifier.isPublic(method.getModifiers())
                            && method.getParameterTypes().length == 0
                            && method.getReturnType() != void.class
                            && method.getName().startsWith("get")) {
                methodsForFwpolicy.add(method);
            }
        }

        //create headers for each object
        //create headers for the fields of a Rlst.java
        String[] headers = new String[methodsForRlst.size()+methodsForFwpolicy.size()];
        for (int j = 0; j < methodsForRlst.size(); j++) {
            Method method = methodsForRlst.get(j);
            String name = method.getName();
            name = name.substring(3);
            name = name.substring(0, 1).toLowerCase() + name.substring(1);
            headers[j] = name;
        }
        for (int j = 0; j < methodsForFwpolicy.size(); j++) {
            Method method = methodsForFwpolicy.get(j);
            String name = method.getName();
            name = name.substring(3);
            name = name.substring(0, 1).toLowerCase() + name.substring(1);
            headers[j+methodsForRlst.size()] = name;
        }

        int rowNum = 0;
        Row row;
        //create a row for each header
        row = sheet.createRow(rowNum++);
        //create a cell for each field of the object
        for (int h = 0; h < headers.length; h++) {
            String header = headers[h];
            Cell cell = row.createCell(h);
            cell.setCellValue(header);
        }

        for (int i = 0; i < inBoth.size(); i++) {
            //if oneOfEm is a List<Union> inBoth
            rlst = inBoth.get(i).union.rlst;
            fwpolicy = inBoth.get(i).union.fwpolicy;

            row = sheet.createRow(rowNum++);
            //create a cell for each field of the object
            for (int j = 0; j < methodsForRlst.size(); j++) {
                Method method = methodsForRlst.get(j);
                Object value = null;
                try {
                    value = method.invoke(rlst);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
                Cell cell = row.createCell(j);
                if (value instanceof String) {
                    cell.setCellValue((String) value);
                } else if (value instanceof Integer) {
                    cell.setCellValue((Integer) value);
                } else if (value instanceof Long) {
                    cell.setCellValue((Long) value);
                } else if (value instanceof Double) {
                    cell.setCellValue((Double) value);
                } else if (value instanceof Boolean) {
                    cell.setCellValue((Boolean) value);
                } else if (value instanceof Date) {
                    cell.setCellValue((Date) value);
                } else if (value instanceof Calendar) {
                    cell.setCellValue((Calendar) value);
                }
            }

            for (int j = 0; j < methodsForFwpolicy.size(); j++) {
                Method method = methodsForFwpolicy.get(j);
                Object value = null;
                try {
                    value = method.invoke(fwpolicy);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
                Cell cell = row.createCell(j+methodsForRlst.size());
                if (value instanceof String) {
                    cell.setCellValue((String) value);
                } else if (value instanceof Integer) {
                    cell.setCellValue((Integer) value);
                } else if (value instanceof Long) {
                    cell.setCellValue((Long) value);
                } else if (value instanceof Double) {
                    cell.setCellValue((Double) value);
                } else if (value instanceof Boolean) {
                    cell.setCellValue((Boolean) value);
                } else if (value instanceof Date) {
                    cell.setCellValue((Date) value);
                } else if (value instanceof Calendar) {
                    cell.setCellValue((Calendar) value);
                }

            }
            //write exists and notexists
            Cell cell = row.createCell(headers.length);
            //convert the list to a string
            String existsAsString = inBoth.get(i).exists.toString();
            cell.setCellValue(existsAsString);
            String destIpString = inBoth.get(i).destIpString;
            cell = row.createCell(headers.length+2);
            cell.setCellValue(destIpString);

            cell = row.createCell(headers.length+1);
            String notExistsAsString = inBoth.get(i).notexists.toString();
            try {
                cell.setCellValue(notExistsAsString);
            } catch (Exception e) {
                continue;
            }
            

        }

        // String file_name = String.format(filename,rowNum);
        // try {
        //     FileOutputStream outputStream = new FileOutputStream(file_name);
        //     workbook.write(outputStream);
        //     workbook.close();
        // } catch (FileNotFoundException e) {
        //     e.printStackTrace();
        // } catch (IOException e) {
        //     e.printStackTrace();
        // }
        //do the above but
        //write the workbook to a file with file name file_name in the directory C:\Users\z004a6nh\IdeaProjects\HibernateProject\chapter03\reports"
        String file_name = String.format(filename,rowNum);
        try {
            FileOutputStream outputStream = new FileOutputStream(path + "\\" + file_name);
            workbook.write(outputStream);
            workbook.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Done writing to file: " + file_name);
    }

    public void writeUnionToJsonFile(List<Report2> inBoth, String filename, String path) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException{//, Map<String,Set<String>> history_iug_map) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("first");

        var u = inBoth.get(0).union;
        var rlst = u.rlst;
        var fwpolicy = u.fwpolicy;
        var exists = inBoth.get(0).exists;
        var notexists = inBoth.get(0).notexists;
        //get each field of the object rlst using reflection
        List<Method> methodsForRlst = new ArrayList<>();
        ///get each field of the object rlst using reflection
        for (Method method : rlst.getClass().getDeclaredMethods()) {
            if (
                    Modifier.isPublic(method.getModifiers())
                            && method.getParameterTypes().length == 0
                            && method.getReturnType() != void.class
                            && method.getName().startsWith("get")) {
                methodsForRlst.add(method);
            }
        }
        //get each field of the object fwpolicy using reflection
        List<Method> methodsForFwpolicy = new ArrayList<>();
        for (Method method : fwpolicy.getClass().getDeclaredMethods()) {
            if (
                    Modifier.isPublic(method.getModifiers())
                            && method.getParameterTypes().length == 0
                            && method.getReturnType() != void.class
                            && method.getName().startsWith("get")) {
                methodsForFwpolicy.add(method);
            }
        }

        //JsonArrayBuilder jsonArray = Json.createArrayBuilder();
        //use GSON instead
        com.google.gson.JsonObject jsonObject = new com.google.gson.JsonObject();

        for (int i = 0; i < inBoth.size(); i++) {
            //JsonObjectBuilder object = Json.createObjectBuilder();
            //use GSON instead
            com.google.gson.JsonObject innerJsonObject = new com.google.gson.JsonObject();
            //set pretty printing

            //if oneOfEm is a List<Union> inBoth
            rlst = inBoth.get(i).union.rlst;
            fwpolicy = inBoth.get(i).union.fwpolicy;

            for (int j = 0; j < methodsForRlst.size(); j++) {
                Method method = methodsForRlst.get(j);
                String name = method.getName();
                name = name.substring(3);
                name = name.substring(0, 1).toLowerCase() + name.substring(1);
                var value = method.invoke(rlst);
                //object.add(name, value.toString());
                //use GSON instead
                innerJsonObject.addProperty(name, value==null ? "null" : value.toString());

            }
            for (int j = 0; j < methodsForFwpolicy.size(); j++) {
                Method method = methodsForFwpolicy.get(j);
                String name = method.getName();
                name = name.substring(3);
                name = name.substring(0, 1).toLowerCase() + name.substring(1);
                var value = method.invoke(fwpolicy);
                //object.add(name, value.toString());
                //use GSON instead
                innerJsonObject.addProperty(name, value.toString());
            }

            String destIpString = inBoth.get(i).destIpString;
            //object.add("destIpString", destIpString);
            //use GSON instead
            innerJsonObject.addProperty("destIpString", destIpString);

            String existsAsString = inBoth.get(i).exists.toString();
            //object.add("exists", existsAsString);
            //use GSON instead
            innerJsonObject.addProperty("exists", existsAsString);
            
            String notExistsAsString = inBoth.get(i).notexists.toString();
            //object.add("notexists", notExistsAsString);
            //use GSON instead
            innerJsonObject.addProperty("notexists", notExistsAsString);

            //jsonArray.add(object);
            //use GSON instead
            jsonObject.add(""+jsonObject.size(),innerJsonObject);
        
        }

        //build the json, write it to a file
        //JsonArray array = jsonArray.build();
        //String json = array.toString();
        //use GSON instead
        
        String uglyJsonString = jsonObject.toString();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonElement je = JsonParser.parseString(uglyJsonString);
        String prettyJsonString = gson.toJson(je);
        String file_name = String.format(filename,inBoth.size());
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
        //return String.format("%d.%d.%d.%d", (ip & 0xff), (ip >> 8 & 0xff), (ip >> 16 & 0xff), (ip >> 24 & 0xff));
        //do the same but with endianess little endian
        return String.format("%d.%d.%d.%d", (ip >> 24 & 0xff), (ip >> 16 & 0xff), (ip >> 8 & 0xff), (ip & 0xff));
    }

    public Logger initLogger(String name, String filename) {
        Logger logger = Logger.getLogger("lel");
        try {
            FileHandler handler = new FileHandler(filename);
            handler.setFormatter(new SimpleFormatter(){
                @Override
                public synchronized String format(LogRecord lr) {
                    return lr.getLevel() + ": " + lr.getMessage() + "\r\n";
                }
            });
            logger.addHandler(handler);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return logger;
    }

    @Test
    public void createXlsx() throws SecurityException, IOException{
        //C:\Users\z004a6nh\IdeaProjects\HibernateProject\chapter03\reports
        String path="C:\\Users\\z004a6nh\\IdeaProjects\\HibernateProject\\chapter03\\reports";
        Logger logger = initLogger("lel", path + "\\"+"log.xml");
        Logger logger2 = initLogger("lel2", path + "\\"+"log2.xml");
        Logger logger3 = initLogger("lel3", path + "\\"+"log3.xml");

//        List<String> iug = NativeQueryTest.createListFromCurrent(param);
//        List<String> history_iug = NativeQueryTest.createListFromHistory(param);
        Report allthree= supplyReport();
        List<Rlst> notInPolicy = allthree.notInPolicy;
        if (notInPolicy.size() > 0){
            writeExcelToFile(notInPolicy,"darwin_notInPolicy_%d.xlsx",path);
        }
        List<Fwpolicy> notInRuleset = allthree.notInRlst;
        if (notInRuleset.size() > 0){
            writeExcelToFile(notInRuleset,"darwin_notInRuleset_%d.xlsx",path);
        }
        List<Union> innerJoin = allthree.inBoth;
        //create a Map which contains all the records from the ip table 
        //where the innerJoin's destination ip is the same as the ip table's destination ip
        Map<Long, Set<Long>> source_group = service.get_ip_map();
        //for each item in the innerJoin list, 
        //create a Map which contains the element of the innerJoin list and the List of source ips
        Map<Union,Map<Long,Set<Long>>> innerJoinMap = provideInnerJoinMap(source_group, innerJoin);
        //create a Map which contains the destination ip and the List of source ips like srcIpList 
        //but without boxing the map as a value of the innerJoinMap
        //Map<Long,Set<Long>> srcIpListExposed = provideSrcIpListExposed(source_group, innerJoin, logger2);
        //provideLogs(source_group, innerJoin, logger3, logger);


        //return a List  of Records witch fields  List<String> exists, List<String> notexists, Union union
        List<Report2> report2 = new ArrayList<>();
        Map<String,String> host=service.get_host_map();
        //for map entry in the innerJoinMap
        Iterator<Map.Entry<Union,Map<Long,Set<Long>>>> it = innerJoinMap.entrySet().iterator();
        while (it.hasNext()) {
            //init  a list for  stuff thats existing in Map<String,String> host

            Map.Entry<Union,Map<Long,Set<Long>>> pair = it.next();
            Union union = pair.getKey();
            Map<Long,Set<Long>> srcIpList = pair.getValue();
            //union contains a destination ip range
            //srcIpList contains a list of source ip ranges for each ip in the destination ip range
            //for each element in the srcIpList
            //we should add an item to report2
            Iterator<Map.Entry<Long,Set<Long>>> it2 = srcIpList.entrySet().iterator();
            while (it2.hasNext()) {
                Map.Entry<Long,Set<Long>> pair2 = it2.next();
                Long dstIp = pair2.getKey();
                String dstIpString = intToIp(dstIp);
                Set<Long> srcIpSet = pair2.getValue();
                //for each element in the srcIpSet
                Iterator<Long> it3 = srcIpSet.iterator();
                List<String> exists = new ArrayList<>();
                List<String> notexists = new ArrayList<>();
                while (it3.hasNext()) {
                    Long srcIp = it3.next();
                    //if the srcIp is in Map<String,String> host
                    if (host.containsKey(intToIp(srcIp))) {
                        //add the srcIp to the exists list
                        exists.add(host.get(intToIp(srcIp)));
                    } else {
                        //add the srcIp to the notexists list
                        notexists.add(intToIp(srcIp));
                    }
                }
                //add the exists, notexists and union to the report2 list
                report2.add(new Report2(dstIpString, exists, notexists, union));
            }
            
        }
        //write the report2 list to an excel file
        try {
            writeUnionToJsonFile(report2, "darwin_bp_%d.json",path);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
//        list_of_list_obj = list_of_list_obj.stream()
//                .filter(x -> x.rlst().stream().filter(y -> y.getIp().equals("139.23.230.92")).findFirst()
//                        .orElse(null)==null).collect(Collectors.toList());
    }

    //split createXlsx() into multiple methods according to the functional paradigm
    //create a method which returns innerJoinMap found in createXlsx()
    //the methods only purpose is to return the innerJoinMap, the rest of the code should be removed
    public Map<Union,Map<Long,Set<Long>>> provideInnerJoinMap(Map<Long,Set<Long>> source_groups, List<Union> innerJoin) {
        Map<Union,Map<Long,Set<Long>>> innerJoinMap = new HashMap<>();
        
        for (int i = 0; i < innerJoin.size(); i++) {
            Union u = innerJoin.get(i);
            //List of source ips
            long start_int = u.fwpolicy.getDest_ip_start_int();
            long end_int = u.fwpolicy.getDest_ip_end_int();
            String start_ip = u.fwpolicy.getDest_ip_start();
            String end_ip = u.fwpolicy.getDest_ip_end();
            
            //create a Map which contains the destination ip and the List of source ips
            Map<Long,Set<Long>> srcIpList = new HashMap<>();
            //iterate over the ip range, starting from the star_int to the end_int
            for (long j = start_int; j <= end_int; j++) {
                //convert the int to an ip address
                String string_ip = intToIp(j);
                //check if the ip is already in the map
                Set<Long> srcIp = source_groups.get(j);
                //if srcIp is not null, then the ip is in the map
                //if so srcIpList.put(j,srcIp);  
                //otherwise don't put it in the map
                if (srcIp!=null) {
                    srcIpList.put(j,srcIp);
                }
            }
            //if the srcIpList is not empty
            //add the destination ip and the List of source ips to the innerJoinMap
            if (!srcIpList.isEmpty()) {
                innerJoinMap.put(u,srcIpList);
            }
        }
        return innerJoinMap;
    }

    public Map<Long,Set<Long>> provideSrcIpListExposed(Map<Long,Set<Long>> source_groups, List<Union> innerJoin, Logger logger2) {
        Map<Long,Set<Long>> srcIpListExposed = new HashMap<>();
        for (int i = 0; i < innerJoin.size(); i++) {
            Union u = innerJoin.get(i);
            //List of source ips
            long start_int = u.fwpolicy.getDest_ip_start_int();
            long end_int = u.fwpolicy.getDest_ip_end_int();
            String start_ip = u.fwpolicy.getDest_ip_start();
            String end_ip = u.fwpolicy.getDest_ip_end();
            //iterate over the ip range, starting from the star_int to the end_int
            for (long j = start_int; j <= end_int; j++) {
                //convert the int to an ip address
                String string_ip = intToIp(j);
                //check if the ip is already in the map
                Set<Long> srcIp = source_groups.get(j);
                //if srcIp is not null, then the ip is in the map
                if (srcIp!=null) {
                    if(srcIpListExposed.containsKey(j)) {
                        String fwpolicyData= "fid:"+u.fwpolicy.getId()+" rule_number:"+u.fwpolicy.getRule_number() + " rule_name:"+u.fwpolicy.getRule_name();
                        String rlstData= "rid:"+u.rlst.getId()+" app_name:"+u.rlst.getApp_name()+" app_id:"+u.rlst.getApp_id();
                        String complete=("duplicated fwpolicy-ruleset pair regarding destination ip: " + string_ip + " " + "in range: " + start_ip + " - " + end_ip +
                                " and the following Union object: " + " " + fwpolicyData + " " + rlstData + " ");
                        logger2.info(complete);
                        
                    } else {
                        srcIpListExposed.put(j,srcIp);
                    }
                }
            }
        }
        return srcIpListExposed;
    }

    public void provideLogs(Map<Long,Set<Long>> source_groups, List<Union> innerJoin,Logger logger3,Logger logger) {
        for (int i = 0; i < innerJoin.size(); i++) {
            Union u = innerJoin.get(i);
            //List of source ips
            long start_int = u.fwpolicy.getDest_ip_start_int();
            long end_int = u.fwpolicy.getDest_ip_end_int();
            String start_ip = u.fwpolicy.getDest_ip_start();
            String end_ip = u.fwpolicy.getDest_ip_end();
            //create a Map which contains the destination ip and the List of source ips
            Map<Long,Set<Long>> srcIpList = new HashMap<>();
            //iterate over the ip range, starting from the star_int to the end_int
            for (long j = start_int; j <= end_int; j++) {
                //convert the int to an ip address
                String string_ip = intToIp(j);
                //check if the ip is already in the map
                Set<Long> srcIp = source_groups.get(j);
                //if srcIp is not null, then the ip is in the map
                //if so srcIpList.put(j,srcIp);  
                //otherwise don't put it in the map
                if (srcIp!=null) {
                    srcIpList.put(j,srcIp);
                } else {
                    //use logger3 to log the destination ip , destination ip range, also the Union object, a new line before and after the Union object
                    String fwpolicyData= "fid:"+u.fwpolicy.getId()+" rule_number:"+u.fwpolicy.getRule_number() + " rule_name:"+u.fwpolicy.getRule_name();
                    String rlstData= "rid:"+u.rlst.getId()+" app_name:"+u.rlst.getApp_name()+" app_id:"+u.rlst.getApp_id();
                    String complete=("no network traffic for destination ip: " + string_ip + " " + "in range: " + start_ip + " - " + end_ip +
                            " and the following Union object: " + " " + fwpolicyData + " " + rlstData + " ");
                    logger3.info(complete);
                }
            }
            //fail the test if srcIpList is empty using assert
            //assert !srcIpList.isEmpty();
            //logger.info("info message");
            if (srcIpList.isEmpty()) {
                String fwpolicyData= "fid:"+u.fwpolicy.getId()+" rule_number:"+u.fwpolicy.getRule_number() + " rule_name:"+u.fwpolicy.getRule_name();
                String rlstData= "rid:"+u.rlst.getId()+" app_name:"+u.rlst.getApp_name()+" app_id:"+u.rlst.getApp_id(); 
                String complete=("no network traffic for destination ip range: " + start_ip + " - " + end_ip +
                " and the following Union object: " + " " + fwpolicyData + " " + rlstData + " ");
                logger.info(complete);
            }
        }
    }

}
