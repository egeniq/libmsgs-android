package io.msgs.common;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Map;

import io.msgs.common.entity.HttpEntity;
import io.msgs.common.log.Logger;

/**
 * API client which talks to the msgs.io API.
 * <p>
 * Created by Daniel Zolnai on 2017-10-17.
 */
public class APIClient {

    private static final int CONNECT_TIMEOUT_MS = 15000;
    private static final int READ_TIMEOUT_MS = 30000;

    private final String _baseURL;
    private final Logger _logger = new Logger();

    public APIClient(String baseURL) {
        if (!baseURL.endsWith("/")) {
            _baseURL = baseURL + "/";
        } else {
            _baseURL = baseURL;
        }
        _logger.setTag(APIClient.class.getName());
    }

    public JSONObject post(String path, HttpEntity entity) throws APIException {
        return post(path, entity, null);
    }

    public JSONObject post(String path, HttpEntity entity, Map<String, String> headers) throws APIException {
        HttpURLConnection connection = _createConnection(path, headers);
        try {
            connection.setRequestMethod("POST");
        } catch (ProtocolException ex) {
            throw new APIException(ex);
        }
        _logger.d("--> POST " + connection.getURL().toString());
        connection.setDoInput(true);
        connection.setDoOutput(true);
        OutputStream outputStream = null;
        try {
            outputStream = connection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            writer.write(entity.getBody());
            writer.flush();
            writer.close();
        } catch (IOException ex) {
            throw new APIException(ex);
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    // Do nothing.
                }
            }
        }
        try {
            connection.connect();
            _logger.d("<-- POST " + connection.getURL().toString() + " [" + connection.getResponseCode() + "]");
            String body = _readString(connection.getInputStream());
            _logger.d(body);
            return new JSONObject(body);
        } catch (IOException | JSONException ex) {
            throw new APIException(ex);
        }
    }

    public JSONObject get(String path, Map<String, String> headers) throws APIException {
        HttpURLConnection connection = _createConnection(path, headers);
        try {
            connection.setRequestMethod("GET");
        } catch (ProtocolException ex) {
            throw new APIException(ex);
        }
        _logger.d("--> GET " + connection.getURL().toString());
        connection.setDoInput(true);
        connection.setDoOutput(false);
        try {
            connection.connect();
            _logger.d("<-- GET " + connection.getURL().toString() + " [" + connection.getResponseCode() + "]");
            String body = _readString(connection.getInputStream());
            _logger.d(body);
            return new JSONObject(body);
        } catch (IOException | JSONException ex) {
            throw new APIException(ex);
        }
    }

    public JSONArray getArray(String path) throws APIException {
        HttpURLConnection connection = _createConnection(path, null);
        try {
            connection.setRequestMethod("GET");
        } catch (ProtocolException ex) {
            throw new APIException(ex);
        }
        _logger.d("--> GET " + connection.getURL().toString());
        connection.setDoInput(true);
        connection.setDoOutput(false);
        try {
            connection.connect();
            _logger.d("<-- GET " + connection.getURL().toString() + " [" + connection.getResponseCode() + "]");
            String body = _readString(connection.getInputStream());
            _logger.d(body);
            return new JSONArray(body);
        } catch (IOException | JSONException ex) {
            throw new APIException(ex);
        }
    }

    public JSONObject delete(String path, Map<String, String> headers) throws APIException {
        HttpURLConnection connection = _createConnection(path, headers);
        try {
            connection.setRequestMethod("DELETE");
        } catch (ProtocolException ex) {
            throw new APIException(ex);
        }
        _logger.d("--> DELETE " + connection.getURL().toString());
        connection.setDoInput(true);
        connection.setDoOutput(false);
        try {
            connection.connect();
            _logger.d("<-- GET " + connection.getURL().toString() + " [" + connection.getResponseCode() + "]");
            String body = _readString(connection.getInputStream());
            _logger.d(body);
            return new JSONObject(body);
        } catch (IOException | JSONException ex) {
            throw new APIException(ex);
        }
    }

    private HttpURLConnection _createConnection(String path, Map<String, String> headers) throws APIException {
        try {
            URL url = new URL(_baseURL + path);
            HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
            urlConnection.setConnectTimeout(CONNECT_TIMEOUT_MS);
            urlConnection.setReadTimeout(READ_TIMEOUT_MS);
            if (headers != null) {
                for (Map.Entry<String, String> header : headers.entrySet()) {
                    urlConnection.addRequestProperty(header.getKey(), header.getValue());
                }
            }
            return urlConnection;
        } catch (IOException ex) {
            throw new APIException(ex);
        }
    }

    private String _readString(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")), 8192);
        StringBuilder builder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }
        try {
            inputStream.close();
        } catch (IOException ex) {
            _logger.w("Tried to close stream, but failed.", ex);
            // Do nothing.
        }
        return builder.toString();
    }


    public void setLoggingEnabled(boolean enabled) {
        _logger.setEnabled(enabled);
    }

    public void setLogLevel(Logger.Level level) {
        _logger.setLevel(level);
    }

}
