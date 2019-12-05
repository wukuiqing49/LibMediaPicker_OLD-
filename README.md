
         LibMediaPicker
             这是一个相册框架,主要功能如下:

             1:支持图片,视频的文件选取 

             2:支持Gif图片选取

             3:支持视频裁剪

             4:支持限定文件大小选取

             5:支持图片裁剪

             6:支持预览图放大功能

             7:适配AndroidQ新版本


    引用方式:
    implementation 'com.github.wukuiqing49:LibMediaPicker:1.0.14'

     注意:需要处理 jdk 8 的兼容  (需要在项目的Bulid下配置)

                        compileOptions {
                                sourceCompatibility 1.8
                                targetCompatibility 1.8
                                         }
        调用方式:
                ```
                    new ImagePicker.Builder()
                        .setSelectGif(true)
                        .maxNum(9)
                        .needCamera(true)
                        .maxVideoSize(media_item_max_size)
                        .maxImageSize(image_item_max_size)
                        .showTime(true)
                        .maxTime(media_item_select_max_time)
                        .selectMode(PickerConfig.PICKER_IMAGE_VIDEO)
                        .cachePath((Build.VERSION.SDK_INT == Build.VERSION_CODES.Q ? getExternalFilesDir("") :                 Environment.getExternalStorageDirectory()) + "/strike/file/")
                        .videoTrimPath((Build.VERSION.SDK_INT == Build.VERSION_CODES.Q ? getExternalFilesDir("") : Environment.getExternalStorageDirectory()) + "/strike/file/")
                        .isFriendCircle(true)
                        .builder()
                        //跳转到图片选择页面 activity    请求码            结果码
                        .start(MainActivity.this, 200, PickerConfig.DEFAULT_RESULT_CODE);

            }
```


                接收方式:
                ```
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

                ```


