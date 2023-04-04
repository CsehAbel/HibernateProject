package chapter03.application;
import java.util.List;

@lombok.Data
public class ExistsAsHost{

    private List<String> exists;
    private List<String> notexists; 

    public ExistsAsHost(List<String> exists, List<String> notexists) {
        this.exists = exists;
        this.notexists = notexists;
    }
}
