package com.amperas17.showhtmlapp;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;

public class GetContentService extends IntentService {

    public GetContentService() {
        super("GetContentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            ResultReceiver receiver = intent.getParcelableExtra(AppContract.RECEIVER_TAG);
            String path = intent.getStringExtra(AppContract.PATH_TAG);

            String content = GetHttpContent.getContent(path);

            Bundle bundle = new Bundle();
            bundle.putString(AppContract.HTML_CONTENT_TAG,content);
            receiver.send(0, bundle);
        }
    }



}
