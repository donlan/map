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

package dong.lan.mapeye.bmob;

import android.util.Log;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import dong.lan.mapeye.bmob.bean.BRecord;
import dong.lan.mapeye.model.Record;
import dong.lan.mapeye.model.users.Group;

/**
 * Created by 梁桂栋 on 17-3-18 ： 下午8:20.
 * Email:       760625325@qq.com
 * GitHub:      github.com/donlan
 * description: map
 */

public class Action {


    private static final String TAG = Action.class.getSimpleName();

    public  static  void saveRecord(Record record, Group group){
        new BRecord(record,group).save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                Log.d(TAG, "done: "+s+","+e);
            }
        });
    }


}
