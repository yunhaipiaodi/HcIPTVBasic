package com.haochuan.core.http;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.haochuan.core.R;

//下载更新对话框
public class UpgradeDialog extends DialogFragment implements View.OnClickListener {

    public static final String TAG = "UpgradeDialog";
    private ImageView mClose;
    private TextView tvTitle;
    private ProgressBar mProgress;
    private String mUpgradeUrl, mVersion;

    private static final int UPDATE_PROGRESS = 0x123;

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATE_PROGRESS:
                    if (mProgress != null) {
                        if (mProgress.getProgress() >= 100) {
                            mProgress.setProgress(100);
                        } else {
                            mProgress.setProgress(msg.arg1);
                        }
                    }
                    break;
            }
        }
    };

    public UpgradeDialog() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null) {
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (getActivity() == null) {
            dismiss();
        }
        Dialog dialog = new Dialog(getActivity(), R.style.MsgDialogStyle);
        View view = View.inflate(getActivity(), R.layout.upgrade_dialog, null);
        tvTitle = view.findViewById(R.id.update_title);
        mProgress = view.findViewById(R.id.progress);
        mClose = view.findViewById(R.id.close);
        mClose.setOnClickListener(this);
        dialog.setContentView(view);
        //关闭按钮获取焦点
        mClose.setFocusable(true);
        return dialog;
    }

    public void changeProgress(int progressValue) {
        Message message = Message.obtain();
        message.what = UPDATE_PROGRESS;
        message.arg1 = progressValue;
        mHandler.sendMessage(message);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.close) {
            dismiss();
        }
    }

    @Override
    public void onDestroyView() {
        DownloadServer.getInstance().stop();
        super.onDestroyView();
    }
}