package ir.bbs.bbs;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.transition.Explode;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.myloadingbutton.MyLoadingButton;
import com.google.android.material.navigation.NavigationView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ir.bbs.bbs.Internet.conCancelRes;
import ir.bbs.bbs.Internet.conGetReservs;
import ir.bbs.bbs.classes.CalendarTool;
import ir.bbs.bbs.classes.CheckInternet;
import ir.bbs.bbs.classes.ShowMessage;
import ir.bbs.bbs.models.ModReservs;

public class Activity_Main_BBS extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private TextView txtShowFullName, txtContentCurrentRes, txtShowClock, txtShowDate, txtShowProfileName, txtShowUserName;
    private MyLoadingButton btnCancelRes, btnGoReserveActivity;
    private LinearLayout linerMain;
    private LinearLayout relativeMain;
    private ImageView imgMenu;
    private ListView lstMenu;

    private Context context = this;
    private String url = "";

    private SharedPreferences prefs;
    private String MY_PREFS_NAME = "arayeshgah";
    private int MODE_PRIVATE = 0;

    private static final int Time_Between_Two_Back = 2000;
    private long TimeBackPressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_bbs);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            explode();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        init();

        prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);

        url = getResources().getString(R.string.getReservs);

        conGetReservs getReservs = new conGetReservs(context, url, prefs.getString("userId", ""), "current");
        getReservs.GetReservs(new conGetReservs.GetReservsData() {
            @Override
            public void onGetReservsData(List<ModReservs> response) {
                if (response != null) {
                    linerMain.setVisibility(View.VISIBLE);
                    btnGoReserveActivity.setVisibility(View.GONE);
                    for (final ModReservs a : response) {
                        txtShowFullName.setText(prefs.getString("firstName", "") + " " + prefs.getString("lastName", "") + " عزیز");
                        txtContentCurrentRes.setText(a.reservType.equals("home") ? "تاریخ و ساعت حضور آرایشگر در منزل شما:" : "تاریخ و ساعت حضور شما در آرایشگاه:");
                        CalendarTool tool = new CalendarTool();
                        tool.setGregorianDate(a.date);
                        try {
                            //check date reserve if that today not show btnCancelRes else show btnCancelRes ****
                            Date reservDate = new SimpleDateFormat("yyyy-MM-dd").parse(a.date);
                            if (reservDate.after(new Date())) {
                                btnCancelRes.setVisibility(View.VISIBLE);
                                btnCancelRes.setMyButtonClickListener(new MyLoadingButton.MyLoadingButtonClick() {
                                    @Override
                                    public void onMyLoadingButtonClick() {
                                        cancelRes(a.id);
                                        btnCancelRes.showNormalButton();
                                    }
                                });
                            } else {
                                btnCancelRes.setVisibility(View.GONE);
                            }

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        txtShowDate.setText(tool.getIranianDate());
                        txtShowClock.setText(a.rangeTime);
                    }
                } else {
                    linerMain.setVisibility(View.GONE);
                    btnGoReserveActivity.setVisibility(View.VISIBLE);
                }


            }
        });


        btnGoReserveActivity.setMyButtonClickListener(new MyLoadingButton.MyLoadingButtonClick() {
            @Override
            public void onMyLoadingButtonClick() {
                if (new CheckInternet(context).CheckNetworkConnection()) {
                    btnGoReserveActivity.showLoadingButton();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(context, Activity_Reserve_BBS.class);
                            startActivity(intent);
                            btnGoReserveActivity.showNormalButton();
                        }
                    }, 1000);
                } else {
                    new ShowMessage(context).ShowMessage_SnackBar_NoNet(relativeMain);
                    btnGoReserveActivity.showNormalButton();
                }

            }
        });

        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        final NavigationView navigationView = findViewById(R.id.nav_view);

        View view = navigationView.getHeaderView(0);
