package com.bapm.bzys.newBzys_store.util;

import java.util.Stack;

import android.app.Activity;
import android.util.Log;

public class ActivityManager {

	private static ActivityManager instance;
	private Stack<Activity> activityStack;// activity栈
	private ActivityManager() {}
	// 单例模式
	public static ActivityManager getInstance() {
		if (instance == null) {
			instance = new ActivityManager();
		}
		return instance;
	}
	// 把一个activity压入栈中
	public void pushOneActivity(Activity actvity) {
		if (activityStack == null) {
			activityStack = new Stack<Activity>();
		}
		activityStack.add(actvity);
		Log.d("MyActivityManager ", "size = " + activityStack.size());
	}

	// 获取栈顶的activity，先进后出原则
	public Activity getLastActivity() {
		return activityStack.lastElement();
	}

	// 移除一个activity
	public void popOneActivity(Activity activity) {
		if (activityStack != null && activityStack.size() > 0) {
			if (activity != null) {
				activity.finish();
				activityStack.remove(activity);
				activity = null;
			}
		}
	}
	// 移除一个activity
	public void popOneActivity(Class cls) {
		Activity activity_remore = null;
		if (cls == null) {
			return;
		}
		if (activityStack != null && activityStack.size() > 0) {
			for (Activity activity : activityStack) {
				if (activity.getClass().equals(cls)) {
					activity.finish();
					activity_remore = activity;
				}
				
			}
			popOneActivity(activity_remore);
		}
	}
	 /** 
     * 退出栈中其他所有Activity 
     *  
     * @param cls Class 类名 
     */  
    @SuppressWarnings("rawtypes")  
    public void popOtherActivity(Class cls)  
    {  
        if(null == cls)  
        {  
            return;  
        }         
        for(Activity activity : activityStack)  
        {  
            if (null == activity || activity.getClass().equals(cls))  
            {  
                continue;  
            }               
            activity.finish();  
        }  
    }
    public boolean isexist(Class cls){
    	for (Activity activity : activityStack) {
			if (null == activity || activity.getClass().equals(cls)) {
				return true;
			}
		}
    	return false;
    }

	// 退出所有activity
	public void finishAllActivity() {
		if (activityStack != null) {
			while (activityStack.size() > 0) {
				Activity activity = getLastActivity();
				if (activity == null)
					break;
				popOneActivity(activity);
			}
		}
	}
}