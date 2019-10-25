package com.haochuan.core.util;

import com.yanzhenjie.nohttp.Headers;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.tools.HeaderUtils;
import com.yanzhenjie.nohttp.tools.IOUtils;

public class MyRequest extends Request<String> {
    public MyRequest(String url) {
        super(url, RequestMethod.GET);
    }

    public MyRequest(String url, RequestMethod requestMethod) {
        super(url, requestMethod);
    }


    @Override
    public String parseResponse(Headers responseHeaders, byte[] responseBody) throws Exception {
        if (responseBody == null || responseBody.length == 0)
            return "";
        String charset = HeaderUtils.parseHeadValue(responseHeaders.getContentType(), "charset", "");
        return IOUtils.toString(responseBody, charset);
    }
}
