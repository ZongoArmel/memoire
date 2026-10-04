package bf.memoire;

import org.json.*;
import java.util.*;

/** Project metadata is separate from notes. Legacy project names remain valid. */
final class ProjectCatalog {
    static JSONArray ensure(JSONObject db) throws JSONException {
        JSONArray projects = db.optJSONArray("projects");
        if (projects == null) { projects = new JSONArray(); db.put("projects", projects); }
        JSONArray notes = db.optJSONArray("notes");
        if (notes != null) for (int i = 0; i < notes.length(); i++) {
            JSONObject note = notes.optJSONObject(i);
            if (note == null || note.optString("project").trim().isEmpty()) continue;
            String space = note.optString("space", "Études");
            String name = note.optString("project").trim();
            if (find(projects, space, name) == null) projects.put(newProject(space, name));
        }
        return projects;
    }
    static JSONObject newProject(String space, String name) throws JSONException {
        if (name.trim().isEmpty()) throw new IllegalArgumentException("Un nom est nécessaire");
        return new JSONObject().put("id", UUID.randomUUID().toString()).put("space", space)
            .put("name", name.trim()).put("goal", "").put("nextAction", "").put("status", "En cours");
    }
    static JSONObject find(JSONArray projects, String space, String name) {
        for (int i = 0; i < projects.length(); i++) {
            JSONObject p = projects.optJSONObject(i);
            if (p != null && p.optString("space").equals(space) && p.optString("name").equals(name)) return p;
        }
        return null;
    }
    static JSONObject summary(JSONArray notes, JSONObject project) throws JSONException {
        int total = 0, done = 0, active = 0, archived = 0, review = 0;
        for (int i = 0; i < notes.length(); i++) {
            JSONObject n = notes.optJSONObject(i);
            if (n == null || n.optBoolean("deleted") || !n.optString("space", "Études").equals(project.optString("space"))
                || !n.optString("project").equals(project.optString("name"))) continue;
            total++;
            if (n.optBoolean("archived")) { archived++; continue; }
            String state = n.optString("status");
            if (state.equals("Résolu") || state.equals("Terminé")) done++;
            else { active++; if (state.equals("À revoir")) review++; }
        }
        return new JSONObject().put("total", total).put("done", done).put("active", active)
            .put("archived", archived).put("review", review);
    }
    static void rename(JSONObject db, JSONObject project, String name) throws JSONException {
        String value = name.trim();
        if (value.isEmpty()) throw new IllegalArgumentException("Un nom est nécessaire");
        JSONObject other = find(ensure(db), project.optString("space"), value);
        if (other != null && other != project) throw new IllegalArgumentException("Ce nom existe déjà dans cet espace");
        String before = project.getString("name"), space = project.getString("space");
        JSONArray notes = db.getJSONArray("notes");
        for (int i = 0; i < notes.length(); i++) {
            JSONObject note = notes.getJSONObject(i);
            if (note.optString("space", "Études").equals(space) && note.optString("project").equals(before)) note.put("project", value);
        }
        JSONArray searches = db.optJSONArray("searches");
        if (searches != null) for (int i = 0; i < searches.length(); i++) {
            JSONObject q = searches.getJSONObject(i);
            if (q.optString("space").equals(space) && q.optString("project").equals(before)) q.put("project", value);
        }
        project.put("name", value);
    }
    static void merge(JSONObject db, JSONObject incoming, String scope) throws JSONException {
        JSONArray target=ensure(db), source=incoming.optJSONArray("projects");
        if(source!=null)for(int i=0;i<source.length();i++){
            JSONObject p=source.getJSONObject(i);String space=p.getString("space");
            if((scope.isEmpty()||scope.equals(space))&&find(target,space,p.getString("name"))==null)target.put(new JSONObject(p.toString()));
        }
    }
    static void remove(JSONObject db, JSONObject project) throws JSONException {
        String space = project.getString("space"), name = project.getString("name");
        JSONArray notes = db.getJSONArray("notes");
        for (int i = 0; i < notes.length(); i++) {
            JSONObject n = notes.getJSONObject(i);
            if (n.optString("space", "Études").equals(space) && n.optString("project").equals(name)) n.put("project", "");
        }
        JSONArray searches=db.optJSONArray("searches");
        if(searches!=null)for(int i=0;i<searches.length();i++){JSONObject q=searches.getJSONObject(i);if(q.optString("space").equals(space)&&q.optString("project").equals(name))q.put("project", "");}
        JSONArray projects = db.getJSONArray("projects");
        for (int i = projects.length() - 1; i >= 0; i--) if (projects.optJSONObject(i) == project) projects.remove(i);
    }
}
