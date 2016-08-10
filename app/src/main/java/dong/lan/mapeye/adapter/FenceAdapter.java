package dong.lan.mapeye.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Locale;

import dong.lan.mapeye.R;
import dong.lan.mapeye.model.Fence;
import dong.lan.mapeye.model.Route;
import io.realm.RealmResults;

/**
 * 项目：  MapEye
 * 作者：  梁桂栋
 * 日期：  7/3/2016  21:02.
 * Email: 760625325@qq.com
 */
public class FenceAdapter extends RecyclerView.Adapter<FenceAdapter.Holder> {


    private final SimpleDateFormat simpleDateFormat;
    private RealmResults<Fence> fences;
    private RealmResults<Route> routes;
    private Context context;
    private StringBuilder sb;
    private OnFenceItemClickListener fenceItemClickListener;


    public void setFenceItemClickListener(OnFenceItemClickListener listener){
        this.fenceItemClickListener = listener;
    }

    public FenceAdapter(Context context, RealmResults<Fence> fences,RealmResults<Route> routes) {
        this.context = context;
        this.fences = fences;
        this.routes = routes;
        sb = new StringBuilder();
        simpleDateFormat = new SimpleDateFormat("yyyy年 MM月 dd日 HH:mm:ss", Locale.CHINA);
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        final Holder holder = new Holder(LayoutInflater.from(context).inflate(R.layout.item_fence,parent, false));
        if(fenceItemClickListener!=null){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = holder.getLayoutPosition();
                    if(pos<fences.size())
                        fenceItemClickListener.onFenceItemClick(fences.get(holder.getLayoutPosition()));
                    else
                        fenceItemClickListener.onRouteItemClick(routes.get(pos - fences.size()));
                }
            });
        }
        return holder;

    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        if(position<fences.size()) {
            Fence fence = fences.get(position);
            holder.label.setText(fence.getLabel());
            sb.delete(0, sb.length());
            sb.append("创建时间：");
            sb.append(simpleDateFormat.format(fence.getCreateTime()));
            sb.append("\n");
            sb.append("围栏顶点数：");
            sb.append(fence.getPoints().size());
            holder.info.setText(sb.toString());
            holder.type.setText("围栏");
            holder.type.setBackgroundResource(R.drawable.circle_rect_red);
        }else{
            Route fence = routes.get(position - fences.size());
            holder.label.setText(fence.label);
            sb.delete(0, sb.length());
            sb.append("创建时间：");
            sb.append(simpleDateFormat.format(fence.time));
            sb.append("\n");
            sb.append("路径顶点数：");
            sb.append(fence.points.size());
            holder.info.setText(sb.toString());
            holder.type.setText("路径");
            holder.type.setBackgroundResource(R.drawable.circle_rect_green);
        }
    }

    @Override
    public int getItemCount() {
        return fences.size() + routes.size();
    }

    static class Holder extends RecyclerView.ViewHolder {
        public TextView label;
        public TextView info;
        public TextView type;

        public Holder(View itemView) {
            super(itemView);
            type = (TextView) itemView.findViewById(R.id.item_fence_type);
            label = (TextView) itemView.findViewById(R.id.item_fence_label);
            info = (TextView) itemView.findViewById(R.id.item_fence_info);
        }
    }
    public interface OnFenceItemClickListener{
        void onFenceItemClick(Fence fence);
        void onRouteItemClick(Route route);
    }
}



