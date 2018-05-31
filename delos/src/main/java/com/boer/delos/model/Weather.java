package com.boer.delos.model;

import java.util.List;

/**
 * Created by Administrator on 2016/8/1 0001.
 * 天气预报解析
 * http://api.map.baidu.com/telematics/v3/weather?location=北京&output=json&ak=yourkey
 */
public class Weather {


    /**
     * date : 2017-04-10
     * error : 0
     * results : [{"currentCity":"无锡","index":[{"des":"建议着厚外套加毛衣等服装。年老体弱者宜着大衣、呢外套加羊毛衫。","tipt":"穿衣指数","title":"穿衣","zs":"较冷"},{"des":"不宜洗车，未来24小时内有雨，如果在此期间洗车，雨水和路上的泥水可能会再次弄脏您的爱车。","tipt":"洗车指数","title":"洗车","zs":"不宜"},{"des":"有降水，虽然风稍大，但温度适宜，适宜旅游，可不要错过机会呦！","tipt":"旅游指数","title":"旅游","zs":"适宜"},{"des":"天冷空气湿度大，易发生感冒，请注意适当增加衣服，加强自我防护避免感冒。","tipt":"感冒指数","title":"感冒","zs":"易发"},{"des":"有降水，且风力较强，可适当在室内低强度运动；若坚持户外运动，请选择合适的运动并携带雨具。","tipt":"运动指数","title":"运动","zs":"较不宜"},{"des":"属弱紫外线辐射天气，无需特别防护。若长期在户外，建议涂擦SPF在8-12之间的防晒护肤品。","tipt":"紫外线强度指数","title":"紫外线强度","zs":"最弱"}],"pm25":"60","weather_data":[{"date":"周一 04月10日 (实时：10℃)","dayPictureUrl":"http://api.map.baidu.com/images/weather/day/xiaoyu.png","nightPictureUrl":"http://api.map.baidu.com/images/weather/night/xiaoyu.png","temperature":"14 ~ 10℃","weather":"小雨","wind":"西北风3-4级"},{"date":"周二","dayPictureUrl":"http://api.map.baidu.com/images/weather/day/yin.png","nightPictureUrl":"http://api.map.baidu.com/images/weather/night/duoyun.png","temperature":"15 ~ 9℃","weather":"阴转多云","wind":"西风3-4级"},{"date":"周三","dayPictureUrl":"http://api.map.baidu.com/images/weather/day/qing.png","nightPictureUrl":"http://api.map.baidu.com/images/weather/night/qing.png","temperature":"18 ~ 8℃","weather":"晴","wind":"东风3-4级"},{"date":"周四","dayPictureUrl":"http://api.map.baidu.com/images/weather/day/qing.png","nightPictureUrl":"http://api.map.baidu.com/images/weather/night/duoyun.png","temperature":"21 ~ 12℃","weather":"晴转多云","wind":"东南风3-4级"}]}]
     * status : success
     */

    private String date;
    private int error;
    private String status;
    /**
     * currentCity : 无锡
     * index : [{"des":"建议着厚外套加毛衣等服装。年老体弱者宜着大衣、呢外套加羊毛衫。","tipt":"穿衣指数","title":"穿衣","zs":"较冷"},{"des":"不宜洗车，未来24小时内有雨，如果在此期间洗车，雨水和路上的泥水可能会再次弄脏您的爱车。","tipt":"洗车指数","title":"洗车","zs":"不宜"},{"des":"有降水，虽然风稍大，但温度适宜，适宜旅游，可不要错过机会呦！","tipt":"旅游指数","title":"旅游","zs":"适宜"},{"des":"天冷空气湿度大，易发生感冒，请注意适当增加衣服，加强自我防护避免感冒。","tipt":"感冒指数","title":"感冒","zs":"易发"},{"des":"有降水，且风力较强，可适当在室内低强度运动；若坚持户外运动，请选择合适的运动并携带雨具。","tipt":"运动指数","title":"运动","zs":"较不宜"},{"des":"属弱紫外线辐射天气，无需特别防护。若长期在户外，建议涂擦SPF在8-12之间的防晒护肤品。","tipt":"紫外线强度指数","title":"紫外线强度","zs":"最弱"}]
     * pm25 : 60
     * weather_data : [{"date":"周一 04月10日 (实时：10℃)","dayPictureUrl":"http://api.map.baidu.com/images/weather/day/xiaoyu.png","nightPictureUrl":"http://api.map.baidu.com/images/weather/night/xiaoyu.png","temperature":"14 ~ 10℃","weather":"小雨","wind":"西北风3-4级"},{"date":"周二","dayPictureUrl":"http://api.map.baidu.com/images/weather/day/yin.png","nightPictureUrl":"http://api.map.baidu.com/images/weather/night/duoyun.png","temperature":"15 ~ 9℃","weather":"阴转多云","wind":"西风3-4级"},{"date":"周三","dayPictureUrl":"http://api.map.baidu.com/images/weather/day/qing.png","nightPictureUrl":"http://api.map.baidu.com/images/weather/night/qing.png","temperature":"18 ~ 8℃","weather":"晴","wind":"东风3-4级"},{"date":"周四","dayPictureUrl":"http://api.map.baidu.com/images/weather/day/qing.png","nightPictureUrl":"http://api.map.baidu.com/images/weather/night/duoyun.png","temperature":"21 ~ 12℃","weather":"晴转多云","wind":"东南风3-4级"}]
     */

    private List<ResultsBean> results;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<ResultsBean> getResults() {
        return results;
    }

    public void setResults(List<ResultsBean> results) {
        this.results = results;
    }

    public static class ResultsBean {
        private String currentCity;
        private String pm25;
        /**
         * date : 周一 04月10日 (实时：10℃)
         * dayPictureUrl : http://api.map.baidu.com/images/weather/day/xiaoyu.png
         * nightPictureUrl : http://api.map.baidu.com/images/weather/night/xiaoyu.png
         * temperature : 14 ~ 10℃
         * weather : 小雨
         * wind : 西北风3-4级
         */

        private List<WeatherDataBean> weather_data;

        public String getCurrentCity() {
            return currentCity;
        }

        public void setCurrentCity(String currentCity) {
            this.currentCity = currentCity;
        }

        public String getPm25() {
            return pm25;
        }

        public void setPm25(String pm25) {
            this.pm25 = pm25;
        }

        public List<WeatherDataBean> getWeather_data() {
            return weather_data;
        }

        public void setWeather_data(List<WeatherDataBean> weather_data) {
            this.weather_data = weather_data;
        }

        public static class WeatherDataBean {
            private String date;
            private String dayPictureUrl;
            private String nightPictureUrl;
            private String temperature;
            private String weather;
            private String wind;

            public String getDate() {
                return date;
            }

            public void setDate(String date) {
                this.date = date;
            }

            public String getDayPictureUrl() {
                return dayPictureUrl;
            }

            public void setDayPictureUrl(String dayPictureUrl) {
                this.dayPictureUrl = dayPictureUrl;
            }

            public String getNightPictureUrl() {
                return nightPictureUrl;
            }

            public void setNightPictureUrl(String nightPictureUrl) {
                this.nightPictureUrl = nightPictureUrl;
            }

            public String getTemperature() {
                return temperature;
            }

            public void setTemperature(String temperature) {
                this.temperature = temperature;
            }

            public String getWeather() {
                return weather;
            }

            public void setWeather(String weather) {
                this.weather = weather;
            }

            public String getWind() {
                return wind;
            }

            public void setWind(String wind) {
                this.wind = wind;
            }
        }
    }
}
