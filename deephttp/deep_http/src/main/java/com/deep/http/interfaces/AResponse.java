package com.deep.http.interfaces;

import android.text.TextUtils;
import com.deep.http.Constants;
import com.deep.http.log.Logger;

/**
 * Created by wangfei on 2017/12/16.
 */

public abstract class AResponse {
    public int stCode;

    public boolean iserror = true;
    public  String errorMessage;
    public AResponse(Integer stCode, String result){
        this.stCode = stCode;
        Logger.single(0,"stcode="+stCode+ "    result="+result.length());
        if (stCode == Constants.ERRORCODE){
            iserror = true;
            errorMessage = Constants.ERRORCODE+"  "+result;
        }else {
            if (TextUtils.isEmpty(result)){
                iserror = true;
                errorMessage = Constants.ERRORCODE+"  "+ Constants.EMPTY_RESULT;
            }else {
                iserror = false;
                parseResult(result);
            }

        }

    }
    public boolean isCorrect(){
        return !iserror;
    }
    public abstract void parseResult(String result);

}
