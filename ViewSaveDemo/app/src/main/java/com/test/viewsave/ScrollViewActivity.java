package com.test.viewsave;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

/**
 * Created by zhuangyufeng on 2018/9/21.
 */

public class ScrollViewActivity extends BaseActivity{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_scrollview_activity);
    }

    public void creenShot(View view){
        ScreenShotUtil.saveScreenShot(findViewById(R.id.scrollview), new OnPicSaveResult() {
            @Override
            public void onPicSaveResult(boolean isSuccess) {
                showToast(isSuccess);
            }
        });
    }
}
