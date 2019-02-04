package net.polarizedions.jamesbot.apis;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Steam {
    private static final String APP_INFO_URL = "https://store.steampowered.com/api/appdetails/?appids=%s&cc=US";

    @Nullable
    public static SteamApp getApp(int id) {
        JsonObject json = Util.getJson(String.format(APP_INFO_URL, id));

        if (json == null) {
            return null;
        }

        json = json.getAsJsonObject(id + "");
        if (!json.get("success").getAsBoolean()) {
            return null;
        }
        json = json.getAsJsonObject("data");

        SteamApp app = new SteamApp();

        app.name = json.get("name").getAsString();
        app.appId = json.get("steam_appid").getAsInt();

        app.isFree = json.get("is_free").getAsBoolean();

        try {
            app.priceCurrency = json.getAsJsonObject("price_overview").get("currency").getAsString();
            app.price = json.getAsJsonObject("price_overview").get("initial").getAsDouble() / 100.00;
            app.discountPercent = json.getAsJsonObject("price_overview").get("discount_percent").getAsDouble() * 100.00;
            app.isDiscount = app.discountPercent > 0;
            app.finalPriceFormatted = json.getAsJsonObject("price_overview").get("final_formatted").getAsString();
        }
        catch (NullPointerException e) {
            app.priceCurrency = "UNKNOWN";
            app.price = -1;
            app.discountPercent = 0;
            app.isDiscount = false;
            app.finalPriceFormatted = "$ unknown";
        }

        app.availableWindows = json.getAsJsonObject("platforms").get("windows").getAsBoolean();
        app.availableMac = json.getAsJsonObject("platforms").get("mac").getAsBoolean();
        app.availableLinux = json.getAsJsonObject("platforms").get("linux").getAsBoolean();

        app.genres = new ArrayList<>();
        for (JsonElement jsonEntry : json.getAsJsonArray("genres")) {
            app.genres.add(jsonEntry.getAsJsonObject().get("description").getAsString());
        }

        return app;
    }

    public static class SteamApp {
        public String name;
        public int appId;
        public boolean isFree;
        public String priceCurrency;
        public double price;
        public boolean isDiscount;
        public double discountPercent;
        public String finalPriceFormatted;
        public boolean availableWindows;
        public boolean availableMac;
        public boolean availableLinux;
        public List<String> genres;
    }
}
