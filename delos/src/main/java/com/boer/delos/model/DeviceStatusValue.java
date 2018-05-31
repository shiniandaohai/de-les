package com.boer.delos.model;

import java.io.Serializable;

/**
 * @author XieQingTing
 * @Description: 设备状态值
 * create at 2016/5/19 16:03
 */
public class DeviceStatusValue implements Serializable {

    //灯
//  private int state;窗帘,插座,环境检测,存在传感器,电磁阀都有int state
    private String state = "0"; // 0: 关 、1:开
    private String state2 = "0";
    private String state3 = "0";
    private String state4 = "0";
    //pannel
    private String state5 = "0";//pannel
    private String state6 = "0";//pannel

    private String coeff; //调光灯用 0-100
    //插座
    private String energy;
    private String I;
    private String P;
    private String U;

    //环境检测
    private String time;
    private String pa;// 甲醛含量
    private String temp;// 温度
    private Integer pm25;// PM2.5
    private String humid;// 湿度

    //健康气体
    private Integer co2;// 二氧化碳含量
    private String o2;// 氧气含量

    //存在传感器
    private Integer set;// 判断存在传感器是否打开布防

    //电磁阀

    private String co;// 一氧化碳
    private String ch4;// 甲烷
    private String smoke;// 浓度

    //门窗磁

    private String lvolt; //欠压 0: false 1: true
    private String position; //打开状态 0: false 1: true
    private String powerPercent;//电量
    //    private String set; // 布防 0关 1开
//    private String state;// 1报警 0灰色

    //music

    private Integer volume;//":5,
    private Integer currNo;//":8,
    private Integer playState;//":1 //0表示停止播放、1表示开始播放、2表示暂停播放
    //窗帘
    private String open;
//    private String state = "0";
    /**
     * 台上桑净水器start
     */
    //台上净水器 http://192.168.16.16/wiki/doku.php?id=%E5%85%B6%E4%BB%96%E4%BA%8B%E9%A1%B9
    private Integer waterLevel;  //水位状态
    //    private Integer state; //运行状态
    private Integer rawTDS;         // 原水TDS  台上、台下
    private Integer pureTDS;        // 净水TDS  台上、台下
    private Integer filterLevel1;       //滤芯  台上、台下
    private Integer filterLevel2;       //
    private Integer filterLevel3;       //
    private Integer filterLevel4;       //
    //
    private Integer rawCisternLevel;        //原水箱水位，0-正常，1-缺水
    private Integer rawCisternPos;      //原水箱位置，0-正常，1-移开
    private Integer purifying;      //静水状态，0-正常，1-制水
    private Integer dewatering;         //排水状态，0-正常，1-缺水
    private Integer machineState;       //整机状态，0-正常，1-故障
    //
    private Integer setTemp;        //设定温度
    private Integer actualTemp;         //实际温度
    private Integer tHotWater;      //今日热水日用量单位ml
    private Integer tWarmWater;         //今日温水日用量单位ml
    private Integer yHotWater;         //昨日热水日用量单位ml
    private Integer yWarmWater;          //昨日温水日用量单位ml
    private Integer totalWater;

    private Integer lackWater; /*台下*///缺水状态，1-表示缺水，0-表示正常
    private Integer leakWater;//漏水故障，2-表示长时间连续制水报警，1-表示漏水故障，0-表示正常无故障
    private Integer rinse;//冲洗状态，0-未冲洗，1-自动冲洗，2-手动冲洗

    //镭豆
    private Integer pm10;
//    private Float temp;
    private String humidity;
    private Integer aqiPm25;
    private Integer aqiPm10;
    private String rtvoc;
//    private Integer pm25;

    public Integer getMode() {
        return mode;
    }

    public void setMode(Integer mode) {
        this.mode = mode;
    }

    private Integer mode;

    public String getOpen() {
        return open;
    }

    public void setOpen(String open) {
        this.open = open;
    }

    public String getState5() {
        return state5;
    }

    public void setState5(String state5) {
        this.state5 = state5;
    }

    public String getState6() {
        return state6;
    }

    public void setState6(String state6) {
        this.state6 = state6;
    }

    public Integer getVolume() {
        if(volume==null){
            return 0;
        }
        return volume;
    }

    public void setVolume(Integer volume) {
        this.volume = volume;
    }

    public Integer getCurrNo() {
        return currNo;
    }

    public void setCurrNo(Integer currNo) {
        this.currNo = currNo;
    }

    public Integer getPlayState() {
        return playState;
    }

    public void setPlayState(Integer playState) {
        this.playState = playState;
    }

    public Integer getyHotWater() {
        return yHotWater;
    }

    public void setyHotWater(Integer yHotWater) {
        this.yHotWater = yHotWater;
    }

    public Integer getyWarmWater() {
        return yWarmWater;
    }

    public void setyWarmWater(Integer yWarmWater) {
        this.yWarmWater = yWarmWater;
    }

    public Integer getLackWater() {
        return lackWater;
    }

    public void setLackWater(Integer lackWater) {
        this.lackWater = lackWater;
    }

    public Integer getLeakWater() {
        return leakWater;
    }

    public void setLeakWater(Integer leakWater) {
        this.leakWater = leakWater;
    }

    public Integer getRinse() {
        return rinse;
    }

    public void setRinse(Integer rinse) {
        this.rinse = rinse;
    }

    public Integer getWaterLevel() {
        return waterLevel;
    }

    public void setWaterLevel(Integer waterLevel) {
        this.waterLevel = waterLevel;
    }

    public Integer getRawTDS() {
        return rawTDS;
    }

