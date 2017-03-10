/*
 *
 *   Copyright (C) 2016 author : 梁桂栋
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 *   Email me : stonelavender@hotmail.com
 *
 */

package dong.lan.mapeye.model;

import com.tencent.qcloud.tlslibrary.helper.JMHelper;

import dong.lan.mapeye.common.MessageHelper;
import dong.lan.mapeye.utils.NetUtils;

/**
 * Created by 梁桂栋 on 16-12-21 ： 下午11:21.
 * Email:       760625325@qq.com
 * GitHub:      github.com/donlan
 * description: map
 */

public class ClientInfo {

    public static final int CHARGE_USB = 1;
    public static final int CHARGE_AC = 0;
    public static final int CHARGE_NONE = -1;


    public static final int NET_WIFI = 100;
    public static final int NET_4G = 101;
    public static final int NET_3G = 102;
    public static final int NET_2G = 103;
    public static final int NET_UNKNOWN = 104;
    public static final int NET_NO = 105;

    private float battery;
    private int chargeStatus;
    private int netStatus;

    public float getBattery() {
        return battery;
    }

    public void setBattery(float battery) {
        this.battery = battery;
    }

    public int getChargeStatus() {
        return chargeStatus;
    }

    public void setChargeStatus(int chargeStatus) {
        this.chargeStatus = chargeStatus;
    }

    public int getNetStatus() {
        return netStatus;
    }

    public void setNetStatus(int netStatus) {
        this.netStatus = netStatus;
    }

    public static int getNetType(NetUtils.NetworkType networkType) {
        if (networkType == null)
            return NET_UNKNOWN;
        if (networkType.equals(NetUtils.NetworkType.NETWORK_WIFI))
            return NET_WIFI;
        if (networkType.equals(NetUtils.NetworkType.NETWORK_4G))
            return NET_4G;
        if (networkType.equals(NetUtils.NetworkType.NETWORK_3G))
            return NET_3G;
        if (networkType.equals(NetUtils.NetworkType.NETWORK_2G))
            return NET_2G;
        if (networkType.equals(NetUtils.NetworkType.NETWORK_NO))
            return NET_NO;
        return NET_UNKNOWN;
    }

    public String netDisplay() {
        if (netStatus == NET_WIFI)
            return "WIFI";
        if (netStatus == NET_4G)
            return "4G";
        if (netStatus == NET_3G)
            return "3G";
        if (netStatus == NET_2G)
            return "2G";
        if (netStatus == NET_NO)
            return "无网络";
        return "未知";
    }

    public String chargeDisplay() {
        if (chargeStatus == CHARGE_AC)
            return "交流电";
        if (chargeStatus == CHARGE_USB)
            return "USB充电";
        if (chargeStatus == CHARGE_NONE)
            return "无充电";
        return "未知";
    }

    @Override
    public String toString() {
        return "手机状态{\n" +
                "电池：" + battery +
                "\n 充当状态：" + chargeDisplay() +
                "\n 网络状态：" + netDisplay() +
                "\n}";
    }

    public String toJson() {
        return MessageHelper.getInstance().toJson(this);
    }
}
