package chapter03.application;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.Iterator;

import org.testng.annotations.Test;

import chapter03.hibernate.Fwpolicy;
import chapter03.hibernate.Rlst;


@lombok.Data
public class Union_Exists_Map_Example_Subclass extends Union_Exists_Map_Example {

    public Union_Exists_Map_Example_Subclass() {
        super();
    }

    @Test
    public void testUnion_Exist_Map_Example_Subclass() {
        System.out.println("map");
    }
    
}
