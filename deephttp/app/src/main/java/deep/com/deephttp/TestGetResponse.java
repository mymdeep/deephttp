package deep.com.deephttp;

import com.deep.http.interfaces.AResponse;

/**
 * Created by wangfei on 2018/1/29.
 */

public class TestGetResponse extends AResponse {
    public String result;
    public TestGetResponse(Integer stCode, String result) {
        super(stCode, result);
    }

    @Override
    public void parseResult(String result) {

            this.result = result;

    }
}
