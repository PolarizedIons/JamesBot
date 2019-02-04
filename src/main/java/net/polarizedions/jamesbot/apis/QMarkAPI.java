package net.polarizedions.jamesbot.apis;

import org.jetbrains.annotations.NotNull;

public class QMarkAPI {
    private static final String URL = "http://qmarkai.com/qmai.php?q=%s";

    @NotNull
    public static String ask(String question) {
        String response = Util.postString(String.format(URL, Util.encodeURIComponent(question)), null);
        if (response == null) {
            return "";
        }

        return String.join(" ", response.replaceAll("\r", "").split("\n"));
    }
}
