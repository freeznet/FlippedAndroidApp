

package com.hkust.android.hack.flipped.ui;

import android.accounts.OperationCanceledException;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hkust.android.hack.flipped.BootstrapServiceProvider;
import com.hkust.android.hack.flipped.R;
import com.hkust.android.hack.flipped.core.ActivityMessage;
import com.hkust.android.hack.flipped.core.BootstrapService;
import com.hkust.android.hack.flipped.events.NavItemSelectedEvent;
import com.hkust.android.hack.flipped.ui.view.ExtendedListView;
import com.hkust.android.hack.flipped.ui.view.MenuRightAnimations;
import com.hkust.android.hack.flipped.util.Ln;
import com.hkust.android.hack.flipped.util.SafeAsyncTask;
import com.hkust.android.hack.flipped.util.UIUtils;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.Views;


/**
 * Initial activity for the application.
 *
 * If you need to remove the authentication from the application please see
 * {@link com.hkust.android.hack.flipped.authenticator.ApiKeyProvider#getAuthKey(android.app.Activity)}
 */
public class MainActivity extends BootstrapFragmentActivity implements ExtendedListView.OnPositionChangedListener {

    @Inject protected BootstrapServiceProvider serviceProvider;

    private boolean userHasAuthenticated = false;

    /** Called when the activity is first created. */
    private boolean areButtonsShowing;

    private ExtendedListView dataListView;
    private FrameLayout clockLayout;


    private MainActivityAdapter messageAdapter;
    private ArrayList<ActivityMessage> messages = new ArrayList<ActivityMessage>();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_activity_ui);

        Views.inject(this);

        checkAuth();

    }

    private boolean isTablet() {
        return UIUtils.isTablet(this);
    }

    @Override
    protected void onPostCreate(final Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }


    @Override
    public void onConfigurationChanged(final Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }


    private void initScreen() {
        if (userHasAuthenticated) {
            MenuRightAnimations.initOffset(MainActivity.this);

            dataListView = (ExtendedListView) findViewById(R.id.list_view);

//            setAdapterForThis();
            dataListView.setCacheColorHint(Color.TRANSPARENT);
            dataListView.setOnPositionChangedListener(this);
            clockLayout = (FrameLayout)findViewById(R.id.clock);
            // clockLayout.setLayoutChangedListener(dataListView);

            // splash.setVisibility(View.GONE);

            ActivityMessage header = new ActivityMessage();
            messages.add(header);

            messageAdapter = new MainActivityAdapter(this, messages);
            dataListView.setAdapter(messageAdapter);

            Ln.d("Foo");
        }

    }

    private void checkAuth() {
        new SafeAsyncTask<Boolean>() {

            @Override
            public Boolean call() throws Exception {
                final BootstrapService svc = serviceProvider.getService(MainActivity.this, true);
                return svc != null;
            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
                super.onException(e);
                if (e instanceof OperationCanceledException) {
                    // User cancelled the authentication process (back button, etc).
                    // Since auth could not take place, lets finish this activity.
                    finish();
                }
            }

            @Override
            protected void onSuccess(final Boolean hasAuthenticated) throws Exception {
                super.onSuccess(hasAuthenticated);
                userHasAuthenticated = true;
                initScreen();
            }
        }.execute();
    }

    private float[] computMinAndHour(int currentMinute, int currentHour) {
        float minuteRadian = 6f * currentMinute;

        float hourRadian = 360f / 12f * currentHour;

        float[] rtn = new float[2];
        rtn[0] = minuteRadian;
        rtn[1] = hourRadian;
        return rtn;
    }

    private float[] lastTime = {
            0f, 0f
    };

    private RotateAnimation[] computeAni(int min, int hour) {

        RotateAnimation[] rtnAni = new RotateAnimation[2];
        float[] timef = computMinAndHour(min, hour);
        System.out.println("min===" + timef[0] + " hour===" + timef[1]);
        // AnimationSet as = new AnimationSet(true);
        // 创建RotateAnimation对象
        // 0--图片从哪开始旋转
        // 360--图片旋转多少度
        // Animation.RELATIVE_TO_PARENT, 0f,// 定义图片旋转X轴的类型和坐标
        // Animation.RELATIVE_TO_PARENT, 0f);// 定义图片旋转Y轴的类型和坐标
        RotateAnimation ra = new RotateAnimation(lastTime[0], timef[0], Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        ra.setFillAfter(true);
        ra.setFillBefore(true);
        // 设置动画的执行时间
        ra.setDuration(800);
        // 将RotateAnimation对象添加到AnimationSet
        // as.addAnimation(ra);
        // 将动画使用到ImageView
        rtnAni[0] = ra;

        lastTime[0] = timef[0];

        // AnimationSet as2 = new AnimationSet(true);
        // 创建RotateAnimation对象
        // 0--图片从哪开始旋转
        // 360--图片旋转多少度
        // Animation.RELATIVE_TO_PARENT, 0f,// 定义图片旋转X轴的类型和坐标
        // Animation.RELATIVE_TO_PARENT, 0f);// 定义图片旋转Y轴的类型和坐标
        RotateAnimation ra2 = new RotateAnimation(lastTime[1], timef[1], Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);

        // 设置动画的执行时间
        ra2.setFillAfter(true);
        ra2.setFillBefore(true);
        ra2.setDuration(800);
        // 将RotateAnimation对象添加到AnimationSet
        // as2.addAnimation(ra2);
        // 将动画使用到ImageView
        rtnAni[1] = ra2;
        lastTime[1] = timef[1];
        return rtnAni;
    }

    @Override
    public void onPositionChanged(ExtendedListView listView, int firstVisiblePosition, View scrollBarPanel) {
        TextView datestr = ((TextView) findViewById(R.id.clock_digital_date));
        datestr.setText("上午");
        ActivityMessage msg = messages.get(firstVisiblePosition);

        int hour = msg.getHour();
        String tmpstr = "";
        if (hour > 12) {
            hour = hour - 12;
            datestr.setText("下午");
            tmpstr += " ";
        } else if (0 < hour && hour < 10) {

            tmpstr += " ";
        }
        tmpstr += hour + ":" + msg.getMin();
        ((TextView) findViewById(R.id.clock_digital_time)).setText(tmpstr);
        RotateAnimation[] tmp = computeAni(msg.getMin(),hour);

        ImageView minView = (ImageView) findViewById(R.id.clock_face_minute);
        minView.startAnimation(tmp[0]);

        ImageView hourView = (ImageView) findViewById(R.id.clock_face_hour);
        hourView.setImageResource(R.drawable.clock_hour_rotatable);
        hourView.startAnimation(tmp[1]);

        }

    @Override
    public void onScollPositionChanged(View scrollBarPanel, int top) {
        System.out.println("onScollPositionChanged======================");
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams)clockLayout.getLayoutParams();
        System.out.println("left=="+layoutParams.leftMargin+" top=="+layoutParams.topMargin+" bottom=="+layoutParams.bottomMargin+" right=="+layoutParams.rightMargin);
        layoutParams.setMargins(0, top, 0, 0);
        clockLayout.setLayoutParams(layoutParams);
    }
}
