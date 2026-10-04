package bf.memoire;
import java.io.*;
import java.nio.file.*;
import java.util.*;
public class SecureArchiveTest {
 public static void main(String[] args)throws Exception {
  Path dir=Files.createTempDirectory("memoire-crypto");File original=dir.resolve("input").toFile();byte[] payload=new byte[200000];new java.security.SecureRandom().nextBytes(payload);Files.write(original.toPath(),payload);char[] password="Une phrase solide 2026!".toCharArray();ByteArrayOutputStream encoded=new ByteArrayOutputStream();SecureArchive.encrypt(original,encoded,password);byte[] bytes=encoded.toByteArray();if(!SecureArchive.encrypted(new ByteArrayInputStream(bytes)))throw new AssertionError();File output=dir.resolve("output").toFile();SecureArchive.decrypt(new ByteArrayInputStream(bytes),output,password);if(!Arrays.equals(payload,Files.readAllBytes(output.toPath())))throw new AssertionError("Round trip failed");
  reject(bytes,"Wrong password!".toCharArray(),dir.resolve("wrong").toFile());byte[] changed=bytes.clone();changed[changed.length-10]^=1;reject(changed,password,dir.resolve("tampered").toFile());reject(Arrays.copyOf(bytes,bytes.length-8),password,dir.resolve("truncated").toFile());if(SecureArchive.encrypted(new ByteArrayInputStream("PK sample".getBytes())))throw new AssertionError();System.out.println("5 encryption checks passed");
 }
 static void reject(byte[] encoded,char[] password,File output)throws Exception{boolean rejected=false;try{SecureArchive.decrypt(new ByteArrayInputStream(encoded),output,password);}catch(Exception e){rejected=true;}if(!rejected||output.exists())throw new AssertionError("Invalid archive was accepted or left plaintext");}
}
