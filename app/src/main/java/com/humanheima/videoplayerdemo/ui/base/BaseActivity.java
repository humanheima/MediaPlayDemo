package com.humanheima.videoplayerdemo.ui.base;

import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import androidx.appcompat.app.AppCompatActivity;
import com.humanheima.videoplayerdemo.R;
import com.humanheima.videoplayerdemo.ui.widget.dialog.LoadingDialog;
import com.humanheima.videoplayerdemo.util.NetWorkUtil;
import com.humanheima.videoplayerdemo.util.ScreenUtil;
import com.humanheima.videoplayerdemo.util.ToastUtil;

/**
 * Created by chenchao on 16/9/27.
 * cc@cchao.org
 */
public abstract class BaseActivity extends AppCompatActivity {

    //打开关闭弹出框
    protected final int OPEN_POP = 0;
    protected final int HIDE_POP = 1;

    private LoadingDialog loadingDialog;

    protected int screenWidth;

    protected int screenHeight;


    private boolean openFlingClose = false;

    private boolean isWifi = false;

    private GestureDetector gestureDetector = new GestureDetector(new GestureDetector.OnGestureListener() {
        @Override
        public boolean onDown(MotionEvent e) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {

        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (e2.getX() - e1.getX() > 200
                    && Math.abs(e2.getY() - e1.getY()) < Math.abs(e2.getX() - e1.getX())) {
                finish();
                return true;
            }
            return false;
        }
    });

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(bindLayout());
        screenWidth = ScreenUtil.width(this).px;
        screenHeight = ScreenUtil.height(this).px;
        initData();
        bindEvent();
    }

    /**
     * 设置是否右滑关闭
     *
     * @param openFlingClose
     */
    public void setOpenFlingClose(boolean openFlingClose) {
        this.openFlingClose = openFlingClose;
    }

    /**
     * 绑定布局文件
     *
     * @return
     */
    protected abstract int bindLayout();

    /**
     * 初始化数据
     */
    protected abstract void initData();

    /**
     * 绑定控件事件
     */
    protected abstract void bindEvent();

    public void netChanged() {
        if (NetWorkUtil.isConnected()) {
            if (NetWorkUtil.isWifi(this)) {
                isWifi = true;
            } else if (isWifi) {
                ToastUtil.Infotoast(this, getResources().getString(R.string.not_wifi));
                isWifi = false;
            }
        } else if (isWifi) {
            ToastUtil.Infotoast(this, getResources().getString(R.string.not_wifi));
            isWifi = false;
        }
    }

    protected void showLoading() {
        showLoading(null);
    }

    protected void showLoading(String content) {
        if (loadingDialog == null) {
            loadingDialog = LoadingDialog.show(this, content);
        } else {
            loadingDialog.show();
        }
    }

    protected void hideLoading() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }


    protected class MyPopWindow extends PopupWindow {

        public MyPopWindow(View contentView, int width, int height, boolean focusable) {
            super(contentView, width, height, focusable);
        }

        @Override
        public void showAtLocation(View parent, int gravity, int x, int y) {
            setWindow(OPEN_POP);
            super.showAtLocation(parent, gravity, x, y);
        }

        @Override
        public void dismiss() {
            setWindow(HIDE_POP);
            super.dismiss();
        }
    }

    /**
     * 设置弹出框出现与隐藏时背景透明度变化
     *
     * @param type
     */
    public void setWindow(int type) {
        //设置背景颜色变暗
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        if (type == OPEN_POP) {
            lp.alpha = 0.7f;
            this.getWindow().setAttributes(lp);
        } else {
            lp.alpha = 1.0f;
            this.getWindow().setAttributes(lp);
        }
    }

}
