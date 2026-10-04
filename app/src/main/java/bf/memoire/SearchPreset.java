package bf.memoire;

import org.json.*;

/** Defaults keep older saved searches usable without inheriting unrelated UI filters. */
final class SearchPreset {
    static JSONObject normalize(JSONObject source) throws JSONException {
        return new JSONObject().put("name", source.optString("name")).put("query", source.optString("query"))
            .put("tag", source.optString("tag")).put("project", source.optString("project"))
            .put("status", source.optString("status")).put("space", source.optString("space", "Études"))
            .put("global", source.optBoolean("global")).put("favorite", source.optBoolean("favorite"))
            .put("afterDate", Math.max(0, source.optLong("afterDate"))).put("inbox", source.optBoolean("inbox"))
            .put("archive", source.optBoolean("archive"));
    }
}
