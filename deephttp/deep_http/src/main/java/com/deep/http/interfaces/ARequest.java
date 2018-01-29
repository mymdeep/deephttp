package com.deep.http.interfaces;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import android.text.TextUtils;
import com.deep.http.Constants;
import com.deep.http.FilePair;

/**
 * Created by wangfei on 2017/12/16.
 */

public abstract class ARequest {
    private String baseUrl ;
    private com.deep.http.Method Method;
    private byte[] data;
    private IEncrypt iEncrypt;
    private Map<String, String> headers;
    private HashMap<String, Object> params;
    public  int connectionTimeOut = 30000;
    public  int readSocketTimeOut = 30000;
    public String Content_Type = Constants.MULTIPART;
    public ARequest(String url, com.deep.http.Method method){
        this.baseUrl = url;
        this.Method = method;
    }

    public com.deep.http.Method getMethod() {
        return Method;
    }

    public void setParams(HashMap<String, Object> params) {
        this.params = params;
    }
    public void addParam(String key,Object value){
        params.put(key,value);
    }
    public String getParamUrl(){
        if (TextUtils.isEmpty(baseUrl) || params == null || params.size() == 0) {
            return "";
        }

        if (!baseUrl.endsWith("?")) {
            baseUrl += "?";
        }

        String paramsStr = buildGetParams(params);
        if (iEncrypt != null) {
            paramsStr = iEncrypt.encrypt(baseUrl, params);

        }

        StringBuilder sbUrl = new StringBuilder(baseUrl);
        sbUrl.append(paramsStr);
        return sbUrl.toString();
    }
    private static String buildGetParams(Map<String, Object> params) {
        StringBuilder sb = new StringBuilder();
        Set<String> keys = params.keySet();
        for (String key : keys) {
            if (params.get(key) == null) {
                continue;
            }
            sb = sb.append(key + "=" + URLEncoder.encode(params.get(key).toString()) + "&");
        }

        return sb.substring(0, sb.length() - 1);
    }
    public  HashMap<String, Object> getParam(){
        return params;
    }
    public HttpURLConnection openUrlConnection(URL url){
       return null;
    }
    public abstract Map<String, FilePair> getFilePair();
    public  Map<String, String> getHeaders(){
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public   InputStream wrapStream(String contentEncoding, InputStream inputStream)
        throws IOException {
        if (contentEncoding == null || "identity".equalsIgnoreCase(contentEncoding)) {
            return inputStream;
        }
        if ("gzip".equalsIgnoreCase(contentEncoding)) {
            return new GZIPInputStream(inputStream);
        }
        if ("deflate".equalsIgnoreCase(contentEncoding)) {
            return new InflaterInputStream(inputStream, new Inflater(false), 512);
        }
        throw new RuntimeException("unsupported content-encoding: " + contentEncoding);
    }
    public  byte[] getRequestData() {
        if (data == null){
            StringBuffer stringBuffer = new StringBuffer();
            try {
                for(Map.Entry<String, Object> entry : params.entrySet()) {
                    stringBuffer.append(entry.getKey())
                        .append("=")
                        .append(URLEncoder.encode(entry.getValue().toString(), "utf-8"))
                        .append("&");
                }
                stringBuffer.deleteCharAt(stringBuffer.length() - 1);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return stringBuffer.toString().getBytes();
        }else {
            return data;
        }

    }
    public   String convertStreamToString(InputStream is) {
        InputStreamReader inputStreamReader = new InputStreamReader(is);
        BufferedReader reader = new BufferedReader(inputStreamReader, 512);
        StringBuilder stringBuilder = new StringBuilder();
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line + "\n");
            }
        } catch (IOException e) {
            return null;
        } finally {
            closeQuietly(inputStreamReader);
            closeQuietly(reader);
        }
        return stringBuilder.toString();
    }
    public void setData(byte[] data) {
        this.data = data;
    }
    private static void closeQuietly(Closeable io) {
        try {
            if (io != null) {
                io.close();
            }
        } catch (IOException e) {
        }
    }
}