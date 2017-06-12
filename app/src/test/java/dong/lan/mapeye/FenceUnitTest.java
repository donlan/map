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

package dong.lan.mapeye;

import com.baidu.mapapi.model.LatLng;

import org.junit.Test;

import java.util.List;

import dong.lan.mapeye.model.Point;
import dong.lan.mapeye.utils.PolygonHelper;

import static org.junit.Assert.assertEquals;

/**
 */

public class FenceUnitTest {


    @Test
    public void poly_fence_detect(List<Point> points,LatLng p,boolean isIn){
       boolean in = !PolygonHelper.isPointInPolygon(points,
                p.latitude,
                p.longitude);
        assertEquals(isIn,in);
    }

    @Test
    public void route_fence_detect(List<Point> points,LatLng p,boolean isIn){
        boolean in = PolygonHelper.isOnRoute(points,
                p.latitude,
                p.longitude);
        assertEquals(isIn,in);
    }
    @Test
    public void poly_fence_detect(Point fence,LatLng p,int radius,boolean isIn){
        boolean in = PolygonHelper.isInCircleFence(fence, p,0,radius);
        assertEquals(isIn,in);
    }
}
