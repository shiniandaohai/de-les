package com.boer.delos.dao.entity;

import java.util.List;

/**
 * Created by apple on 17/5/6.
 */

public class WeatherDetailBean {

    /**
     * city : 无锡
     * updatetime : 22:15
     * wendu : 20
     * fengli : 2级
     * shidu : 69%
     * fengxiang : 东风
     * sunrise_1 : 05:10
     * sunset_1 : 18:42
     * sunrise_2 :
     * sunset_2 :
     * environment : {"aqi":145,"pm25":76,"suggest":"儿童、老年人及心脏、呼吸系统疾病患者人群应减少长时间或高强度户外锻炼","quality":"轻度污染","MajorPollutants":"颗粒物(PM10)","o3":60,"co":1,"pm10":237,"so2":15,"no2":68,"time":"22:00:00"}
     * yesterday : {"date_1":"5日星期五","high_1":"高温 24℃","low_1":"低温 16℃","day_1":{"type_1":"多云","fx_1":"西北风","fl_1":"3-4级"},"night_1":{"type_1":"多云","fx_1":"西北风","fl_1":"3-4级"}}
     * forecast : {"weather":[{"date":"6日星期六","high":"高温 25℃","low":"低温 17℃","day":{"type":"多云","fengxiang":"东风","fengli":"3-4级"},"night":{"type":"多云","fengxiang":"东南风","fengli":"微风级"}},{"date":"7日星期天","high":"高温 27℃","low":"低温 16℃","day":{"type":"阴","fengxiang":"东南风","fengli":"微风级"},"night":{"type":"阵雨","fengxiang":"东南风","fengli":"3-4级"}},{"date":"8日星期一","high":"高温 20℃","low":"低温 17℃","day":{"type":"小雨","fengxiang":"东南风","fengli":"3-4级"},"night":{"type":"小雨","fengxiang":"西风","fengli":"3-4级"}},{"date":"9日星期二","high":"高温 25℃","low":"低温 14℃","day":{"type":"多云","fengxiang":"西风","fengli":"3-4级"},"night":{"type":"多云","fengxiang":"东南风","fengli":"微风级"}},{"date":"10日星期三","high":"高温 30℃","low":"低温 18℃","day":{"type":"多云","fengxiang":"东南风","fengli":"微风级"},"night":{"type":"多云","fengxiang":"南风","fengli":"微风级"}}]}
     * zhishus : {"zhishu":[{"name":"晨练指数","value":"较适宜","detail":"早晨气象条件较适宜晨练，但天气阴沉，晨练时会感觉有点凉，晨练时着装不要过于单薄，并避免在林中晨练。"},{"name":"舒适度","value":"较舒适","detail":"白天以阴或多云天气为主，但稍会让您感到有点儿热，但大部分人完全可以接受。"},{"name":"穿衣指数","value":"舒适","detail":"建议着长袖T恤、衬衫加单裤等服装。年老体弱者宜着针织长袖衬衫、马甲和长裤。"},{"name":"感冒指数","value":"少发","detail":"各项气象条件适宜，无明显降温过程，发生感冒机率较低。"},{"name":"晾晒指数","value":"不太适宜","detail":"天气阴沉，不利于水分的迅速蒸发，不太适宜晾晒。若需要晾晒，请尽量选择通风的地点。"},{"name":"旅游指数","value":"适宜","detail":"天气较好，温度适宜，总体来说还是好天气哦，这样的天气适宜旅游，您可以尽情地享受大自然的风光。"},{"name":"紫外线强度","value":"最弱","detail":"属弱紫外线辐射天气，无需特别防护。若长期在户外，建议涂擦SPF在8-12之间的防晒护肤品。"},{"name":"洗车指数","value":"较适宜","detail":"较适宜洗车，未来一天无雨，风力较小，擦洗一新的汽车至少能保持一天。"},{"name":"运动指数","value":"较不宜","detail":"阴天，且天气较热，请减少运动时间并降低运动强度。"},{"name":"约会指数","value":"较适宜","detail":"虽然天空有些阴沉，但情侣们可以放心外出，不用担心天气来调皮捣乱而影响了兴致。"},{"name":"雨伞指数","value":"不带伞","detail":"阴天，但降水概率很低，因此您在出门的时候无须带雨伞。"}]}
     */

    private String city;
    private String updatetime;
    private int wendu;
    private String fengli;
    private String shidu;
    private String fengxiang;
    private String sunrise_1;
    private String sunset_1;
    private String sunrise_2;
    private String sunset_2;
    private EnvironmentBean environment;
    private YesterdayBean yesterday;
    private ForecastBean forecast;
    private ZhishusBean zhishus;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(String updatetime) {
        this.updatetime = updatetime;
    }

