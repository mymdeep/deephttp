package com.deep.http;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.net.ssl.HttpsURLConnection;

import android.text.TextUtils;
import com.deep.http.interfaces.ARequest;
import com.deep.http.interfaces.AResponse;
import com.deep.http.log.Logger;

/**
 * Created by wangfei on 2017/12/16.
 */

public class Client {

    private static final String END = "\r\n";



    public  <T extends AResponse> T exec(ARequest request, Class<T> clazz) {
        if (request == null){
            return null;
        }
        if (Method.GET == request.getMethod()){
            return get(request,clazz);
        }else if (Method.POST == request.getMethod()){
            Logger.single(0,"post");
            return post(request,clazz);
        }else {
            Logger.error("暂不支持其他方式");
        }
        return null;
    }
    public  <T extends AResponse> T get(ARequest request, Class<T> clazz) {
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;
        int code = Constants.ERRORCODE;
        String message;
        try {
            URL url = new URL(request.getParamUrl());
            httpURLConnection = openUrlConnection(url);
            normalSetting(httpURLConnection, Method.GET, request);
            if (httpURLConnection == null) {
                return createResponse(Constants.NOHHTTP,code,clazz);
            }
            int responseCode = httpURLConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                inputStream = httpURLConnection.getInputStream();
                String contentEncoding = httpURLConnection.getContentEncoding();
                InputStream stream = null;
                try {
                    stream = request.wrapStream(contentEncoding, inputStream);
                    String data = convertStreamToString(stream);
                    return createResponse(data,responseCode,clazz);
                } catch (IOException e) {
                    Logger.error("error:"+e.getMessage());
                    return createResponse(e.getMessage(),code,clazz);
                } finally {
                    closeQuietly(stream);
                }

            }
            return createResponse("",responseCode,clazz);
        } catch (IOException e) {
            Logger.error("error:"+e.getMessage());
            message = e.getMessage();
        }
        return createResponse(message,code,clazz);
    }
    private  <T extends AResponse> T createResponse(String result, int responseCode, Class<T> clazz) {
        if (TextUtils.isEmpty(result)) {
            return null;
        }

        Constructor<T> constructor;
        try {
            Logger.single(0,"class="+clazz.getSimpleName());
            constructor = clazz.getConstructor(Integer.class, String.class);
            Logger.single(0,"constructor="+constructor.getName());
            return constructor.newInstance(responseCode, result);
        } catch (SecurityException | NoSuchMethodException | InstantiationException | IllegalArgumentException | InvocationTargetException | IllegalAccessException e) {
            Logger.error("error:"+e.getMessage());
        }
        return null;
    }





    private static void normalSetting(HttpURLConnection urlConnection, Method method, ARequest request) throws
        ProtocolException {
        if (request ==null){
            Logger.error("error request = null");
            return;
        }
        urlConnection.setConnectTimeout(request.connectionTimeOut);
        urlConnection.setReadTimeout(request.readSocketTimeOut);
        urlConnection.setRequestMethod(method.toString());
        if (method == Method.GET) {
            //urlConnection.setRequestProperty("Accept-Encoding", "gzip");
            if (request.getHeaders() != null && request.getHeaders().size() > 0) {
                Set<String> stringKeys = request.getHeaders().keySet();
                for (String key : stringKeys) {
                    urlConnection.setRequestProperty(key, request.getHeaders().get(key));
                }
            }
        } else if (method == Method.POST) {
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            if (request.getHeaders() != null && request.getHeaders().size() > 0) {
                Set<String> stringKeys = request.getHeaders().keySet();
                for (String key : stringKeys) {
                    urlConnection.setRequestProperty(key, request.getHeaders().get(key));
                }
            }
        }
    }


    private  String convertStreamToString(InputStream is) {
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

    private static void closeQuietly(Closeable io) {
        try {
            if (io != null) {
                io.close();
            }
        } catch (IOException e) {
        }
    }


    public  <T extends AResponse> T post(ARequest request, Class<T> clazz) {
        String boundary = UUID.randomUUID().toString();
        HttpURLConnection httpURLConnection;
        OutputStream outputStream = null;
        InputStream inputStream = null;
        URL url;
        int code = Constants.ERRORCODE;
        String message;
        try {
            url = new URL(request.getBaseUrl());
            httpURLConnection = openUrlConnection(url);
            normalSetting(httpURLConnection, Method.POST,request);
            if (request.Content_Type.equals(Constants.MULTIPART)){
                if (request.getParam() != null && request.getParam().size() > 0) {
                    httpURLConnection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
                    outputStream = httpURLConnection.getOutputStream();
                    addBodyParams(request.getParam(),request.getFilePair(), outputStream, boundary);
                }
            }
            if (request.Content_Type.equals(Constants.WWW_FORM)){
                if (request.getRequestData()!=null){
                    byte[] data = request.getRequestData();
                    //httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    httpURLConnection.setRequestProperty("Content-Length", String.valueOf(data.length));
                    outputStream = httpURLConnection.getOutputStream();
                    outputStream.write(data);
                    Logger.single(0,"post data size="+data.length);
                }

            }
            if (outputStream!=null){
                outputStream.flush();
            }
            int responseCode = httpURLConnection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                inputStream = httpURLConnection.getInputStream();
                String contentEncoding = httpURLConnection.getContentEncoding();
                InputStream stream = request.wrapStream(contentEncoding, inputStream);
                String data = convertStreamToString(stream);
                Logger.single(0,"data="+data);
                return createResponse(data,responseCode,clazz);

            }

            return createResponse("", Constants.ERRORCODE,clazz);
        } catch (IOException e) {
            message = e.getMessage();
            Logger.error("error:"+e.getMessage());
        }
        return createResponse(message, Constants.ERRORCODE,clazz);
    }
    private static HttpURLConnection openUrlConnection(URL url) throws IOException {
        String scheme = url.getProtocol();
        boolean isHttpsRequest = false;
        if ("https".equals(scheme)) {
            isHttpsRequest = true;
        }
        if (isHttpsRequest) {
            return (HttpsURLConnection) (url).openConnection();
            // TODO 处理https证书 1,需要测试https请求;2如需设置证书，需验证是否会对其它https请求有影响

        } else {
            return (HttpURLConnection) (url).openConnection();
        }

    }
    private static void addBodyParams(HashMap<String, Object> map, Map<String, FilePair> filePair, OutputStream outputStream, String boundary) throws IOException {
        boolean didWriteData = false;
        StringBuilder stringBuilder = new StringBuilder();
        Map<String, Object> bodyPair =map;
        Set<String> keys = bodyPair.keySet();
        for (String key : keys) {
            if (bodyPair.get(key) != null) {
                addFormField(stringBuilder, key, bodyPair.get(key).toString(), boundary);
            }
        }

        if (stringBuilder.length() > 0) {
            didWriteData = true;
            outputStream = new DataOutputStream(outputStream);
            outputStream.write(stringBuilder.toString().getBytes());
        }

        // upload files like POST files to server
        if (filePair != null && filePair.size() > 0) {
            Set<String> fileKeys = filePair.keySet();
            for (String key : fileKeys) {
                FilePair pair = filePair.get(key);
                byte[] data = pair.mBinaryData;
                if (data == null || data.length < 1) {
                    continue;
                } else {
                    didWriteData = true;
                    addFilePart(pair.mFileName, data, boundary, outputStream);
                }
            }
        }

        if (didWriteData) {
            finishWrite(outputStream, boundary);
        }
    }
    private static void addFormField(StringBuilder writer, final String name, final String value, String boundary) {
        writer.append("--").append(boundary).append(END)
            .append("Content-Disposition: form-data; name=\"").append(name)
            .append("\"").append(END)
            .append("Content-Type: text/plain; charset=").append("UTF-8")
            .append(END).append(END).append(value).append(END);
    }


    private static void addFilePart(final String fieldName, byte[] data, String boundary, OutputStream outputStream)
        throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("--").append(boundary).append(END)
            .append("Content-Disposition: form-data; name=\"")
            .append("pic").append("\"; filename=\"").append(fieldName)
            .append("\"").append(END).append("Content-Type: ")
            .append("application/octet-stream").append(END)
            .append("Content-Transfer-Encoding: binary").append(END)
            .append(END);
        outputStream.write(stringBuilder.toString().getBytes());
        outputStream.write(data);
        outputStream.write(END.getBytes());
    }

    private static void finishWrite(OutputStream outputStream, String boundary) throws IOException {
        outputStream.write(END.getBytes());
        outputStream.write(("--" + boundary + "--").getBytes());
        outputStream.write(END.getBytes());
        outputStream.flush();
        outputStream.close();
    }
}
