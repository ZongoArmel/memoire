package bf.memoire;
import android.content.*;
import android.database.*;
import android.database.sqlite.*;
import org.json.*;
import java.io.*;
import java.text.Normalizer;
import java.util.*;

final class LocalStore extends SQLiteOpenHelper {
    final Map<String,String> cached=new HashMap<>();
    LocalStore(Context c){super(c,"memoire.sqlite",null,1);setWriteAheadLoggingEnabled(true);}
    public void onCreate(SQLiteDatabase db){db.execSQL("CREATE TABLE metadata (id INTEGER PRIMARY KEY CHECK(id=1), payload TEXT NOT NULL)");db.execSQL("CREATE TABLE notes (id TEXT PRIMARY KEY, payload TEXT NOT NULL, search TEXT NOT NULL, space TEXT, project TEXT, updated INTEGER)");db.execSQL("CREATE INDEX space_project ON notes(space,project)");db.execSQL("CREATE INDEX recent ON notes(updated DESC)");}
    public void onUpgrade(SQLiteDatabase db,int a,int b){throw new IllegalStateException("Migration inconnue");}
    static String normalized(String text){return Normalizer.normalize(text,Normalizer.Form.NFD).replaceAll("\\p{M}","").toLowerCase(Locale.ROOT);}
    static String searchable(JSONObject n){StringBuilder out=new StringBuilder();for(String key:new String[]{"title","body","tags","project","status"})out.append(n.optString(key)).append(' ');for(String group:new String[]{"fields","blocks","files"}){JSONArray a=n.optJSONArray(group);if(a!=null)for(int i=0;i<a.length();i++){JSONObject item=a.optJSONObject(i);if(item!=null)for(String key:new String[]{"name","value","text"})out.append(item.optString(key)).append(' ');}}return normalized(out.toString());}
    JSONObject load()throws Exception {
        SQLiteDatabase sql=getReadableDatabase();JSONObject db;
        try(Cursor c=sql.rawQuery("SELECT payload FROM metadata WHERE id=1",null)){if(!c.moveToFirst())return null;db=new JSONObject(DataCipher.open(c.getString(0)));}
        JSONArray notes=new JSONArray();try(Cursor c=sql.rawQuery("SELECT payload FROM notes ORDER BY updated DESC",null)){while(c.moveToNext()){String payload=DataCipher.open(c.getString(0));JSONObject n=new JSONObject(payload);notes.put(n);cached.put(n.getString("id"),payload);}}db.put("notes",notes);return db;
    }
    void save(JSONObject db)throws Exception {
        SQLiteDatabase sql=getWritableDatabase();sql.beginTransaction();try {
            JSONObject meta=new JSONObject();Iterator<String> keys=db.keys();while(keys.hasNext()){String key=keys.next();if(!key.equals("notes"))meta.put(key,db.opt(key));}ContentValues m=new ContentValues();m.put("id",1);m.put("payload",DataCipher.seal(meta.toString()));sql.insertWithOnConflict("metadata",null,m,SQLiteDatabase.CONFLICT_REPLACE);
            JSONArray notes=db.getJSONArray("notes");Set<String> ids=new HashSet<>();for(int i=0;i<notes.length();i++){JSONObject n=notes.getJSONObject(i);String id=n.getString("id");ids.add(id);String payload=n.toString();if(payload.equals(cached.get(id)))continue;ContentValues v=new ContentValues();v.put("id",id);v.put("payload",DataCipher.seal(payload));v.put("search","");v.put("space","");v.put("project","");v.put("updated",n.optLong("updated"));sql.insertWithOnConflict("notes",null,v,SQLiteDatabase.CONFLICT_REPLACE);}
            List<String> removed=new ArrayList<>();try(Cursor c=sql.rawQuery("SELECT id FROM notes",null)){while(c.moveToNext())if(!ids.contains(c.getString(0)))removed.add(c.getString(0));}for(String id:removed)sql.delete("notes","id=?",new String[]{id});sql.setTransactionSuccessful();
        }finally{sql.endTransaction();}
        cached.clear();JSONArray saved=db.getJSONArray("notes");for(int i=0;i<saved.length();i++){JSONObject n=saved.getJSONObject(i);cached.put(n.getString("id"),n.toString());}
    }
}
