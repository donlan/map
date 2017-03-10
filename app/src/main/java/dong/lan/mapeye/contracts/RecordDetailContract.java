

package dong.lan.mapeye.contracts;


import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.model.LatLng;

import java.util.List;

import dong.lan.mapeye.model.users.Contact;
import dong.lan.mapeye.model.Record;
import dong.lan.mapeye.model.TraceLocation;

/**
 * Created by 梁桂栋 on 16-11-10 ： 下午7:15.
 * Email:       760625325@qq.com
 * GitHub:      github.com/donlan
 * description: map
 */

public final class RecordDetailContract {

    private RecordDetailContract() {
    }

    public interface RecordDetailView {
        int REFRESH = 1;
        String KEY_RECORD = "record";
        String KEY_POSITION = "position";

        /**
         * 初始化
         */
        void init();

        /**
         * 刷新监听用户的列表
         */
        void refreshList();

        /**
         * 检测跳转过来的Intent中是否含有 KEY_RECORD ，KEY_POSITION
         */
        void checkIntent();


        /**
         * 设置监听用户的列表
         */
        void setAdapter();

        /**
         * 将地图定位到指定点
         *
         * @param point Record中的第一个点
         */
        void setRecordLocation(LatLng point);

        /**
         * 在地图上绘制围栏
         *
         * @param points 构成围栏的点
         */
        void drawRecord(List<LatLng> points, int type, int radius);


        /**
         * @return 当前页面的baiduMap实例
         */
        BaiduMap getMap();

        /**
         * @return 当前页面地图显示的Markert图标
         */
        BitmapDescriptor getMarkerIcon();


        /**
         * 开始编辑记录
         */
        void beginEditAction();

        /**
         * 结束编辑记录
         */
        void endEditAction();


    }

    public interface Presenter extends BaseLifeCyclePresenter {

        /**
         * @return 返回监听用户的个数
         */
        int getMonitorUsersSize();

        /**
         * @return 所有的监听用户
         */
        List<Contact> getMonitors();

        /**
         * @param position 列表中的位置
         * @return 指定位置的监听用户
         */
        Contact getContact(int position);

        /**
         * 根据id加载Record
         *
         * @param id Record 的id
         */
        void initByRecord(String id);

        /**
         * 删除当前的Record
         */
        void deleteRecord();

        /**
         * 记录从主页跳转过来的列表位置
         *
         * @param position 主页记录的显示位置
         */
        void setPosition(int position);

        /**
         * @return 返回当前的记录
         */
        Record initByRecord();

        /**
         * 开始重新编辑记录
         */
        void reeditRecord();

        /**
         * 重新编辑是添加一个新的节点
         */
        void addPoint(LatLng point);

        /**
         * 拖动节点标记
         *
         * @param marker 拖动的节点
         */
        void dragMarker(Marker marker);

        /**
         * 检查重新编辑的记录并保存
         */
        void checkEdit();

        /**
         * 撤销所有编辑操作
         */
        void cancelEdit();

        /**
         * 开始某个用户的位置监听
         *
         * @param position 列表中的位置（通过该位置找到指定的用户）
         */
        void startMonitor(int position);

        /**
         * 结束某个用户的位置监听
         *
         * @param position 列表中的位置（通过该位置找到指定的用户）
         */
        void stopMonitor(int position);

        /**
         * 将某个用户永久移出监听列表
         *
         * @param position 列表中的位置（通过该位置找到指定的用户）
         */
        void removeMonitorUser(int position);

        /**
         * 处理发送过来的位置信息
         *
         * @param msg 消息类型为位置信息的消息
         */
        void handlerLocationMessage(TraceLocation traceLocation);

        /**
         * 记录编辑的撤销操作
         */
        void undoAction();

        /**
         * 记录编辑的重做操作
         */
        void redoAction();

        /**
         * 设置远程监听设备定位频率
         *
         * @param layoutPosition 列表位置：可以知道是哪一个监听用户
         */
        void setMonitorLocationSpeed(int layoutPosition);

        /**
         * 重命名一个记录
         */
        void renameRecord();

        /**
         * 发送获取绑定用户的手机信息的请求信息
         */
        void sendClientInfoReq(int position);

        /**
         * 跳转到监听用户的定时任务中心
         * @param position 列表索引
         */
        void toMonitorTimerTask(int position);
    }
}
