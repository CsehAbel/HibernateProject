package chapter03.application;

import chapter03.hibernate.IP_Unique_G;
import chapter03.hibernate.Rlst_G;
import chapter03.hibernate.ST_Ports_G;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ReportToExcelTest {
    QueryService service = new ImplementationQueryService();

    public List<List<Object>> selectAllThree() {
        List<List<Object>> readyToExport = new ArrayList<>();
        var list = service.selectIUGPK();
        var e=service.selectEagleIP();
        var notineagle=new ArrayList<>(list);
        notineagle.removeAll(e);

        //list=select dst_ip from eagle
        for (int i = 0; i < notineagle.size(); i++) {
            var param = notineagle.get(i);
            var iug = service.selectIUG(param);
            var rg = service.selectRlstG(param);
            var stpg = service.selectSTPortsG(param);

            if (stpg.size() == 0) {
                if (rg.size() == 0) {

                } else {
                    //write rg to not_in_policy
                    //missing case where dest_ip of rg is not in table ip_unique_g
                }
            }
            if (rg.size() == 0) {
                if (stpg.size() == 0) {

                } else {
                    //write stpg to not_in_ruleset
                    //missing case where dest_ip of stpg is not in table ip_unique_g
                }
            }

            if (0 < rg.size() && 0 < stpg.size()) {
                //i except 2 element in rg (dest_ip, app_id) pairs
                //and 1..3 elements in stpg (dest_ip, rule_name) pairs
                //for each rg there should be a separate row in the excel
                //for each rg combine the ports from the elements of rg
                List<Object> objectList = new ArrayList<>();
                objectList.add(iug);
                objectList.add(rg);
                objectList.add(stpg);
                readyToExport.add(objectList);
            }

        }
        return readyToExport;
    }

    //for each dest_ip get value from NativeQueryTest.createMapFromHistory()
    //create two Set's resulting from the difference of two Sets:
    public void writeExcelToFile(List<List<Object>> readyToExport, Map<String, Set<String>> map){//, Map<String,Set<String>> history_iug_map) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("first");

        IP_Unique_G iug = null;
        List<Rlst_G> rg = null;
        List<ST_Ports_G> stpg = null;
        int rowNum = 0;
        //List<List<Object>> readyToExport
        for (int i = 0; i < readyToExport.size(); i++) {
            var current = readyToExport.get(i);
            iug = (IP_Unique_G) current.get(0);
            rg = (List<Rlst_G>) current.get(1);
            stpg = (List<ST_Ports_G>) current.get(2);
            var history_iug=map.get(iug.getDst_ip());

            for (int j = 0; j < rg.size(); j++) {


                //take all elements in stpg and combine the Set port and Set rule_name
                //return two Sets altogether
                Map<String, String> concat_map_stpg_list = concat_stpg_list_to_map(stpg);

                Rlst_G rlst_g = rg.get(j);
                var r_dst_ip = rlst_g.getDst_ip();
                var r_app_id = rlst_g.getApp_id();
                var r_concat_app_name = getCollect(rlst_g.getApp_name());

                //var iug_sources = getCollect(iug.getSources());
                var new_temp=new HashSet<>(iug.getSources());
                var old_temp= history_iug!=null ? new HashSet<>(history_iug) : new HashSet<String>();
//              if(history_iug==null){ then new_temp stays iug_getSources, old_temp will be empty
                    //onlyinnew
                new_temp.removeAll(history_iug);
                    //onlyinold
                old_temp.removeAll(iug.getSources());

                Row row;
                String[] once=("last_week,this_week," +
                        "added,removed," +
                        "r_dst_ip, r_app_id, r_concat_app_name," +
                        "concat_map_stpg_list.get(port)," +
                        "concat_map_stpg_list.get(rule_name)").split(",");
                if(i==0 && j==0){
                    row = sheet.createRow(rowNum++);
                    for (int kek = 0; kek < once.length; kek++) {
                        String s = once[kek];
                        Cell cell = row.createCell(kek);
                        cell.setCellValue(s);
                    }
                }
                List<String> stringList = new ArrayList<>(
                        Arrays.asList(
                                new String[]{
                                        ""+history_iug.size(),""+iug.getSources().size(),getCollect(new_temp),getCollect(old_temp), r_dst_ip, r_app_id, r_concat_app_name,
                                        concat_map_stpg_list.get("port"),
                                        concat_map_stpg_list.get("rule_name")
                                }
                        )
                );
                row = sheet.createRow(rowNum++);
                for (int k = 0; k < stringList.size(); k++) {
                    String s = stringList.get(k);
                    Cell cell = row.createCell(k);
                    cell.setCellValue(s);
                }
            }
        }
        String file_name = String.format("darwin_bp_%d.xlsx",rowNum);
        try {
            FileOutputStream outputStream = new FileOutputStream(file_name);
            workbook.write(outputStream);
            workbook.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Done");
    }

    private Map<String, String> concat_stpg_list_to_map(List<ST_Ports_G> stpg) {
        Set<String> stpg_concat_rule_set = new HashSet<>();
        Set<String> stpg_concat_port_set = new HashSet<>();
        String stpg_concat_rule = "";
        String stpg_concat_port = "";
        for (int k = 0; k < stpg.size(); k++) {
            stpg_concat_port_set.addAll(stpg.get(k).getPort());
            stpg_concat_rule_set.addAll(stpg.get(k).getName());
        }
        stpg_concat_port = getCollect(stpg_concat_port_set);
        stpg_concat_rule = getCollect(stpg_concat_rule_set);
        Map<String,String> concat_map_stpg_list=new HashMap<>();
        concat_map_stpg_list.put("port",stpg_concat_port);
        concat_map_stpg_list.put("rule_name",stpg_concat_rule);
        return concat_map_stpg_list;
    }

    private String getCollect(Set<String> stpg) {
        return stpg.stream().collect(Collectors.joining(","));
    }
    @Test
    public void createXlsx(){
        writeExcelToFile(selectAllThree(), NativeQueryTest.createMapFromHistory());
    }
}