//        txtShowProfileName = view.findViewById(R.id.txtShowProfileName);
//        txtShowUserName = view.findViewById(R.id.txtShowUserName);
        txtShowProfileName = findViewById(R.id.txtShowProfileName);
        txtShowUserName = findViewById(R.id.txtShowUserName);
        lstMenu = findViewById(R.id.lstMenu);

        txtShowProfileName.setText(prefs.getString("firstName", "") + " " + prefs.getString("lastName", ""));
        txtShowUserName.setText(prefs.getString("phone", ""));

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);


        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);


        imgMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(GravityCompat.START);
            }
        });

        final ArrayList list = new ArrayList();
        list.add("ویرایش اطلاعات");
        list.add("درخواست ها");
        list.add("خدمات");
        list.add("وبسایت");
        list.add("درباره ما");
        list.add("خروج");

        final ArrayList<Integer> list1 = new ArrayList<>();
        list1.add(R.drawable.logo_about);
        list1.add(R.drawable.logo_about);
        list1.add(R.drawable.logo_about);
        list1.add(R.drawable.logo_about);
        list1.add(R.drawable.logo_about);
        list1.add(R.drawable.logo_about);

        ArrayAdapter adapterMenu = new ArrayAdapter(context, R.layout.row_menu_listview, list) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                convertView = getLayoutInflater().inflate(R.layout.row_menu_listview, null, false);

                TextView txtRow = convertView.findViewById(R.id.txtRow);
                ImageView imgRow = convertView.findViewById(R.id.imgRow);

                txtRow.setText(list.get(position) + "");
                imgRow.setImageResource(list1.get(position));

                return convertView;
            }
        };

        lstMenu.setAdapter(adapterMenu);


        lstMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                switch (list.get(position) + "") {
                    case "ویرایش اطلاعات":
                        Intent intent = new Intent(context, Activity_RegisterEdit_BBS.class);
                        intent.putExtra("flagEdit", "editProfile");
                        startActivity(intent);
                        break;
                    case "درخواست ها":
                        Intent intent2 = new Intent(context, Activity_ReserveShow_BBS.class);
                        startActivity(intent2);
                        break;
                    case "خدمات":
                        Toast.makeText(Activity_Main_BBS.this, "این قسمت درحال بروزرسانی می باشد", Toast.LENGTH_SHORT).show();
                        break;
                    case "وبسایت":
                        Intent intent3 = new Intent(Intent.ACTION_VIEW);
                        intent3.setData(Uri.parse("http://weblauncher.ir"));
                        startActivity(intent3);
                        break;
                    case "درباره ما":
                        startActivity(new Intent(context,Activity_AboutUs_BBS.class));
                        break;
                    case "خروج":
                        new AlertDialog.Builder(context,R.style.CustomDialogTheme)
                                .setTitle("خروج")
                                .setMessage("آیا مطمئن هستید ؟")
                                .setPositiveButton("بله", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        logOut();
                                    }
                                })
                                .setNegativeButton("خیر", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .create()
                                .show();
                        break;
                }
                drawer.closeDrawer(GravityCompat.START);

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

    private void init() {
        linerMain = findViewById(R.id.linerMain);
        txtShowFullName = findViewById(R.id.txtShowFullName);
        txtContentCurrentRes = findViewById(R.id.txtContentCurrentRes);
        txtShowClock = findViewById(R.id.txtShowClock);
        txtShowDate = findViewById(R.id.txtShowDate);
        btnCancelRes = findViewById(R.id.btnCancelRes);
        relativeMain = findViewById(R.id.relativeMain);
        btnGoReserveActivity = findViewById(R.id.btnGoReserveActivity);
        imgMenu = findViewById(R.id.imgMenu);

    }

    //cancel reserve action button
    private void cancelRes(String reserveId) {
        conCancelRes cancelRes = new conCancelRes(context, getResources().getString(R.string.cancelReserve), reserveId);
        cancelRes.CancelRes(new conCancelRes.CancelResData() {
            @Override
            public void onCancelResData(Boolean response) {
                if (response) {
                    Toast.makeText(context, "درخواست شما با موفقیت لغو شد", Toast.LENGTH_SHORT).show();
                    finish();
                    startActivity(getIntent());
                } else {
                    Toast.makeText(context, "درخواست لغو با مشکل مواجهه شد", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            Intent intent = new Intent(context, Activity_RegisterEdit_BBS.class);
            intent.putExtra("flagEdit", "editProfile");
            startActivity(intent);
        } else if (id == R.id.nav_gallery) {
            Intent intent = new Intent(context, Activity_ReserveShow_BBS.class);
            startActivity(intent);
        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {
            logOut();
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logOut() {
        SharedPreferences.Editor editor = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        editor.putString("userId", "");
        editor.putString("firstName", "");
        editor.putString("lastName", "");
        editor.putString("phone", "");
        editor.apply();
        finish();
        Intent intent = new Intent(context, Activity_Login_BBS.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (TimeBackPressed + Time_Between_Two_Back > System.currentTimeMillis()) {
                super.onBackPressed();
                return;
            } else {
                new ShowMessage(context).ShowMessage_SnackBar(relativeMain, "برای خروج دوباره کلیک کنید");
            }
            TimeBackPressed = System.currentTimeMillis();
        }
    }
}
