package net.polarizedions.jamesbot.apis;

import com.google.gson.JsonObject;

public class QMarkAPI {
    private static final String URL = "http://qmarkai.com/qmai.php?q=%s";

    public static String ask(String question) {
        String response = Util.postString(String.format(URL, Util.encodeURIComponent(question)), null);
        return response.split("\n")[0];
    }
}
