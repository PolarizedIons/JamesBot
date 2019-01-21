package net.polarizedions.jamesbot.apis;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Youtube {
    //    private static final String VIDEO_INFO_URL = "https://www.googleapis.com/youtube/v3/videos?part=snippet&id=%s&fields=items(ageGating%%2CcontentDetails(countryRestriction%%2Cduration)%%2Cid%%2Ckind%%2CliveStreamingDetails%%2FconcurrentViewers%%2CmonetizationDetails%%2Csnippet(channelTitle%%2Cdescription%%2Ctitle)%%2Cstatistics%%2Cstatus(privacyStatus%%2CpublishAt))&key=%s";
    private static final String VIDEO_INFO_URL = "https://www.googleapis.com/youtube/v3/videos?part=snippet,contentDetails,statistics&id=%s&key=%s";
    private static final Pattern youtubeFullLinkPattern = Pattern.compile("youtube\\.com\\/watch?.*&?v=(.+)&?.*?", Pattern.CASE_INSENSITIVE);
    private static final Pattern youtubeShortinkPattern = Pattern.compile("youtu\\.be\\/(.+)", Pattern.CASE_INSENSITIVE);
    private static final Logger logger = LogManager.getLogger("YoutubeAPI");
    private final String API_KEY;


    public Youtube(String apiKey) {
        this.API_KEY = apiKey;
    }

    public YoutubeVideo getVideo(String link) {
        System.out.println("getvideo ");
        String id = link;
        if (link.startsWith("http")) {
            Matcher matcher1 = youtubeFullLinkPattern.matcher(link);
            Matcher matcher2 = youtubeShortinkPattern.matcher(link);

            // Calling .find() twice changes outcome >.> :REEEEEEEEE;
            boolean found1 = matcher1.find();
            boolean found2 = matcher2.find();

            if (!found1 && !found2) {
                System.out.println("doesn't match" + found1 + found2);
                return null;
            }

            id = found1 ? matcher1.group(1) : matcher2.group(1);
        }
        logger.info("Fetching video {} ({})", link, id);

        JsonObject json = Util.getJson(String.format(VIDEO_INFO_URL, Util.encodeURIComponent(id), Util.encodeURIComponent(API_KEY))).getAsJsonArray("items").get(0).getAsJsonObject();

        YoutubeVideo video = new YoutubeVideo();
        video.kind = Kind.getFor(json.get("kind").getAsString());
        video.publishDate = Instant.parse(json.getAsJsonObject("snippet").get("publishedAt").getAsString());
        video.id = json.get("id").getAsString();
        video.title = json.getAsJsonObject("snippet").get("title").getAsString();
        video.channel = json.getAsJsonObject("snippet").get("channelTitle").getAsString();
        video.duration = json.getAsJsonObject("contentDetails").get("duration").getAsString().replaceAll("^PT", "").toLowerCase();
        video.viewCount = Long.parseLong(json.getAsJsonObject("statistics").get("viewCount").getAsString());
        video.likeCount = Long.parseLong(json.getAsJsonObject("statistics").get("likeCount").getAsString());
        video.dislikeCount = Long.parseLong(json.getAsJsonObject("statistics").get("dislikeCount").getAsString());

        video.allowedRegions = new ArrayList<>();
        try {
            for (Map.Entry<String, JsonElement> entry : json.getAsJsonObject("contentDetails").getAsJsonObject("regionRestriction").getAsJsonObject("allowed").entrySet()) {
                video.allowedRegions.add(entry.getValue().getAsString());
            }
        } catch (NullPointerException e) { /* NOOP */}

        video.restrictedRegions = new ArrayList<>();
        try {
            for (Map.Entry<String, JsonElement> entry : json.getAsJsonObject("contentDetails").getAsJsonObject("regionRestriction").getAsJsonObject("allowed").entrySet()) {
                video.restrictedRegions.add(entry.getValue().getAsString());
            }
        } catch (NullPointerException e) { /* NOOP */}

        video.isRegionRestricted = video.restrictedRegions.size() > 0;

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
        public Instant publishDate;
        public String title;
        public String channel;
        public String duration;
        public boolean isRegionRestricted;
        public List<String> allowedRegions;
        public List<String> restrictedRegions;
        public long viewCount;
        public long likeCount;
        public long dislikeCount;
    }
}
