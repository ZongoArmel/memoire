package bf.memoire;

import java.util.*;
import org.json.*;

final class RelationLinks {
    static List<String> ids(String value) {
        Set<String> ordered = new LinkedHashSet<>();
        for (String id : value.split("\\r?\\n")) { String clean = id.trim(); if (!clean.isEmpty()) ordered.add(clean); }
        return new ArrayList<>(ordered);
    }
    static boolean references(JSONObject note, String id) {
        JSONArray fields = note.optJSONArray("fields");
        if (fields != null) for (int i = 0; i < fields.length(); i++) {
            JSONObject f = fields.optJSONObject(i);
            if (f != null && f.optString("type").startsWith("Relation") && ids(f.optString("value")).contains(id)) return true;
        }
        return false;
    }
    static List<JSONObject> incoming(JSONArray notes, String targetId) {
        List<JSONObject> result = new ArrayList<>();
        for (int i = 0; i < notes.length(); i++) {
            JSONObject n = notes.optJSONObject(i);
            if (n != null && !n.optBoolean("deleted") && !n.optString("id").equals(targetId) && references(n, targetId)) result.add(n);
        }
        return result;
    }
}
