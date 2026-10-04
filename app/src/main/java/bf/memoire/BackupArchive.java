package bf.memoire;
import java.io.*;
import java.util.*;
import java.util.zip.*;
import org.json.*;
final class BackupArchive {
    static JSONObject unpack(InputStream source,File root)throws Exception {
        if(!root.isDirectory()&&!root.mkdirs())throw new IOException("Dossier temporaire inaccessible");Set<String> paths=new HashSet<>();long total=0;
        try(ZipInputStream zip=new ZipInputStream(source)){ZipEntry entry;while((entry=zip.getNextEntry())!=null){String name=entry.getName();if(!name.equals("memory.json")&&!name.matches("attachments/[a-fA-F0-9-]{36}"))throw new IOException("Chemin de sauvegarde interdit");if(!paths.add(name))throw new IOException("Entrée dupliquée");File target=new File(root,name);target.getParentFile().mkdirs();try(OutputStream out=new FileOutputStream(target)){byte[] buffer=new byte[8192];int count;long size=0;while((count=zip.read(buffer))!=-1){total+=count;size+=count;if(total>1024L*1024*1024||(name.equals("memory.json")&&size>32L*1024*1024))throw new IOException("Archive trop volumineuse");out.write(buffer,0,count);}}}}
        File json=new File(root,"memory.json");ByteArrayOutputStream bytes=new ByteArrayOutputStream();try(InputStream in=new FileInputStream(json)){byte[] buffer=new byte[8192];int count;while((count=in.read(buffer))!=-1)bytes.write(buffer,0,count);}JSONObject db=new JSONObject(bytes.toString("UTF-8"));ArchiveValidation.validate(db,root);return db;
    }
}
