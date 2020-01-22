package ir.bbs.bbs;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.transition.Explode;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myloadingbutton.MyLoadingButton;
import com.google.android.material.textfield.TextInputLayout;

import ir.bbs.bbs.Internet.conChangePass;
import ir.bbs.bbs.Internet.conEditUser;
import ir.bbs.bbs.Internet.conRegister;
import ir.bbs.bbs.classes.CheckInternet;
import ir.bbs.bbs.classes.EnglishNumber;
import ir.bbs.bbs.classes.ShowMessage;

public class Activity_RegisterEdit_BBS extends AppCompatActivity {

    private EditText edtFirstName, edtLastName, edtPhone, edtPass, edtPassRe;
    private TextInputLayout TIedtPass, TIedtPassRe;
    private CheckBox chkChangePass;
    private MyLoadingButton btnRegister;
    private LinearLayout linearMain;

    private Context context = this;
    private String url = "";
    private SharedPreferences prefs;
    private String MY_PREFS_NAME = "arayeshgah";
    private int MODE_PRIVATE = 0;
    private String userId = "";
    private String firstName = "";
    private String lastName = "";
    private String phone = "";
    private boolean changePass = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_edit_bbs);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            explode();

        prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);

        userId = prefs.getString("userId", "");
        firstName = prefs.getString("firstName", "");
        lastName = prefs.getString("lastName", "");
        phone = prefs.getString("phone", "");

        init();

        final String flagEdit = getIntent().getExtras().getString("flagEdit", "");

        chkChangePass.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    changePass = true;
                    edtFirstName.setEnabled(false);
                    edtLastName.setEnabled(false);
                    TIedtPass.setVisibility(View.VISIBLE);
                    TIedtPassRe.setVisibility(View.VISIBLE);
                    btnRegister.setButtonLabel("تغییر رمزعبور");
                } else {
                    changePass = false;
                    edtFirstName.setEnabled(true);
                    edtLastName.setEnabled(true);
                    TIedtPass.setVisibility(View.GONE);
                    TIedtPassRe.setVisibility(View.GONE);
                    btnRegister.setButtonLabel("ویرایش اطلاعات");
                }
            }
        });
        btnRegister.setButtonLabel("ویرایش اطلاعات");
        if (flagEdit.equals("editProfile")) {
            edtPhone.setEnabled(false);
            edtFirstName.setText(firstName);
            edtLastName.setText(lastName);
            edtPhone.setText(phone);
            TIedtPass.setVisibility(View.GONE);
            TIedtPassRe.setVisibility(View.GONE);
            btnRegister.setButtonLabel("ویرایش اطلاعات");
        } else {
            edtPhone.setEnabled(true);
        }


        btnRegister.setMyButtonClickListener(new MyLoadingButton.MyLoadingButtonClick() {
            @Override
            public void onMyLoadingButtonClick() {

                if (new CheckInternet(context).CheckNetworkConnection()) {

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            String firstName = EnglishNumber.convert(edtFirstName.getText().toString());
                            String lastName = EnglishNumber.convert(edtLastName.getText().toString());
                            String phone = EnglishNumber.convert(edtPhone.getText().toString());
                            String password = EnglishNumber.convert(edtPass.getText().toString());
                            String passwordRe = EnglishNumber.convert(edtPassRe.getText().toString());
                            firstName = firstName.replace(" ", "");
                            lastName = lastName.replace(" ", "");
                            phone = phone.replace(" ", "");
                            password = password.replace(" ", "");
                            passwordRe = passwordRe.replace(" ", "");

                            if (firstName.equals("") || lastName.equals("") || phone.equals("") || password.equals("") || passwordRe.equals("")) {
                                new ShowMessage(context).ShowMessage_SnackBar(linearMain, "لطفا تمام فیلد های مورد نظر را پر کنید");
                                btnRegister.showErrorButton();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        btnRegister.showNormalButton();
                                    }
                                }, 1000);
                            } else if (phone.length() != 11) {
                                new ShowMessage(context).ShowMessage_SnackBar(linearMain, "اندازه شماره تلفن همراه باید 11 رقم باشد");
                                btnRegister.showErrorButton();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        btnRegister.showNormalButton();
                                    }
                                }, 1000);
                            } else if (!password.equals(passwordRe)) {
                                new ShowMessage(context).ShowMessage_SnackBar(linearMain, "پسورد ها شبیه به هم نیستن");
                                btnRegister.showErrorButton();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        btnRegister.showNormalButton();
                                    }
                                }, 1000);
                            } else {
                                if (flagEdit.equals("editProfile") && !changePass) {
                                    editUser();
                                } else if (changePass) {
                                    changePass();
                                } else {
                                    registerUser();
                                }
                            }
                        }
                    }, 1000);

                } else
                    new ShowMessage(context).ShowMessage_SnackBar_NoNet(linearMain);

            }
        });
        if (flagEdit.equals("register")) {
            chkChangePass.setVisibility(View.GONE);
            btnRegister.setButtonLabel("ثبت نام");
        }


    }

    private void init() {
        edtFirstName = findViewById(R.id.edtFirstName);
        edtLastName = findViewById(R.id.edtLastName);
        edtPhone = findViewById(R.id.edtPhone);
        edtPass = findViewById(R.id.edtPass);
        edtPassRe = findViewById(R.id.edtPassRe);
        TIedtPass = findViewById(R.id.TIedtPass);
        TIedtPassRe = findViewById(R.id.TIedtPassRe);
        btnRegister = findViewById(R.id.btnRegister);
        chkChangePass = findViewById(R.id.chkChangePass);
        linearMain = findViewById(R.id.linearMain);
    }

    private void registerUser() {
        String firstName = EnglishNumber.convert(edtFirstName.getText().toString());
        String lastName = EnglishNumber.convert(edtLastName.getText().toString());
        String phone = EnglishNumber.convert(edtPhone.getText().toString());
        String password = EnglishNumber.convert(edtPass.getText().toString());

        url = getResources().getString(R.string.registerUrl);
        conRegister register = new conRegister(context, url, firstName, lastName, phone, password);

        register.register(new conRegister.UserRegisterData() {
            @Override
            public void onUserRegisterData(String response) {
                if (response.equals("ثبت نام شما با موفقیت انجام شد")) {
//                    Intent intent = new Intent(context, Activity_Main_BBS.class);
//                    startActivity(intent);
                    Toast.makeText(context, "" + response, Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    btnRegister.showErrorButton();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            btnRegister.showNormalButton();
                        }
                    }, 1000);
                    new ShowMessage(context).ShowMessage_SnackBar(linearMain, response);
//                    Toast.makeText(context, response + "", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void editUser() {
        String firstName = EnglishNumber.convert(edtFirstName.getText().toString());
        String lastName = EnglishNumber.convert(edtLastName.getText().toString());


        conEditUser editUser = new conEditUser(context, getResources().getString(R.string.editUser), userId, firstName, lastName);

        editUser.editUser(new conEditUser.EditUserData() {
            @Override
            public void onEditUserData(String response) {
                if (response.equals("ویرایش اطلاعت با موفقیت انجام شد")) {
                    Toast.makeText(context, response + "", Toast.LENGTH_SHORT).show();
                    finish();
                    Intent intent = new Intent(context, Activity_Main_BBS.class);
                    startActivity(intent);
                } else {
                    btnRegister.showErrorButton();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            btnRegister.showNormalButton();
                        }
                    }, 1000);
                    new ShowMessage(context).ShowMessage_SnackBar(linearMain, response);
//                    Toast.makeText(context, response + "", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void changePass() {
        String password = EnglishNumber.convert(edtPass.getText().toString());

        conChangePass changePass = new conChangePass(context, getResources().getString(R.string.changePass), userId, password);
        changePass.ChangePass(new conChangePass.ChangePassData() {
            @Override
            public void onChangePassData(Boolean response) {
                if (response) {
                    Toast.makeText(context, "رمزعبور شما با موفقیت تغییر کرد لطفا دوباره وارد شوید", Toast.LENGTH_SHORT).show();
                    SharedPreferences.Editor editor = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                    editor.putString("userId", "");
                    editor.putString("firstName", "");
                    editor.putString("lastName", "");
                    editor.putString("phone", "");
                    editor.apply();
                    finish();
                    Intent intent = new Intent(context, Activity_Login_BBS.class);
                    startActivity(intent);
                } else {
                    btnRegister.showErrorButton();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            btnRegister.showNormalButton();
                        }
                    }, 1000);
                    new ShowMessage(context).ShowMessage_SnackBar(linearMain, "تغییر رمزعبور با مشکل مواجه شد");
//                    Toast.makeText(context, "تغییر رمز عبور با مشکل مواجه شد", Toast.LENGTH_SHORT).show();
                }
            }
        });

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
