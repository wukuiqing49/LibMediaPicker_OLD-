package com.wkq.media.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wkq.media.R;


/**
 * Created by Lynn on 2018/3/27.
 */

public class AlertDialogUtils {

    public static Dialog showTwoButtonDialog(Context context, String leftString, String rightString, String content, int leftTextColor, int rightTextColor, final DialogTwoListener l) {
        final Dialog dialog = new Dialog(context, R.style.CustomDialogStyleMediaPicker);
        View view = LayoutInflater.from(context).inflate(R.layout.layout_alert_dialog_2_btn, null);
        ((TextView) (view.findViewById(R.id.content))).setText(content);
        ((TextView) (view.findViewById(R.id.btn_left))).setText(leftString);
        ((TextView) (view.findViewById(R.id.btn_left))).setTextColor(context.getResources().getColor(leftTextColor));
        ((TextView) (view.findViewById(R.id.btn_right))).setText(rightString);
        ((TextView) (view.findViewById(R.id.btn_right))).setTextColor(context.getResources().getColor(rightTextColor));
        ViewGroup.LayoutParams vl = new ViewGroup.LayoutParams(ScreenUtil.getScreenWidth(context) - DensityUtils.dp2px(context, 146), ViewGroup.LayoutParams.WRAP_CONTENT);
        view.findViewById(R.id.btn_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (l == null) {
                    dialog.dismiss();
                } else {
                    l.onClickLeft(dialog);
                }
            }
        });
        view.findViewById(R.id.btn_right).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (l == null) {
                    dialog.dismiss();
                } else {
                    l.onClickRight(dialog);
                }
            }
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.addContentView(view, vl);
        dialog.show();
        return dialog;
    }

    public interface DialogTwoListener {
        void onClickLeft(Dialog dialog);

        void onClickRight(Dialog dialog);
    }
}
