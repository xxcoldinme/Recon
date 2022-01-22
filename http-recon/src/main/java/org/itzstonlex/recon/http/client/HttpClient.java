package org.itzstonlex.recon.http.client;

import org.itzstonlex.recon.http.util.HttpUtils;
import org.itzstonlex.recon.log.ReconLog;
import org.itzstonlex.recon.util.InputUtils;

import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public final class HttpClient {

    private final ReconLog logger = new ReconLog("HttpClient");

    public ReconLog logger() {
        return logger;
    }

    public HttpResponse executeGet(String url) {
        return execute(url, new HttpRequestConfig(HttpUtils.REQUEST_GET));
    }

    public HttpResponse executeHead(String url) {
        return execute(url, new HttpRequestConfig(HttpUtils.REQUEST_HEAD));
    }

    public HttpResponse executePost(String url) {
        return execute(url, new HttpRequestConfig(HttpUtils.REQUEST_POST));
    }

    public HttpResponse executePut(String url) {
        return execute(url, new HttpRequestConfig(HttpUtils.REQUEST_PUT));
    }

    public HttpResponse executeDelete(String url) {
        return execute(url, new HttpRequestConfig(HttpUtils.REQUEST_DELETE));
    }

    public HttpResponse executeConnect(String url) {
        return execute(url, new HttpRequestConfig(HttpUtils.REQUEST_CONNECT));
    }

    public HttpResponse executeOptions(String url) {
        return execute(url, new HttpRequestConfig(HttpUtils.REQUEST_OPTIONS));
    }

    public HttpResponse executeTrace(String url) {
        return execute(url, new HttpRequestConfig(HttpUtils.REQUEST_TRACE));
    }

    public HttpResponse executePatch(String url) {
        return execute(url, new HttpRequestConfig(HttpUtils.REQUEST_PATCH));
    }

    public HttpResponse execute(String url, HttpRequestConfig requestConfig) {
        if (url == null) {
            throw new NullPointerException("url");
        }

        if (requestConfig == null) {
            throw new NullPointerException("requestConfig");
        }

        url = HttpUtils.getProtocol(url) + "://" + HttpUtils.trim(url) + "/";

        try {
            HttpURLConnection connection = ((HttpURLConnection) new URL(url).openConnection());

            connection.setConnectTimeout(requestConfig.getConnectTimeout());
            connection.setReadTimeout(requestConfig.getReadTimeout());
            connection.setRequestMethod(requestConfig.getMethod());

            requestConfig.getProperties().forEach(connection::setRequestProperty);

            // Response handle.
            byte[] callbackArray = InputUtils.toByteArray(connection.getInputStream());
            String callback = new String(callbackArray, 0, callbackArray.length, StandardCharsets.UTF_8);

            HttpResponse httpResponse = HttpResponse.create(connection.getContentLength(), connection.getResponseCode(), callback, null);

            // Shutdown http connection.
            connection.disconnect();
            return httpResponse;
        }
        catch (Exception exception) {
            return HttpResponse.create(0, HttpURLConnection.HTTP_BAD_REQUEST, null, exception);
        }
    }

}