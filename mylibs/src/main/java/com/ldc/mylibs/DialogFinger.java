package com.ldc.mylibs;

import android.Manifest;
import android.app.Activity;
import android.app.KeyguardManager;
import android.app.Service;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.*;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialog;
import androidx.core.content.ContextCompat;
import androidx.core.hardware.fingerprint.FingerprintManagerCompat;


public class DialogFinger extends AppCompatDialog {
    private RadioButton rbFinger;
    private Button btnCancle;
    private FingerprintManagerCompat fingerprintManagerCompat;
    private KeyguardManager keyguardManager;
    private Context context;
    private static final int notify_code = 0x000;
    private static final int finger_state_code = 0x001;
    private FingerResultCallback fingerResultCallback;


    final public void setFingerResultCallback(FingerResultCallback fingerResultCallback) {
        this.fingerResultCallback = fingerResultCallback;
    }

    //暴露接口
    public interface FingerResultCallback {
        public void update(boolean success, String message);
    }

    //消息
    private final Handler uiHandler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            if (msg.what == notify_code) {
                final String info = (String) msg.obj;
                Toast.makeText(context, info, Toast.LENGTH_SHORT).show();
                return true;
            } else if (msg.what == finger_state_code) {
                final DialogFingerBean dt = (DialogFingerBean) msg.obj;
                if (null == dt) return false;
                Toast.makeText(context, dt.message, Toast.LENGTH_SHORT).show();
                checkSuccess(dt.success);
                return true;
            }

            return false;
        }
    });
    //事件
    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (R.id.btn_cancle == v.getId()) {
                //hideDialog();
                hideDialog();
            }
        }
    };
    //监听
    private DialogFingerHandler.FingerResult fingerHandlerResult = new DialogFingerHandler.FingerResult() {
        @Override
        public void update(boolean success, String message) {
            if (null != fingerResultCallback) {
                fingerResultCallback.update(success, message);
            }
            final DialogFingerBean fingerBean = new DialogFingerBean(success, message);
            Message msg = uiHandler.obtainMessage(finger_state_code);
            msg.obj = fingerBean;
            uiHandler.sendMessage(msg);

        }
    };


    public DialogFinger(Context context) {
        super(context, R.style.finger_dialog_style);
        this.context = context;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_finger);
        rbFinger = findViewById(R.id.rb_finger);
        btnCancle = findViewById(R.id.btn_cancle);
        //
        btnCancle.setOnClickListener(clickListener);
    }


    //检测是否支持指纹
    private void checkFinger() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            fingerprintManagerCompat = FingerprintManagerCompat.from(context);
            keyguardManager = (KeyguardManager) context.getSystemService(Service.KEYGUARD_SERVICE);
            String str_info = "";
            if (!fingerprintManagerCompat.isHardwareDetected()) {
                //检测到硬件
                str_info = "未发现指纹模块";
            } else if (ContextCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
                //没有权限
                str_info = "没有指纹权限，请开启";
            } else if (!keyguardManager.isDeviceSecure()) {
                //没有设置指纹
                str_info = "请设置锁屏设置";
            } else if (!fingerprintManagerCompat.hasEnrolledFingerprints()) {
                //已经注册指纹
                str_info = "请设置一个指纹";
            } else {
                //可以使用
                DialogFingerHandler fingerHandler = new DialogFingerHandler(context);
                fingerHandler.startAuth(fingerprintManagerCompat, null);
                fingerHandler.setFingerResult(fingerHandlerResult);
            }
            if (!TextUtils.isEmpty(str_info)) {
                Message message = uiHandler.obtainMessage(notify_code);
                message.obj = str_info;
                uiHandler.sendMessage(message);
            }
        } else {
            Message message = uiHandler.obtainMessage(notify_code);
            message.obj = "不支持指纹模块";
            uiHandler.sendMessage(message);
        }
    }

    final public void showDialog() {
        if (!isShowing()) {
            checkFinger();
            show();
        }
    }

    final public void hideDialog() {
        if (isShowing()) {
            dismiss();
        }
    }

    final public void checkSuccess(boolean isSuccess) {
        if (null == rbFinger) return;
        rbFinger.setChecked(isSuccess);
        uiHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isSuccess) {
                    hideDialog();
                }
            }
        }, 1500);
    }


    public static class Builder {
        private Activity activity;
        private DialogFinger dialogFinger;

        public Builder(Activity var) {
            activity = var;
            dialogFinger = new DialogFinger(activity);
        }

        final public Builder setResultCallBack(DialogFinger.FingerResultCallback resultCallBack) {
            dialogFinger.setFingerResultCallback(resultCallBack);
            return this;
        }

        final public DialogFinger build() {
            dialogFinger.setCancelable(false);
            dialogFinger.setCanceledOnTouchOutside(false);
            return dialogFinger;
        }
    }

}
