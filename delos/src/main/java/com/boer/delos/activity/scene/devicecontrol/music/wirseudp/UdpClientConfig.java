package com.boer.delos.activity.scene.devicecontrol.music.wirseudp;


import com.boer.delos.activity.scene.devicecontrol.music.wirseudp.utils.CharsetUtil;

/**
 * udp配置
 */
public class UdpClientConfig {
    private String charsetName = CharsetUtil.UTF_8;//默认编码
    private long receiveTimeout = 10000;//接受消息的超时时间,0为无限大
    private int localPort = 11001;
    private int receivePort = 11000;

    private UdpClientConfig() {
    }

    public String getCharsetName() {
        return charsetName;
    }


    public long getReceiveTimeout() {
        return receiveTimeout;
    }

    public int getLocalPort() {
        return localPort;
    }

    public static class Builder {
        private UdpClientConfig mTcpConnConfig;

        public Builder() {
            mTcpConnConfig = new UdpClientConfig();
        }

        public UdpClientConfig create() {
            return mTcpConnConfig;
        }

        public Builder setCharsetName(String charsetName) {
            mTcpConnConfig.charsetName = charsetName;
            return this;
        }

        public Builder setReceiveTimeout(long timeout) {
            mTcpConnConfig.receiveTimeout = timeout;
            return this;
        }

        public Builder setLocalPort(int localPort) {
            mTcpConnConfig.localPort = localPort;
            return this;
        }

        public Builder setReceivePort(int receivePort) {
            mTcpConnConfig.receivePort = receivePort;
            return this;
        }
    }

    public int getReceivePort() {
        return receivePort;
    }

    public void setReceivePort(int receivePort) {
        this.receivePort = receivePort;
    }
}
