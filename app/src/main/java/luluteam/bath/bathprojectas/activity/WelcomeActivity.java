package luluteam.bath.bathprojectas.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import luluteam.bath.bathprojectas.R;
import luluteam.bath.bathprojectas.tools.JumpHelper;

/**
 * 仅仅作为显示欢迎页面
 */
public class WelcomeActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcom);

        // 避免从桌面启动程序后，会重新实例化入口类的activity
        //bug现象以及原因：  https://www.cnblogs.com/net168/p/5722752.html
        if (!this.isTaskRoot()) {
            Intent intent = getIntent();
            if (intent != null) {
                String action = intent.getAction();
                if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && Intent.ACTION_MAIN.equals(action)) {
                    finish();
                    return;
                }
            }
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                JumpHelper.gotoLoginActivity(context, false);
                finish();
            }
        }, 1000);
    }
}
