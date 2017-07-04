package com.chen.library.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chen.library.R;
import com.chen.library.api.ApiClient;
import com.chen.library.application.BaseApplication;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;


/**
 * Created by chenzhimin on 2015/12/15 0015.
 */
public class BaseActivity extends AppCompatActivity {
    protected BaseApplication mApp;
    protected View loadingView;
    protected TextView progressText;
    public boolean isDestroyed;

    public Context getContext() {
        return this;
    }

    private CompositeSubscription mCompositeSubscription;


    public CompositeSubscription getCompositeSubscription() {
        if (this.mCompositeSubscription == null) {
            this.mCompositeSubscription = new CompositeSubscription();
        }

        return this.mCompositeSubscription;
    }


    public void addSubscription(Subscription s) {
        if (this.mCompositeSubscription == null) {
            this.mCompositeSubscription = new CompositeSubscription();
        }

        this.mCompositeSubscription.add(s);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mApp = BaseApplication.getInstance();
        isDestroyed = false;
//        StatusBarUtil.setColor(this, getResources().getColor(R.color.main_title), 0);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        final View backView = findViewById(R.id.back_btn);
        if (backView != null) {
            if (canBack()) {
                backView.setVisibility(View.VISIBLE);
                backView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                    }
                });
            } else backView.setVisibility(View.GONE);
        }
    }


    public void showLoadingView() {
        if (loadingView == null) {
            loadingView = LayoutInflater.from(this).inflate(R.layout.loading_view, null);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams
                    .MATCH_PARENT);
            params.addRule(RelativeLayout.CENTER_IN_PARENT);
            AVLoadingIndicatorView progressView = (AVLoadingIndicatorView) loadingView.findViewById(R.id.loadprograss);
            progressText = (TextView) loadingView.findViewById(R.id.progress_text);
            progressView.setIndicatorColor(getResources().getColor(R.color.white));
            addContentView(loadingView, params);
            loadingView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
        }
        loadingView.setVisibility(View.VISIBLE);
    }


    public void showLoadingView(String text) {
        if (loadingView == null) {
            loadingView = LayoutInflater.from(this).inflate(R.layout.loading_view, null);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams
                    .MATCH_PARENT);
            params.addRule(RelativeLayout.CENTER_IN_PARENT);
            AVLoadingIndicatorView progressView = (AVLoadingIndicatorView) loadingView.findViewById(R.id.loadprograss);
            progressText = (TextView) loadingView.findViewById(R.id.progress_text);
            progressView.setIndicatorColor(getResources().getColor(R.color.white));
            addContentView(loadingView, params);
            loadingView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
        }
        progressText.setVisibility(View.VISIBLE);
        progressText.setText(text);
        loadingView.setVisibility(View.VISIBLE);
    }


    /**
     * this Method　must be set after {@link #setContentView(int)}
     */
    protected void setTitle(String titleName) {
        TextView textView = (TextView) findViewById(R.id.title);
        if (textView != null) {
            textView.setText(titleName);
        }
    }

    /**
     * this Method　must be set after {@link #setContentView(int)}
     */
    public void setRightText(String rightText) {
        TextView textView = (TextView) findViewById(R.id.right_text);
        if (textView != null) {
            textView.setVisibility(View.VISIBLE);
            textView.setText(rightText);
        }
    }

    protected void setRightVisible(int visible) {
        TextView textView = (TextView) findViewById(R.id.right_text);
        textView.setVisibility(visible);
    }


    /**
     * this Method　must be set after {@link #setContentView(int)}
     */
    protected void setRightImg(int resId) {
        ImageView img = (ImageView) findViewById(R.id.right_image);
        img.setVisibility(View.VISIBLE);
        if (img != null) {
            img.setImageResource(resId);
        }
    }

    public void disMissLoadingView() {
        if (loadingView != null && loadingView.getVisibility() == View.VISIBLE) {
            loadingView.setVisibility(View.GONE);
        }
    }


    public void showMsg(String msg) {
        ToastUtils.showShort(msg, Toast.LENGTH_SHORT);
    }

    public void showConfirmDialog(String msg) {
        showConfirmDialog(msg, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
    }

    public void showConfirmDialog(String msg, DialogInterface.OnClickListener onClickListener) {
        final AlertDialog alertDialog = new AlertDialog.Builder(getContext()).setMessage(msg).setPositiveButton("确认", onClickListener).create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BaseApplication.getEventBus().unregister(this);
        if (this.mCompositeSubscription != null) {
            this.mCompositeSubscription.unsubscribe();
        }
//        if (onCancelRequest()) MzApi.cancelAll(this);
//        ButterKnife.unbind(this);
        if (loadingView != null) {
            loadingView = null;
        }
        isDestroyed = true;
    }

    protected boolean canBack() {
        return true;
    }

    protected boolean onCancelRequest() {
        return true;
    }


    protected void showMsg(int resId) {
        ToastUtils.showShort(getString(resId));
    }


    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        FragmentManager fm = getSupportFragmentManager();
        int index = requestCode >> 16;
        if (index != 0) {
            index--;
            if (fm.getFragments() == null || index < 0 || index >= fm.getFragments().size()) {
                Log.w("print", "Activity result fragment index out of range: 0x" + Integer.toHexString(requestCode));
                return;
            }
            Fragment frag = fm.getFragments().get(index);
            if (frag == null || !frag.isVisible()) {
                Log.w("print", "Activity result no fragment exists for index: 0x" + Integer.toHexString(requestCode));
            } else {
                handleResult(frag, requestCode, resultCode, data);
            }
            return;
        }
        //        super.onActivityResult(requestCode,resultCode,data);
    }*/

    /**
     * 递归调用，对所有子Fragement生效
     *
     * @param frag
     * @param requestCode
     * @param resultCode
     * @param data
     */
    private void handleResult(Fragment frag, int requestCode, int resultCode, Intent data) {
        frag.onActivityResult(requestCode & 0xffff, resultCode, data);
        List<Fragment> frags = frag.getChildFragmentManager().getFragments();
        if (frags != null) {
            for (Fragment f : frags) {
                if (f != null && f.isVisible()) {
                    LogUtils.w("print", "requestCode :" + requestCode + " ,  resultCode :" + resultCode);
                    handleResult(f, requestCode, resultCode, data);
                }
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public <T> Subscription flatResult(Observable<ApiClient.Result<T>> observable, Subscriber<T> subscriber) {
        final Subscription subscribe = ApiClient.flatResult(observable).subscribe(subscriber);
        addSubscription(subscribe);
        return subscribe;
    }


    public abstract class subScriberNetWorkActivity<T> extends ApiClient.SubscriberNetWorkWithString<T> {
        @Override
        public void onErrorM(String message, Throwable e) {
//            if (e instanceof ApiClient.ApiException) showErrorLayout();
            disMissLoadingView();
            showMsg(message);
        }
    }

    public interface OnRetryListener {
        void onRetryClick();
    }
}
