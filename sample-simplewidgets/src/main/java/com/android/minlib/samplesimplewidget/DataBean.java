package com.android.minlib.samplesimplewidget;

import java.util.List;


public class DataBean {

    /**
     * shidu : 80%
     * pm25 : 17.0
     * pm10 : 26.0
     * quality : 优
     * wendu : 28
     * ganmao : 各类人群可自由活动
     * yesterday : {"date":"09日星期四","sunrise":"05:58","high":"高温 33.0℃","low":"低温 27.0℃","sunset":"19:00","aqi":29,"fx":"无持续风向","fl":"<3级","type":"多云","notice":"阴晴之间，谨防紫外线侵扰"}
     * forecast : [{"date":"10日星期五","sunrise":"05:58","high":"高温 31.0℃","low":"低温 26.0℃","sunset":"19:00","aqi":17,"fx":"东南风","fl":"3-4级","type":"阵雨","notice":"阵雨来袭，出门记得带伞"},{"date":"11日星期六","sunrise":"05:58","high":"高温 30.0℃","low":"低温 26.0℃","sunset":"18:59","aqi":17,"fx":"东南风","fl":"<3级","type":"暴雨","notice":"关好门窗，外出避开低洼地"},{"date":"12日星期日","sunrise":"05:59","high":"高温 30.0℃","low":"低温 26.0℃","sunset":"18:58","aqi":17,"fx":"东南风","fl":"3-4级","type":"暴雨","notice":"关好门窗，外出避开低洼地"},{"date":"13日星期一","sunrise":"05:59","high":"高温 31.0℃","low":"低温 26.0℃","sunset":"18:58","aqi":20,"fx":"无持续风向","fl":"<3级","type":"阵雨","notice":"阵雨来袭，出门记得带伞"},{"date":"14日星期二","sunrise":"05:59","high":"高温 32.0℃","low":"低温 27.0℃","sunset":"18:57","aqi":26,"fx":"无持续风向","fl":"<3级","type":"阵雨","notice":"阵雨来袭，出门记得带伞"}]
     */

    private String shidu;
    private double pm25;
    private double pm10;
    private String quality;
    private String wendu;
    private String ganmao;
    private YesterdayBean yesterday;
    private List<ForecastBean> forecast;

    public String getShidu() {
        return shidu;
    }

    public void setShidu(String shidu) {
        this.shidu = shidu;
    }

    public double getPm25() {
        return pm25;
    }

    public void setPm25(double pm25) {
        this.pm25 = pm25;
    }

    public double getPm10() {
        return pm10;
    }

    public void setPm10(double pm10) {
        this.pm10 = pm10;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public String getWendu() {
        return wendu;
    }

    public void setWendu(String wendu) {
        this.wendu = wendu;
    }

    public String getGanmao() {
        return ganmao;
    }

    public void setGanmao(String ganmao) {
        this.ganmao = ganmao;
    }

    public YesterdayBean getYesterday() {
        return yesterday;
    }

    public void setYesterday(YesterdayBean yesterday) {
        this.yesterday = yesterday;
    }

    public List<ForecastBean> getForecast() {
        return forecast;
    }

    public void setForecast(List<ForecastBean> forecast) {
        this.forecast = forecast;
    }

    public static class YesterdayBean {
        /**
         * date : 09日星期四
         * sunrise : 05:58
         * high : 高温 33.0℃
         * low : 低温 27.0℃
         * sunset : 19:00
         * aqi : 29.0
         * fx : 无持续风向
         * fl : <3级
         * type : 多云
         * notice : 阴晴之间，谨防紫外线侵扰
         */

        private String date;
        private String sunrise;
        private String high;
        private String low;
        private String sunset;
        private double aqi;
        private String fx;
        private String fl;
        private String type;
        private String notice;

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getSunrise() {
            return sunrise;
        }

        public void setSunrise(String sunrise) {
            this.sunrise = sunrise;
        }

        public String getHigh() {
            return high;
        }

        public void setHigh(String high) {
            this.high = high;
        }

        public String getLow() {
            return low;
        }

        public void setLow(String low) {
            this.low = low;
        }

        public String getSunset() {
            return sunset;
        }

        public void setSunset(String sunset) {
            this.sunset = sunset;
        }

        public double getAqi() {
            return aqi;
        }

        public void setAqi(double aqi) {
            this.aqi = aqi;
        }

        public String getFx() {
            return fx;
        }

        public void setFx(String fx) {
            this.fx = fx;
        }

        public String getFl() {
            return fl;
        }

        public void setFl(String fl) {
            this.fl = fl;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getNotice() {
            return notice;
        }

        public void setNotice(String notice) {
            this.notice = notice;
        }
    }

    public static class ForecastBean {
        /**
         * date : 10日星期五
         * sunrise : 05:58
         * high : 高温 31.0℃
         * low : 低温 26.0℃
         * sunset : 19:00
         * aqi : 17.0
         * fx : 东南风
         * fl : 3-4级
         * type : 阵雨
         * notice : 阵雨来袭，出门记得带伞
         */

        private String date;
        private String sunrise;
        private String high;
        private String low;
        private String sunset;
        private double aqi;
        private String fx;
        private String fl;
        private String type;
        private String notice;

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getSunrise() {
            return sunrise;
        }

        public void setSunrise(String sunrise) {
            this.sunrise = sunrise;
        }

        public String getHigh() {
            return high;
        }

        public void setHigh(String high) {
            this.high = high;
        }

        public String getLow() {
            return low;
        }

        public void setLow(String low) {
            this.low = low;
        }

        public String getSunset() {
            return sunset;
        }

        public void setSunset(String sunset) {
            this.sunset = sunset;
        }

        public double getAqi() {
            return aqi;
        }

        public void setAqi(double aqi) {
            this.aqi = aqi;
        }

        public String getFx() {
            return fx;
        }

        public void setFx(String fx) {
            this.fx = fx;
        }

        public String getFl() {
            return fl;
        }

        public void setFl(String fl) {
            this.fl = fl;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getNotice() {
            return notice;
        }

        public void setNotice(String notice) {
            this.notice = notice;
        }
    }
}
