package ir.bbs.bbs.classes;

import android.app.Application;

public class BaseActivity extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        TypefaceUtil.overrideFont(getApplicationContext(),
                "SERIF",
                "fonts/vazir.ttf");
        // font from assets: "assets/fonts/Roboto-Regular.ttf
    }
}
