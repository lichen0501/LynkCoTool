package pers.lichen.lynkcotool;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    private static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();

        //创建无障碍服务
        createAccessibilityService();
        //检查无障碍服务
        check();
        //显示配置
        showConfig();
    }

    @Override
    public void onStart() {
        super.onStart();
        //检查无障碍服务
        check();
    }

    public void check(){
        //获取无障碍状态
        TextView msgView = (TextView)findViewById(R.id.textViewMsg);
        if (!isAccessibilitySettingsOn(context)) {
            msgView.setText("无障碍未开启，点击跳转");
            msgView.setTextColor(Color.parseColor("#F10303"));
        }else {
            msgView.setText("无障碍已开启");
            msgView.setTextColor(Color.parseColor("#FF03F107"));
        }
    }

    /**
     * 跳转无障碍
     * @param view
     */
    public void jump(View view){
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    /**
     * 显示配置
     */
    private void showConfig() {
        String fromVal = SharedPreferencesUtil.getString(context, SharedPreferencesUtil.FROM, "");
        String toVal = SharedPreferencesUtil.getString(context, SharedPreferencesUtil.TO, "");

        EditText from = (EditText)findViewById(R.id.editTextTextFrom);
        from.setText(fromVal);
        EditText to = (EditText)findViewById(R.id.editTextTextTo);
        to.setText(toVal);
    }

    /**
     * 保存配置
     * @param view
     */
    public void saveConfig(View view){
        EditText from = (EditText)findViewById(R.id.editTextTextFrom);
        EditText to = (EditText)findViewById(R.id.editTextTextTo);
        SharedPreferencesUtil.putString(context, SharedPreferencesUtil.FROM, from.getText().toString());
        SharedPreferencesUtil.putString(context, SharedPreferencesUtil.TO, to.getText().toString());
        Toast.makeText(getApplication(), "保存成功", Toast.LENGTH_SHORT).show();
    }

    /**
     * 创建无障碍服务
     */
    private void createAccessibilityService(){
        Intent intentDetection = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        intentDetection.setClass(this,DetectionService.class);
        this.startService(intentDetection);
    }


    /**
     * 检查无障碍是否打开
     * @param mContext
     * @return
     */
    private boolean isAccessibilitySettingsOn(Context mContext) {
        int accessibilityEnabled = 0;
        final String service = getPackageName() + "/" + DetectionService.class.getCanonicalName();
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                    mContext.getApplicationContext().getContentResolver(),
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
            Log.v(TAG, "accessibilityEnabled = " + accessibilityEnabled);
        } catch (Settings.SettingNotFoundException e) {
            Log.e(TAG, "Error finding setting, default accessibility to not found: "
                    + e.getMessage());
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
            Log.v(TAG, "***ACCESSIBILITY IS ENABLED*** -----------------");
            String settingValue = Settings.Secure.getString(
                    mContext.getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue);
                while (mStringColonSplitter.hasNext()) {
                    String accessibilityService = mStringColonSplitter.next();

                    Log.v(TAG, "-------------- > accessibilityService :: " + accessibilityService + " " + service);
                    if (accessibilityService.equalsIgnoreCase(service)) {
                        Log.v(TAG, "We've found the correct setting - accessibility is switched on!");
                        return true;
                    }
                }
            }
        } else {
            Log.v(TAG, "***ACCESSIBILITY IS DISABLED***");
        }
        return false;
    }
}