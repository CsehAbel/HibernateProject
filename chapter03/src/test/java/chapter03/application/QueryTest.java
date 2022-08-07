package chapter03.application;

import chapter03.hibernate.IP_Unique;
import chapter03.hibernate.IP_Unique_G;
import org.testng.annotations.Test;

public class QueryTest {
    QueryService service=new ImplementationQueryService();

    //Query using IP_Unique.class and create two new entities from Map<String, List<String>>
    //create new objects: new IP_Unique_G() and persist them and associate them to the IP_Unique.class
    @Test
    public void getResult(){
        String parameter="";
        var res_list=service.selectAll(IP_Unique.class);
        for (int i = 0; i < res_list.size(); i++) {
            var iu=res_list.get(i);
            var iug=service.saveIP_Unique_G(iu.getDst_ip());
            var iu_res=service.updateIP_Unique(iug.getDst_ip(),iu.getDst_ip(),iu.getSrc_ip());
        }


    }
}
