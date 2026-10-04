package bf.memoire;
import org.json.*;
import java.nio.file.*;
import java.io.*;
public class ArchiveValidationTest {
    static int count=0;
    static JSONObject base() {return new JSONObject("{\"schema\":1,\"notes\":[{\"id\":\"00000000-0000-0000-0000-000000000001\",\"fields\":[],\"files\":[],\"versions\":[]}],\"templates\":[]}");}
    interface Check {void run()throws Exception;}
    static void reject(Check c)throws Exception {try{c.run();}catch(Exception expected){count++;return;}throw new AssertionError("Invalid archive accepted");}
    public static void main(String[] args)throws Exception {
        File dir=Files.createTempDirectory("memoire-test").toFile();
        ArchiveValidation.validate(base(),dir);count++;
        JSONObject wrong=base().put("schema",2);reject(()->ArchiveValidation.validate(wrong,dir));
        JSONObject missing=base();missing.getJSONArray("notes").getJSONObject(0).put("files",new JSONArray().put(new JSONObject().put("id","00000000-0000-0000-0000-000000000002").put("name","test.pdf")));reject(()->ArchiveValidation.validate(missing,dir));
        JSONObject malformed=base();malformed.getJSONArray("notes").getJSONObject(0).put("fields",new JSONArray().put(JSONObject.NULL));reject(()->ArchiveValidation.validate(malformed,dir));
        JSONObject duplicate=base();duplicate.getJSONArray("notes").put(duplicate.getJSONArray("notes").getJSONObject(0));reject(()->ArchiveValidation.validate(duplicate,dir));
        JSONObject templates=base().put("templates",new JSONArray().put(new JSONObject().put("name","Broken")));reject(()->ArchiveValidation.validate(templates,dir));
        JSONObject traversal=base();traversal.getJSONArray("notes").getJSONObject(0).put("files",new JSONArray().put(new JSONObject().put("id","../../etc/passwd").put("name","test")));reject(()->ArchiveValidation.validate(traversal,dir));
        File attachments=new File(dir,"attachments");attachments.mkdir();new File(attachments,"00000000-0000-0000-0000-000000000002").createNewFile();ArchiveValidation.validate(missing,dir);count++;
        System.out.println(count+" archive validation checks passed");
    }
}
