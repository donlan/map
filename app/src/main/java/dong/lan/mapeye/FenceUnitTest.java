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
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import dong.lan.mapeye.model.Point;
import dong.lan.mapeye.model.Record;
import dong.lan.mapeye.utils.PolygonHelper;


/**
 */

public class FenceUnitTest {


    public boolean poly_fence_detect(List<Point> points,LatLng p,boolean isIn){
      return !PolygonHelper.isPointInPolygon(points,
                p.latitude,
                p.longitude);
    }

    public boolean route_fence_detect(List<Point> points,LatLng p,boolean isIn){
        boolean in = PolygonHelper.isOnRoute(points,
                p.latitude,
                p.longitude);
       return !in;
    }
    public boolean poly_fence_detect(Point fence,LatLng p,int radius,boolean isIn){
        boolean in = PolygonHelper.isInCircleFence(fence, p,0,radius);
        return !in;
    }

    public void test(int type, List<LatLng> points, LatLng point,int radius, boolean isIn) {
        StringBuilder sb = new StringBuilder();
        if(type == Record.TYPE_CIRCLE){
            sb.append("半径围栏：");
            Point p = new Point(points.get(0).latitude,points.get(0).latitude);
                sb.append("(");
                sb.append(p.getLng());
                sb.append(",");
                sb.append(p.getLat());
                sb.append(")");
            sb.append("\n算法检测在围栏中：");
            sb.append(poly_fence_detect(p,point,radius,isIn));
            sb.append("\n实际结果：");
            sb.append(isIn);
        }else if(type == Record.TYPE_FENCE){
            sb.append("多边形围栏：");
            List<Point> sp = new ArrayList<>();
            for (LatLng p:
                    points) {
                sb.append("(");
                sb.append(p.longitude);
                sb.append(",");
                sb.append(p.latitude);
                sb.append(")");
                sp.add(new Point(p));
            }
            sb.append("\n算法检测在围栏中：");
            sb.append(poly_fence_detect(sp,point,isIn));
            sb.append("\n实际结果：");
            sb.append(isIn);
        }else if(type == Record.TYPE_ROUTE){
            sb.append("折线围栏：");
            List<Point> sp = new ArrayList<>();
            for (LatLng p:
                    points) {
                sb.append("(");
                sb.append(p.longitude);
                sb.append(",");
                sb.append(p.latitude);
                sb.append(")");
                sp.add(new Point(p));
            }
            sb.append("\n算法检测在围栏中：");
            sb.append(route_fence_detect(sp,point,isIn));
            sb.append("\n实际结果：");
            sb.append(isIn);
        }
        Logger.d(sb.toString());
    }
}
