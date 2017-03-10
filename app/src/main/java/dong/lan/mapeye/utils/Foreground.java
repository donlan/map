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

package dong.lan.mapeye.utils;


import com.tencent.TIMBaseApplication;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

public class Foreground implements TIMBaseApplication.ActivityLifecycleCallbacks{
	
	//单例
	private static Foreground instance = new Foreground();
	
	private static String TAG = Foreground.class.getSimpleName();
	private final int CHECK_DELAY = 500;
	
	//用于判断是否程序在前台
	private boolean foreground = false, paused = true;
	//handler用于处理切换activity时的短暂时期可能出现的判断错误
	private Handler handler = new Handler();
	private Runnable check;
	
	public static void init(Application app){
		app.registerActivityLifecycleCallbacks(instance);
    }

    public static Foreground get(){
        return instance;
    }

    private Foreground(){}
	
	@Override
	public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onActivityDestroyed(Activity activity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onActivityPaused(Activity activity) {
		paused = true;
	    if (check != null)
	        handler.removeCallbacks(check);
	    handler.postDelayed(check = new Runnable(){
	        @Override
	        public void run() {
	            if (foreground && paused) {
	                foreground = false;
	                Log.i(TAG, "went background");	                
	            } else {
	                Log.i(TAG, "still foreground");
	            }
	        }
	    }, CHECK_DELAY);
		
	}

	@Override
	public void onActivityResumed(Activity activity) {
		paused = false;	    
	    foreground = true;
	    if (check != null)
	        handler.removeCallbacks(check);	    
		
	}

	@Override
	public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onActivityStarted(Activity activity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onActivityStopped(Activity activity) {
		// TODO Auto-generated method stub
		
	}
	
	public boolean isForeground(){
	    return foreground;
	}

}
