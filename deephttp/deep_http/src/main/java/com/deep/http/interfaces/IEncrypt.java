package com.deep.http.interfaces;

import java.util.Map;

/**
 * Created by wangfei on 2017/12/16.
 */

public interface IEncrypt {
    public String encrypt(String urlPath, Map<String, Object> params);
    public String dencrypt();
}
