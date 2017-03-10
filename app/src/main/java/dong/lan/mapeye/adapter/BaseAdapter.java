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

package dong.lan.mapeye.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by 梁桂栋 on 16-11-18 ： 下午1:11.
 * Email:       760625325@qq.com
 * GitHub:      github.com/donlan
 * description: map
 */

public abstract  class BaseAdapter<T> extends RecyclerView.Adapter<BaseHolder> {

    private List<T> data;

    public BaseAdapter(List<T> data) {
        this.data = data;
    }


    public T getValue(int position){
        if(position>=getItemCount())
            return null;
        return data.get(position);
    }
    @Override
    public BaseHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return bindHolder(parent,viewType);
    }

    @Override
    public void onBindViewHolder(BaseHolder holder, int position) {
        bindData(data.get(position),holder,position);
    }

    @Override
    public int getItemCount() {
        return data==null?0:data.size();
    }

    public abstract BaseHolder bindHolder(ViewGroup parent, int type);

    public abstract void bindData(T value,BaseHolder holder,int position);
}
