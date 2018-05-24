package com.android.minlib.smarthttp.strategy;

public class URLStrategy {

    private static final String HTTP = "http://";
    private static final String HTTPS = "https://";
    private String DOMAIN ;
    private IURLStrategy iurlStrategy;
    private int domainIndex = -1;


    private String url = "";

    public String generateUrl(String methodName){
        if(iurlStrategy == null){
            throw new IllegalArgumentException("请先设置域名策略");
        }
        if(iurlStrategy.isHttps()){
            url += HTTPS;
        }else {
            url += HTTP;
        }

        DOMAIN = iurlStrategy.getDomains();

        url += DOMAIN + methodName;

        return url;
    }

    public void setIurlStrategy(IURLStrategy iurlStrategy) {
        this.iurlStrategy = iurlStrategy;
    }
}
