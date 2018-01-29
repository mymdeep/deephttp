package deep.com.deephttp;

import java.util.HashMap;
import java.util.Map;

import com.deep.http.FilePair;
import com.deep.http.interfaces.ARequest;

/**
 * Created by wangfei on 2018/1/29.
 */

public class TestPostRequest extends ARequest {
    private HashMap<String,Object> params;
    public TestPostRequest(String url, com.deep.http.Method method) {
        super(url, method);
        Map<String,String> header = new HashMap<String, String>();
        header.put("header1","1111");
        header.put("header2","2222");
        setHeaders(header);
        HashMap<String,Object> params = new HashMap<String, Object>();
        params.put("param1","1111");
        params.put("param2","2222");
        setParams(params);
    }



    @Override
    public Map<String, FilePair> getFilePair() {
        return null;
    }
}
