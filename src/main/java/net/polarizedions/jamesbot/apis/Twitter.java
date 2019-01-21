package net.polarizedions.jamesbot.apis;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.polarizedions.jamesbot.config.BotConfig;
import net.polarizedions.jamesbot.core.BuildInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

public class Twitter {
    private static final Logger logger = LogManager.getLogger("TwitterAPI");
    private static final String AUTHENTICATION_URL = "https://api.twitter.com/oauth2/token";
    private static final String SHOW_TWEET_URL = "https://api.twitter.com/1.1/statuses/show.json?id=";
    private static final SimpleDateFormat TWITTER_DATE_FORMAT = new SimpleDateFormat("E L d HH:mm:ss Z yyyy");  // Mon Jan 21 12:14:14 +0000 2019

    private String bearerCode;
    private List<String[]> bearerHeader;

    public Twitter(@NotNull BotConfig config) {
        this.auth(config.apiKeys.twitterApiKey, config.apiKeys.twitterApiSecret);
    }

    private void auth(@NotNull String consumerKey, @NotNull String consumerSecret) {
        if (consumerKey.isEmpty() || consumerSecret.isEmpty()) {
            return;
        }

        String encoded = new String(Base64.getEncoder().encode(((Util.encodeURIComponent(consumerKey) + ":" + Util.encodeURIComponent(consumerSecret)).getBytes())));

        List<String[]> headers = new ArrayList<>();
        headers.add(new String[] {"Authorization", "Basic " + encoded});
        headers.add(new String[] {"Content-Type", "application/x-www-form-urlencoded;charset=UTF-8"});
        headers.add(new String[] {"User-Agent", "Jamesbot v" + BuildInfo.version});

        InputStream is = Util.request(AUTHENTICATION_URL, true, headers, "grant_type=client_credentials");
        if (is == null) {
            logger.error("Error getting twitter bearer token! Please check your consumer key & secret!");
            throw new IllegalStateException("Unable to get bearer token.");
        }
        JsonObject response = Util.parser.parse(new InputStreamReader(is)).getAsJsonObject();

        if (! response.get("token_type").getAsString().equalsIgnoreCase("bearer")) {
            logger.error("Twitter gave me " + response.get("token_type").getAsString() + " but I expected a bearer token!");
            throw new IllegalStateException("Didn't get a bearer token!");
        }

        this.bearerCode = response.get("access_token").getAsString();
        this.bearerHeader = new ArrayList<>();
        this.bearerHeader.add(new String[] {"Authorization", "Bearer " + this.bearerCode});

        logger.debug("Successfully authenticated!");
    }

    @Nullable
    public Tweet getTweet(long id) {
        if (! this.isAuthed()) {
            return null;
        }

        JsonObject tweetJson = this.getJson(SHOW_TWEET_URL + id);

        if (tweetJson == null) {
            return null;
        }

        Tweet tweet = new Tweet();

        try {
            tweet.createdAt = TWITTER_DATE_FORMAT.parse(tweetJson.get("created_at").getAsString());
        } catch (ParseException e) {
            tweet.createdAt = Date.from(Instant.ofEpochSecond(1));
        }
        tweet.id = tweetJson.get("id").getAsLong();

        String text = tweetJson.get("text").getAsString();
        for (JsonElement urlJson : tweetJson.getAsJsonObject("entities").getAsJsonArray("urls")) {
            String fullUrl = urlJson.getAsJsonObject().get("expanded_url").getAsString();
            JsonArray indices = urlJson.getAsJsonObject().getAsJsonArray("indices");
            int start = indices.get(0).getAsInt();
            int end = indices.get(1).getAsInt();

            String textBefore = text.substring(0, start);
            String textEnd = text.substring(end);
            text = textBefore + fullUrl + textEnd;
        }

        tweet.text = text;

        JsonObject userJson = tweetJson.getAsJsonObject("user");
        tweet.userDisplayName = userJson.get("name").getAsString();
        tweet.userUsername = userJson.get("screen_name").getAsString();

        tweet.retweetCount = tweetJson.get("retweet_count").getAsInt();
        tweet.favouriteCount = tweetJson.get("favorite_count").getAsInt();

        return tweet;
    }

    @Nullable
    private JsonObject getJson(String uri) {
        InputStream is = Util.request(uri, false, this.bearerHeader, null);
        return is == null ? null : Util.parser.parse(new InputStreamReader(is)).getAsJsonObject();
    }

    public boolean isAuthed() {
        return ! this.bearerCode.isEmpty();
    }

    public static class Tweet {
        public Date createdAt;
        public long id;
        public String text;
        public String userDisplayName;
        public String userUsername;
        public int retweetCount;
        public int favouriteCount;
    }
}
