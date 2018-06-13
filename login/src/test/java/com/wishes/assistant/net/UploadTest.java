package com.wishes.assistant.net;

import org.junit.Test;

/**
 * Created by 10988 on 2018/5/16.
 */
public class UploadTest {
    @Test
    public void uploadBasicInfo() throws Exception {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Upload.uploadBasicInfo("142208100008","123","郑龙");
            }
        }).start();
    }

}