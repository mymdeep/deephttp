package deep.com.deephttp;

import java.util.HashMap;
import java.util.Map;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import com.deep.http.Client;
import com.deep.http.Constants;
import com.deep.http.Method;

public class MainActivity extends AppCompatActivity {
    private String url ="http://30.30.140.23:8332/deep/test";
    private static Client client = new Client();
    private TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView)findViewById(R.id.message);
        findViewById(R.id.get).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
               MyQueue.runInBack(new Runnable() {
                   @Override
                   public void run() {
                       TestGetRequest request = new TestGetRequest(url, Method.GET);


                       final TestGetResponse response = client.exec(request,TestGetResponse.class);
                       MyQueue.runInMain(new Runnable() {
                           @Override
                           public void run() {
                               if (response!=null){
                                   if (response.isCorrect()){
                                       textView.setText(stringToJSON(response.result));
                                   }else {
                                       textView.setText(stringToJSON(response.errorMessage));
                                   }

                               }
                           }
                       });

                   }
               });


            }
        });
        findViewById(R.id.www).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                MyQueue.runInBack(new Runnable() {
                    @Override
                    public void run() {
                        TestPostRequest request = new TestPostRequest(url, Method.POST);
                        Map<String,String> map = new HashMap<String, String>();
                        map.put("header1","1111");
                        map.put("header2","2222");
                        request.setHeaders(map);
                        request.Content_Type = com.deep.http.Constants.WWW_FORM;
                        final TestPostResponse response = client.exec(request,TestPostResponse.class);
                        MyQueue.runInMain(new Runnable() {
                            @Override
                            public void run() {
                                if (response!=null){
                                    if (response.isCorrect()){
                                        textView.setText(stringToJSON(response.result));
                                    }else {
                                        textView.setText(stringToJSON(response.errorMessage));
                                    }

                                }
                            }
                        });
                    }
                });


            }
        });
        findViewById(R.id.formdata).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                MyQueue.runInBack(new Runnable() {
                    @Override
                    public void run() {
                        TestPostRequest request = new TestPostRequest(url, Method.POST);
                        Map<String,String> map = new HashMap<String, String>();
                        map.put("header1","1111");
                        map.put("header2","2222");
                        request.setHeaders(map);
                        request.Content_Type = Constants.MULTIPART;
                        final TestPostResponse response = client.exec(request,TestPostResponse.class);
                        MyQueue.runInMain(new Runnable() {
                            @Override
                            public void run() {
                                if (response!=null){
                                    if (response.isCorrect()){
                                        textView.setText(stringToJSON(response.result));
                                    }else {
                                        textView.setText(stringToJSON(response.errorMessage));
                                    }

                                }
                            }
                        });
                    }
                });

            }
        });
    }



    public static String stringToJSON(String strJson) {
        // 计数tab的个数
        int tabNum = 0;
        StringBuffer jsonFormat = new StringBuffer();
        int length = strJson.length();

        char last = 0;
        for (int i = 0; i < length; i++) {
            char c = strJson.charAt(i);
            if (c == '{') {
                tabNum++;
                jsonFormat.append(c + "\n");
                jsonFormat.append(getSpaceOrTab(tabNum));
            }
            else if (c == '}') {
                tabNum--;
                jsonFormat.append("\n");
                jsonFormat.append(getSpaceOrTab(tabNum));
                jsonFormat.append(c);
            }
            else if (c == ',') {
                jsonFormat.append(c + "\n");
                jsonFormat.append(getSpaceOrTab(tabNum));
            }
            else if (c == ':') {
                jsonFormat.append(c + " ");
            }
            else if (c == '[') {
                tabNum++;
                char next = strJson.charAt(i + 1);
                if (next == ']') {
                    jsonFormat.append(c);
                }
                else {
                    jsonFormat.append(c + "\n");
                    jsonFormat.append(getSpaceOrTab(tabNum));
                }
            }
            else if (c == ']') {
                tabNum--;
                if (last == '[') {
                    jsonFormat.append(c);
                }
                else {
                    jsonFormat.append("\n" + getSpaceOrTab(tabNum) + c);
                }
            }
            else {
                jsonFormat.append(c);
            }
            last = c;
        }
        return jsonFormat.toString();
    }

    // 是空格还是tab
    private static String getSpaceOrTab(int tabNum) {
        StringBuffer sbTab = new StringBuffer();
        for (int i = 0; i < tabNum; i++) {
            sbTab.append('\t');
        }
        return sbTab.toString();
    }
}
