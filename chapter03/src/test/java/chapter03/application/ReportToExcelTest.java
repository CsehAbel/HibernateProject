package chapter03.application;

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
import java.util.stream.Collectors;

public class ReportToExcelTest {
    QueryService service = new ImplementationQueryService();

    public record Union(List<String> iug,List<Rlst_G> rlst_gList, List<ST_Ports_G> stpg,  List<String> history_iug){

    }
    public List<Union> selectAllThree(CreateGTablesTest ct) {

        List<Union> readyToExport = new ArrayList<>();
        List<String> list = NativeQueryTest.listPK();
        for (int i = 0; i < list.size(); i++) {
            String param = list.get(i);

            Map<CreateGTablesTest.RlstKey, Rlst_G> map_rg = ct.get_rlst_gList(param);
            List<Rlst_G> rg_list = new ArrayList<Rlst_G>(map_rg.values());

            List<ST_Ports_G> stpg = ct.get_stports_map(param);

            if (stpg.size() == 0) {
                if (rg_list.size() == 0) {

                } else {
                    //write rg to not_in_policy
                    //missing case where dest_ip of rg is not in table ip_unique_g
                }
            }
            if (rg_list.size() == 0) {
                if (stpg.size() == 0) {

                } else {
                    //write stpg to not_in_ruleset
                    //missing case where dest_ip of stpg is not in table ip_unique_g
                }
            }

            if (0 < rg_list.size() && 0 < stpg.size()) {
                //i except 2 element in rg (dest_ip, app_id) pairs
                //and 1..3 elements in stpg (dest_ip, rule_name) pairs
                //for each rg there should be a separate row in the excel
                //for each rg combine the ports from the elements of stpg
                List<String> iug = NativeQueryTest.createListFromCurrent(param);
                List<String> history_iug = NativeQueryTest.createListFromHistory(param);
                Union u = new Union(iug,rg_list,stpg,history_iug);
                readyToExport.add(u);
            }

        }
        return readyToExport;
    }

    //for each dest_ip get value from NativeQueryTest.createMapFromHistory()
    //create two Set's resulting from the difference of two Sets:
    public void writeExcelToFile(List<Union> readyToExport){//, Map<String,Set<String>> history_iug_map) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("first");


        int rowNum = 0;
        //List<List<Object>> readyToExport
        for (int i = 0; i < readyToExport.size(); i++) {
            var current = readyToExport.get(i);
            var iug =  new HashSet<>(current.iug());
            var rg = current.rlst_gList();
            var stpg = current.stpg();
            var history_iug= new HashSet<>(current.history_iug());
            //ToDo: instead of Map return Set<String> for the specific dst_ip


            for (int j = 0; j < rg.size(); j++) {


                //take all elements in stpg and concatenate the Set port to String and Set rule_name to String
                //return Map.Entry<"port","80,8080,443">, Map.Entry<"rule_name","a_112,wuser112">
                Map<String, String> concat_map_stpg_list = concat_stpg_list_to_map(stpg);

                Rlst_G rlst_g = rg.get(j);
                var r_dst_ip = rlst_g.getDst_ip();
                var r_app_id = rlst_g.getApp_id();
                var r_concat_app_name = getCollect(rlst_g.getApp_name());

                var new_temp=new HashSet<>(iug);
                var old_temp= history_iug!=null ? new HashSet<>(history_iug) : new HashSet<String>();

                new_temp.removeAll(history_iug);
                old_temp.removeAll(iug);

                Row row;
                /*String[] once=("last_week,this_week," +
                        "added,removed," +
                        "r_dst_ip, r_app_id, r_concat_app_name," +
                        "concat_map_stpg_list.get(port)," +
                        "concat_map_stpg_list.get(rule_name)").split(",");*/
                String[] once=("sources," +
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
                String s1 = "" + history_iug.size();
                String s2 = "" + iug.size();
                String s3 = getCollect(new_temp);
                String s4 = getCollect(old_temp);
                /*String[] a = {
                        s1, s2, s3, s4, r_dst_ip, r_app_id, r_concat_app_name,
                        concat_map_stpg_list.get("port"),
                        concat_map_stpg_list.get("rule_name")
                };*/
                String[] a = {
                        getCollect(iug), r_dst_ip, r_app_id, r_concat_app_name,
                        concat_map_stpg_list.get("port"),
                        concat_map_stpg_list.get("rule_name")
                };
                List<String> asList = Arrays.asList(a);
                List<String> stringList = new ArrayList<>(asList);
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

//        INSERT IGNORE INTO ip_unique (`src_ip`,`dst_ip`)
//        SELECT ip.source_ip,ip.dest_ip
//        FROM ip;
        CreateGTablesTest ct=new CreateGTablesTest();
        List<Union> list_of_list_obj= selectAllThree(
                ct);
        list_of_list_obj = list_of_list_obj.stream()
                .filter(x -> x.rlst_gList().stream().filter(y -> y.getDst_ip().equals("139.23.230.92")).findFirst()
                        .orElse(null)==null).collect(Collectors.toList());
        writeExcelToFile(list_of_list_obj);
    }
}
