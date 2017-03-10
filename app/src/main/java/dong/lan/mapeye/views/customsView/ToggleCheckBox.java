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

package dong.lan.mapeye.views.customsView;

import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.CheckBox;

/**
 * Created by 梁桂栋 on 16-12-21 ： 下午6:28.
 * Email:       760625325@qq.com
 * GitHub:      github.com/donlan
 * description: map
 */

public class ToggleCheckBox extends CheckBox {

    public ToggleCheckBox(Context context) {
        this(context, null);
    }

    public ToggleCheckBox(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ToggleCheckBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (isChecked()) {
            this.animate().rotation(90).setDuration(200).start();
        } else {
            this.animate().rotation(0).setDuration(200).start();
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isChecked()) {
            this.animate().rotation(0).setDuration(200).start();
        } else {
            this.animate().rotation(90).setDuration(200).start();
        }
        return super.onTouchEvent(event);
    }
}
