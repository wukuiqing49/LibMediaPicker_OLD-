package com.wkq.demo;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.wkq.media.ImagePicker;
import com.wkq.media.PickerConfig;
import com.wkq.media.entity.Media;
import com.wkq.media.ui.camera.DiyCameraActivity;
import com.wkq.media.utils.AndroidQUtil;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ImageView imageView;
    //发布文件最大值
    private static final int media_item_max_size = 30 * 1024 * 1024;
    private static final int image_item_max_size = 20 * 1024 * 1024;
    //发布视频最大时长
    //发布视频最大时长
    private static final int media_item_max_time = 15_000;
    private static final int media_item_select_max_time = 10 * 1000;

    public static final int camera_def = 0;
    public static final int camera_bx = 1;

    public static final int ACTION_CAMERA_BX = 20010;
    public static final int REQUEST_CODE_START_MOMENT_CAMERA = 10001;
    public static final int REQUEST_CODE_START_MOMENT_BX_CAMERA = 10002;
    public static final int REQUEST_CODE_START_MOMENT_READ_WRITE = 10003;
    public static final int REQUEST_CODE_START_DOWNLOAD_READ_WRITE = 10012;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.iv);

        findViewById(R.id.tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ImagePicker.Builder()
                        .setSelectGif(true)
                        .maxNum(9)
                        .needCamera(true)
                        .maxVideoSize(media_item_max_size)
                        .maxImageSize(image_item_max_size)
                        .showTime(true)
                        .maxTime(media_item_select_max_time)
                        .selectMode(PickerConfig.PICKER_IMAGE_VIDEO)
                        .cachePath((Build.VERSION.SDK_INT == Build.VERSION_CODES.Q ? getExternalFilesDir("") : Environment.getExternalStorageDirectory()) + "/strike/file/")
                        .videoTrimPath((Build.VERSION.SDK_INT == Build.VERSION_CODES.Q ? getExternalFilesDir("") : Environment.getExternalStorageDirectory()) + "/strike/file/")
                        .isFriendCircle(true)
                        .builder()
                        //跳转到图片选择页面 activity    请求码            结果码
                        .start(MainActivity.this, 200, PickerConfig.DEFAULT_RESULT_CODE);

            }
        });

    File temp=    new File(getCacheDir().getAbsolutePath()+"ttt.png");

        findViewById(R.id.tv_2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DiyCameraActivity.start( MainActivity.this,getCacheDir().getPath(),new ArrayList<Media>(),15000,19901026,259,"");
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == PickerConfig.DEFAULT_RESULT_CODE) {
            ArrayList<Media> select = data.getParcelableArrayListExtra(PickerConfig.EXTRA_RESULT);

            Uri  uri=null;
            if (AndroidQUtil.isAndroidQ()){
                uri=Uri.parse( select.get(0).fileUri);
            }else {
                uri=Uri.fromFile( new File(select.get(0).path));
            }

            Glide.with(this).load(uri).into(imageView);

        }
        if (requestCode==200){
            if (data==null)return;
            ArrayList<Media> select = data.getParcelableArrayListExtra(PickerConfig.EXTRA_RESULT);
            Uri  uri=null;
            if (AndroidQUtil.isAndroidQ()){
                uri=Uri.parse( select.get(0).fileUri);
            }else {
                uri=Uri.fromFile( new File(select.get(0).path));
            }
            Glide.with(this).load(uri).into(imageView);
        }


    }

}
