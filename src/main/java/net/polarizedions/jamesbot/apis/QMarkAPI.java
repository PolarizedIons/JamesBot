package net.polarizedions.jamesbot.apis;

import net.polarizedions.jamesbot.apis.apiutil.HTTPRequest;
import net.polarizedions.jamesbot.apis.apiutil.WebHelper;
import org.jetbrains.annotations.NotNull;

public class QMarkAPI {
    private static final String URL = "http://qmarkai.com/qmai.php?q=%s";

    @NotNull
    public static String ask(String question) {
        String response = HTTPRequest.POST(String.format(URL, WebHelper.encodeURIComponent(question)))
                .doRequest()
                .getBody();

        if (response == null) {
            return "";
        }

        return String.join(" ", response.replaceAll("\r", "").split("\n"));
    }
}
