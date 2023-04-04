package chapter03.application;

import chapter03.hibernate.Fwpolicy;
import chapter03.hibernate.Rlst;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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

public class ReportToExcelTest {

    // C:\Users\z004a6nh\IdeaProjects\HibernateProject\chapter03\reports
    private String path = "C:\\Users\\z004a6nh\\IdeaProjects\\HibernateProject\\chapter03\\reports";

    private Map<Long, Set<Long>> srcIpListExposed;

    // write the result of supplyReport() to an excel file
    // write etiher a List<Rlst> notInPolicy, List<Fwpolicy> notInRlst
    public <T> void writeSetDifference(List<T> oneOfEm, String filename, String path) {// , Map<String,Set<String>>
                                                                                     // history_iug_map) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("first");

        var rlst = oneOfEm.get(0);
        // get each field of the object rlst using reflection
        List<Method> methods = new ArrayList<>();
        for (Method method : rlst.getClass().getDeclaredMethods()) {
            if (Modifier.isPublic(method.getModifiers())
                    && method.getParameterTypes().length == 0
                    && method.getReturnType() != void.class
                    && method.getName().startsWith("get")) {
                methods.add(method);
            }
        }
        // create headers for each object
        // create headers for the fields of a Rlst.java
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
        // create a row for each header
        row = sheet.createRow(rowNum++);
        // create a cell for each field of the object
        for (int h = 0; h < headers.length; h++) {
            String header = headers[h];
            Cell cell = row.createCell(h);
            cell.setCellValue(header);
        }

        for (int i = 0; i < oneOfEm.size(); i++) {
            // if oneOfEm is a List<Rlst> notInPolicy
            // if oneOfEm is a List<Fwpolicy> notInRlst
            rlst = oneOfEm.get(i);

            row = sheet.createRow(rowNum++);
            // create a cell for each field of the object
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

        String file_name = String.format(filename, rowNum);
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

    public Logger initLogger(String name, String filename) {
        Logger logger = Logger.getLogger("lel");
        try {
            FileHandler handler = new FileHandler(filename);
            handler.setFormatter(new SimpleFormatter() {
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
    public void createReportsXlsx() throws SecurityException, IOException {
        Report allthree = new Report_Example().getReport();

        List<Rlst> notInPolicy = allthree.getNotInPolicy();
        if (notInPolicy.size() > 0) {
            writeSetDifference(notInPolicy, "energy_notInPolicy_%d.xlsx", path);
        }

        List<Fwpolicy> notInRuleset = allthree.getNotInRlst();
        if (notInRuleset.size() > 0) {
            writeSetDifference(notInRuleset, "energy_notInRuleset_%d.xlsx", path);
        }
    }

    @Test
    public void provideSrcIpListExposed() {
        Map<Long, Set<Long>> source_groups = new IP_Map_Example().getMap();
        List<Union> innerJoin = new Report_Example().getReport().getInBoth();
        Logger logger2 = initLogger("lel2", this.path + "\\" + "log2.xml");

        Map<Long, Set<Long>> srcIpListExposed = new HashMap<>();
        for (int i = 0; i < innerJoin.size(); i++) {
            Union u = innerJoin.get(i);
            // List of source ips
            long start_int = u.getFwpolicy().getDest_ip_start_int();
            long end_int = u.getFwpolicy().getDest_ip_end_int();
            String start_ip = u.getFwpolicy().getDest_ip_start();
            String end_ip = u.getFwpolicy().getDest_ip_end();
            // iterate over the ip range, starting from the star_int to the end_int
            for (long j = start_int; j <= end_int; j++) {
                // convert the int to an ip address
                String string_ip = intToIp(j);
                // check if the ip is already in the map
                Set<Long> srcIp = source_groups.get(j);
                // if srcIp is not null, then the ip is in the map
                if (srcIp != null) {
                    if (srcIpListExposed.containsKey(j)) {
                        String fwpolicyData = "fid:" + u.getFwpolicy().getId() + " rule_number:"
                                + u.getFwpolicy().getRule_number() + " rule_name:" + u.getFwpolicy().getRule_name();
                        String rlstData = "rid:" + u.getRlst().getId() + " app_name:" + u.getRlst().getApp_name()
                                + " app_id:" + u.getRlst().getApp_id();
                        String complete = ("duplicated fwpolicy-ruleset pair regarding destination ip: " + string_ip
                                + " " + "in range: " + start_ip + " - " + end_ip +
                                " and the following Union object: " + " " + fwpolicyData + " " + rlstData + " ");
                        logger2.info(complete);

                    } else {
                        srcIpListExposed.put(j, srcIp);
                    }
                }
            }
        }
        this.srcIpListExposed = srcIpListExposed;
    }

    @Test
    public void provideLogs() {
        Map<Long, Set<Long>> source_groups = new IP_Map_Example().getMap();
        List<Union> innerJoin = new Report_Example().getReport().getInBoth();
        Logger logger = initLogger("lel", this.path + "\\" + "log.xml");
        Logger logger3 = initLogger("lel3", this.path + "\\" + "log3.xml");

        for (int i = 0; i < innerJoin.size(); i++) {
            Union u = innerJoin.get(i);
            // List of source ips
            long start_int = u.getFwpolicy().getDest_ip_start_int();
            long end_int = u.getFwpolicy().getDest_ip_end_int();
            String start_ip = u.getFwpolicy().getDest_ip_start();
            String end_ip = u.getFwpolicy().getDest_ip_end();
            // create a Map which contains the destination ip and the List of source ips
            Map<Long, Set<Long>> srcIpList = new HashMap<>();
            // iterate over the ip range, starting from the star_int to the end_int
            for (long j = start_int; j <= end_int; j++) {
                // convert the int to an ip address
                String string_ip = intToIp(j);
                // check if the ip is already in the map
                Set<Long> srcIp = source_groups.get(j);
                // if srcIp is not null, then the ip is in the map
                // if so srcIpList.put(j,srcIp);
                // otherwise don't put it in the map
                if (srcIp != null) {
                    srcIpList.put(j, srcIp);
                } else {
                    // use logger3 to log the destination ip , destination ip range, also the Union
                    // object, a new line before and after the Union object
                    String fwpolicyData = "fid:" + u.getFwpolicy().getId() + " rule_number:"
                            + u.getFwpolicy().getRule_number() + " rule_name:" + u.getFwpolicy().getRule_name();
                    String rlstData = "rid:" + u.getRlst().getId() + " app_name:" + u.getRlst().getApp_name()
                            + " app_id:" + u.getRlst().getApp_id();
                    String complete = ("no network traffic for destination ip: " + string_ip + " " + "in range: "
                            + start_ip + " - " + end_ip +
                            " and the following Union object: " + " " + fwpolicyData + " " + rlstData + " ");
                    logger3.info(complete);
                }
            }
            // fail the test if srcIpList is empty using assert
            // assert !srcIpList.isEmpty();
            // logger.info("info message");
            if (srcIpList.isEmpty()) {
                String fwpolicyData = "fid:" + u.getFwpolicy().getId() + " rule_number:"
                        + u.getFwpolicy().getRule_number() + " rule_name:" + u.getFwpolicy().getRule_name();
                String rlstData = "rid:" + u.getRlst().getId() + " app_name:" + u.getRlst().getApp_name() + " app_id:"
                        + u.getRlst().getApp_id();
                String complete = ("no network traffic for destination ip range: " + start_ip + " - " + end_ip +
                        " and the following Union object: " + " " + fwpolicyData + " " + rlstData + " ");
                logger.info(complete);
            }
        }
    }

    public String intToIp(long ipv4address) {
        long ip = ipv4address;
        // return String.format("%d.%d.%d.%d", (ip & 0xff), (ip >> 8 & 0xff), (ip >> 16
        // & 0xff), (ip >> 24 & 0xff));
        // do the same but with endianess little endian
        return String.format("%d.%d.%d.%d", (ip >> 24 & 0xff), (ip >> 16 & 0xff), (ip >> 8 & 0xff), (ip & 0xff));
    }
}
