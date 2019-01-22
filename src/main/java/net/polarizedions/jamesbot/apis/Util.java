package net.polarizedions.jamesbot.apis;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.List;

public class Util {
    public static final String USER_AGENT = "Mozilla/5.0 (X11; Linux x86_64; rv:64.0) Gecko/20100101 Firefox/64.0";
    private static final Logger logger = LogManager.getLogger("WebHelper");
    static final JsonParser parser = new JsonParser();

    public static JsonObject post(String uri, JsonObject data) {
        InputStream is = request(uri, true, Collections.emptyList(), data == null ? "" : data.toString());
        return is == null ? null : parser.parse(new InputStreamReader(is)).getAsJsonObject();
    }

    @Nullable
    public static InputStream request(String uri, boolean isPost, List<String[]> headers, String body) {
        logger.debug("Fetching url: " + uri);
        URL url;
        try {
            url = new URL(uri);
        } catch (MalformedURLException e) {
            logger.error("Error fetching url: Malformed url!", e);
            return null;
        }

        HttpURLConnection httpConn;
        try {
            httpConn = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            logger.error("Error fetching url: IOException while opening connection!", e);
            return null;
        }
        httpConn.addRequestProperty("User-Agent", USER_AGENT);

        boolean contentTypeSet = false;
        for (String[] header : headers) {
            httpConn.addRequestProperty(header[0], header[1]);
            if (header[0].equalsIgnoreCase("Content-Type")) {
                contentTypeSet = true;
            }
        }

        if (isPost) {
            try {
                httpConn.setRequestMethod("POST");
            } catch (ProtocolException e) {
                logger.error("Error post'ing url: ProtocolException while setting request method!", e);
                return null;
            }

            if (!body.isEmpty()) {
                if (! contentTypeSet) {
                    httpConn.setRequestProperty("Content-Type", "application/json");
                }

                httpConn.setDoOutput(true);

                DataOutputStream dataOutputStream = null;
                try {
                    dataOutputStream = new DataOutputStream(httpConn.getOutputStream());
                    dataOutputStream.writeBytes(body);
                    dataOutputStream.flush();
                    dataOutputStream.close();
                } catch (IOException e) {
                    logger.error("Error post'ing url: IOException while sending body!", e);
                    return null;
                }
            }
        }

        try {
            return httpConn.getInputStream();
        } catch (IOException e) {
            logger.error("Error fetching url: Can't open stream!", e);
            return null;
        }
    }

    public static String postString(String uri, JsonObject data) {
        InputStream is = request(uri, true, Collections.emptyList(), data == null ? "" : data.toString());

        if (is == null) {
            return null;
        }

        StringBuilder response = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line).append("\n");
            }
        } catch (IOException e) {
            logger.error("Error reading post response into string! {}", e);
        }

        return response.toString();
    }

    @Nullable
    public static JsonObject getJson(String uri) {
        InputStream is = get(uri);
        return is == null ? null : parser.parse(new InputStreamReader(is)).getAsJsonObject();
    }

    public static InputStream get(String uri) {
        return request(uri, false, Collections.emptyList(), null);
    }

    @Nullable
    public static String fetchPart(String uri, int size) {
        URL url;
        try {
            url = new URL(uri);
        } catch (MalformedURLException e) {
            logger.error("Error fetching url part: Malformed url!", e);
            return null;
        }

        HttpURLConnection httpConn;
        try {
            httpConn = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            logger.error("Error fetching url part: IOException while opening connection!", e);
            return null;
        }
        httpConn.addRequestProperty("User-Agent", USER_AGENT);
        InputStream is;
        try {
            is =  httpConn.getInputStream();
        } catch (IOException e) {
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
        catch(IOException e) {
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
        } catch (UnsupportedEncodingException e) {
            result = s;
        }

        return result;
    }
}
