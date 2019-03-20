package net.polarizedions.jamesbot.apis;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.Nullable;

public class MediaWiki {
    @Nullable
    public static String searchPage(String domain, String query) {
        String url = domain + "/api.php?action=query&list=search&utf8=&format=json&srsearch=" + APIUtil.encodeURIComponent(query);
        JsonObject json = APIUtil.getJson(url);

        if (json == null) {
            return null;
        }

        json = json.getAsJsonObject("query");
        if (json.getAsJsonObject("searchinfo").get("totalhits").getAsInt() == 0) {
            return null;
        }

        JsonObject result = json.getAsJsonArray("search").get(0).getAsJsonObject();

        return domain + "/" + APIUtil.encodeURIComponent(result.get("title").getAsString());
    }
}
