package com.lansosdk.videoeditor;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

import com.lansosdk.LanSongFilter.LanSongFilter;
import com.lansosdk.box.ILayerInterface;
import com.lansosdk.box.LSOAsset;
import com.lansosdk.box.LSOCamLayer;
import com.lansosdk.box.LSOCamRelativeLayout;
import com.lansosdk.box.LSOCameraLiveRunnable;
import com.lansosdk.box.LSOCameraRunnable;
import com.lansosdk.box.LSOCameraSizeType;
import com.lansosdk.box.LSOFrameLayout;
import com.lansosdk.box.LSOLog;
import com.lansosdk.box.OnAddPathListener;
import com.lansosdk.box.OnCameraResumeErrorListener;
import com.lansosdk.box.OnCameraSizeChangedListener;
import com.lansosdk.box.OnCreateListener;
import com.lansosdk.box.OnLanSongSDKErrorListener;
import com.lansosdk.box.OnLayerTextureOutListener;
import com.lansosdk.box.OnPlayerDataOutListener;
import com.lansosdk.box.OnRemoveCompletedListener;
import com.lansosdk.box.OnResumeListener;
import com.lansosdk.box.OnSetCompletedListener;
import com.lansosdk.box.OnTakePictureListener;
import com.lansosdk.box.OnTextureAvailableListener;

import java.io.File;


public class LSOCameraLive extends LSOFrameLayout implements ILSOTouchInterface {

    private int compWidth = 1080;

    private int compHeight = 1920;

    private LSOCameraLiveRunnable render;

    public LSOCameraLive(Context context) {
        super(context);
    }

