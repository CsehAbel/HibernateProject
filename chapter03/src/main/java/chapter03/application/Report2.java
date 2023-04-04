package chapter03.application;
import java.util.List;

@lombok.Data
public class Report2{

    private String destIpString;
    private List<String> exists;
    private List<String> notexists; 
    private Union union;

    public Report2(String destIpString, List<String> exists, List<String> notexists, Union union) {
        this.destIpString = destIpString;
        this.exists = exists;
        this.notexists = notexists;
        this.union = union;
    }
}
