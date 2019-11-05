package com.wkq.media.adapter;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.wkq.media.PickerConfig;
import com.wkq.media.R;
import com.wkq.media.entity.Media;
import com.wkq.media.utils.AndroidQUtil;
import com.wkq.media.utils.DoublePressed;
import com.wkq.media.utils.FileTypeUtil;
import com.wkq.media.utils.FileUtils;
import com.wkq.media.utils.PermissionChecker;
import com.wkq.media.utils.ScreenUtils;
import com.wkq.media.utils.StringUtils;

import java.io.File;
import java.util.ArrayList;

import static com.wkq.media.ui.PickerActivity.REQUEST_CODE_PERMISSION_CAMERA;


/**
 * Created by dmcBig on 2017/7/5.
 */

public class MediaGridAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_CAMERA = 0;
    private static final int TYPE_NORMAL = 1;

    private ArrayList<Media> medias;
    Context context;
    FileUtils fileUtils = new FileUtils();
    ArrayList<Media> selectMedias = new ArrayList<>();
    long maxSelect, maxVideoSize, maxImageSize;

    private boolean showCamera = false;
    private boolean isFriendCircle;
    private boolean isSinglePick;
    private boolean showTime;
    private boolean isSelectGift;
    private int maxTime;

    public MediaGridAdapter(ArrayList<Media> list, Context context, ArrayList<Media> select, int max, long maxVideoSize, long maxImageSize, boolean selectGift, boolean isFriendCircle, boolean isSinglePick, boolean showTime, int maxTime) {
        if (select != null) {
            this.selectMedias = select;
        }
        this.maxSelect = max;
        this.maxVideoSize = maxVideoSize;
        this.maxImageSize = maxImageSize;
        this.medias = list;
        this.context = context;
        this.isFriendCircle = isFriendCircle;
        this.isSinglePick = isSinglePick;
        this.isSelectGift = selectGift;
        this.showTime = showTime;
        this.maxTime = maxTime;
    }


    public MediaGridAdapter(ArrayList<Media> list, Context context, ArrayList<Media> select, int max, long maxVideoSize, long maxImageSize, boolean isFriendCircle, boolean isSinglePick) {
        if (select != null) {
            this.selectMedias = select;
        }
        this.maxSelect = max;
        this.maxVideoSize = maxVideoSize;
        this.maxImageSize = maxImageSize;
        this.medias = list;
        this.context = context;
        this.isFriendCircle = isFriendCircle;
        this.isSinglePick = isSinglePick;
    }

    public void setShowCamera(boolean showCamera) {
        this.showCamera = showCamera;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView media_image, check_image;
        public View mask_view;
        public TextView textView_size;
        public RelativeLayout video_info;

        public MyViewHolder(View view) {
            super(view);
            media_image = (ImageView) view.findViewById(R.id.media_image);
            check_image = (ImageView) view.findViewById(R.id.check_image);
            mask_view = view.findViewById(R.id.mask_view);
            video_info = (RelativeLayout) view.findViewById(R.id.video_info);
            textView_size = (TextView) view.findViewById(R.id.textView_size);
            itemView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getItemWidth())); //让图片是个正方形
        }
    }

    public class CameraViewHolder extends RecyclerView.ViewHolder {

        public TextView cameraText;

        public CameraViewHolder(View itemView) {
            super(itemView);
            cameraText = (TextView) itemView.findViewById(R.id.camera_text);
            itemView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getItemWidth()));
        }
    }

    int getItemWidth() {
        return (ScreenUtils.getScreenWidth(context) / PickerConfig.GridSpanCount) - PickerConfig.GridSpanCount;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType) {
            case TYPE_CAMERA:
                View cameraView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.camera_item, viewGroup, false);
                viewHolder = new CameraViewHolder(cameraView);
                break;
            case TYPE_NORMAL:
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.media_view_item, viewGroup, false);
                viewHolder = new MyViewHolder(view);
            default:
                break;
        }
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        switch (getItemViewType(position)) {
            case TYPE_CAMERA:
                CameraViewHolder cameraViewHolder = (CameraViewHolder) holder;
                cameraViewHolder.cameraText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (hasPermission())
                            if (mOnItemClickListener != null)
                                mOnItemClickListener.onItemClick(v, position, null, null);
                    }
                });
                break;
            case TYPE_NORMAL:
                final MyViewHolder myViewHolder = (MyViewHolder) holder;
                final Media media;

                if (showCamera) {
                    media = medias.get(position - 1);
                } else {
                    media = medias.get(position);
                }

                if (AndroidQUtil.isAndroidQ()) {
                    Uri mediaUri = Uri.parse(media.fileUri);
                    if (media.mediaType==3){
                        Glide.with(context)
                                .load(mediaUri)
                                .into(myViewHolder.media_image);
                    }else {
                        Glide.with(context)
                                .load(mediaUri)
                                .into(myViewHolder.media_image);
                    }
                } else {
                    Uri mediaUri = Uri.parse("file://" + media.path);
                    Glide.with(context)
                            .load(mediaUri)
                            .into(myViewHolder.media_image);
                }

                int isSelect = isSelect(media);

                if (media.mediaType == 3) {
                    myViewHolder.video_info.setVisibility(View.VISIBLE);

                    myViewHolder.textView_size.setText(showTime ? StringUtils.gennerTime(media.duration / 1000) : fileUtils.getSizeByUnit(media.size));
                    if (isFriendCircle) {
                        myViewHolder.check_image.setVisibility(View.GONE);
                    } else {
                        myViewHolder.check_image.setVisibility(View.VISIBLE);
                    }
                } else {
                    myViewHolder.video_info.setVisibility(View.INVISIBLE);
                    myViewHolder.check_image.setVisibility(View.VISIBLE);

                }

                myViewHolder.check_image.setImageDrawable(isSelect >= 0 ? ContextCompat.getDrawable(context, R.drawable.xc_xuanzhong) : ContextCompat.getDrawable(context, R.drawable.xc_weixuan));
                if (isSinglePick) {
                    myViewHolder.check_image.setVisibility(View.GONE);
                }

                myViewHolder.mask_view.setVisibility(isSelect >= 0 ? View.VISIBLE : View.INVISIBLE);
               final File file = new File(media.path);
                myViewHolder.check_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!file.exists()){
                            Toast.makeText(context, "文件已损坏", Toast.LENGTH_LONG).show();

                            return;
                        }
                        /*if (!com.cnlive.libs.base.util.FileUtils.isFileExists(media.path)) {
                            AlertUtil.showFailedToast(context, "文件已损坏");
                            return;
                        }*/
                        int isSelect = isSelect(media);
                        if (selectMedias.size() >= maxSelect && isSelect < 0) {
                            Toast.makeText(context, context.getString(R.string.msg_amount_limit), Toast.LENGTH_SHORT).show();
                        } else {
                            if (isSelect >= 0) {
                                myViewHolder.mask_view.setVisibility(View.INVISIBLE);
                                myViewHolder.check_image.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.xc_weixuan));
                                setSelectMedias(media);
                                if (mOnItemClickListener != null)
                                    mOnItemClickListener.onItemCheckClick(view, showCamera ? position - 1 : position, media, selectMedias);
                            } else {
                                if (media.mediaType == 3 && !isFriendCircle && media.size > maxVideoSize) {
                                    Toast.makeText(context, context.getString(R.string.msg_size_limit) + (FileUtils.fileSize(maxVideoSize)), Toast.LENGTH_LONG).show();
                                    return;
                                }
                                if (!isSelectGift && FileTypeUtil.getFileType(media.path).equals("gif")) {
                                    if(DoublePressed.onDoublePressed())return;
                                    Toast.makeText(context, context.getString(R.string.msg_gif_limit), Toast.LENGTH_LONG).show();
                                    return;
                                }
                                if (media.mediaType == 1 && media.size > maxImageSize) {
                                    Toast.makeText(context, context.getString(R.string.msg_size_limit) + (FileUtils.fileSize(maxImageSize)), Toast.LENGTH_LONG).show();
                                    return;
                                }

                                if (media.duration > maxTime && !isFriendCircle) {
                                    Toast.makeText(context, context.getString(R.string.msg_time_limit) + (StringUtils.gennerMinSec(maxTime / 1000)), Toast.LENGTH_LONG).show();
                                    return;
                                }
                                myViewHolder.mask_view.setVisibility(View.VISIBLE);
                                myViewHolder.check_image.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.xc_xuanzhong));
                                setSelectMedias(media);
                                if (mOnItemClickListener != null)
                                    mOnItemClickListener.onItemCheckClick(view, showCamera ? position - 1 : position, media, selectMedias);

                            }
                        }
                    }
                });

                myViewHolder.media_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!file.exists()){
                            Toast.makeText(context, "文件已损坏", Toast.LENGTH_LONG).show();
                            return;
                        }
                        /*if (!com.cnlive.libs.base.util.FileUtils.isFileExists(media.path)) {
                            AlertUtil.showFailedToast(context, "文件已损坏");
                            return;
                        }*/

                        if (selectMedias.size() > 0 && media.mediaType == 3 && isFriendCircle) {
                            Toast.makeText(context, context.getString(R.string.msg_type_limit), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (media.mediaType == 3 && !isFriendCircle && media.size > maxVideoSize) {
                            Toast.makeText(context, context.getString(R.string.msg_size_limit) + (FileUtils.fileSize(maxVideoSize)), Toast.LENGTH_LONG).show();
                            return;
                        }
                        if (!isSelectGift && FileTypeUtil.getFileType(media.path).equals("gif")) {
                            if(DoublePressed.onDoublePressed())return;
                            Toast.makeText(context, context.getString(R.string.msg_gif_limit), Toast.LENGTH_LONG).show();
                            return;
                        }
                        if (media.mediaType == 1 && media.size > maxImageSize) {
                            Toast.makeText(context, context.getString(R.string.msg_size_limit) + (FileUtils.fileSize(maxImageSize)), Toast.LENGTH_LONG).show();
                            return;
                        }

                        if (media.duration > maxTime && !isFriendCircle) {
                            Toast.makeText(context, context.getString(R.string.msg_time_limit) + (StringUtils.gennerMinSec(maxTime / 1000)), Toast.LENGTH_LONG).show();
                            return;
                        }
                        if (mOnItemClickListener != null)
                            mOnItemClickListener.onItemClick(v, showCamera ? position - 1 : position, media, selectMedias);
                    }
                });


                break;
            default:
                break;
        }

    }

    public void setSelectMedias(Media media) {
        int index = isSelect(media);
        if (index == -1) {
            selectMedias.add(media);
        } else {
            selectMedias.remove(index);
        }
    }

    /**
     * @param media
     * @return 大于等于0 就是表示以选择，返回的是在selectMedias中的下标
     */
    public int isSelect(Media media) {
        int is = -1;
        if (selectMedias.size() <= 0) {
            return is;
        }
        for (int i = 0; i < selectMedias.size(); i++) {
            Media m = selectMedias.get(i);
            if (m.path.equals(media.path)) {
                is = i;
                break;
            }
        }
        return is;
    }

    public void updateSelectAdapter(ArrayList<Media> select) {
        if (select != null) {
            this.selectMedias = select;
        }
        notifyDataSetChanged();
    }

    public void updateAdapter(ArrayList<Media> list) {
        this.medias = list;
        notifyDataSetChanged();
    }

    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public ArrayList<Media> getSelectMedias() {
        return selectMedias;
    }

    @Override
    public int getItemCount() {

        return showCamera ? medias.size() + 1 : medias.size();
    }


    @Override
    public int getItemViewType(int position) {
        if (showCamera && position == 0) {
            return TYPE_CAMERA;
        }
        return TYPE_NORMAL;
    }

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int position, Media data, ArrayList<Media> selectMedias);

        void onItemCheckClick(View view, int position, Media data, ArrayList<Media> selectMedias);
    }

    public ArrayList<Media> getMedias() {
        return medias;
    }

    public boolean isShowCamera() {
        return showCamera;
    }

    private boolean hasPermission() {
        return PermissionChecker.checkPermissions((Activity) context
                , new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}
                , REQUEST_CODE_PERMISSION_CAMERA, R.string.dialog_imagepicker_permission_camera_message);
    }
}
