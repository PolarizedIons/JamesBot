package net.polarizedions.jamesbot.apis.apiutil;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class WebHelper {
    private static final Logger logger = LogManager.getLogger("WebHelper");

    @Nullable
    public static String fetchPart(String uri, int size) {
        URL url;
        try {
            url = new URL(uri);
        }
        catch (MalformedURLException e) {
            logger.error("Error fetching url part: Malformed url!", e);
            return null;
        }

        HttpURLConnection httpConn;
        try {
            httpConn = (HttpURLConnection)url.openConnection();
        }
        catch (IOException e) {
            logger.error("Error fetching url part: IOException while opening connection!", e);
            return null;
        }
        httpConn.addRequestProperty("User-Agent", HTTPRequest.USER_AGENT);
        InputStream is;
        try {
            is = httpConn.getInputStream();
        }
        catch (IOException e) {
            logger.error("Error fetching url part: Can't open stream!", e);
            return null;
        }

        StringBuilder output = new StringBuilder();
        InputStreamReader reader = new InputStreamReader(is);
        int read = 0;
        try {
            while (read < size) {
                char[] buffer = new char[512];
                int readNow = reader.read(buffer, 0, 512);
                System.out.println("reading " + readNow);
                if (readNow == -1) {
                    break;
                }
                read += readNow;
                output.append(buffer);
            }
        }
        catch (IOException e) {
            logger.error("Error fetching url part: Stream Reader not ready!", e);
            return null;
        }

        return output.toString();
    }

    // From: https://stackoverflow.com/a/14424783
    public static String encodeURIComponent(String s) {
        String result;

        try {
            result = URLEncoder.encode(s, "UTF-8")
                    .replaceAll("\\+", "%20")
                    .replaceAll("\\%21", "!")
                    .replaceAll("\\%27", "'")
                    .replaceAll("\\%28", "(")
                    .replaceAll("\\%29", ")")
                    .replaceAll("\\%7E", "~");
        }
        catch (UnsupportedEncodingException e) {
            result = s;
        }

        return result;
    }
}
