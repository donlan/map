package dong.lan.mapeye.contracts;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.model.LatLng;

import dong.lan.mapeye.model.Point;

/**
 * Created by 梁桂栋 on 16-11-7 ： 下午10:54.
 * Email:       760625325@qq.com
 * GitHub:      github.com/donlan
 * description: map
 */

public final class AddRecordContract {
    private AddRecordContract() {
    }

    public interface AddRecordView {
        void clearMap();
    }

    public interface Presenter extends BaseLifeCyclePresenter {
        void location(BaiduMap baiduMap, LatLng latLng);

        void resetRecord();

        void addPoint(Point point);

        void saveRecord();

        void drawFence(BaiduMap baiduMap);

        void drawRoute(BaiduMap baiduMap);

        void drawAndAddPoint(BaiduMap baiduMap, LatLng latLng);

        void drawCircleFence(BaiduMap baiduMap);

        void test(BaiduMap baiduMap,LatLng point,boolean isIn);
    }
}
