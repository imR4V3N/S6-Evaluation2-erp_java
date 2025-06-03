package mg.erp.utils.toolkit.io;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ObjectFile {
    public void exportObject(Object object, String path) throws Exception{
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(path))){
            objectOutputStream.writeObject(object);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }

    public Object importObject(String path) throws Exception{
        try (ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(path))) {
                Object object = objectInputStream.readObject();
                objectInputStream.close();
                return object;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }
}
