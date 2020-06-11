package com.ldc.fingermodel;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.ldc.mylibs.DialogFinger;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    //指纹
    final public void FingerCheck(View view) {
        new DialogFinger.Builder(this)
                .setResultCallBack(new DialogFinger.FingerResultCallback() {
                    @Override
                    public void update(boolean success, String message) {
                        if (success) {
                            Toast.makeText(MainActivity.this, "true", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .build().showDialog();
    }
}
