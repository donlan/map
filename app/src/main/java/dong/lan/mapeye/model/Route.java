package dong.lan.mapeye.model;

import android.util.Log;

import java.util.Date;

import dong.lan.mapeye.BuildConfig;
import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by 梁桂栋 on 2016/7/10.
 * Email: 760625325@qq.com
 * Github: github.com/donlan
 */
public class Route extends RealmObject{
    public static final double DIS = 30;
    public String label;
    public Date time;
    public int tag;
    public RealmList<Line> lines;
    public RealmList<Point> points;
    public Route(){
        lines = new RealmList<>();
        points = new RealmList<>();
    }

    public boolean isOnRoute(double lat,double lng){
        if(lines==null || lines.size()<=0)
            return false;
        int tag = 0;
        for(int i=0,s=lines.size();i<s;i++){
            if (BuildConfig.DEBUG) Log.d("Route", "tag:" + tag);
            if(lines.get(i).isOnline(lat,lng))
                return true;
            tag++;
        }
        return false;
    }

    public void createLinesByPoints(){
        if(points.size()<1)
            return;
        for(int i=0,s=points.size()-1;i<s;i++){
            Line line = new Line();
            line.init(points.get(i),points.get(i+1));
            lines.add(line);
        }
    }

    public void copyTo(Route route){
        label = route.label;
        time = route.time;
        tag = route.tag;
        points.clear();
        points.addAll(0, route.points);
        lines.clear();
        lines.addAll(0,route.lines);
    }

}
