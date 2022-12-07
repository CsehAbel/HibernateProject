package chapter03.application;

import chapter03.hibernate.Fwpolicy;
import chapter03.hibernate.Rlst;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.logging.FileHandler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class ReportToExcelTest {
    QueryService service = new QueryService();

    public record Union(Rlst rlst, Fwpolicy fwpolicy){
    }

    //used for returning the result of supplyInnerJoin()
    //stores  List<Rlst> notInPolicy, List<Fwpolicy> notInRlst, List<Union> inBoth
    public record Report(List<Rlst> notInPolicy, List<Fwpolicy> notInRlst, List<Union> inBoth){
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
    public <T> void writeExcelToFile(List<T> oneOfEm, String filename){//, Map<String,Set<String>> history_iug_map) {
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
            FileOutputStream outputStream = new FileOutputStream(file_name);
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
    public void writeUnionToFile(List<Union> inBoth, String filename){//, Map<String,Set<String>> history_iug_map) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("first");

        var rlst = inBoth.get(0).rlst;
        var fwpolicy = inBoth.get(0).fwpolicy;
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
            rlst = inBoth.get(i).rlst;
            fwpolicy = inBoth.get(i).fwpolicy;

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
        }

        String file_name = String.format(filename,rowNum);
        try {
            FileOutputStream outputStream = new FileOutputStream(file_name);
            workbook.write(outputStream);
            workbook.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
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

    @Test
    public void createXlsx() throws SecurityException, IOException{
        FileHandler handler = new FileHandler("log.xml");
        //handler.setFormatter(new SimpleFormatter());
        //overwrite SimpleFormatter with format: Severity: Message
        handler.setFormatter(new SimpleFormatter(){
            @Override
            public synchronized String format(LogRecord lr) {
                return lr.getLevel() + ": " + lr.getMessage() + "\r\n";
            }
        });
        Logger logger = Logger.getLogger("lel");
        logger.addHandler(handler);
        
        FileHandler handler2 = new FileHandler("log2.xml");
        //handler2.setFormatter(new SimpleFormatter());
        //overwrite SimpleFormatter with format: Severity: Message
        handler2.setFormatter(new SimpleFormatter(){
            @Override
            public synchronized String format(LogRecord lr) {
                return lr.getLevel() + ": " + lr.getMessage() + "\r\n";
            }
        });
        Logger logger2 = Logger.getLogger("lel2");
        logger2.addHandler(handler2);
        
        FileHandler handler3 = new FileHandler("log3.xml");
        //handler3.setFormatter(new SimpleFormatter());
        //overwrite SimpleFormatter with format: Severity: Message
        handler3.setFormatter(new SimpleFormatter(){
            @Override
            public synchronized String format(LogRecord lr) {
                return lr.getLevel() + ": " + lr.getMessage() + "\r\n";
            }
        });
        Logger logger3 = Logger.getLogger("lel3");
        logger3.addHandler(handler3);

//        List<String> iug = NativeQueryTest.createListFromCurrent(param);
//        List<String> history_iug = NativeQueryTest.createListFromHistory(param);
        Report allthree= supplyReport();
        List<Rlst> notInPolicy = allthree.notInPolicy;
        writeExcelToFile(notInPolicy,"energy_notInPolicy_%d.xlsx");
        List<Fwpolicy> notInRuleset = allthree.notInRlst;
        writeExcelToFile(notInRuleset,"energy_notInRuleset_%d.xlsx");
        List<Union> innerJoin = allthree.inBoth;
        //create a Map which contains all the records from the ip table 
        //where the innerJoin's destination ip is the same as the ip table's destination ip
        Map<Long, Set<Long>> map = service.get_ip_map();
        //for each item in the innerJoin list, 
        //create a Map which contains the element of the innerJoin list and the List of source ips
        Map<Union,Map<Long,Set<Long>>> innerJoinMap = new HashMap<>();
        //create a Map which contains the destination ip and the List of source ips like srcIpList 
        //but without boxing the map as a value of the innerJoinMap
        Map<Long,Set<Long>> srcIpListExposed = new HashMap<>();
        //for (Union u: innerJoin) {
        for (int i = 0; i < innerJoin.size(); i++) {
            Union u = innerJoin.get(i);
            //List of source ips
            long start_int = u.fwpolicy.getDest_ip_start_int();
            long end_int = u.fwpolicy.getDest_ip_end_int();
            String start_ip = u.fwpolicy.getDest_ip_start();
            String end_ip = u.fwpolicy.getDest_ip_end();
            
            //create a List which contains the destination ip and the List of source ips
            //List<String,List<String>> srcIpList = new ArrayList<>();
            //instead of a List, create a Map which contains the destination ip and the List of source ips
            Map<Long,Set<Long>> srcIpList = new HashMap<>();
            //iterate over the ip range, starting from the star_int to the end_int
            for (long j = start_int; j <= end_int; j++) {
                //convert the int to an ip address
                String string_ip = intToIp(j);
                //check if the ip is already in the map
                Set<Long> srcIp = map.get(j);
                //if srcIp is not null, then the ip is in the map
                //if so srcIpList.put(j,srcIp);  
                //otherwise don't put it in the map
                if (srcIp!=null) {
                    long srcIpSize=srcIp.size();
                    srcIpList.put(j,srcIp);
                    //if srcIp is already in srcIpListExposed log the destination ip range, also the Union object, a new line before and after the Union object
                    if(srcIpListExposed.containsKey(j)) {
                        String fwpolicyData= "fid:"+u.fwpolicy.getId()+" rule_number:"+u.fwpolicy.getRule_number() + " rule_name:"+u.fwpolicy.getRule_name();
                        String rlstData= "rid:"+u.rlst.getId()+" app_name:"+u.rlst.getApp_name()+" app_id:"+u.rlst.getApp_id();
                        logger2.info("duplicated fwpolicy-ruleset pair regarding destination ip: " + string_ip + " " + "in range: " + start_ip + " - " + end_ip +
                                " and the following Union object: " + " " + fwpolicyData + " " + rlstData + " ");
                    } else {
                        srcIpListExposed.put(j,srcIp);
                    }
                } else {
                    //use logger3 to log the destination ip , destination ip range, also the Union object, a new line before and after the Union object
                    String fwpolicyData= "fid:"+u.fwpolicy.getId()+" rule_number:"+u.fwpolicy.getRule_number() + " rule_name:"+u.fwpolicy.getRule_name();
                    String rlstData= "rid:"+u.rlst.getId()+" app_name:"+u.rlst.getApp_name()+" app_id:"+u.rlst.getApp_id();
                    logger3.info("no network traffic for destination ip: " + string_ip + " " + "in range: " + start_ip + " - " + end_ip +
                            " and the following Union object: " + " " + fwpolicyData + " " + rlstData + " ");
                }
            }
            //if the srcIpList is not empty
            //add the destination ip and the List of source ips to the innerJoinMap
            if (!srcIpList.isEmpty()) {
                innerJoinMap.put(u,srcIpList);
            }
            //fail the test if srcIpList is empty using assert
            //assert !srcIpList.isEmpty();
            //logger.info("info message");
            if (srcIpList.isEmpty()) {
                String fwpolicyData= "fid:"+u.fwpolicy.getId()+" rule_number:"+u.fwpolicy.getRule_number() + " rule_name:"+u.fwpolicy.getRule_name();
                String rlstData= "rid:"+u.rlst.getId()+" app_name:"+u.rlst.getApp_name()+" app_id:"+u.rlst.getApp_id(); 
                logger.info("no network traffic for destination ip range: " + start_ip + " - " + end_ip +
                " and the following Union object: " + " " + fwpolicyData + " " + rlstData + " ");
            }
        }
        writeUnionToFile(innerJoin,"energy_bp_%d.xlsx");
        //write the result of supplyReport() to an excel file
        //write etiher a List<Rlst> notInPolicy, List<Fwpolicy> notInRlst, List<Union> inBoth

//        list_of_list_obj = list_of_list_obj.stream()
//                .filter(x -> x.rlst().stream().filter(y -> y.getIp().equals("139.23.230.92")).findFirst()
//                        .orElse(null)==null).collect(Collectors.toList());
//        supply the inner join of ruleset and fwpolicy
//        supply the elastic logs for the specific dst_ip
//        writeExcelToFile(list_of_list_obj);
    }
}
