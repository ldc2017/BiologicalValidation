package com.ldc.mylibs;

import android.content.Context;
import androidx.core.hardware.fingerprint.FingerprintManagerCompat;
import androidx.core.os.CancellationSignal;

public class DialogFingerHandler extends FingerprintManagerCompat.AuthenticationCallback {
    private Context context;
    private FingerResult fingerResult;

    public DialogFingerHandler(Context context) {
        this.context = context;
    }


    final public void setFingerResult(FingerResult fingerResult) {
        this.fingerResult = fingerResult;
    }

    final public void startAuth(FingerprintManagerCompat fingerprintManagerCompat, FingerprintManagerCompat.CryptoObject cryptoObject) {
        final CancellationSignal cancellationSignal = new CancellationSignal();
        fingerprintManagerCompat.authenticate(cryptoObject, 0, cancellationSignal, this, null);
    }

    @Override
    public void onAuthenticationError(int errMsgId, CharSequence errString) {
        //super.onAuthenticationError(errMsgId, errString);
        if (null != fingerResult) {
            fingerResult.update(false, String.format("%s", errString));
        }
    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
        //super.onAuthenticationSucceeded(result);
        if (null != fingerResult) {
            fingerResult.update(true, "认证成功");
        }
    }

    @Override
    public void onAuthenticationFailed() {
        //super.onAuthenticationFailed();
        if (null != fingerResult) {
            fingerResult.update(false, "认证失败");
        }
    }

    @Override
    public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
        //super.onAuthenticationHelp(helpMsgId, helpString);
        if (null != fingerResult) {
            fingerResult.update(false, String.format("%s", helpString));
        }
    }

    public interface FingerResult {
        public void update(boolean success, String message);
    }
}
