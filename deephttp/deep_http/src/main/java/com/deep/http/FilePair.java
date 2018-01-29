package com.deep.http;

/**
 * Created by wangfei on 2017/12/16.
 */

public  class FilePair{
    String mFileName;
    byte[] mBinaryData;
    public FilePair(String fileName, byte[] data) {
        this.mFileName = fileName;
        this.mBinaryData = data;
    }
}

