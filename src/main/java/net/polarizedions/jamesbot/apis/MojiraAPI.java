package net.polarizedions.jamesbot.apis;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import net.polarizedions.jamesbot.utils.Pair;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.io.InputStreamReader;

public class MojiraAPI {
    private static final String ISSUE_API_URL = "https://bugs.mojang.com/rest/api/2/issue/%s";

    @Contract("_ -> new")
    @NotNull
    public static Pair<API_RETURN, MojiraIssue> getIssue(String key) {
        Pair<Integer, InputStream> httpResp = APIUtil.get(String.format(ISSUE_API_URL, key));
        if (httpResp == null) {
            return new Pair<>(API_RETURN.UNKNOWN, null);
        }

        if (httpResp.getOne() == 401) {
            return new Pair<>(API_RETURN.NO_PERMISSION, null);
        }
        if (httpResp.getOne() == 404) {
            return new Pair<>(API_RETURN.NOT_FOUND, null);
        }


        JsonObject json = APIUtil.parser.parse(new InputStreamReader(httpResp.getTwo())).getAsJsonObject();

        if (json == null) {
            return new Pair<>(API_RETURN.UNKNOWN, null);
        }

        if (json.get("errorMessages") != null) {
            String errMsg = json.getAsJsonArray("errorMessages").get(0).getAsString();

            if (errMsg.equals("Issue Does Not Exist")) {
                return new Pair<>(API_RETURN.NOT_FOUND, null);
            }

            return new Pair<>(API_RETURN.UNKNOWN, null);
        }

        String summery = json.getAsJsonObject("fields").get("summary").getAsString();
        JsonElement resolution = json.getAsJsonObject("fields").get("resolution");
        JsonArray fixVersions = json.getAsJsonObject("fields").getAsJsonArray("fixVersions");
        JsonArray issueLinks = json.getAsJsonObject("fields").getAsJsonArray("issuelinks");

        MojiraIssue issue = new MojiraIssue();
        issue.key = json.get("key").getAsString();
        issue.description = summery.length() > 70 ? summery.substring(0, 69) + '\u2026' : summery;
        issue.state = !(resolution instanceof JsonNull) ? resolution.getAsJsonObject().get("name").getAsString() : "Open";
        issue.fixVersion = fixVersions.size() > 0 ? fixVersions.get(0).getAsJsonObject().get("name").getAsString().replace("Minecraft", "") : null;
        issue.duplicate = issueLinks.size() > 0 && issueLinks.get(0).getAsJsonObject().get("outwardIssue") != null ? issueLinks.get(0).getAsJsonObject().getAsJsonObject("outwardIssue").get("key").getAsString() : null;

        return new Pair<>(API_RETURN.ISSUE, issue);
    }

    public enum API_RETURN {
        ISSUE,
        NO_PERMISSION,
        NOT_FOUND,
        UNKNOWN,
    }

    public static class MojiraIssue {
        public String key;
        public String description;
        public String state;
        public String fixVersion;
        public String duplicate;
    }
}
