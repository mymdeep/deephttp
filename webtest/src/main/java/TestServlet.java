import net.sf.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Enumeration;

/**
 * Created by wangfei on 2018/1/29.
 */
public class TestServlet extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Enumeration headers =  request.getHeaderNames();
        Enumeration<String>
                paraNames=request.getParameterNames();
        JSONObject responseJSONObject = new JSONObject();
        JSONObject header = new JSONObject();
        JSONObject params = new JSONObject();
        while(headers.hasMoreElements()){
            String value = (String)headers.nextElement();//调用nextElement方法获得元素
            header.put(value,request.getHeader(value));
        }
        for(Enumeration e=paraNames;e.hasMoreElements();){
            String thisName=e.nextElement().toString();

            String thisValue=request.getParameter(thisName);
            params.put(thisName,thisValue);
        }
        responseJSONObject.put("header",header);
        responseJSONObject.put("param",params);
        sendResponse(response,responseJSONObject);
    }
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Enumeration headers =  request.getHeaderNames();
        Enumeration<String>
                paraNames=request.getParameterNames();
        InputStream inputStream = request.getInputStream();
        JSONObject responseJSONObject = new JSONObject();
        JSONObject header = new JSONObject();
        JSONObject params = new JSONObject();

        while(headers.hasMoreElements()){
            String value = (String)headers.nextElement();//调用nextElement方法获得元素
            header.put(value,request.getHeader(value));
        }
        for(Enumeration e=paraNames;e.hasMoreElements();){
            String thisName=e.nextElement().toString();

            String thisValue=request.getParameter(thisName);
            params.put(thisName,thisValue);
        }
        responseJSONObject.put("header",header);
        responseJSONObject.put("param",params);
        responseJSONObject.put("body",convertStreamToString(inputStream));
        sendResponse(response,responseJSONObject);
    }
    public void sendResponse(HttpServletResponse response, JSONObject jsonObject){
        PrintWriter printWriter = null ;
        try {
            printWriter = response.getWriter();
            printWriter.append(jsonObject.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (printWriter != null) {
                printWriter.close();
            }
        }
    }
    public String convertStreamToString(InputStream is) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));

        StringBuilder sb = new StringBuilder();



        String line = null;

        try {

            while ((line = reader.readLine()) != null) {

                sb.append(line + "/n");

            }

        } catch (IOException e) {

            e.printStackTrace();

        } finally {

            try {

                is.close();

            } catch (IOException e) {

                e.printStackTrace();

            }

        }



        return sb.toString();

    }
}
