package com.test.viewsave;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

/**
 * Created by zhuangyufeng on 2018/9/21.
 */

public class BaseActivity extends AppCompatActivity {

    public void entryActivity(Class<?> cls) {
        Intent intent = new Intent(FrameworkUtil.getContext(), cls);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        FrameworkUtil.getContext().startActivity(intent);
    }

    protected void showToast(boolean isSuccess) {
        Toast.makeText(FrameworkUtil.getContext(), isSuccess ? "保存成功" : "保存失败", Toast.LENGTH_LONG).show();
    }
}