    public int getWendu() {
        return wendu;
    }

    public void setWendu(int wendu) {
        this.wendu = wendu;
    }

    public String getFengli() {
        return fengli;
    }

    public void setFengli(String fengli) {
        this.fengli = fengli;
    }

    public String getShidu() {
        return shidu;
    }

    public void setShidu(String shidu) {
        this.shidu = shidu;
    }

    public String getFengxiang() {
        return fengxiang;
    }

    public void setFengxiang(String fengxiang) {
        this.fengxiang = fengxiang;
    }

    public String getSunrise_1() {
        return sunrise_1;
    }

    public void setSunrise_1(String sunrise_1) {
        this.sunrise_1 = sunrise_1;
    }

    public String getSunset_1() {
        return sunset_1;
    }

    public void setSunset_1(String sunset_1) {
        this.sunset_1 = sunset_1;
    }

    public String getSunrise_2() {
        return sunrise_2;
    }

    public void setSunrise_2(String sunrise_2) {
        this.sunrise_2 = sunrise_2;
    }

    public String getSunset_2() {
        return sunset_2;
    }

    public void setSunset_2(String sunset_2) {
        this.sunset_2 = sunset_2;
    }

    public EnvironmentBean getEnvironment() {
        return environment;
    }

    public void setEnvironment(EnvironmentBean environment) {
        this.environment = environment;
    }

    public YesterdayBean getYesterday() {
        return yesterday;
    }

    public void setYesterday(YesterdayBean yesterday) {
        this.yesterday = yesterday;
    }

    public ForecastBean getForecast() {
        return forecast;
    }

    public void setForecast(ForecastBean forecast) {
        this.forecast = forecast;
    }

    public ZhishusBean getZhishus() {
        return zhishus;
    }

    public void setZhishus(ZhishusBean zhishus) {
        this.zhishus = zhishus;
    }

    public static class EnvironmentBean {
        /**
         * aqi : 145
         * pm25 : 76
         * suggest : 儿童、老年人及心脏、呼吸系统疾病患者人群应减少长时间或高强度户外锻炼
         * quality : 轻度污染
         * MajorPollutants : 颗粒物(PM10)
         * o3 : 60
         * co : 1
         * pm10 : 237
         * so2 : 15
         * no2 : 68
         * time : 22:00:00
         */

        private int aqi;
        private int pm25;
        private String suggest;
        private String quality;
        private String MajorPollutants;
        private int o3;
        private int co;
        private int pm10;
        private int so2;
        private int no2;
        private String time;

        public int getAqi() {
            return aqi;
        }

        public void setAqi(int aqi) {
            this.aqi = aqi;
        }

        public int getPm25() {
            return pm25;
        }

        public void setPm25(int pm25) {
            this.pm25 = pm25;
        }

        public String getSuggest() {
            return suggest;
        }

        public void setSuggest(String suggest) {
            this.suggest = suggest;
        }

        public String getQuality() {
            return quality;
        }

        public void setQuality(String quality) {
            this.quality = quality;
        }

        public String getMajorPollutants() {
            return MajorPollutants;
        }

        public void setMajorPollutants(String MajorPollutants) {
            this.MajorPollutants = MajorPollutants;
        }

        public int getO3() {
            return o3;
        }

        public void setO3(int o3) {
            this.o3 = o3;
        }

        public int getCo() {
            return co;
        }

        public void setCo(int co) {
            this.co = co;
        }

        public int getPm10() {
            return pm10;
        }

        public void setPm10(int pm10) {
            this.pm10 = pm10;
        }

        public int getSo2() {
            return so2;
        }

        public void setSo2(int so2) {
            this.so2 = so2;
        }

        public int getNo2() {
            return no2;
        }

        public void setNo2(int no2) {
            this.no2 = no2;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }
    }

    public static class YesterdayBean {
        /**
         * date_1 : 5日星期五
         * high_1 : 高温 24℃
         * low_1 : 低温 16℃
         * day_1 : {"type_1":"多云","fx_1":"西北风","fl_1":"3-4级"}
         * night_1 : {"type_1":"多云","fx_1":"西北风","fl_1":"3-4级"}
         */

        private String date_1;
        private String high_1;
        private String low_1;
        private Day1Bean day_1;
        private Night1Bean night_1;

        public String getDate_1() {
            return date_1;
        }

        public void setDate_1(String date_1) {
            this.date_1 = date_1;
        }

        public String getHigh_1() {
            return high_1;
        }

        public void setHigh_1(String high_1) {
            this.high_1 = high_1;
        }

        public String getLow_1() {
            return low_1;
        }

