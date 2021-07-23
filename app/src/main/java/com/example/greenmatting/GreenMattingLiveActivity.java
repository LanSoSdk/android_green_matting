package com.example.greenmatting;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.PowerManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.lansosdk.box.ILayerInterface;
import com.lansosdk.box.LSOCamLayer;
import com.lansosdk.box.LSOCameraSizeType;
import com.lansosdk.box.OnCreateListener;
import com.lansosdk.box.OnLanSongSDKErrorListener;
import com.lansosdk.box.OnRemoveCompletedListener;
import com.lansosdk.box.OnResumeListener;
import com.lansosdk.videoeditor.LSOCameraLive;
import com.lansosdk.videoeditor.LSOLayerTouchView;
import com.lansosdk.videoeditor.oldVersion.LanSongUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class GreenMattingLiveActivity extends BaseActivity {


    ImageView closeImage;

    LSOCameraLive lsoCamera;
    SeekBar colorHold;
    SeekBar strong;


    private PowerManager.WakeLock wakeLock;

    public GreenMattingLiveActivity() {
        super(R.layout.green_matting_camera_live_layout);
    }

    public static void launchActivity(Context context) {
        Intent intent = new Intent(context, GreenMattingLiveActivity.class);
        if (!(context instanceof Activity)) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    @Override
    protected boolean customImmersive() {
        if (NotchAdapter.isHasNotch()) {
            NotchAdapter.noNavBarImmersive(getWindow());
        } else {
            NotchAdapter.immersive(getWindow());
        }
        return true;
    }

    private void findViews() {

        closeImage = findViewById(R.id.shoot_video_close);
        closeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GreenMattingLiveActivity.this.finish();
            }
        });

        lsoCamera = findViewById(R.id.shoot_video_camera);
    }

    @Override
    public void initView() {
        findViews();
        if (NotchAdapter.isHasNotch()) {
            NotchAdapter.viewMoveToSafeInset2(closeImage);
        }

        strong=findViewById(R.id.id_green_strong_seek_bar);
        strong.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(lsoCamera!=null && lsoCamera.getCameraLayer()!=null){
                    lsoCamera.getCameraLayer().setGreenMattingLevel(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        colorHold=findViewById(R.id.id_green_strong_color_hold);
        colorHold.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                if(lsoCamera!=null && lsoCamera.getCameraLayer()!=null){
                    lsoCamera.getCameraLayer().setGreenMattingColorHold(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        findViewById(R.id.id_green_button_switch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(lsoCamera!=null){
                    if (lsoCamera.isGreenMatting()){
                        lsoCamera.cancelGreenMatting();
                        findViewById(R.id.id_green_button_switch).setBackgroundResource(R.drawable.icon_shoot_no_matting);
                    }else{
                        lsoCamera.setGreenMatting();
                        findViewById(R.id.id_green_button_switch).setBackgroundResource(R.drawable.icon_shoot_matting);
                    }
                }
            }
        });
        initCamera();
    }

    private void initCamera() {
        LanSongUtil.hideBottomUIMenu(this);

        if (!LanSongUtil.checkCameraPermission(getBaseContext())) {
            Toast.makeText(getApplicationContext(), "无权限 no_permissions", Toast.LENGTH_LONG).show();
            finish();
        }

        lsoCamera.onCreateFullScreen(new OnCreateListener() {
            @Override
            public void onCreate() {
                startCamera();
            }
        });

        lsoCamera.setFrontCamera(false);

    }


    private void startCamera() {

        if (lsoCamera.isRunning()) {
            return;
        }

        lsoCamera.setPreviewSize(LSOCameraSizeType.TYPE_1080P);



        if (lsoCamera.start()) {
            lsoCamera.setGreenMatting();
            String path=copyBackGround();
            lsoCamera.setBackGroundBitmapPath(path);


            strong.setProgress(60);
            lsoCamera.getCameraLayer().setGreenMattingLevel(60);

            colorHold.setProgress(100);
            lsoCamera.getCameraLayer().setGreenMattingColorHold(100);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (wakeLock == null) {
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            wakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "CameraMatting");
            wakeLock.acquire();
        }

        if (NotchAdapter.isHasNotch()) {
            // 先取消掉沉浸式
            NotchAdapter.notchNoImmersive(getWindow(), Color.TRANSPARENT);
            // 设置伪完全沉浸式
            NotchAdapter.noNavBarImmersive(getWindow());
            // 最后适配
            NotchAdapter.immersive(getWindow());
        }
        if (lsoCamera != null) {
            lsoCamera.onResumeAsync(new OnResumeListener() {
                @Override
                public void onResume() {
                    startCamera();
                }
            });
        }


    }


    @Override
    protected void onPause() {
        super.onPause();
        lsoCamera.onPause();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();


        lsoCamera.onDestroy();
    }
    private String  copyBackGround(){
        String targetDir = getFilesDir().getAbsolutePath();
        String model="bg_default22.png";
        String modelPath = targetDir + "/" + model;

        copyAsset(getAssets(), model, modelPath);
        return modelPath;
    }

    public static boolean copyAsset(AssetManager assetManager,
                                    String fromAssetPath, String toPath) {
        InputStream in = null;
        OutputStream out = null;
        try {
            in = assetManager.open(fromAssetPath);

            new File(toPath).createNewFile();
            out = new FileOutputStream(toPath);
            copyFile(in, out);
            in.close();
            in = null;
            out.flush();
            out.close();
            out = null;
            return true;
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public static void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }

}
