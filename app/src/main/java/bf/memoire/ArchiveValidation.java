package bf.memoire;
import java.io.*;
import java.util.*;
import org.json.*;
/** Validate restored data before changing any live file. */
final class ArchiveValidation {
    static void validate(JSONObject db,File root)throws Exception {
        validateWithFiles(db,new File(root,"attachments"));
    }
    static void validateWithFiles(JSONObject db,File attachments)throws Exception {
        if(db.optInt("schema")!=1)throw new IOException("Version de sauvegarde incompatible");
        JSONArray notes=db.getJSONArray("notes");Set<String> ids=new HashSet<>();
        for(int i=0;i<notes.length();i++){
            JSONObject n=notes.getJSONObject(i);String id=n.getString("id");
            if(!id.matches("[a-fA-F0-9-]{36}")||!ids.add(id))throw new IOException("Identifiant de fiche invalide ou dupliqué");
            JSONArray blocks=n.optJSONArray("blocks");if(n.has("blocks")&&blocks==null)throw new IOException("Blocs invalides");if(blocks!=null){Set<String> blockIds=new HashSet<>();for(int j=0;j<blocks.length();j++){JSONObject block=blocks.getJSONObject(j);String blockId=block.getString("id");if(blockId.isEmpty()||!blockIds.add(blockId))throw new IOException("Bloc dupliqué");String type=block.getString("type");if(type.equals("Image")||type.equals("Fichier")){String fileId=block.getString("fileId");if(!fileId.matches("[a-fA-F0-9-]{36}")||!new File(attachments,fileId).isFile())throw new IOException("Fichier de bloc absent");}else block.getString("text");}}
            fields(n.getJSONArray("fields"));JSONArray versions=n.getJSONArray("versions");if(versions.length()>20)throw new IOException("Trop de versions");
            for(int j=0;j<versions.length();j++)fields(versions.getJSONObject(j).getJSONArray("fields"));
            JSONArray files=n.getJSONArray("files");
            for(int j=0;j<files.length();j++){JSONObject f=files.getJSONObject(j);String fileId=f.getString("id");if(!fileId.matches("[a-fA-F0-9-]{36}")||!new File(attachments,fileId).isFile())throw new IOException("Pièce jointe absente");f.getString("name");}
        }
        JSONArray ts=db.optJSONArray("templates");if(db.has("templates")&&ts==null)throw new IOException("Modèles invalides");
        if(ts!=null)for(int i=0;i<ts.length();i++){JSONObject t=ts.getJSONObject(i);t.getString("name");fields(t.getJSONArray("fields"));}
    }
    static void fields(JSONArray fields)throws Exception {
        Set<String> ids=new HashSet<>();for(int i=0;i<fields.length();i++){JSONObject f=fields.getJSONObject(i);String id=f.getString("id");if(id.isEmpty()||!ids.add(id))throw new IOException("Champ invalide");f.getString("name");f.getString("type");f.getString("value");}
    }
}
