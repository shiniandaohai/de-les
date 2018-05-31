package com.boer.delos.utils.updateapp;

/**
 * Created by Administrator on 2016/5/5 0005.
 * 与服务端保存版本信息的文件内容对应
 * update.txt格式
 * XXX&1.3&这里写点描述&http://XXXX/XX.apk
 */
public class UpdateInfo {
    private String version;
    private String description;
    private String url;

    public String getVersion()
    {
        return version;
    }
    public void setVersion(String version)
    {
        this.version = version;
    }
    public String getDescription()
    {
        return description;
    }
    public void setDescription(String description)
    {
        this.description = description;
    }
    public String getUrl()
    {
        return url;
    }
    public void setUrl(String url)
    {
        this.url = url;
    }
}
