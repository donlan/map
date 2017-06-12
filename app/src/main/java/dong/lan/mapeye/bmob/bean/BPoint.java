/*
 *
 *   Copyright (C) 2017 author : 梁桂栋
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

package dong.lan.mapeye.bmob.bean;

import com.baidu.mapapi.model.LatLng;

/**
 * Created by 梁桂栋 on 17-3-18 ： 下午7:42.
 * Email:       760625325@qq.com
 * GitHub:      github.com/donlan
 * description: map
 */

public class BPoint {
    public double lat;
    public double lng;

    public BPoint() {
    }

    public BPoint(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public BPoint(LatLng latLng) {
        this.lat = latLng.latitude;
        this.lng = latLng.longitude;
    }


    @Override
    public String toString() {
        return "BPoint{" +
                "lat=" + lat +
                ", lng=" + lng +
                '}';
    }
}
