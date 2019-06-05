package com.wkq.demo;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.wkq.media.ImagePicker;
import com.wkq.media.PickerConfig;
import com.wkq.media.entity.Media;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ImagePicker
                        .Builder()
                        .setSelectGif(false)
                        //最大选择图片数目，默认40张
                        .maxNum(1)
                        //是否需要相机（默认false）
                        .needCamera(true)
                        //选择模式（默认PICKER_IMAGE_VIDEO）     PICKER_IMAGE-->图片 PICKER_VIDEO-->视频  PICKER_IMAGE_VIDEO-->图片&视频
                        .selectMode(PickerConfig.PICKER_IMAGE)
                        //拍照或录像存储位置 默认Environment.getExternalStorageDirectory().getPath() + File.separator + "JCamera"
                        .cachePath(Environment.getExternalStorageDirectory() + "/strike/file/")
                        //是否为发朋友圈模式（默认false）  true-->图片与视频不能同时选择
//                        .isFriendCircle(true)
                        //裁剪 只有在单选模式下才可以使用
//                        .doCrop(1, 1, 300, 300)
                        //是否开启单选模式（默认false）
                        .isSinglePick(true)
                        .builder()
                        .start(MainActivity.this, PickerConfig.PICKER_IMAGE, PickerConfig.DEFAULT_RESULT_CODE);

            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == PickerConfig.DEFAULT_RESULT_CODE) {
            ArrayList<Media> select = data.getParcelableArrayListExtra(PickerConfig.EXTRA_RESULT);
            String path = select.get(0).path;
        }


    }

}
