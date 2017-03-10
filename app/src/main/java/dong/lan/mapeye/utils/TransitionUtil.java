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

package dong.lan.mapeye.utils;

import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.model.LatLng;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import dong.lan.mapeye.model.Point;

/**
 * Created by 梁桂栋 on 16-11-13 ： 下午11:06.
 * Email:       760625325@qq.com
 * GitHub:      github.com/donlan
 * description: map
 */

public class TransitionUtil {
    private TransitionUtil() {
    }

    public static List<LatLng> Point2Latlng(List<Point> points) {
        if (points == null)
            return null;
        List<LatLng> res = new ArrayList<>();
        for (Point p : points)
            res.add(p.toLatlng());
        return res;
    }

    public static List<LatLng> Marker2Latlng(List<Marker> markers) {
        if (markers == null)
            return null;
        List<LatLng> res = new ArrayList<>();
        for (Marker p : markers)
            res.add(p.getPosition());
        return res;
    }

}
