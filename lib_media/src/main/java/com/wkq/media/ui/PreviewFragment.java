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
import java.io.InputStream;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

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

        media = getArguments().getParcelable("media");

        play_view = (ImageView) view.findViewById(R.id.play_view);
        gifView = (ImageView) view.findViewById(R.id.gifView);
        mPhotoView = (LargeImageView) view.findViewById(R.id.photoview);
        ivVideoPre = (ImageView) view.findViewById(R.id.iv_video_pre);
        setPlayView(media);
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

                if ("gif".equals(FileTypeUtil.getFileType(media.path))) {
                    mPhotoView.setVisibility(View.GONE);
                    Glide.with(getActivity()).asGif().load(media.path).into(gifView);
                } else if ("bmp".equals(FileTypeUtil.getFileType(media.path))) {
                    ivVideoPre.setVisibility(View.GONE);
                    mPhotoView.setVisibility(View.VISIBLE);
                    if (AndroidQUtil.isAndroidQ()) {
                        mPhotoView.setImage(AndroidQUtil.getBitmapFromUri(getActivity(), Uri.parse(media.fileUri)));
                    } else {
                        Bitmap myBitmap = BitmapFactory.decodeFile(media.path);
                        mPhotoView.setImage(myBitmap);
                    }

                } else {
                    ivVideoPre.setVisibility(View.GONE);
                    mPhotoView.setVisibility(View.VISIBLE);
                    if (AndroidQUtil.isAndroidQ()) {
                        Uri imgUri = Uri.parse(media.fileUri);
                        InputStream input = null;
                        try {
                            input = getActivity().getContentResolver().openInputStream(imgUri);
                            int size = input.available();
                            if (size / 1024 / 1024 >= 6) {
                                showBigImg(imgUri);
                            } else {
                                mPhotoView.setImage(AndroidQUtil.getBitmapFromUri(getActivity(), imgUri));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            showBigImg(imgUri);
                        }


                    } else {
                        mPhotoView.setImage(new FileBitmapDecoderFactory(new File(media.path)));
                    }

                }

            }

        } else {
            Toast.makeText(getActivity(), "文件已损坏",Toast.LENGTH_SHORT).show();
        }

    }

    private void showBigImg(Uri imgUri) {
        Observable.create((ObservableOnSubscribe<String>) emitter -> {
            String path = AndroidQUtil.copyAndroidQFile(getActivity(), imgUri.toString(), media.name + "");
            if (path != null && FileUtils.isFileExists(path)) {
                emitter.onNext(path);
            } else {
                emitter.onError(new Throwable(""));
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(String path) {
                mPhotoView.setImage(new FileBitmapDecoderFactory(path));
            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(getActivity(), "文件已损坏",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onComplete() {

            }
        });
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