    public LSOCameraLive(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LSOCameraLive(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public LSOCameraLive(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    //---------------------copy code start---------------------
    protected void sendOnCreateListener() {
        super.sendOnCreateListener();
        if (render != null) {

            if (fullscreen) {
                DisplayMetrics dm = new DisplayMetrics();
                dm = getResources().getDisplayMetrics();
                compWidth = dm.widthPixels;
                compHeight = dm.heightPixels;
            }

            render.setSurface(compWidth, compHeight, getSurfaceTexture(), getViewWidth(), getViewHeight());
        }
    }

    public void sendOnResumeListener() {

        super.sendOnResumeListener();
        if (render != null) {
            render.setSurface(compWidth, compHeight, getSurfaceTexture(), getViewWidth(), getViewHeight());
        }
    }

    //??????????????????
    public boolean onTextureViewTouchEvent(MotionEvent event) {
        if (isEnableTouch) {
            super.onTextureViewTouchEvent(event);
            return onTouchEvent(event);
        } else {
            return false;
        }
    }



    private OnCreateListener onCreateListener;
    private boolean fullscreen = false;

    public void onCreateFullScreen(OnCreateListener listener) {

        fullscreen = true;
        if (isTextureAvailable() && listener != null) {
            if (render == null) {
                render = new LSOCameraLiveRunnable(getContext(), getWidth(), getHeight());
            }
            listener.onCreate();
        } else {
            onCreateListener = listener;
            setOnTextureAvailableListener(new OnTextureAvailableListener() {
                @Override
                public void onTextureUpdate(int width, int height) {
                    if (render == null) {
                        render = new LSOCameraLiveRunnable(getContext(), getWidth(), getHeight());
                    }
                    onCreateListener.onCreate();
                }
            });
        }
    }




    public void onResumeAsync(OnResumeListener listener) {
        super.onResumeAsync(listener);
        if (render != null) {
            render.onActivityPaused(false);
        }
    }

    public void onPause() {
        super.onPause();
        setOnTextureAvailableListener(new OnTextureAvailableListener() {
            @Override
            public void onTextureUpdate(int width, int height) {
                if (render != null) {
                    render.setSurface(compWidth, compHeight, getSurfaceTexture(), getViewWidth(), getViewHeight());
                }
            }
        });


        if (render != null) {
            render.onActivityPaused(true);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        release();
    }

    //---------------render start-----------------------------------------
    private static boolean isCameraOpened = false;

    private boolean frontCamera = false;

    private OnFocusEventListener onFocusListener;

    /**
     * ?????????????????????,??????????????????
     * ????????????????????????;
     * @param is
     */
    public void setFrontCamera(boolean is) {
        if (!isRunning()) {
            frontCamera = is;
        } else {
            LSOLog.e("setFrontCamera error render have been setup .");
        }
    }

    /**
     * ??????????????????, ???????????????.
     */
    public void setPreviewSize(LSOCameraSizeType type) {
        if (render != null && !render.isRunning()) {
            render.setPreviewSize(type);
        }
    }

    /**
     * ????????????????????????, ???????????????
     */
    public void setCameraSize(LSOCameraSizeType type) {
        if (render != null && render.isRunning()) {
            render.setCameraSize(type);
        }
    }

    /**
     * ???????????????;
     */
    public LSOCameraSizeType getCameraSize(){
        if (render != null ) {
            return render.getCameraSize();
        }else{
            return LSOCameraSizeType.TYPE_1080P;
        }
    }



    public boolean isRunning() {
        return render != null && render.isRunning();
    }



    /**
     * ???camera??????????????????,
     * ????????????????????????????????????????????????;
     * @param listener
     */
    public void setOnCameraResumeErrorListener(OnCameraResumeErrorListener listener) {
        if (render != null) {
            render.setOnCameraResumeErrorListener(listener);
        }
    }


    /**
     * ????????????
     */
    public void setOnLanSongSDKErrorListener(OnLanSongSDKErrorListener listener) {
        if (render != null) {
            render.setOnLanSongSDKErrorListener(listener);
        }
    }


    /**
     * ????????????
     */
    public boolean start() {
        super.start();
        if (isCameraOpened) {
            LSOLog.d("LSOCamera  start error. is opened...");
            return true;
        }

        if (getSurfaceTexture() != null) {
            render.setFrontCamera(frontCamera);

            if (render != null) {
                render.setDisplaySurface(getSurfaceTexture(), getViewWidth(), getViewHeight());
                isCameraOpened = render.start();
                if (!isCameraOpened) {
                    LSOLog.e("open LSOCamera error.\n");
                } else {
                    LSOLog.d("LSOCameraLive start preview...");
                }
            }
        } else {
            LSOLog.w("mSurfaceTexture error.");
        }
        return isCameraOpened;
    }

    public void setFilter(LanSongFilter filter) {
        if (render != null) {
            render.setFilter(filter);
        }
    }


    /**
     * ??????, ?????????0.0---1.0;
     * 0.0 ????????????, 1.0:????????????;
     * @param level
     */
    public void setBeautyLevel(float level) {
        if (render != null) {
            render.setBeautyLevel(level);
        }
    }


    /**
     * ????????????;
     */
    public void setDisableBeauty() {
        if (render != null) {
            render.setBeautyLevel(0.0f);
        }
    }


    /**
     * ??????????????????
     *
     * @return
     */
    public boolean isGreenMatting() {
        return render != null && render.isGreenMatting();
    }

    /**
     * ??????????????????
     */
    public void setGreenMatting() {
        if (render != null) {
            render.setGreenMatting();
        } else {
            LSOLog.e("setGreenMatting error. render is null");
        }
    }

    /**
     * ??????????????????
     */
    public void cancelGreenMatting() {
        if (render != null) {
            render.cancelGreenMatting();
        }
    }


    private String bgPath = null;

    /**
     * ??????????????????;
     */
    public String getBackGroundPath() {
        return bgPath;
    }


    /**
     * ??????????????????;
     */
    public boolean setBackGroundBitmapPath(String path) {
        if (render != null && isRunning() && path != null) {
            try {
                bgPath = path;
                return render.setBackGroundBitmapPath(path);
            } catch (Exception e) {
                e.printStackTrace();
                LSOLog.e("setBackGroundPath error, input is:" + path);
                bgPath = null;
            }
        }
        return false;
    }


    /**
     * ????????????, ??????????????????????????????, ???????????????????????????
     * @param path
     * @param audioVolume ??????, ???????????????, ????????????
     * @param listener ?????? ???????????????;
     */
    public void setBackGroundPath(String path, float audioVolume, OnSetCompletedListener listener) {

        if(!fileExist(path)){
            listener.onSuccess(false);
            return;
        }

        if (render != null && isRunning()) {
            try {
                String suffix=getFileSuffix(path);
                if(isBitmapSuffix(suffix)){
                    bgPath = path;
                    setBackGroundBitmapPath(path);
                    listener.onSuccess(true);
                }else if(isVideoSuffix(suffix)){
                    bgPath = path;
                    render.setBackGroundVideoPath(path, audioVolume,listener);
                }
            } catch (Exception e) {
                e.printStackTrace();
                LSOLog.e("setBackGroundPath error, input is:" + path);
                listener.onSuccess(false);
            }
        }else{
            listener.onSuccess(false);
        }
    }


    /**
     * ????????????????????????;
     * @param width  ???????????????
     * @param height ???????????????
     * @return ????????????????????????;
     */
    public LSOCamLayer addSurfaceLayer(int width, int height) {
        if (render != null && render.isRunning()) {
            return render.addSurfaceLayer(width, height);
        } else {
            return null;
        }
    }


    /**
     *  ????????????yuv???????????????;
     * @param yuvWidth yuv???????????????;
     * @param yuvHeight yuv???????????????
     * @param yuvAngle yuv???????????????;
     * @param isNv12 ?????????????????????nv12; ?????????nv21?????????false;
     * @return
     */
    public LSOCamLayer addNv21Layer(int yuvWidth, int yuvHeight, int yuvAngle, boolean isNv12) {
        if (render != null && render.isRunning()) {
            return render.addNv21Layer(yuvWidth,yuvHeight,yuvAngle, isNv12);
        } else {
            return null;
        }
    }

    /**
     * ????????????????????????
     * @return
     */
    public LSOCamLayer addBitmapLayer(String path) {
        if (render != null && render.isRunning()) {
            return render.addBitmapLayer(path);
        } else {
            return null;
        }
    }


    /**
     *
     * @param bmp
     * @return
     */
    public LSOCamLayer addBitmapLayer(Bitmap bmp) {
        if (render != null && render.isRunning()) {
            return render.addBitmapLayer(bmp);
        } else {
            return null;
        }
    }



    /**
     *  ????????????????????????????????????;
     *  ??????: ?????????????????????????????????1080P, ??????2??????, ?????????????????????5???;
     * @param path ??????????????????????????????,
     * @param cache ??????????????????, ?????????????????????1080P,?????????????????????;???????????????????????????, ??????false;
     * @param listener ???????????????;
     */
    public void addGreenFileAsync(String path,boolean cache, OnAddPathListener listener){
        if (render != null && render.isRunning()) {
            render.addGreenFileAsync(path,cache, listener);
        }
    }


    /**
     * @param path gif??????;
     * @return
     */
    public LSOCamLayer addGifPath(String path){
        if (render != null && render.isRunning()) {
            return render.addGifPath(path);
        }else{
            LSOLog.e("addGifPath error. ");
            return null;
        }
    }

    /**
     * ??????????????????
     * @param layer
     * @param listener
     */
    public void removeLayer(LSOCamLayer layer, OnRemoveCompletedListener listener) {
        if (render != null && render.isRunning()) {
            render.removeLayer(layer, listener);
        }
    }


    /**
     * ??????????????????.
     * ?????????setBackGroundPath???????????????????????????, ??????????????????;
     * ??????????????????30???????????????
     * ???????????????;
     * @return
     */
    public LSOCamLayer getBackGroundLayer() {
        if (render != null) {
            return render.getBackGroundLayer();
        }
        return null;
    }


    /**
     * ????????????view?????????
     * @param layout ??????????????????, ????????????enable, ????????????????????????;
     * @param enable ????????????
     */
    public void setRelativeLayout(LSOCamRelativeLayout layout, boolean enable) {
        if(render!=null){
            render.setRelativeLayout(layout,enable);
        }
    }



    /**
     * ???????????????;
     */
    public void removeBackGroundLayer() {
        if (render != null) {
            bgPath = null;
            render.removeBackGroundLayer();
        }
    }


    /**
     * ?????????????????????????????????, ????????????????????????????????????;
     * @param listener
     */
    public void setOnCameraLayerTextureOutListener(OnLayerTextureOutListener listener){
        if(render!=null){
            render.setOnCameraLayerTextureOutListener(listener);
        }
    }


    /**
     *
     * ??????????????????.
     * ????????????????????????1080P, ???????????????????????????;
     * @param width ???????????????????????????,??????????????????16?????????
     * @param height ????????????????????????,??????????????????16?????????
     * @param listener ??????????????????, ??????????????????opengles?????????.
     */
    public void  setOnPlayerDataOutListener(int width, int height, OnPlayerDataOutListener listener){
        if(render!=null){
            render.setOnPlayerDataOutListener(width,height,listener);
        }
    }

    /**
     * camera????????????????????????; listener???????????????????????????;
     * @param listener
     */
    public void setOnCameraSizeChangedListener(OnCameraSizeChangedListener listener){
        if(render!=null){
            render.setOnCameraSizeChangedListener(listener);
        }
    }

    /**
     * ??????????????????; ????????????????????????, ?????????????????????;
     * @return
     */
    public LSOCamLayer getCameraLayer() {
        if (render != null) {
            return render.getCameraLayer();
        } else {
            return null;
        }
    }


    /**
     * ??????????????????????????????;
     * @return
     */
    public MediaPlayer getMediaPlayer() {
        if (render != null) {
            return render.getMediaPlayer();
        } else {
            return null;
        }
    }


    private String fgBitmapPath = null;
    private String fgColorPath = null;

    /**
     * ??????????????????;
     *
     * @param path ????????????
     */
    public void setForeGroundBitmap(String path) {

        if (fgBitmapPath != null && fgBitmapPath.equals(path)) {
            return;
        }

        if (render != null && isRunning()) {
            try {
                fgBitmapPath = path;
                fgColorPath = null;
                LSOLog.d("Camera setForeGroundBitmap...");
                render.setForeGroundBitmap(new LSOAsset(path));
            } catch (Exception e) {
                e.printStackTrace();
                fgBitmapPath = null;
            }
        }
    }

    /**
     * ????????????????????????,
     *
     * @param colorPath mv color path
     * @param maskPath  mv mask path
     */
    public void setForeGroundVideoPath(String colorPath, String maskPath) {

        if (fgColorPath != null && fgColorPath.equals(colorPath)) {
            return;
        }

        if (render != null && isRunning()) {
            fgBitmapPath = null;
            fgColorPath = colorPath;
            render.setForeGroundVideoPath(colorPath, maskPath);
        } else {
            LSOLog.e("add MVLayer error!");
        }
    }

    /**
     * ??????????????????
     */
    public void removeForeGroundLayer() {
        fgBitmapPath = null;
        fgColorPath = null;

        if (render != null) {
            render.removeForeGroundLayer();
        }
    }

    /**
     * ??????
     *
     * @param listener
     */
    public void takePictureAsync(OnTakePictureListener listener) {
        if (render != null && render.isRunning()) {
            render.takePictureAsync(listener);
        } else if (listener != null) {
            listener.onTakePicture(null);
        }
    }

    /**
     * ???????????????.
     * change front or back camera;
     */
    public void changeCamera() {
        if (render != null && LSOCameraRunnable.isSupportFrontCamera()) {
            frontCamera = !frontCamera;
            render.changeCamera();
        }
    }


    /**
     * ????????????????????????
     *
     * @return
     */
    public boolean isFrontCamera() {
        return frontCamera;
    }

    /**
     * ????????????????????????; ??????????????????;
     */
    public void changeFlash() {
        if (render != null) {
            render.changeFlash();
        }
    }

    /**
     * ??????????????????????????????????????????;
     */
    public void setAllLayerTouchEnable(boolean is) {
        if (render != null) {
            render.setAllLayerTouchEnable(is);
        }
    }


    private static String getFileSuffix(String path) {
        if (path == null)
            return "";
        int index = path.lastIndexOf('.');
        if (index > -1)
            return path.substring(index + 1);
        else
            return "";
    }


    private boolean isBitmapSuffix(String suffix) {

        return "jpg".equalsIgnoreCase(suffix)
                || "JPEG".equalsIgnoreCase(suffix)
                || "png".equalsIgnoreCase(suffix)
                || "heic".equalsIgnoreCase(suffix);
    }

    private boolean isVideoSuffix(String suffix) {
        return "mp4".equalsIgnoreCase(suffix)
                || "mov".equalsIgnoreCase(suffix);
    }

    private float spacing(MotionEvent event) {
        if (event == null) {
            return 0;
        }
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }


    private boolean isEnableTouch = true;

    public void setTouchEnable(boolean enable) {
        isEnableTouch = enable;
    }


    public void setCameraFocusListener(OnFocusEventListener listener) {
        this.onFocusListener = listener;
    }

    @Override
    public ILayerInterface getTouchPointLayer(float x, float y) {
        if (render != null) {
            return render.getTouchPointLayer(x, y);
        } else {
            return null;
        }
    }


    public interface OnFocusEventListener {
        void onFocus(int x, int y);
    }

    float x1 = 0;
    float x2 = 0;
    float y1 = 0;
    float y2 = 0;
    private long downTimeMs;
    private boolean isClickEvent = false;
    private boolean isSlideEvent = false;
    private boolean isZoomEvent = false;
    private float touching;
    private boolean disableZoom = false;

    public void setDisableZoom(boolean is) {
        disableZoom = is;
    }

    public boolean onTouchEvent(MotionEvent event) {
        int touchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        if (render == null || !isEnableTouch) { // ???????????????touch??????,???????????????false;
            return false;
        }
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            // ??????????????????
            case MotionEvent.ACTION_DOWN:
                isZoomEvent = false;
                isClickEvent = true;
                isSlideEvent = true;
                x1 = event.getX();
                y1 = event.getY();
                downTimeMs = System.currentTimeMillis();

                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                // ??????????????????????????????
                if (isRunning()) {
                    touching = spacing(event);
                    isZoomEvent = true;
                    isClickEvent = false;
                    isSlideEvent = false;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (isRunning()) {
                    if (isZoomEvent) {
//                        if (event.getPointerCount() >= 2 && !disableZoom) {// ???????????????????????????
//                            float endDis = spacing(event);// ????????????
//                            int scale = (int) ((endDis - touching) / 10f); // ?????????10f
//                            // zoom???1, ????????????;
//                            if (scale != 0) {
//                                int zoom = render.getZoom() + scale;
//                                render.setZoom(zoom);
//                                touching = endDis;
//                            }
//                        }
                    }
                    if (isClickEvent && (Math.abs(x1 - event.getX()) > touchSlop ||
                            Math.abs(y1 - event.getY()) > touchSlop)) {
                        isClickEvent = false;
                        isSlideEvent = true;
                    }
                }
                break;
            // ??????????????????
            case MotionEvent.ACTION_UP:
                if (isRunning()) {
                    if (isClickEvent && System.currentTimeMillis() - downTimeMs < 200) {
                        float x = event.getX();
                        float y = event.getY();
                        render.doFocus((int) x, (int) y);

                        if (onFocusListener != null) {
                            onFocusListener.onFocus((int) x, (int) y);
                        }

                        isClickEvent = false;
                    }

                    if (!isZoomEvent && !isClickEvent && isSlideEvent) {
                        float offsetX = x1 - event.getX();
                        float offsetY = y1 - event.getY();
                        if (Math.abs(offsetX) < touchSlop && Math.abs(offsetY) < touchSlop) {
                            break;
                        }

                        if (Math.abs(Math.abs(offsetX) - Math.abs(offsetY)) < touchSlop) {
                            break;
                        }

                        if (Math.abs(offsetX) > Math.abs(offsetY)) {
                            if (offsetX > 0) {
                                if (onSlideListener != null) {
                                    onSlideListener.onHorizontalSlide(true);
                                }
                            } else {
                                if (onSlideListener != null) {
                                    onSlideListener.onHorizontalSlide(false);
                                }
                            }

                        } else {
                            if (offsetY > 0) {
                                if (onSlideListener != null) {
                                    onSlideListener.onVerticalSlide(true);
                                }
                            } else {
                                if (onSlideListener != null) {
                                    onSlideListener.onVerticalSlide(false);
                                }
                            }
                        }
                    }

                }
                isZoomEvent = false;
                break;
            case MotionEvent.ACTION_CANCEL:
                isZoomEvent = false;
                isClickEvent = false;
                break;
            default:
                break;
        }
        return true;
    }

    private void setup() {
        if (render == null) {
            DisplayMetrics dm = getResources().getDisplayMetrics();
            if (dm.widthPixels * dm.heightPixels < 1080 * 1920) {
                compWidth = 720;
                compHeight = 1280;
            }
            render = new LSOCameraLiveRunnable(getContext(), compWidth, compHeight);
        }
    }


    public void release() {
        isCameraOpened = false;
        bgPath = null;
        fgBitmapPath = null;
        fgColorPath = null;
        fullscreen = false;
        if (render != null) {
            render.release();
            render = null;
        }
    }

    OnSlideListener onSlideListener;

    public void setOnSlideListener(OnSlideListener onSlideListener) {
        this.onSlideListener = onSlideListener;
    }

    public static boolean fileExist(String absolutePath) {
        if (absolutePath == null)
            return false;
        else {
            File file = new File(absolutePath);
            if (file.exists()){
                return true;
            }
        }
        return false;
    }

    public interface OnSlideListener {

        void onHorizontalSlide(boolean slideLeft);

        void onVerticalSlide(boolean slideUp);
    }
}
