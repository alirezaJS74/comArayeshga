package ir.bbs.bbs;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.transition.Explode;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myloadingbutton.MyLoadingButton;

import ir.bbs.bbs.Internet.conLogin;
import ir.bbs.bbs.classes.CheckInternet;
import ir.bbs.bbs.classes.EnglishNumber;
import ir.bbs.bbs.classes.ShowMessage;

public class Activity_Login_BBS extends AppCompatActivity {

    private EditText edtPhoneLogin, edtPassLogin;
    private MyLoadingButton btnLogin;
    private MyLoadingButton btnGoRegister;
    private RelativeLayout relativeMain;

    private Context context = this;
    private String url = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_bbs);
        url = getResources().getString(R.string.loginUrl);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            explode();

        init();
        btnGoRegister.setMyButtonClickListener(new MyLoadingButton.MyLoadingButtonClick() {
            @Override
            public void onMyLoadingButtonClick() {
                Intent intent = new Intent(context, Activity_RegisterEdit_BBS.class);
                intent.putExtra("flagEdit", "register");
                startActivity(intent);
                btnGoRegister.showNormalButton();
            }
        });

        btnLogin.setMyButtonClickListener(new MyLoadingButton.MyLoadingButtonClick() {
            @Override
            public void onMyLoadingButtonClick() {

                if (new CheckInternet(context).CheckNetworkConnection()) {
                    btnLogin.showLoadingButton();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            login();
                        }
                    }, 1000);
                } else {
                    new ShowMessage(context).ShowMessage_SnackBar_NoNet(relativeMain);
                    btnLogin.showNormalButton();
                }
            }
        });

    }

    private void init() {
        edtPhoneLogin = findViewById(R.id.edtPhoneLogin);
        edtPassLogin = findViewById(R.id.edtPassLogin);
        btnLogin = findViewById(R.id.btnLogin);
        btnGoRegister = findViewById(R.id.btnGoRegister);
        relativeMain = findViewById(R.id.relativeMain);
    }

    private void login() {
        String phone = EnglishNumber.convert(edtPhoneLogin.getText().toString());
        String password = EnglishNumber.convert(edtPassLogin.getText().toString());

        if (phone.equals("") || password.equals("")) {
            btnLogin.showErrorButton();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    btnLogin.showNormalButton();
                }
            }, 1000);
            new ShowMessage(context).ShowMessage_SnackBar(relativeMain, "لطفا تمام فیلد های مورد نظر را پر کنید");
        } else {
            conLogin login = new conLogin(context, url, phone, password);
            login.login(new conLogin.UserLoginData() {
                @Override
                public void onUserLoginData(String response) {
                    if (response.equals("ورود با موفقیت انجام شد")) {
                        btnLogin.showDoneButton();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(context, Activity_Main_BBS.class);
                                startActivity(intent);
                                finish();
                            }
                        }, 1000);
                    } else {
                        btnLogin.showErrorButton();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                btnLogin.showNormalButton();
                            }
                        }, 1000);
                        new ShowMessage(context).ShowMessage_SnackBar(relativeMain, response);
                    }
                }
            });
        }
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
