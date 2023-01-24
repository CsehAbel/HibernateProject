package chapter03.application;

public class BPToExcelTest {
    
    @Test
    public void createBPXlsx() throws SecurityException, IOException {

        eagle_Map_Example = new Eagle_Map_Example();
        report2_Example = new Report2_Example();
        // write the report2 list to an excel file
        try {
            writeUnionToJsonFile(report2_Example.getCReport2s(), "energy_bp_%d.json", path);
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