    public void setRawTDS(Integer rawTDS) {
        this.rawTDS = rawTDS;
    }

    public Integer getPureTDS() {
        return pureTDS;
    }

    public void setPureTDS(Integer pureTDS) {
        this.pureTDS = pureTDS;
    }

    public Integer getFilterLevel1() {
        return filterLevel1;
    }

    public void setFilterLevel1(Integer filterLevel1) {
        this.filterLevel1 = filterLevel1;
    }

    public Integer getFilterLevel2() {
        return filterLevel2;
    }

    public void setFilterLevel2(Integer filterLevel2) {
        this.filterLevel2 = filterLevel2;
    }

    public Integer getFilterLevel3() {
        return filterLevel3;
    }

    public void setFilterLevel3(Integer filterLevel3) {
        this.filterLevel3 = filterLevel3;
    }

    public Integer getFilterLevel4() {
        return filterLevel4;
    }

    public void setFilterLevel4(Integer filterLevel4) {
        this.filterLevel4 = filterLevel4;
    }

    public Integer getRawCisternLevel() {
        return rawCisternLevel;
    }

    public void setRawCisternLevel(Integer rawCisternLevel) {
        this.rawCisternLevel = rawCisternLevel;
    }

    public Integer getRawCisternPos() {
        return rawCisternPos;
    }

    public void setRawCisternPos(Integer rawCisternPos) {
        this.rawCisternPos = rawCisternPos;
    }

    public Integer getPurifying() {
        return purifying;
    }

    public void setPurifying(Integer purifying) {
        this.purifying = purifying;
    }

    public Integer getDewatering() {
        return dewatering;
    }

    public void setDewatering(Integer dewatering) {
        this.dewatering = dewatering;
    }

    public Integer getMachineState() {
        return machineState;
    }

    public void setMachineState(Integer machineState) {
        this.machineState = machineState;
    }

    public Integer getSetTemp() {
        return setTemp;
    }

    public void setSetTemp(Integer setTemp) {
        this.setTemp = setTemp;
    }

    public Integer getActualTemp() {
        return actualTemp;
    }

    public void setActualTemp(Integer actualTemp) {
        this.actualTemp = actualTemp;
    }

    public Integer gettHotWater() {
        return tHotWater;
    }

    public void settHotWater(Integer tHotWater) {
        this.tHotWater = tHotWater;
    }

    public Integer gettWarmWater() {
        return tWarmWater;
    }

    public void settWarmWater(Integer tWarmWater) {
        this.tWarmWater = tWarmWater;
    }

    public Integer getTotalWater() {
        return totalWater;
    }

    public void setTotalWater(Integer totalWater) {
        this.totalWater = totalWater;
    }

    /**
     * 台上桑净水器end
     */

    public Integer getSet() {
        return set;
    }

    public String getCoeff() {
        return coeff;
    }

    public void setCoeff(String coeff) {
        this.coeff = coeff;
    }

    public void setSet(Integer set) {
        this.set = set;
    }

    public String getLvolt() {
        return lvolt;
    }

    public void setLvolt(String lvolt) {
        this.lvolt = lvolt;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getPowerPercent() {
        return powerPercent;
    }

    public void setPowerPercent(String powerPercent) {
        this.powerPercent = powerPercent;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getState2() {
        return state2;
    }

    public void setState2(String state2) {
        this.state2 = state2;
    }

    public String getState3() {
        return state3;
    }

    public void setState3(String state3) {
        this.state3 = state3;
    }

    public String getEnergy() {
        return energy;
    }

    public void setEnergy(String energy) {
        this.energy = energy;
    }

    public String getI() {
        return I;
    }

    public void setI(String i) {
        I = i;
    }

    public String getP() {
        return P;
    }

    public void setP(String p) {
        P = p;
    }

    public String getU() {
        return U;
    }

    public void setU(String u) {
        U = u;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPa() {
        return pa;
    }

    public void setPa(String pa) {
        this.pa = pa;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public Integer getPm25() {
        return pm25;
    }

    public void setPm25(Integer pm25) {
        this.pm25 = pm25;
    }

    public String getHumid() {
        return humid;
    }

    public void setHumid(String humid) {
        this.humid = humid;
    }

    public Integer getCo2() {
        return co2;
    }

    public void setCo2(Integer co2) {
        this.co2 = co2;
    }

    public String getO2() {
        return o2;
    }

    public void setO2(String o2) {
        this.o2 = o2;
    }

    public String getCo() {
        return co;
    }

    public void setCo(String co) {
        this.co = co;
    }

    public String getCh4() {
        return ch4;
    }

    public void setCh4(String ch4) {
        this.ch4 = ch4;
    }

    public String getSmoke() {
        return smoke;
    }

    public void setSmoke(String smoke) {
        this.smoke = smoke;
    }

    public String getState4() {
        return state4;
    }

    public void setState4(String state4) {
        this.state4 = state4;
    }

    public Integer getPm10() {
        return pm10;
    }

    public void setPm10(Integer pm10) {
        this.pm10 = pm10;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public Integer getAqiPm25() {
        return aqiPm25;
    }

    public void setAqiPm25(Integer aqiPm25) {
        this.aqiPm25 = aqiPm25;
    }

    public Integer getAqiPm10() {
        return aqiPm10;
    }

    public void setAqiPm10(Integer aqiPm10) {
        this.aqiPm10 = aqiPm10;
    }

    public String getRtvoc() {
        if(rtvoc==null)return "";
        return rtvoc;
    }

    public void setRtvoc(String rtvoc) {
        this.rtvoc = rtvoc;
    }
}
