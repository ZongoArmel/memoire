package bf.memoire;
import org.json.*;
public class OrganizationTest {
 static int checks;static void check(boolean v){checks++;if(!v)throw new AssertionError("check "+checks);}
 static JSONObject note(String space,String project,String status)throws Exception{return new JSONObject().put("id",java.util.UUID.randomUUID().toString()).put("space",space).put("project",project).put("status",status).put("fields",new JSONArray());}
 public static void main(String[] args)throws Exception{
  JSONObject a=note("Travail","Réseau","Terminé"),b=note("Études","Réseau","À revoir"),c=note("Travail","Réseau","En cours").put("archived",true),d=note("Travail","Réseau","En cours").put("deleted",true);
  JSONObject db=new JSONObject().put("notes",new JSONArray().put(a).put(b).put(c).put(d)).put("searches",new JSONArray().put(new JSONObject().put("space","Travail").put("project","Réseau")));
  JSONArray catalog=ProjectCatalog.ensure(db);check(catalog.length()==2);check(ProjectCatalog.ensure(db).length()==2);
  JSONObject p=ProjectCatalog.find(catalog,"Travail","Réseau"),s=ProjectCatalog.summary(db.getJSONArray("notes"),p);check(s.getInt("total")==2&&s.getInt("done")==1&&s.getInt("archived")==1);
  ProjectCatalog.rename(db,p,"Infrastructure");check(a.getString("project").equals("Infrastructure")&&b.getString("project").equals("Réseau"));check(db.getJSONArray("searches").getJSONObject(0).getString("project").equals("Infrastructure"));
  catalog.put(ProjectCatalog.newProject("Travail","Autre"));boolean rejected=false;try{ProjectCatalog.rename(db,p,"Autre");}catch(IllegalArgumentException e){rejected=true;}check(rejected&&p.getString("name").equals("Infrastructure"));
  JSONObject incoming=new JSONObject().put("notes",new JSONArray()).put("projects",new JSONArray().put(ProjectCatalog.newProject("Travail","Nouveau").put("goal","Objectif")));ProjectCatalog.merge(db,incoming,"Études");check(ProjectCatalog.find(catalog,"Travail","Nouveau")==null);ProjectCatalog.merge(db,incoming,"");check(ProjectCatalog.find(catalog,"Travail","Nouveau").getString("goal").equals("Objectif"));
  ProjectCatalog.remove(db,p);check(db.getJSONArray("notes").length()==4&&a.getString("project").isEmpty()&&b.getString("project").equals("Réseau"));check(db.getJSONArray("searches").getJSONObject(0).getString("project").isEmpty());
  check(RelationLinks.ids("a\r\nb\na\n").size()==2);a.getJSONArray("fields").put(new JSONObject().put("type","Relations multiples").put("value",b.getString("id")+"\nx"));check(RelationLinks.references(a,b.getString("id")));check(!RelationLinks.references(a,"x1"));check(RelationLinks.incoming(db.getJSONArray("notes"),b.getString("id")).size()==1);
  JSONObject preset=SearchPreset.normalize(new JSONObject().put("favorite",true).put("archive",true).put("afterDate",123).put("space","Travail"));check(preset.getBoolean("favorite")&&preset.getBoolean("archive")&&preset.getLong("afterDate")==123);JSONObject old=SearchPreset.normalize(new JSONObject().put("afterDate",-1));check(!old.getBoolean("favorite")&&old.getLong("afterDate")==0&&old.getString("space").equals("Études"));
  System.out.println(checks+" organization checks passed");
 }
}
