package com.startlink.camplus;

import android.app.Activity;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ActivityManager {

    private static ActivityManager activityManager;

    /**
     * task map，用于记录activity栈，方便退出程序（这里为了不影响系统回收activity，所以用软引用）
     */
    private final HashMap<String, SoftReference<Activity>> taskMap = new HashMap<String, SoftReference<Activity>>();

    public static ActivityManager getActivityManager(){
        synchronized (ActivityManager.class){
            if(activityManager == null)
                activityManager = new ActivityManager();
        }
        return activityManager;
    }

    private ActivityManager() {
    }


    /**
     * 往应用task map加入activity
     */
    public final void putActivity(Activity atv) {
        taskMap.put(atv.toString(), new SoftReference<Activity>(atv));
      //  Log.i("PutActivity", "" + atv);
    }

    /**
     * 往应用task map加入activity
     */
    public final void removeActivity(Activity atv) {
        taskMap.remove(atv.toString());
    }

    /**
     * 清除应用的task栈，如果程序正常运行这会导致应用退回到桌面
     */
    public final void exit() {
        for (Iterator<Map.Entry<String, SoftReference<Activity>>> iterator = taskMap
                .entrySet().iterator(); iterator.hasNext();) {
            SoftReference<Activity> activityReference = iterator.next()
                    .getValue();
            Activity activity = activityReference.get();
            //Log.i("ActivityList", "" + activity);
            if (activity != null) {
                activity.finish();
            }
        }
        taskMap.clear();
    }


}
