package com.example.greenmatting;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        findViewById(R.id.id_green_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean per=PermissionManager.checkCamera(MainActivity.this, 0) && PermissionManager.checkStorage(MainActivity.this, 0);
                if (per) {
                    startActivity(new Intent(MainActivity.this,GreenMattingLiveActivity.class));
                }else{
                    ToastUtil.toastShortShow(MainActivity.this,"无权限 (no permission)");
                }
            }
        });

    }
}