package com.haochuan.hciptvbasic.webview;

import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;


public class HCWebChromeClient extends WebChromeClient {

    private String errorMsg;
    private String TAG = "HCWebApp";

    @Override
    public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
        errorMsg = consoleMessage.message() + " -- From line "
                + consoleMessage.lineNumber() + " of "
                + consoleMessage.sourceId();
        Log.d(TAG,  errorMsg);
        return true;
    }

    public String getErrorMsg(){
        return errorMsg;
    }

}
