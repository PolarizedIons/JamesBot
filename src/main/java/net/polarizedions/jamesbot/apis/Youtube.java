package net.polarizedions.jamesbot.apis;

import com.google.gson.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Youtube {
    private static final String VIDEO_INFO_URL = "https://www.googleapis.com/youtube/v3/videos?part=snippet&id=%s&fields=items(ageGating%%2CcontentDetails(countryRestriction%%2Cduration)%%2Cid%%2Ckind%%2CliveStreamingDetails%%2FconcurrentViewers%%2CmonetizationDetails%%2Csnippet(channelTitle%%2Cdescription%%2Ctitle)%%2Cstatistics%%2Cstatus(privacyStatus%%2CpublishAt))&key=%s";
    private static final Pattern youtubeLinkPattern = Pattern.compile("(?:youtube.com/.*\\?.*v=(.+).*|youtu.be/(.+))");
    private static final Logger logger = LogManager.getLogger("YoutubeAPI");
    private final String API_KEY;


    public Youtube(String apiKey) {
        this.API_KEY = apiKey;
    }

    public YoutubeVideo getVideo(String link) {
        String id = link;
        if (link.startsWith("http")) {
            Matcher matcher = youtubeLinkPattern.matcher(link);

            if (!matcher.matches()) {
                return null;
            }

            id = matcher.group(1).isEmpty() ? matcher.group(2) : matcher.group(1);
        }
        logger.debug("Fetching video {} ({})", link, id);

        JsonObject json = Util.getJson(String.format(VIDEO_INFO_URL, Util.encodeURIComponent(id), Util.encodeURIComponent(API_KEY))).getAsJsonArray("items").get(0).getAsJsonObject();

        YoutubeVideo video = new YoutubeVideo();
        video.kind = Kind.getFor(json.get("kind").getAsString());
        video.id = json.get("id").getAsString();
        video.channel = json.getAsJsonObject("snippet").get("channelTitle").getAsString();
        video.title = json.getAsJsonObject("snippet").get("title").getAsString();
        video.desctiption = json.getAsJsonObject("snippet").get("description").getAsString();

        System.out.println("done" + video);
        return video;
    }


    public enum Kind {
        VIDEO("youtube#video");

        String typeStr;
        Kind(String typeStr) {
            this.typeStr = typeStr;
        }

        @Nullable
        public static Kind getFor(String typeStr) {
            for (Kind k : Kind.values()) {
                if (k.typeStr.equals(typeStr)) {
                    return k;
                }
            }

            return null;
        }
    }

    public static class YoutubeVideo {
        public Kind kind;
        public String id;
        public String title;
        public String desctiption;
        public String channel;

        // REST????
    }
}
