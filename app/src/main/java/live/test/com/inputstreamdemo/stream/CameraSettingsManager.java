package live.test.com.inputstreamdemo.stream;

import android.content.Context;
import android.hardware.Camera;
import android.view.MotionEvent;
import android.view.View;

import com.qiniu.pili.droid.streaming.AVCodecType;
import com.qiniu.pili.droid.streaming.CameraStreamingSetting;
import com.qiniu.pili.droid.streaming.MediaStreamingManager;

/**
 * Created by dignzheng on 2017/11/13.
 */

public class CameraSettingsManager {

    //CAMERA_FACING_FONT为前置摄像头, CAMERA_FACING_BACK为后置
    private static final int DEFAULT_CAMERA_ID = Camera.CameraInfo.CAMERA_FACING_FRONT;

    /* FOCUS_MODE_CONTINUOUS_PICTURE 自动对焦(Picture);
   FOCUS_MODE_CONTINUOUS_VIDEO   自动对焦(Video);
   FOCUS_MODE_AUTO               手动对焦*/
    private static final String DEFAULT_FOCUS_MOD = Camera.Parameters.FOCUS_MODE_AUTO;

    /* 使用 PREVIEW_SIZE_LEVEL 和 PREVIEW_SIZE_RATIO 共同确定一个预览Size
    PREVIEW_SIZE_LEVEL和相机预览的清晰度有关系, 设置为SMALL预览的画面会很不清晰
    PREVIEW_SIZE_RATIO SDK提供的两种规格16_9 和 4_3  */
    private static final CameraStreamingSetting.PREVIEW_SIZE_LEVEL
            DEFAULT_SIZE_LEVEL = CameraStreamingSetting.PREVIEW_SIZE_LEVEL.MEDIUM;

    private static final CameraStreamingSetting.PREVIEW_SIZE_RATIO
            DEFAULT_SIZE_RATIO = CameraStreamingSetting.PREVIEW_SIZE_RATIO.RATIO_16_9;

    private CameraStreamingSetting cameraStreamingSetting;
    private static CameraSettingsManager INSTANCE = null;


    public CameraSettingsManager() {
        cameraStreamingSetting = new CameraStreamingSetting();
    }

    public CameraSettingsManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CameraSettingsManager();
        }
        return INSTANCE;
    }

    public void buildCameraConfig() {
        cameraStreamingSetting.setCameraId(DEFAULT_CAMERA_ID)
                .setCameraPrvSizeLevel(DEFAULT_SIZE_LEVEL)
                .setCameraPrvSizeRatio(DEFAULT_SIZE_RATIO)
                // 设置自动对焦
                .setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)
                .setContinuousFocusModeEnabled(true)
                //前置摄像头预览是否镜像
                .setFrontCameraPreviewMirror(true)
                /*在部分机型开启 Recording Hint 之后，会出现画面卡帧等风险，所以请慎用该API。
                如果需要实现高 fps 推流，可以考虑开启并加入白名单机制
                 */
                .setFrontCameraMirror(true).setRecordingHint(false)
                .setResetTouchFocusDelayInMs(3000)
                //--------------美颜相关设置---------
                .setBuiltInFaceBeautyEnabled(true)
                //参数说明：1，beautyLevel；2，whiten ；3，redden.
                .setFaceBeautySetting(new CameraStreamingSetting.FaceBeautySetting(1.0f, 1.0f, 0.8f))
                .setVideoFilter(CameraStreamingSetting.VIDEO_FILTER_TYPE.VIDEO_FILTER_BEAUTY);
    }

//    public void prepare(Context context, CameraPreviewFrameView view) {
//        buildCameraConfig();
//        mMediaStreamingManager = new MediaStreamingManager(context, view, AVCodecType.SW_VIDEO_WITH_SW_AUDIO_CODEC);
//        //调用Mediastreamingmanager中视频推流的方法，但是不传递其他参数，最终实现只打开摄像头而不做其他操作
//        mMediaStreamingManager.prepare(cameraStreamingSetting, null, null, null);
//    }

//    public void showCamera() {
//        mMediaStreamingManager.resume();
//    }
//
//    public void pauseCamera() {
//        mMediaStreamingManager.pause();
//    }
//
//    public void doSingleTapUp(MotionEvent e) {
//        mMediaStreamingManager.doSingleTapUp((int) e.getX(), (int) e.getY());
//    }
//
//    public void setFocusAreaIndicator(RotateLayout mRotateLayout, View indicator) {
//        mMediaStreamingManager.setFocusAreaIndicator(mRotateLayout, indicator);
//    }
//
//    public void switchCamera(CameraStreamingSetting.CAMERA_FACING_ID facingId) {
//        mMediaStreamingManager.switchCamera(facingId);
//    }

}
