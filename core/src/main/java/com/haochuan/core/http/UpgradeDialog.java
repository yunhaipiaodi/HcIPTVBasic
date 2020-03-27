package com.haochuan.core.http;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.haochuan.core.Logger;
import com.haochuan.core.R;
import com.haochuan.core.http.bean.UpdateResponseBean;
import com.haochuan.core.util.ToolsUtil;
import com.yanzhenjie.nohttp.Headers;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.download.DownloadRequest;
import com.yanzhenjie.nohttp.download.SimpleDownloadListener;

import java.io.File;


public class UpgradeDialog extends DialogFragment implements View.OnClickListener {

    public static final String TAG = "UpgradeDialog";
    public static final String RESPONSE_DATA_KEY = "response_data_key";
    private ImageButton mClose;
    private TextView tvTitle;
    private ProgressBar mProgress;
    private Button btnOk;
    private UpdateResponseBean responseBean;

    private static final int UPDATE_PROGRESS = 0x120;
    private static final int UPDATE_TITLE = 0x121;

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATE_PROGRESS:
                    if (mProgress != null) {
                        mProgress.setVisibility(View.VISIBLE);
                        if (mProgress.getProgress() >= 100) {
                            mProgress.setProgress(100);
                        } else {
                            mProgress.setProgress(msg.arg1);
                        }
                    }
                    break;
                case UPDATE_TITLE:
                    downLoad((String)msg.obj);
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
            responseBean = (UpdateResponseBean) bundle.getSerializable(RESPONSE_DATA_KEY);
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
        btnOk = view.findViewById(R.id.btn_ok);
        mClose.setOnClickListener(this);
        btnOk.setOnClickListener(this);
        dialog.setContentView(view);
        //关闭按钮获取焦点
        btnOk.setFocusable(true);
        return dialog;
    }

    private void changeProgress(int progressValue) {
        Message message = Message.obtain();
        message.what = UPDATE_PROGRESS;
        message.arg1 = progressValue;
        mHandler.sendMessage(message);
    }

    private void downLoad(String title) {
        if (mProgress == null) {
            return;
        }
        if (btnOk == null) {
            return;
        }
        if (tvTitle == null) {
            return;
        }
        if (getActivity() == null) {
            showToast("出意外了,请重启试试");
            return;
        }
        if (responseBean == null) {
            showToast("下载失败,请重启试试");
        }
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            //判断内存是否可用
            showToast("当前内存不可用,请检查");
            return;
        }
        mProgress.setVisibility(View.VISIBLE);
        btnOk.setVisibility(View.GONE);
        tvTitle.setText(title);
        //剩余内存和安装包大小作比较,+1是为了防止单位换算的误差
        if ((ToolsUtil.getAvailableMemory(getActivity()) / 1024d / 1024d)
                < responseBean.getData().getApk_size() + 1) {
            showToast("剩余内存不足");
            return;
        }
        String folder = Environment.getExternalStorageDirectory().getAbsolutePath()
                + File.separator + "apk";
        String url = responseBean.getData().getApk_url();
        String fileName = getFileNameFromUrl(url);
        File downFile = new File(folder + File.separator + fileName);
        Logger.d("downFile:" + folder + File.separator + fileName);
        //删掉旧包,保证每次都下最新的包
        if (downFile.exists()) {
            downFile.delete();
        }
        DownloadRequest downloadRequest = new DownloadRequest(url, RequestMethod.GET, folder,
                fileName, true, true);
        DownloadServer.getInstance().download(120, downloadRequest, new SimpleDownloadListener() {

            @Override
            public void onProgress(int what, int progress, long fileCount, long speed) {
                super.onProgress(what, progress, fileCount, speed);
                Logger.d("onProgress:" + progress);
                changeProgress(progress);
            }

            @Override
            public void onFinish(int what, String filePath) {
                super.onFinish(what, filePath);
                Logger.d("onFinish:" + filePath);
                //下载好后安装APK
                if (getActivity() != null) {
                    ToolsUtil.installApk(getActivity(), filePath);
                } else {
                    showToast("出意外了,请重启试试");
                }
                dismiss();
            }
        });
    }

    private String getFileNameFromUrl(String url) {
        String[] splitArray = url.split("/");
        return splitArray[splitArray.length - 1];
    }

    private void showToast(String text) {
        if (getActivity() != null) {
            Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.close) {
            dismiss();
        } else if (v.getId() == R.id.btn_ok) {
            Message message = Message.obtain();
            message.what = UPDATE_TITLE;
            message.obj = "检测到更新,下载中...";
            mHandler.sendMessage(message);
        }
    }

    @Override
    public void onDestroyView() {
        DownloadServer.getInstance().stop();
        super.onDestroyView();
    }
}