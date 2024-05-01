package com.ustwo.lwp.wallpapers.util.owm;

import com.ustwo.lwp.wallpapers.util.owm.enums.ErrorResult;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.concurrent.*;
//https://openweathermap.org/api/one-call-api

public class HttpUrlConnector {
    public static final URL API_URL = Util.constantURL("https://api.openweathermap.org/data/2.5/weather");

    private static HttpURLConnection createUrlConnection(URL url) throws OWMConnectorException {
        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection(Proxy.NO_PROXY);
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-length", "0");
            connection.setUseCaches(false);
            connection.setAllowUserInteraction(false);
            return connection;
        } catch (IOException ignored) {
            throw new OWMConnectorException(ErrorResult.INTERNAL_INVALID_URL);
        }
    }

    public static OWMWeatherCode getWeatherCode(String APIKey, double lat, double lon) throws OWMConnectorException {
        ExecutorService exec = Executors.newSingleThreadExecutor();
        //lat={lat}&lon={lon}&appid={API key}
        final URL url =
                Util.concatenateURL(
                Util.concatenateURL(
                Util.concatenateURL(
                        API_URL, "lat=" + lat), "lon=" + lon), "appid=" + APIKey);
        Future<OWMWeatherCode> cod = exec.submit(() -> {
            HttpURLConnection connection =  createUrlConnection(url);
            int weatherId = Util.getWeatherIdFromJson(readInputStream(connection));
            return new OWMWeatherCode(weatherId);
        });
        OWMWeatherCode responseCode;
        try {
            responseCode = cod.get();
        } catch (InterruptedException | CancellationException e) {
            throw new OWMConnectorException(e.getMessage());
        } catch (ExecutionException e){
            if (e.getCause() != null && e.getCause() instanceof OWMConnectorException) {
                throw (OWMConnectorException) e.getCause();
            }
           throw new OWMConnectorException(e.getMessage());
        }
        return responseCode;
    }

    /** @noinspection CharsetObjectCanBeUsed*/
    private static String readInputStream(final HttpURLConnection connection) throws OWMConnectorException {
        InputStream inputStream = null;
        try {
            final int status = connection.getResponseCode();
            if (status < 400) {
                inputStream = connection.getInputStream();
                final String result = Util.toString(inputStream, Charset.forName("UTF-8"));
                if (result.isEmpty()) {
                    throw new OWMConnectorException(ErrorResult.NO_INTERNET);
                }
                return result;
            } else if (status == HttpURLConnection.HTTP_UNAUTHORIZED || status == HttpURLConnection.HTTP_FORBIDDEN) {
                throw new OWMConnectorException(ErrorResult.INVALID_API_KEY);
            } else if (status == 429) {
                throw new OWMConnectorException(ErrorResult.INTERNAL_TOO_MUCH_REQUESTS);
            } else if (status >= 500) {
                throw new OWMConnectorException(ErrorResult.INTERNAL_SERVER_ERROR);
            }
            throw new OWMConnectorException(ErrorResult.UNKNOWN);
        } catch (final IOException e) {
            throw new OWMConnectorException(ErrorResult.NO_INTERNET);
        } finally {
            Util.closeQuietly(inputStream);
        }
    }
}
