package net.polarizedions.jamesbot.apis;

import com.google.gson.JsonObject;
import net.polarizedions.jamesbot.config.BotConfig;
import net.polarizedions.jamesbot.core.BuildInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

public class Twitter {
    private static final String AUTHENTICATION_URL = "https://api.twitter.com/oauth2/token";
    private static final String SHOW_TWEET_URL = "https://api.twitter.com/1.1/statuses/show.json?id=";

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

        JsonObject response = Util.parser.parse(new InputStreamReader(Objects.requireNonNull(Util.request(AUTHENTICATION_URL, true, headers, "grant_type=client_credentials")))).getAsJsonObject();

        if (! response.get("token_type").getAsString().equalsIgnoreCase("bearer")) {
            throw new IllegalStateException("Didn't get a bearer token!");
        }

        this.bearerCode = response.get("access_token").getAsString();
        this.bearerHeader = new ArrayList<>();
        this.bearerHeader.add(new String[] {"Authorization", "Bearer " + this.bearerCode});

        System.out.println("Successfully authenticated! ");
    }

    public Tweet getTweet(long id) {
        if (! this.isAuthed()) {
            return null;
        }

        JsonObject tweetJson = this.getJson(SHOW_TWEET_URL + id);
        System.out.println(tweetJson);

        return new Tweet();
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

    }
}
