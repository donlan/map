package dong.lan.mapeye.model;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * 项目：  MapEye
 * 作者：  梁桂栋
 * 日期：  7/3/2016  14:26.
 * Email: 760625325@qq.com
 */
public class Fence extends RealmObject {
    private Date createTime;
    private int tag;
    private String label;
    public RealmList<Point> points;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public RealmList<Point> getPoints() {
        return points;
    }

    public void setPoints(RealmList<Point> points) {
        this.points = points;
    }
}