        public void setLow_1(String low_1) {
            this.low_1 = low_1;
        }

        public Day1Bean getDay_1() {
            return day_1;
        }

        public void setDay_1(Day1Bean day_1) {
            this.day_1 = day_1;
        }

        public Night1Bean getNight_1() {
            return night_1;
        }

        public void setNight_1(Night1Bean night_1) {
            this.night_1 = night_1;
        }

        public static class Day1Bean {
            /**
             * type_1 : 多云
             * fx_1 : 西北风
             * fl_1 : 3-4级
             */

            private String type_1;
            private String fx_1;
            private String fl_1;

            public String getType_1() {
                return type_1;
            }

            public void setType_1(String type_1) {
                this.type_1 = type_1;
            }

            public String getFx_1() {
                return fx_1;
            }

            public void setFx_1(String fx_1) {
                this.fx_1 = fx_1;
            }

            public String getFl_1() {
                return fl_1;
            }

            public void setFl_1(String fl_1) {
                this.fl_1 = fl_1;
            }
        }

        public static class Night1Bean {
            /**
             * type_1 : 多云
             * fx_1 : 西北风
             * fl_1 : 3-4级
             */

            private String type_1;
            private String fx_1;
            private String fl_1;

            public String getType_1() {
                return type_1;
            }

            public void setType_1(String type_1) {
                this.type_1 = type_1;
            }

            public String getFx_1() {
                return fx_1;
            }

            public void setFx_1(String fx_1) {
                this.fx_1 = fx_1;
            }

            public String getFl_1() {
                return fl_1;
            }

            public void setFl_1(String fl_1) {
                this.fl_1 = fl_1;
            }
        }
    }

    public static class ForecastBean {
        private List<WeatherBean> weather;

        public List<WeatherBean> getWeather() {
            return weather;
        }

        public void setWeather(List<WeatherBean> weather) {
            this.weather = weather;
        }

        public static class WeatherBean {
            /**
             * date : 6日星期六
             * high : 高温 25℃
             * low : 低温 17℃
             * day : {"type":"多云","fengxiang":"东风","fengli":"3-4级"}
             * night : {"type":"多云","fengxiang":"东南风","fengli":"微风级"}
             */

            private String date;
            private String high;
            private String low;
            private DayBean day;
            private NightBean night;

            public String getDate() {
                return date;
            }

            public void setDate(String date) {
                this.date = date;
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

            public DayBean getDay() {
                return day;
            }

            public void setDay(DayBean day) {
                this.day = day;
            }

            public NightBean getNight() {
                return night;
            }

            public void setNight(NightBean night) {
                this.night = night;
            }

            public static class DayBean {
                /**
                 * type : 多云
                 * fengxiang : 东风
                 * fengli : 3-4级
                 */

                private String type;
                private String fengxiang;
                private String fengli;

                public String getType() {
                    return type;
                }

                public void setType(String type) {
                    this.type = type;
                }

                public String getFengxiang() {
                    return fengxiang;
                }

                public void setFengxiang(String fengxiang) {
                    this.fengxiang = fengxiang;
                }

                public String getFengli() {
                    return fengli;
                }

                public void setFengli(String fengli) {
                    this.fengli = fengli;
                }
            }

            public static class NightBean {
                /**
                 * type : 多云
                 * fengxiang : 东南风
                 * fengli : 微风级
                 */

                private String type;
                private String fengxiang;
                private String fengli;

                public String getType() {
                    return type;
                }

                public void setType(String type) {
                    this.type = type;
                }

                public String getFengxiang() {
                    return fengxiang;
                }

                public void setFengxiang(String fengxiang) {
                    this.fengxiang = fengxiang;
                }

                public String getFengli() {
                    return fengli;
                }

                public void setFengli(String fengli) {
                    this.fengli = fengli;
                }
            }
        }
    }

    public static class ZhishusBean {
        private List<ZhishuBean> zhishu;

        public List<ZhishuBean> getZhishu() {
            return zhishu;
        }

        public void setZhishu(List<ZhishuBean> zhishu) {
            this.zhishu = zhishu;
        }

        public static class ZhishuBean {
            /**
             * name : 晨练指数
             * value : 较适宜
             * detail : 早晨气象条件较适宜晨练，但天气阴沉，晨练时会感觉有点凉，晨练时着装不要过于单薄，并避免在林中晨练。
             */

            private String name;
            private String value;
            private String detail;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getValue() {
                return value;
            }

            public void setValue(String value) {
                this.value = value;
            }

            public String getDetail() {
                return detail;
            }

            public void setDetail(String detail) {
                this.detail = detail;
            }
        }
    }
}
