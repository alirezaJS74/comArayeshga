package ir.bbs.bbs;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.transition.Explode;
import android.view.WindowManager;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

public class Activity_Splash__BBS extends AppCompatActivity {

    private Context context = this;
    private SharedPreferences prefs;
    private String MY_PREFS_NAME = "arayeshgah";
    private int MODE_PRIVATE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_bbs);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            explode();

//        animationRotate = AnimationUtils.loadAnimation(context, R.anim.rotate_anim);
//        animationFade = AnimationUtils.loadAnimation(context, R.anim.fade_anim);

//        imgLogo.startAnimation(animationRotate);
//        txtLogo.startAnimation(animationFade);

        prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (prefs.getString("userId" , "").isEmpty()){
                    Intent intent = new Intent(context , Activity_Login_BBS.class);
                    startActivity(intent);
                    finish();
                }else {
                    Intent intent = new Intent(context , Activity_Main_BBS.class);
                    startActivity(intent);
                    finish();
                }
            }
        },3000);

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void explode() {

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Explode explode = new Explode();
        explode.setDuration(300);
//        explode.excludeTarget(R.id.explode_toolbar, true);
        getWindow().setEnterTransition(explode);
        getWindow().setExitTransition(explode);
        getWindow().setAllowEnterTransitionOverlap(false);
        getWindow().setAllowReturnTransitionOverlap(false);
    }


}
