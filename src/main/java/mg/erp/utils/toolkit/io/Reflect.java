package mg.erp.utils.toolkit.io;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class Reflect {
    public static Field [] getColumns (Object obj){
        Field [] attr = obj.getClass().getDeclaredFields();
        List<Field> fields = new ArrayList<>();
        for (Field f : attr){
            fields.add(f);
        }
        return fields.toArray(new Field[]{});
    }
}
