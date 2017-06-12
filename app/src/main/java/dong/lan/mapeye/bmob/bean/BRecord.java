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

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobUser;
import dong.lan.mapeye.model.Point;
import dong.lan.mapeye.model.Record;
import dong.lan.mapeye.model.users.Contact;
import dong.lan.mapeye.model.users.Group;

/**
 * Created by 梁桂栋 on 17-3-18 ： 下午7:34.
 * Email:       760625325@qq.com
 * GitHub:      github.com/donlan
 * description: map
 */

public class BRecord extends BmobObject {

    private String id;                  //默认是创建时的时间的毫秒数
    private BUser own;                   //创建者
    private Long createTime;            //创建时间
    private String label;               //标签
    private String info;                //说明信息
    private Integer type;                   //类型
    private Integer radius;                 //如果类型为半径围栏则保存围栏的半径。单位米
    private List<BPoint> points;    //包含的所有位置点
    private List<BContact> contacts;

    public BRecord() {
    }

    public BRecord(Record record, Group group) {
        id = record.getId();
        own = BmobUser.getCurrentUser(BUser.class);
        createTime = record.getCreateTime();
        label = record.getLabel();
        info  = record.getInfo();
        type = record.getType();
        radius = record.getRadius();
        points = new ArrayList<>();
        for(Point point:record.getPoints()){
            points.add(new BPoint(point.lat,point.lng));
        }

        contacts = new ArrayList<>();
        if(group.getMembers()!=null){
            for(Contact contact:group.getMembers()){
                contacts.add(new BContact(contact));
            }
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public BUser getOwn() {
        return own;
    }

    public void setOwn(BUser own) {
        this.own = own;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
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

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getRadius() {
        return radius;
    }

    public void setRadius(Integer radius) {
        this.radius = radius;
    }

    public List<BPoint> getPoints() {
        return points;
    }

    public void setPoints(List<BPoint> points) {
        this.points = points;
    }

    public List<BContact> getContacts() {
        return contacts;
    }

    public void setContacts(List<BContact> contacts) {
        this.contacts = contacts;
    }

    @Override
    public String toString() {
        return "BRecord{" +
                "id='" + id + '\'' +
                ", own=" + own +
                ", createTime=" + createTime +
                ", label='" + label + '\'' +
                ", info='" + info + '\'' +
                ", type=" + type +
                ", radius=" + radius +
                ", points=" + points +
                ", contacts=" + contacts +
                '}';
    }
}
