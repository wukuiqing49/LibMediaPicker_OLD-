//package com.wkq.media.ui;
//
//import android.animation.ValueAnimator;
//import android.app.Activity;
//import android.content.Intent;
//import android.graphics.Color;
//import android.net.Uri;
//import android.os.Bundle;
//import android.os.Environment;
//import android.os.Handler;
//import android.os.Message;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.text.TextUtils;
//import android.util.Log;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.ViewConfiguration;
//import android.view.animation.LinearInterpolator;
//import android.widget.FrameLayout;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import com.cnlive.libs.base.util.AlertUtil;
//import com.cnlive.libs.base.util.StatusBarUtil2;
//import com.cnlive.libs.video.video.VideoView;
//import com.cnlive.media.picker.ExtractFrameWorkThread;
//import com.cnlive.media.picker.PickerConfig;
//import com.cnlive.media.picker.R;
//import com.cnlive.media.picker.adapter.VideoEditAdapter;
//import com.cnlive.media.picker.data.VideoEditInfo;
//import com.cnlive.media.picker.entity.Media;
//import com.cnlive.media.picker.utils.ExtractVideoInfoUtil;
//import com.cnlive.media.picker.utils.PictureUtils;
//import com.cnlive.media.picker.utils.ScreenUtils;
//import com.cnlive.media.picker.view.EditSpacingItemDecoration;
//import com.cnlive.media.picker.view.RangeSeekBar;
//import com.qiniu.pili.droid.shortvideo.PLShortVideoTrimmer;
//import com.qiniu.pili.droid.shortvideo.PLVideoSaveListener;
//import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
//import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
//
//import java.io.File;
//import java.lang.ref.WeakReference;
//import java.util.ArrayList;
//
//public class EditVideoActivity extends AppCompatActivity implements View.OnClickListener {
//    private static final String PATH = "path";
//    private static final String MAX_DURATION = "max_duration";
//    private static final String RESULT_CODE = "result_code";
//    private static final String VIDEO_TRIM_PATH = "video_trim_path";
//
//    private static final long MIN_CUT_DURATION = 3 * 1000L;// 最小剪辑时间3s
//    private static long MAX_CUT_DURATION = 5 * 60 * 1000L;//视频最多剪切多长时间
//    private static final int MAX_COUNT_RANGE = 10;//seekBar的区域内一共有多少张图片
//    private LinearLayout seekBarLayout;
//    private ExtractVideoInfoUtil mExtractVideoInfoUtil;
//    private int mMaxWidth;
//
//    private ImageView backBtn;
//    private TextView doneBtn;
//    private long duration;
//    private RangeSeekBar seekBar;
//    private VideoView mVideoView;
//    private RecyclerView mRecyclerView;
//    private ImageView positionIcon;
//    private VideoEditAdapter videoEditAdapter;
//    private float averageMsPx;//每毫秒所占的px
//    private float averagePxMs;//每px所占用的ms毫秒
//    private String OutPutFileDirPath;
//    private ExtractFrameWorkThread mExtractFrameWorkThread;
//    private String path;
//    private int resultCode;
//    private long leftProgress, rightProgress;
//    private long scrollPos = 0;
//    private int mScaledTouchSlop;
//    private int lastScrollX;
//
//    private PLShortVideoTrimmer mShortVideoTrimmer;
//    private QMUITipDialog tipDialog;
//    private String videoTrimPath;
//    private int thumbnailsCount;
//
//
//    /**
//     * @param context     上下文
//     * @param path        本地视频路径
//     * @param maxDuration 视频裁剪后最大时长
//     */
//    public static void startEditActivity(Activity context, String path, long maxDuration, String videoTrimPath, int resultCode) {
//        Intent intent = new Intent(context, EditVideoActivity.class);
//        intent.putExtra(PATH, path);
//        intent.putExtra(MAX_DURATION, maxDuration);
//        intent.putExtra(RESULT_CODE, resultCode);
//        intent.putExtra(VIDEO_TRIM_PATH, videoTrimPath);
//        context.startActivityForResult(intent, 200);
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_edit_video);
//        QMUIStatusBarHelper.setStatusBarDarkMode(this);
//        StatusBarUtil2.setColor(this, Color.BLACK, 0);
//        initData();
//        initView();
//        initEditVideo();
//        initPlay();
//
//        tipDialog = new QMUITipDialog.Builder(EditVideoActivity.this)
//                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
//                .setTipWord("裁剪中...")
//                .create();
//
//        tipDialog.setOnCancelListener(dialog -> mShortVideoTrimmer.cancelTrim());
//    }
//
//    private void initData() {
//        path = getIntent().getStringExtra(PATH);
//        MAX_CUT_DURATION = getIntent().getLongExtra(MAX_DURATION, 0);
//        resultCode = getIntent().getIntExtra(RESULT_CODE, 0);
//        videoTrimPath = getIntent().getStringExtra(VIDEO_TRIM_PATH);
//
//        if (TextUtils.isEmpty(videoTrimPath)) {
//            videoTrimPath = Environment.getExternalStorageDirectory().getPath() + File.separator + "trim";
//        } else {
//            videoTrimPath = videoTrimPath.substring(0, videoTrimPath.length() - 1);
//        }
//
//        File videoTrimFile = new File(videoTrimPath);
//        if (!videoTrimFile.exists()) {
//            videoTrimFile.mkdirs();
//        }
//
//        mExtractVideoInfoUtil = new ExtractVideoInfoUtil(path);
//        duration = Long.valueOf(mExtractVideoInfoUtil.getVideoLength());
//
//
//        mMaxWidth = ScreenUtils.getScreenWidth(this) - ScreenUtils.dp2px(this, 70);
//        mScaledTouchSlop = ViewConfiguration.get(this).getScaledTouchSlop();
//
//    }
//
//    private void initView() {
//        seekBarLayout = (LinearLayout) findViewById(R.id.id_seekBarLayout);
//        mVideoView = (VideoView) findViewById(R.id.uVideoView);
//        positionIcon = (ImageView) findViewById(R.id.positionIcon);
//        mRecyclerView = (RecyclerView) findViewById(R.id.id_rv_id);
//        backBtn = findViewById(R.id.back_btn);
//        backBtn.setOnClickListener(this);
//        doneBtn = findViewById(R.id.done);
//        doneBtn.setOnClickListener(this);
//        LinearLayoutManager linearLayoutManager;
//        mRecyclerView.setLayoutManager(linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
//        videoEditAdapter = new VideoEditAdapter(this,
//                (ScreenUtils.getScreenWidth(this) - ScreenUtils.dp2px(this, 70)) / 10);
//        mRecyclerView.setAdapter(videoEditAdapter);
//        mRecyclerView.addOnScrollListener(mOnScrollListener);
//    }
//
//    private void initEditVideo() {
//        //for video edit
//        long startPosition = 0;
//        long endPosition = duration;
//        int rangeWidth;
//        boolean isOver_60_s;
//        if (endPosition <= MAX_CUT_DURATION) {
//            isOver_60_s = false;
//            thumbnailsCount = MAX_COUNT_RANGE;
//            rangeWidth = mMaxWidth;
//        } else {
//            isOver_60_s = true;
//            thumbnailsCount = (int) (endPosition * 1.0f / (MAX_CUT_DURATION * 1.0f) * MAX_COUNT_RANGE);
//            rangeWidth = mMaxWidth / MAX_COUNT_RANGE * thumbnailsCount;
//        }
//        mRecyclerView.addItemDecoration(new EditSpacingItemDecoration(ScreenUtils.dp2px(this, 35), ScreenUtils.dp2px(this, 35), thumbnailsCount));
//
//        //init seekBar
//        if (isOver_60_s) {
//            seekBar = new RangeSeekBar(this, 0L, MAX_CUT_DURATION);
//            seekBar.setSelectedMinValue(0L);
//            seekBar.setSelectedMaxValue(MAX_CUT_DURATION);
//        } else {
//            seekBar = new RangeSeekBar(this, 0L, endPosition);
//            seekBar.setSelectedMinValue(0L);
//            seekBar.setSelectedMaxValue(endPosition);
//        }
//        seekBar.setMin_cut_time(MIN_CUT_DURATION);//设置最小裁剪时间
//        seekBar.setNotifyWhileDragging(true);
//        seekBar.setOnRangeSeekBarChangeListener(mOnRangeSeekBarChangeListener);
//        seekBarLayout.addView(seekBar);
//
//        averageMsPx = duration * 1.0f / rangeWidth * 1.0f;
//        OutPutFileDirPath = PictureUtils.getSaveEditThumbnailDir(this);
//        int extractW = (ScreenUtils.getScreenWidth(this) - ScreenUtils.dp2px(this, 70)) / MAX_COUNT_RANGE;
//        int extractH = ScreenUtils.dp2px(this, 55);
//        mExtractFrameWorkThread = new ExtractFrameWorkThread(extractW, extractH, mUIHandler, path, OutPutFileDirPath, startPosition, endPosition, thumbnailsCount);
//        mExtractFrameWorkThread.start();
//
//        //init pos icon start
//        leftProgress = 0;
//        if (isOver_60_s) {
//            rightProgress = MAX_CUT_DURATION;
//        } else {
//            rightProgress = endPosition;
//        }
//        averagePxMs = (mMaxWidth * 1.0f / (rightProgress - leftProgress));
//
//    }
//
//    private void initPlay() {
//        mVideoView.setDataSource(this, Uri.parse(path));
//        //设置videoview的OnPrepared监听
//        mVideoView.setOnPreparedListener(iMediaPlayer -> videoStart());
//        videoStart();
//    }
//
//    private boolean isOverScaledTouchSlop;
//
//    private final RecyclerView.OnScrollListener mOnScrollListener = new RecyclerView.OnScrollListener() {
//        @Override
//        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//            super.onScrollStateChanged(recyclerView, newState);
//            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
//                videoStart();
//            } else {
//                if (isOverScaledTouchSlop && mVideoView != null && mVideoView.isPlaying()) {
//                    videoPause();
//                }
//            }
//        }
//
//        @Override
//        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//            super.onScrolled(recyclerView, dx, dy);
//            int scrollX = getScrollXDistance();
//            Log.e("LynnTest", "scrollX " + scrollX + " ,paddingLeft " + seekBar.getPaddingLeft());
//            //达不到滑动的距离
//            if (Math.abs(lastScrollX - scrollX) < mScaledTouchSlop) {
//                isOverScaledTouchSlop = false;
//                return;
//            }
//            isOverScaledTouchSlop = true;
//            //初始状态,why ? 因为默认的时候有35dp的空白！
//            if (scrollX == -ScreenUtils.dp2px(EditVideoActivity.this, 35)) {
//                scrollPos = 0;
//            } else {
//                // why 在这里处理一下,因为onScrollStateChanged早于onScrolled回调
//                if (mVideoView != null && mVideoView.isPlaying()) {
//                    videoPause();
//                }
//                scrollPos = (long) (averageMsPx * (ScreenUtils.dp2px(EditVideoActivity.this, 35) + scrollX));
//                leftProgress = seekBar.getSelectedMinValue() + scrollPos;
//                rightProgress = seekBar.getSelectedMaxValue() + scrollPos;
//                mVideoView.seekTo((int) leftProgress);
//            }
//            lastScrollX = scrollX;
//        }
//    };
//
//    /**
//     * 水平滑动了多少px
//     *
//     * @return int px
//     */
//    private int getScrollXDistance() {
//        LinearLayoutManager layoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
//        int position = layoutManager.findFirstVisibleItemPosition();
//        View firstVisibleChildView = layoutManager.findViewByPosition(position);
//        int itemWidth = firstVisibleChildView.getWidth();
//        return (position) * itemWidth - firstVisibleChildView.getLeft();
//    }
//
//    private ValueAnimator animator;
//
//    private void anim() {
//        if (positionIcon.getVisibility() == View.GONE) {
//            positionIcon.setVisibility(View.VISIBLE);
//        }
//        final FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) positionIcon.getLayoutParams();
//        int start = (int) (ScreenUtils.dp2px(this, 35) + (leftProgress/*mVideoView.getCurrentPosition()*/ - scrollPos) * averagePxMs);
//        int end = (int) (ScreenUtils.dp2px(this, 35) + (rightProgress - scrollPos) * averagePxMs);
//        animator = ValueAnimator
//                .ofInt(start, end)
//                .setDuration((rightProgress - scrollPos) - (leftProgress/*mVideoView.getCurrentPosition()*/ - scrollPos));
//        animator.setInterpolator(new LinearInterpolator());
//        animator.addUpdateListener(animation -> {
//            params.leftMargin = (int) animation.getAnimatedValue();
//            positionIcon.setLayoutParams(params);
//        });
//        animator.start();
//    }
//
//    private final MainHandler mUIHandler = new MainHandler(this);
//
//    @Override
//    public void onClick(View v) {
//        if (v.getId() == R.id.back_btn) {
//            onBackPressed();
//        } else if (v.getId() == R.id.done) {
//            trim();
//        }
//    }
//
//    private static class MainHandler extends Handler {
//        private final WeakReference<EditVideoActivity> mActivity;
//
//        MainHandler(EditVideoActivity activity) {
//            mActivity = new WeakReference<>(activity);
//        }
//
//        @Override
//        public void handleMessage(Message msg) {
//            EditVideoActivity activity = mActivity.get();
//            if (activity != null) {
//                if (msg.what == ExtractFrameWorkThread.MSG_SAVE_SUCCESS) {
//                    if (activity.videoEditAdapter != null) {
//                        VideoEditInfo info = (VideoEditInfo) msg.obj;
//                        activity.videoEditAdapter.addItemVideoInfo(info);
//                    }
//                }
//            }
//        }
//    }
//
//    EditSpacingItemDecoration editSpacingItemDecoration;
//    private final RangeSeekBar.OnRangeSeekBarChangeListener mOnRangeSeekBarChangeListener = new RangeSeekBar.OnRangeSeekBarChangeListener() {
//        @Override
//        public void onRangeSeekBarValuesChanged(RangeSeekBar bar, long minValue, long maxValue, int action, boolean isMin, RangeSeekBar.Thumb pressedThumb, float rangeL, float rangeR) {
//            Log.e("LynnTest", "rangeL : " + rangeL + " , rangeR : " + rangeR);
//            leftProgress = minValue + scrollPos;
//            rightProgress = maxValue + scrollPos;
//            switch (action) {
//                case MotionEvent.ACTION_DOWN:
//                    videoPause();
//                    break;
//                case MotionEvent.ACTION_MOVE:
//                    mVideoView.seekTo((int) (pressedThumb == RangeSeekBar.Thumb.MIN ?
//                            leftProgress : rightProgress));
//                    break;
//                case MotionEvent.ACTION_UP:
//                    //从minValue开始播
//                    mVideoView.seekTo((int) leftProgress);
//                    videoStart();
//                    break;
//                default:
//                    break;
//            }
//        }
//    };
//
//
//    private void videoStart() {
//        mVideoView.start();
//        positionIcon.clearAnimation();
//        if (animator != null && animator.isRunning()) {
//            animator.cancel();
//        }
//        anim();
//        handler.removeCallbacks(run);
//        handler.post(run);
//    }
//
//    private void videoProgressUpdate() {
//        long currentPosition = mVideoView.getCurrentPosition();
//        if (currentPosition >= (rightProgress)) {
//            mVideoView.seekTo((int) leftProgress);
//            positionIcon.clearAnimation();
//            if (animator != null && animator.isRunning()) {
//                animator.cancel();
//            }
//            anim();
//        }
//    }
//
//    private void videoPause() {
//        if (mVideoView != null && mVideoView.isPlaying()) {
//            mVideoView.pause();
//            handler.removeCallbacks(run);
//        }
//        if (positionIcon.getVisibility() == View.VISIBLE) {
//            positionIcon.setVisibility(View.GONE);
//        }
//        positionIcon.clearAnimation();
//        if (animator != null && animator.isRunning()) {
//            animator.cancel();
//        }
//    }
//
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        if (mVideoView != null) {
//            mVideoView.seekTo((int) leftProgress);
//            videoStart();
//        }
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        if (mVideoView != null && mVideoView.isPlaying()) {
//            videoPause();
//        }
//    }
//
//    private Handler handler = new Handler();
//    private Runnable run = new Runnable() {
//
//        @Override
//        public void run() {
//            videoProgressUpdate();
//            handler.postDelayed(run, 1000);
//        }
//    };
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if (animator != null) {
//            animator.cancel();
//        }
//        if (mVideoView != null) {
//            mVideoView.stop();
//        }
//        if (mExtractVideoInfoUtil != null) {
//            mExtractVideoInfoUtil.release();
//        }
//        mRecyclerView.removeOnScrollListener(mOnScrollListener);
//        if (mExtractFrameWorkThread != null) {
//            mExtractFrameWorkThread.stopExtract();
//        }
//        mUIHandler.removeCallbacksAndMessages(null);
//        handler.removeCallbacksAndMessages(null);
//        if (!TextUtils.isEmpty(OutPutFileDirPath)) {
//            PictureUtils.deleteFile(new File(OutPutFileDirPath));
//        }
//    }
//
//    /**
//     * 裁剪
//     */
//    public void trim() {
//        if (mShortVideoTrimmer != null)
//            mShortVideoTrimmer.destroy();
//        mShortVideoTrimmer = new PLShortVideoTrimmer(this, path, videoTrimPath + "/trim-" + System.currentTimeMillis() + ".mp4");
//
//        tipDialog.show();
//        mShortVideoTrimmer.trim(leftProgress, rightProgress, new PLVideoSaveListener() {
//            @Override
//            public void onSaveVideoSuccess(String s) {
//                done(s, resultCode);
//            }
//
//            @Override
//            public void onSaveVideoFailed(int i) {
//                runOnUiThread(() -> AlertUtil.showFailedToast(EditVideoActivity.this, "裁剪失败"));
//            }
//
//            @Override
//            public void onSaveVideoCanceled() {
//                tipDialog.dismiss();
//                runOnUiThread(() -> AlertUtil.showDeftToast(EditVideoActivity.this, "取消裁剪"));
//            }
//
//            @Override
//            public void onProgressUpdate(float v) {
//            }
//        });
//    }
//
//    //完成
//    public void done(String path, int code) {
//        ArrayList<Media> selectList = new ArrayList<>();
//        File file = new File(path);
//        Media media = new Media(file.getPath(), file.getName(), 1, 3, file.length(), 0, "", (int) (rightProgress - leftProgress), true);
//        selectList.add(media);
//        Intent intent = new Intent();
//        intent.putParcelableArrayListExtra(PickerConfig.EXTRA_RESULT, selectList);
//        setResult(code, intent);
//        finish();
//    }
//
//}
