package com.test.viewsave;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void saveViewPic(View view) {
        ScreenShotUtil.saveScreenShot(getWindow().getDecorView(), new OnPicSaveResult() {
            @Override
            public void onPicSaveResult(boolean isSuccess) {
                showToast(isSuccess);
            }
        });

    }

    public void entryScrollView(View view) {
        entryActivity(ScrollViewActivity.class);
    }

    public void entryHorizontalScrollView(View view) {
        entryActivity(HorizontalScrollViewActivity.class);

    }

    public void entryListView(View view) {

    }

    public void entryRecyclerView(View view) {

    }


}
