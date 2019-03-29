package net.polarizedions.jamesbot.apis;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import net.polarizedions.jamesbot.apis.apiutil.HTTPRequest;
import net.polarizedions.jamesbot.apis.apiutil.HTTPResponse;
import net.polarizedions.jamesbot.utils.Pair;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class MojiraAPI {
    private static final String ISSUE_API_URL = "https://bugs.mojang.com/rest/api/2/issue/%s";

    @Contract("_ -> new")
    @NotNull
    public static Pair<STATUS, MojiraIssue> getIssue(String key) {
        HTTPResponse response = HTTPRequest.GET(String.format(ISSUE_API_URL, key))
                .doRequest();

        if (response == null) {
            return new Pair<>(STATUS.UNKNOWN, null);
        }

        if (response.getCode() == 401) {
            return new Pair<>(STATUS.NO_PERMISSION, null);
        }
        else if (response.getCode() == 404) {
            return new Pair<>(STATUS.NOT_FOUND, null);
        }
        else if (response.getCode() != 200) {
            return new Pair<>(STATUS.UNKNOWN, null);
        }

        JsonObject json = response.asJsonObject();

        if (json == null) {
            return new Pair<>(STATUS.UNKNOWN, null);
        }

        String summery = json.getAsJsonObject("fields").get("summary").getAsString();
        JsonElement resolution = json.getAsJsonObject("fields").get("resolution");
        JsonArray fixVersions = json.getAsJsonObject("fields").getAsJsonArray("fixVersions");
        JsonArray issueLinks = json.getAsJsonObject("fields").getAsJsonArray("issuelinks");

        MojiraIssue issue = new MojiraIssue();
        issue.project = json.getAsJsonObject("fields").get("project").getAsJsonObject().get("key").getAsString();
        issue.key = json.get("key").getAsString();
        issue.description = summery.length() > 70 ? summery.substring(0, 69) + '\u2026' : summery;
        issue.state = !(resolution instanceof JsonNull) ? resolution.getAsJsonObject().get("name").getAsString() : "Open";
        issue.fixVersion = fixVersions.size() > 0 ? fixVersions.get(0).getAsJsonObject().get("name").getAsString().replace("Minecraft", "") : null;
        issue.duplicate = issueLinks.size() > 0 && issueLinks.get(0).getAsJsonObject().get("outwardIssue") != null ? issueLinks.get(0).getAsJsonObject().getAsJsonObject("outwardIssue").get("key").getAsString() : null;

        return new Pair<>(STATUS.OK, issue);
    }

    public enum STATUS {
        OK,
        NO_PERMISSION,
        NOT_FOUND,
        UNKNOWN,
    }

    public static class MojiraIssue {
        public String project;
        public String key;
        public String description;
        public String state;
        public String fixVersion;
        public String duplicate;
    }
}
