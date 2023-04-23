package pers.lichen.lynkcotool;

import android.accessibilityservice.AccessibilityService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.view.accessibility.AccessibilityEvent;

public class DetectionService extends AccessibilityService {
    private static final String TAG = "DetectionService";

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        if( accessibilityEvent.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED ){
            String packageName = accessibilityEvent.getPackageName().toString();
            //监听当前窗口变化，获取Package名和Class名
            String fromVal = SharedPreferencesUtil.getString(getApplicationContext(), SharedPreferencesUtil.FROM, "");
            if (fromVal.equals(packageName)){
                //如果打开伴听，直接打开诗川随听
                String toVal = SharedPreferencesUtil.getString(getApplicationContext(), SharedPreferencesUtil.TO, "");
                Context applicationContext = getApplicationContext();
                openApp(applicationContext, toVal);
            }
        }
    }

    /**
     * 打开app
     * @param context
     * @param packageName
     */
    public void openApp(Context context, String packageName) {
        final Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        context.startActivity(intent);
    }

    /**
     * 打开具体app的某个界面
     * @param context
     * @param packageName
     * @param activity
     */
    public void openAppAndView(Context context, String packageName, String activity){
        Intent intent = new Intent();
        ComponentName cn = new ComponentName(packageName, activity);
        intent.setComponent(cn);
        startActivity(intent);
    }

    @Override
    public void onInterrupt() {
    }
}
