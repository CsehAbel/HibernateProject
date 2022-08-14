package chapter03.application;

import chapter03.hibernate.IP_Unique_G;
import chapter03.hibernate.Rlst_G;
import chapter03.hibernate.ST_Ports_G;
import org.testng.annotations.Test;

public class ReportToExcelTest {
    QueryService service = new ImplementationQueryService();
    @Test
    public void selectAllThree(){
        var list=service.selectIUGPK();
        for (int i = 0; i <list.size() ; i++) {
            var param=list.get(i);
            var a = service.selectIUG(param);
            var b = service.selectRlstG(param);
            var c = service.selectSTPortsG(param);
        }
        //INNER JOIN Rlst_G ST_Ports_G
    }
}
