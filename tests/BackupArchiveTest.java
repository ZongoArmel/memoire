package bf.memoire;
import java.io.*;import java.nio.file.*;import java.util.zip.*;
public class BackupArchiveTest {
 static byte[] archive(String name,String json)throws Exception{ByteArrayOutputStream out=new ByteArrayOutputStream();try(ZipOutputStream z=new ZipOutputStream(out)){z.putNextEntry(new ZipEntry(name));z.write(json.getBytes("UTF-8"));z.closeEntry();}return out.toByteArray();}
 static void reject(byte[] zip)throws Exception{boolean failed=false;try{BackupArchive.unpack(new ByteArrayInputStream(zip),Files.createTempDirectory("memoire-invalid").toFile());}catch(Exception e){failed=true;}if(!failed)throw new AssertionError();}
 public static void main(String[] args)throws Exception{String json="{\"schema\":1,\"notes\":[],\"templates\":[]}";BackupArchive.unpack(new ByteArrayInputStream(archive("memory.json",json)),Files.createTempDirectory("memoire-valid").toFile());reject(archive("../escape",json));reject(archive("attachments/../../escape",json));reject(archive("memory.json","not json"));reject(archive("memory.json","{\"schema\":99,\"notes\":[]}"));System.out.println("5 ZIP import checks passed");}
}
