package live.test.com.inputstreamdemo;

import android.animation.ValueAnimator;
import android.hardware.Camera;
import android.net.NetworkInfo;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.qiniu.pili.droid.streaming.AVCodecType;
import com.qiniu.pili.droid.streaming.AudioSourceCallback;
import com.qiniu.pili.droid.streaming.StreamStatusCallback;
import com.qiniu.pili.droid.streaming.StreamingEnv;
import com.qiniu.pili.droid.streaming.StreamingManager;
import com.qiniu.pili.droid.streaming.StreamingProfile;
import com.qiniu.pili.droid.streaming.StreamingSessionListener;
import com.qiniu.pili.droid.streaming.StreamingState;
import com.qiniu.pili.droid.streaming.StreamingStateChangedListener;
import com.qiniu.pili.droid.streaming.av.common.PLFourCC;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.List;

import live.test.com.inputstreamdemo.core.ExtAudioCapture;
import live.test.com.inputstreamdemo.core.ExtVideoCapture;

public class MainActivity extends AppCompatActivity implements
        StreamingSessionListener,
        StreamStatusCallback,
        StreamingStateChangedListener,
        AudioSourceCallback {
    private ExtAudioCapture mExtAudioCapture;
    private ExtVideoCapture mExtVideoCapture;
    private TextureView textureView;
    private FrameLayout mParentLayout;
    private StreamingManager mStreamingManager;
    protected StreamingProfile mProfile = new StreamingProfile();
    private boolean isLarge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏
        StreamingEnv.init(getApplicationContext());
        setContentView(R.layout.activity_main);
        textureView = (TextureView) findViewById(R.id.camerapreview_textureview);
        mParentLayout = (FrameLayout) findViewById(R.id.mParentLayout);
//初始化传输格式文件
        initEncodingProfile();
        //初始化引擎
        initStreamingManager();

        mExtVideoCapture = new ExtVideoCapture(textureView);
        mExtVideoCapture.setOnPreviewFrameCallback(mOnPreviewFrameCallback);


        mExtAudioCapture = new ExtAudioCapture();
    }

    public void switchCamera(View view) {
        mExtVideoCapture.switchCamera(textureView.getSurfaceTexture());
    }

    public void enLarge(View view) {
        if (isLarge) {//放缩小
            isLarge = false;
            ValueAnimator mAnimator = ValueAnimator.ofFloat(0, 1f);
            mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float animatorValue = (float) animation.getAnimatedValue();
                    //camera要缩小
                    ViewGroup.LayoutParams cameraLayoutParams = new FrameLayout.LayoutParams(480, 360, Gravity.LEFT | Gravity.BOTTOM);
                    cameraLayoutParams.height = (int) (mParentLayout.getHeight() - animatorValue * (mParentLayout.getHeight() - 360));
                    cameraLayoutParams.width = (int) (mParentLayout.getWidth() - animatorValue * (mParentLayout.getWidth() - 480));
                    textureView.setLayoutParams(cameraLayoutParams);
                }
            });
            mAnimator.setDuration(500);
            mAnimator.start();
        } else {//放大
            isLarge = true;
            ValueAnimator mAnimator = ValueAnimator.ofFloat(0, 1f);
            mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float animatorValue = (float) animation.getAnimatedValue();
                    //camera要放大
                    ViewGroup.LayoutParams pagerLayoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, Gravity.LEFT | Gravity.BOTTOM);
                    pagerLayoutParams.height = (int) (360 + animatorValue * (mParentLayout.getHeight() - 360));
                    pagerLayoutParams.width = (int) (480 + animatorValue * (mParentLayout.getWidth() - 480));
                    textureView.setLayoutParams(pagerLayoutParams);
                }
            });
            mAnimator.setDuration(500);
            mAnimator.start();
        }
    }

    private void initEncodingProfile() {
        StreamingProfile.AudioProfile aProfile = null;
        StreamingProfile.VideoProfile vProfile = null;

//        if (!mEncodingConfig.mIsAudioOnly) {
//            // video quality
//            if (mEncodingConfig.mIsVideoQualityPreset) {
//                mProfile.setVideoQuality(mEncodingConfig.mVideoQualityPreset);
//            } else {
//                vProfile = new StreamingProfile.VideoProfile(
//                        mEncodingConfig.mVideoQualityCustomFPS,
//                        mEncodingConfig.mVideoQualityCustomBitrate * 1024,
//                        mEncodingConfig.mVideoQualityCustomMaxKeyFrameInterval
//                );
//            }
//
//            // video size
//            if (mEncodingConfig.mIsVideoSizePreset) {
//                mProfile.setEncodingSizeLevel(mEncodingConfig.mVideoSizePreset);
//            } else {
//                mProfile.setPreferredVideoEncodingSize(mEncodingConfig.mVideoSizeCustomWidth, mEncodingConfig.mVideoSizeCustomHeight);
//            }
//
//            // video misc
//            mProfile.setEncodingOrientation(mEncodingConfig.mVideoOrientationPortrait ? StreamingProfile.ENCODING_ORIENTATION.PORT : StreamingProfile.ENCODING_ORIENTATION.LAND);
//            mProfile.setEncoderRCMode(mEncodingConfig.mVideoRateControlQuality ? StreamingProfile.EncoderRCModes.QUALITY_PRIORITY : StreamingProfile.EncoderRCModes.BITRATE_PRIORITY);
//            mProfile.setBitrateAdjustMode(mEncodingConfig.mBitrateAdjustMode);
//            mProfile.setFpsControllerEnable(mEncodingConfig.mVideoFPSControl);
//            if (mEncodingConfig.mBitrateAdjustMode == StreamingProfile.BitrateAdjustMode.Auto) {
//                mProfile.setVideoAdaptiveBitrateRange(mEncodingConfig.mAdaptiveBitrateMin * 1024, mEncodingConfig.mAdaptiveBitrateMax * 1024);
//            }
//        }
        //video quality
        mProfile.setVideoQuality(StreamingProfile.VIDEO_QUALITY_HIGH3);
        mProfile.setEncodingSizeLevel(1);
        // audio quality
        mProfile.setAudioQuality(11);
        mProfile.setEncodingOrientation(StreamingProfile.ENCODING_ORIENTATION.LAND);

        try {
            mProfile.setPublishUrl("rtmp://pili-publish.discovery-live.qingshuxuetang.com/discovery-live/TEST006?e=1511948059&token=pCyqfXl1B4KjNCHB-hdnyaEhdLgvUYKmxX8Hl4kT:o_bzF5caQ0Xar6mtQCeWgKiPris=");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        // custom
//        if (aProfile != null || vProfile != null) {
//            StreamingProfile.AVProfile avProfile = new StreamingProfile.AVProfile(vProfile, aProfile);
//            mProfile.setAVProfile(avProfile);
//        }

    }


    private void initStreamingManager() {
        mStreamingManager = new StreamingManager(this, AVCodecType.HW_VIDEO_YUV_AS_INPUT_WITH_HW_AUDIO_CODEC);
        mProfile.setPreferredVideoEncodingSize(1080, 1920);
        //   mProfile.setEncodingSizeLevel(0);
        mStreamingManager.prepare(mProfile);
        mStreamingManager.setStreamingSessionListener(this);
        mStreamingManager.setStreamStatusCallback(this);
        mStreamingManager.setStreamingStateListener(this);

    }

    public void startStreaming(View view) {
        //camera数据获取

        //音频数据获取-->resume里已经在拿

        //开始推流
        startStreaming();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mExtAudioCapture.startCapture();
        mExtAudioCapture.setOnAudioFrameCapturedListener(mOnAudioFrameCapturedListener);
        mStreamingManager.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mExtAudioCapture.stopCapture();
        mStreamingManager.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mStreamingManager.destroy();
    }


    protected boolean startStreaming() {
        android.util.Log.d("ysh", "startStreaming");
        return mStreamingManager.startStreaming();
    }

    protected boolean stopStreaming() {
        return mStreamingManager.stopStreaming();
    }

    private ExtVideoCapture.OnPreviewFrameCallback mOnPreviewFrameCallback = new ExtVideoCapture.OnPreviewFrameCallback() {
        @Override
        public void onPreviewFrameCaptured(byte[] data, int width, int height, int orientation, boolean mirror, int fmt, long tsInNanoTime) {
            mStreamingManager.inputVideoFrame(data, width, height, orientation, false, fmt, tsInNanoTime);
        }
    };

    private ExtAudioCapture.OnAudioFrameCapturedListener mOnAudioFrameCapturedListener = new ExtAudioCapture.OnAudioFrameCapturedListener() {
        @Override
        public void onAudioFrameCaptured(byte[] audioData) {
            long timestamp = System.nanoTime();
            mStreamingManager.inputAudioFrame(audioData, timestamp, false);
        }
    };

    @Override
    public void onAudioSourceAvailable(ByteBuffer byteBuffer, int i, long l, boolean b) {

    }

    @Override
    public void notifyStreamStatusChanged(StreamingProfile.StreamStatus streamStatus) {

    }

    @Override
    public boolean onRecordAudioFailedHandled(int i) {
        return false;
    }

    @Override
    public boolean onRestartStreamingHandled(int i) {
        return false;
    }

    @Override
    public Camera.Size onPreviewSizeSelected(List<Camera.Size> list) {
        return null;
    }

    @Override
    public void onStateChanged(StreamingState streamingState, Object o) {

    }
}
