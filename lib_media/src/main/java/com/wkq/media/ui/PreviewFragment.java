package com.wkq.media.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cnlive.largeimage.LargeImageView;
import com.cnlive.largeimage.factory.FileBitmapDecoderFactory;
import com.wkq.media.R;
import com.wkq.media.entity.Media;
import com.wkq.media.utils.AndroidQUtil;
import com.wkq.media.utils.FileTypeUtil;
import com.wkq.media.utils.FileUtils;

import java.io.File;

/**
 * Created by dmcBig on 2017/8/16.
 */

public class PreviewFragment extends Fragment {
    private LargeImageView mPhotoView;
    ImageView play_view;

    ViewGroup mRootView;
    Media media;
    ImageView ivVideoPre;
    ImageView gifView;

    public static PreviewFragment newInstance(Media media) {
        PreviewFragment f = new PreviewFragment();
        Bundle b = new Bundle();
        b.putParcelable("media", media);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setRetainInstance(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = (ViewGroup) inflater.inflate(R.layout.preview_fragment_item, container, false);
        return mRootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        Media media = getArguments().getParcelable("media");

        media = getArguments().getParcelable("media");

        play_view = (ImageView) view.findViewById(R.id.play_view);
        gifView = (ImageView) view.findViewById(R.id.gifView);
        mPhotoView = (LargeImageView) view.findViewById(R.id.photoview);
        ivVideoPre = (ImageView) view.findViewById(R.id.iv_video_pre);


        setPlayView(media);

        //获取屏幕宽度
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager manager = getActivity().getWindowManager();
        manager.getDefaultDisplay().getMetrics(metrics);
        final int width = metrics.widthPixels / 2;

        //按宽度等比例缩放，不然会OOM
//        int[] width_height = getImageWidthHeight(media.path);
//        float ratio = (float) ((width_height[0] * 1.0) / (width * 1.0));
//        int height = (int) (width_height[1] * 1.0 / ratio);

//        RequestOptions options = new RequestOptions();
//        options.diskCacheStrategy(DiskCacheStrategy.NONE);
//        options.priority(Priority.LOW);
//        options.override(width, height);
        if (FileUtils.isFileExists(media.path)) {
            if (media.mediaType == 3) {
                mPhotoView.setVisibility(View.GONE);
                ivVideoPre.setVisibility(View.VISIBLE);
                if (AndroidQUtil.isAndroidQ()) {
                    Uri mediaUri = Uri.parse(media.fileUri);
                    Glide.with(getActivity())
                            .load(mediaUri)
                            .into(ivVideoPre);
                } else {
                    Glide.with(getActivity())
                            .load(media.path)
                            .into(ivVideoPre);
                }
            } else {
                mPhotoView.setVisibility(View.VISIBLE);
                if ("gif".equals(FileTypeUtil.getFileType(media.path))) {

                    Glide.with(getActivity()).asGif().load(media.path).into(gifView);
                } else {
                    ivVideoPre.setVisibility(View.GONE);
                    if (AndroidQUtil.isAndroidQ()) {

                        mPhotoView.setImage(AndroidQUtil.getBitmapFromUri(getActivity(), Uri.parse(media.fileUri)));
                    } else {

                        mPhotoView.setImage(new FileBitmapDecoderFactory(new File(media.path)));
                    }

                }

            }

        } else {
            Toast.makeText(getActivity(), "请选择媒体文件", Toast.LENGTH_SHORT).show();
        }

    }

    //在不加载图片情况下获取图片大小
    public static int[] getImageWidthHeight(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        /**
         * 最关键在此，把options.inJustDecodeBounds = true;
         * 这里再decodeFile()，返回的bitmap为空，但此时调用options.outHeight时，已经包含了图片的高了
         */
        options.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options); // 此时返回的bitmap为null
        /**
         *options.outHeight为原始图片的高
         */
        return new int[]{options.outWidth, options.outHeight};
    }

    void setPlayView(final Media media) {
        if (media.mediaType == 3) {
            play_view.setVisibility(View.VISIBLE);
            play_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri uri = Uri.parse(media.path);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(uri, "video/*");
                    startActivity(intent);
                }
            });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
