package net.polarizedions.jamesbot.apis.apiutil;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HTTPRequest {
    static final String USER_AGENT = "Mozilla/5.0 (X11; Linux x86_64; rv:64.0) Gecko/20100101 Firefox/64.0";
    static final Logger logger = LogManager.getLogger("HTTPRequest");

    HTTPMethod method;
    String url;
    Map<String, String> headers;
    String body;

    public HTTPRequest(HTTPMethod method, String url) {
        this.method = method;
        this.url = url;
        this.headers = new HashMap<>();
        this.headers.put("User-Agent", USER_AGENT);
        this.body = null;
    }

    public static HTTPRequest GET(String url) {
        return new HTTPRequest(HTTPMethod.GET, url);
    }

    public static HTTPRequest POST(String url) {
        return new HTTPRequest(HTTPMethod.POST, url);
    }

    public static HTTPRequest POST_JSON(String url, String json) {
        return new HTTPRequest(HTTPMethod.POST, url)
                .setHeader("Content-Type", "application/json")
                .setBody(json);
    }

    public HTTPRequest setMethod(HTTPMethod method) {
        this.method = method;
        return this;
    }

    public HTTPRequest setUrl(String url) {
        this.url = url;
        return this;
    }

    public HTTPRequest setHeader(String key, String value) {
        this.headers.put(key, value);
        return this;
    }

    public HTTPRequest setBody(String body) {
        this.body = body;
        return this;
    }

    public HTTPResponse doRequest() {
        logger.debug("New {} request to {}", this.method, this.url);
        URL url;
        try {
            url = new URL(this.url);
        }
        catch (MalformedURLException e) {
            logger.error("Malformed url!", e);
            return null;
        }

        HttpURLConnection httpConn;
        try {
            httpConn = (HttpURLConnection)url.openConnection();
        }
        catch (IOException e) {
            logger.error("IOException while opening connection!", e);
            return null;
        }

        for (Map.Entry<String, String> entry : this.headers.entrySet()) {
            httpConn.addRequestProperty(entry.getKey(), entry.getValue());
        }

        if (this.body != null && ! this.body.isEmpty()) {
            httpConn.setDoOutput(true);

            DataOutputStream dataOutputStream = null;
            try {
                dataOutputStream = new DataOutputStream(httpConn.getOutputStream());
                dataOutputStream.writeBytes(this.body);
                dataOutputStream.flush();
                dataOutputStream.close();
            }
            catch (IOException e) {
                logger.error("IOException while sending body!", e);
                return null;
            }
        }

        int responseCode;
        StringBuilder responseBody = new StringBuilder();
        Map<String, String> responseHeaders = new HashMap<>();

        try {
            responseCode = httpConn.getResponseCode();
        }
        catch (IOException e) {
            responseCode = 500;
        }

        for (Map.Entry<String, List<String>> entry : httpConn.getHeaderFields().entrySet()) {
            responseHeaders.put(entry.getKey(), String.join(" ", entry.getValue()));
        }

        InputStream is = null;
        try {
            is = httpConn.getInputStream();
        }
        catch (IOException e) {
            logger.error("Error opening input stream");
        }

        if (is != null) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                String line;
                while (( line = reader.readLine() ) != null) {
                    responseBody.append(line).append("\n");
                }
            }
            catch (IOException e) {
                logger.error("Error reading response into string! {}", e);
            }
        }

        return new HTTPResponse(responseCode, responseHeaders, responseBody.toString());
    }

    enum HTTPMethod {
        GET,
        POST,
    }
}
