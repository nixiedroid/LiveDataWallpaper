package com.ustwo.lwp.wallpapers.util.owm;

import android.text.format.DateFormat;
import com.ustwo.lwp.wallpapers.util.owm.enums.ErrorResult;
import org.json.JSONObject;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.Locale;

public class Util {

    public static int getWeatherIdFromJson(String json) throws OWMConnectorException {
        try {
            JSONObject object = new JSONObject(json).getJSONArray("weather").getJSONObject(0);
            return object.getInt("id");
        } catch (Exception e) {
            throw new OWMConnectorException(ErrorResult.INTERNAL_FAILED_TO_PARSE_JSON);
        }
    }

    public static URL concatenateURL(URL url, String query) {
        try {
            if (url.getQuery() == null)
                return new URL(url.getProtocol(), url.getHost(), url.getPort(), url.getFile() + "?" + query);
            if (url.getQuery().isEmpty())
                return new URL(url.getProtocol(), url.getHost(), url.getPort(), url.getFile() + "?" + query);
            return new URL(url.getProtocol(), url.getHost(), url.getPort(), url.getFile() + "&" + query);
        } catch (MalformedURLException ex) {
            throw new IllegalArgumentException("Could not concatenate given URL with GET arguments!", ex);
        }
    }

    public static URL constantURL(String url) {
        try {
            return new URL(url);
        } catch (MalformedURLException ex) {
            throw new RuntimeException("Couldn't create constant for " + url, ex);
        }
    }

    public static String toString(final InputStream input, final Charset encoding) throws IOException {
        final StringBuilder output = new StringBuilder();
        final InputStreamReader in = new InputStreamReader(input, encoding);
        final char[] buffer = new char[4096];
        int n;
        while (-1 != (n = in.read(buffer))) {
            output.append(buffer, 0, n);
        }
        return output.toString();
    }

    public static String epochToString(long epochMs) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(epochMs);
        return DateFormat.format("dd-MM-yyyy  hh:mm", cal).toString();
    }

    public static void closeQuietly(final Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (final IOException ignored) {
        }
    }


}
