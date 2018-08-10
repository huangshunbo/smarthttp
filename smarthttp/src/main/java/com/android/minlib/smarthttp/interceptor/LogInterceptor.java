package com.android.minlib.smarthttp.interceptor;

import com.android.minlib.smarthttp.log.SmartLog;
import com.google.gson.Gson;
import com.google.gson.JsonParser;

import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.Connection;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.http.HttpHeaders;
import okhttp3.internal.platform.Platform;
import okio.Buffer;
import okio.BufferedSource;

public class LogInterceptor implements Interceptor {

    private static final Charset UTF8 = Charset.forName("UTF-8");
    private static final AtomicInteger ID_GENERATOR = new AtomicInteger(0);

    private String urlStr = "",headersStr = "\n ",requestStr = "",responseStr = "";

    public LogInterceptor() {
    }


    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        final int id = ID_GENERATOR.incrementAndGet();
        {
            final String LOG_PREFIX = "【 #" + id + "】";
            RequestBody requestBody = request.body();
            boolean hasRequestBody = requestBody != null;
            Connection connection = chain.connection();
            Protocol protocol = connection != null ? connection.protocol() : Protocol.HTTP_1_1;
            String requestStartMessage = "   " + request.method() + ' ' + request.url() + ' ' + protocol;
            urlStr = LOG_PREFIX + requestStartMessage + "\n";
            if (hasRequestBody) {
                // Request body headers are only present when installed as a network interceptor. Force
                // them to be included (when available) so there values are known.
                if (requestBody.contentType() != null) {
                    headersStr += "Content-Type: " + requestBody.contentType() + "\n";
                }
                if (requestBody.contentLength() != -1) {
                    headersStr += "Content-Length: " + requestBody.contentLength() + "\n";
                }
            }

            Headers headers = request.headers();
            for (int i = 0, count = headers.size(); i < count; i++) {
                String name = headers.name(i);
                // Skip headers from the request body as they are explicitly logged above.
                if (!"Content-Type".equalsIgnoreCase(name) && !"Content-Length".equalsIgnoreCase(name)) {
                    headersStr += name + ": " + headers.value(i) + "\n";
                }
            }

            if (hasRequestBody && !bodyEncoded(request.headers())) {
                Buffer buffer = new Buffer();
                requestBody.writeTo(buffer);

                Charset charset = UTF8;
                MediaType contentType = requestBody.contentType();
                if (contentType != null) {
                    charset = contentType.charset(UTF8);
                }

                if (isPlaintext(buffer)) {
                    requestStr = buffer.readString(charset);
                }
            }
        }

        {
            final String LOG_PREFIX = "[" + id + " response]";
            long startNs = System.nanoTime();
            Response response;
            try {
                response = chain.proceed(request);
            } catch (Exception e) {
                SmartLog.LOG(urlStr + headersStr,requestStr,null,e);
                throw e;
            }
            long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);

            ResponseBody responseBody = response.body();
            long contentLength = responseBody.contentLength();
            responseStr += response.code() + ' ' + response.message()  + " (" + tookMs + "ms" + "" + ')' + "\n";

            Headers headers = response.headers();
            for (int i = 0, count = headers.size(); i < count; i++) {
                responseStr += headers.name(i) + ": " + headers.value(i) + "\n";
            }

            if (HttpHeaders.hasBody(response) && !bodyEncoded(response.headers())) {
                BufferedSource source = responseBody.source();
                source.request(Long.MAX_VALUE); // Buffer the entire body.
                Buffer buffer = source.buffer();

                Charset charset = UTF8;
                MediaType contentType = responseBody.contentType();
                if (contentType != null) {
                    try {
                        charset = contentType.charset(UTF8);
                    } catch (UnsupportedCharsetException e) {
                        SmartLog.LOG(urlStr  + headersStr,requestStr,null,e);
                        return response;
                    }
                }

                if (!isPlaintext(buffer)) {
                    return response;
                }

                if (contentLength != 0) {
                    String tmp = buffer.clone().readString(charset);
                    if (contentType != null && "json".equals(contentType.subtype())) {
                        tmp = stringToJSON(tmp);
                    }
                    responseStr += tmp;
                    SmartLog.LOG(urlStr + headersStr,requestStr,responseStr,null);
                }

            }
            return response;
        }
    }

    static boolean isPlaintext(Buffer buffer) {
        try {
            Buffer prefix = new Buffer();
            long byteCount = buffer.size() < 64 ? buffer.size() : 64;
            buffer.copyTo(prefix, 0, byteCount);
            for (int i = 0; i < 16; i++) {
                if (prefix.exhausted()) {
                    break;
                }
                int codePoint = prefix.readUtf8CodePoint();
                if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                    return false;
                }
            }
            return true;
        } catch (EOFException e) {
            return false; // Truncated UTF-8 sequence.
        }
    }

    private boolean bodyEncoded(Headers headers) {
        String contentEncoding = headers.get("Content-Encoding");
        return contentEncoding != null && !contentEncoding.equalsIgnoreCase("identity");
    }

    static String stringToJSON(String strJson) {
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
    static String getSpaceOrTab(int tabNum) {
        StringBuffer sbTab = new StringBuffer();
        for (int i = 0; i < tabNum; i++) {
            sbTab.append('\t');
        }
        return sbTab.toString();
    }

}
