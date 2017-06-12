package dong.lan.mapeye.model;

import com.baidu.mapapi.model.LatLng;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import dong.lan.mapeye.model.users.User;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

/**
 * Created by 梁桂栋 on 16-11-6 ： 下午3:59.
 * Email:       760625325@qq.com
 * GitHub:      github.com/donlan
 * description: 一个记录就是用户添加的围栏或者路径
 *
 * @see Record#TYPE_FENCE
 * @see Record#TYPE_ROUTE
 * @see Record#TYPE_CIRCLE
 */

public class Record extends RealmObject implements Serializable {
    public static final int TYPE_FENCE = 0;
    public static final int TYPE_ROUTE = 1;
    public static final int TYPE_CIRCLE = 2;
    public static final String GROUP_ID = "groupId";

    @PrimaryKey
    private String id;                  //默认是创建时的时间的毫秒数
    private User own;                   //创建者
    private long createTime;            //创建时间
    private String label;               //标签
    private String info;                //说明信息
    private int type;                   //类型
    private int radius;                 //如果类型为半径围栏则保存围栏的半径。单位米
    private RealmList<Point> points;    //包含的所有位置点
    private RealmList<User> users;      //目前无使用.....

    @Ignore
    private List<LatLng> latLngs;

    public List<LatLng> getLatLngPoints() {
        if (latLngs == null) {
            latLngs = new ArrayList<>(points.size());
            for (int i = 0; i < points.size(); i++) {
                latLngs.add(new LatLng(points.get(i).getLat(), points.get(i).getLng()));
            }
        }
        return latLngs;
    }

    public static String getRecordTypeStr(int type) {
        if (type == TYPE_FENCE)
            return "围栏";
        else if (type == TYPE_ROUTE)
            return "路径";
        else if (type == TYPE_CIRCLE)
            return "半径围栏";
        else
            return "未知";
    }


    public String recordTittleInfo() {
        return "[" + getRecordTypeStr(type) + "]" + label;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getOwn() {
        return own;
    }

    public void setOwn(User own) {
        this.own = own;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public RealmList<Point> getPoints() {
        return points;
    }

    public void setPoints(RealmList<Point> points) {
        this.points = points;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public RealmList<User> getUsers() {
        return users;
    }

    public void setUsers(RealmList<User> users) {
        this.users = users;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    /**
     * @return 不在记录规定的访问内时的提示信息
     */
    public String getNotInRecordStr() {
        if (type == TYPE_FENCE)
            return " 不在围栏 " + label + " 内";
        else if (type == TYPE_ROUTE)
            return " 不在路径 " + label + " 上";
        else if (type == TYPE_CIRCLE)
            return " 不在半径围栏 " + label + " 内";
        else
            return " 不在 " + label + " 指定的范围内";
    }

    @Override
    public String toString() {
        return "Record{" +
                "id='" + id + '\'' +
                ", own=" + own +
                ", createTime=" + createTime +
                ", label='" + label + '\'' +
                ", info='" + info + '\'' +
                ", type=" + type +
                ", points=" + points +
                ", users=" + users +
                '}';
    }
}
