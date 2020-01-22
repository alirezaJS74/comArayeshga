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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myloadingbutton.MyLoadingButton;
import com.google.android.material.textfield.TextInputLayout;
import com.mohamadamin.persianmaterialdatetimepicker.date.DatePickerDialog;
import com.mohamadamin.persianmaterialdatetimepicker.utils.PersianCalendar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import ir.bbs.bbs.Internet.conGetAddresses;
import ir.bbs.bbs.Internet.conRangeTime;
import ir.bbs.bbs.Internet.conSubmitReserve;
import ir.bbs.bbs.classes.CalendarTool;
import ir.bbs.bbs.classes.CheckInternet;
import ir.bbs.bbs.classes.EnglishNumber;
import ir.bbs.bbs.classes.ShowMessage;
import ir.bbs.bbs.models.ModAddress;
import ir.bbs.bbs.models.ModRangeTime;

public class Activity_Reserve_BBS extends
        AppCompatActivity implements View.OnClickListener {

    private TextView txtChooseDate, txtChooseDateHome,
            txtShowLastAdd, txtChooseAddress, txtLastAddress;
    private EditText edtNewAdd;
    private AutoCompleteTextView atcSearchAdd;
    private CheckBox chkNewAdd;
    private LinearLayout linerSalon, linerHome;
    private Spinner spnRangeTime, spnRangeTimeHome, spinnerChooseAddress;
    private MyLoadingButton btnSubmitReserve;
    private ImageView imgBarber;
    private ImageView imgHome;
    private TextInputLayout TIatcSearchAdd;
    private TextInputLayout TIedtNewAdd;
    private LinearLayout linerMain;

    private Context context = this;
    private SharedPreferences prefs;
    private String MY_PREFS_NAME = "arayeshgah";
    private int MODE_PRIVATE = 0;
    private String userId = "";
    private String itemSelect = "";
    private String itemSelectId = "";
    private String addressSelectId = "";
    private String reservType = "salon";
    private String dateMiladi = "";
    private boolean newAddress = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserve_bbs);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            explode();

        prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        userId = prefs.getString("userId", "");

        init();

        conGetAddresses getAddresses = new conGetAddresses(context, getResources().getString(R.string.getAddresses), userId);
        getAddresses.GetAddresses(new conGetAddresses.GetAddressesData() {
            @Override
            public void onGetAddressesData(final List<ModAddress> response) {
                if (response.size() != 0) {
                    txtShowLastAdd.setText(response.get(0).address);
                    addressSelectId = response.get(0).addressId;

                    List<String> data = new ArrayList<>();
                    for (int o = 0; o < response.size(); o++)
                        data.add(response.get(o).address);

                    ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(
                            context, R.layout.spinner_textview_alone, data);
                    dataAdapter.setDropDownViewResource(
                            android.R.layout.simple_spinner_dropdown_item);
                    spinnerChooseAddress.setAdapter(dataAdapter);

                    spinnerChooseAddress.setOnItemSelectedListener(
                            new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                    for (ModAddress b : response) {
                                        if (b.address.equals(adapterView.getItemAtPosition(i).toString())) {
                                            addressSelectId = b.addressId;
                                        }
                                    }
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> adapterView) {

                                }
                            });

                    setDataForAutoComp(response);

                } else {
                    spinnerChooseAddress.setVisibility(View.GONE);
                    txtLastAddress.setVisibility(View.GONE);
                }

            }
        });

        conRangeTime rangeTime = new conRangeTime(context, getResources().getString(R.string.getRangeTime));
        rangeTime.rangeTime(new conRangeTime.RangeTimeData() {
            @Override
            public void onRangeTimeData(List<ModRangeTime> response) {
                setDataForSpinner(response);
            }
        });

        chkNewAdd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                newAddress = b;
                if (b) {
                    spinnerChooseAddress.setVisibility(View.GONE);
                    TIedtNewAdd.setVisibility(View.VISIBLE);
                } else {
                    spinnerChooseAddress.setVisibility(View.VISIBLE);
                    TIedtNewAdd.setVisibility(View.GONE);
                }
            }
        });


        reservType = "salon";
        linerSalon.setVisibility(View.VISIBLE);
        linerHome.setVisibility(View.GONE);
        imgBarber.setImageResource(R.drawable.barbershop);
        imgHome.setImageResource(R.drawable.home_dis);

        imgBarber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reservType = "salon";
                linerSalon.setVisibility(View.VISIBLE);
                linerHome.setVisibility(View.GONE);
                imgBarber.setImageResource(R.drawable.barbershop);
                imgHome.setImageResource(R.drawable.home_dis);

            }
        });

        imgHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reservType = "home";
                linerSalon.setVisibility(View.GONE);
                linerHome.setVisibility(View.VISIBLE);

                imgHome.setImageResource(R.drawable.home);
                imgBarber.setImageResource(R.drawable.barbershop_dis);


            }
        });

        txtChooseDate.setOnClickListener(this);
        txtChooseDateHome.setOnClickListener(this);

        btnSubmitReserve.setMyButtonClickListener(new MyLoadingButton.MyLoadingButtonClick() {
            @Override
            public void onMyLoadingButtonClick() {

                if (dateMiladi.equals("")) {
                    new ShowMessage(context).ShowMessage_SnackBar(linerMain, "لطفا تمام فیلد های مورد نظر را پر کنید");
                    btnSubmitReserve.showNormalButton();
                } else if (new CheckInternet(context).CheckNetworkConnection()) {
                    btnSubmitReserve.showLoadingButton();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            Calendar rightNow = Calendar.getInstance();
                            CalendarTool tool = new CalendarTool();
                            rightNow.setTimeZone(TimeZone.getTimeZone("GMT+3:30"));
                            int currentHourIn24Format = rightNow.get(Calendar.HOUR_OF_DAY);
                            int currentMin = rightNow.get(Calendar.MINUTE);
                            int chooseTime = Integer.parseInt(itemSelect.substring(0, 2).trim());

                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                            try {
                                Date chooseDate = format.parse(dateMiladi);
                                Date currentDate = format.parse(tool.getGregorianDate());

                                if (chooseTime == currentHourIn24Format && currentMin < 45 && chooseDate.equals(currentDate)) {
                                    submitReserve();
                                } else if (chooseTime > currentHourIn24Format && chooseDate.equals(currentDate)) {
                                    submitReserve();
                                } else if (chooseDate.after(currentDate)) {
                                    submitReserve();
                                } else {
                                    new ShowMessage(context).ShowMessage_SnackBar(linerMain, "بازه زمانی انتخابی شما درست نمی باشد");
                                    btnSubmitReserve.showNormalButton();
                                }

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                        }
                    }, 1000);
                } else {
                    new ShowMessage(context).ShowMessage_SnackBar_NoNet(linerMain);
                    btnSubmitReserve.showNormalButton();
                }
            }

        });


    }

    private void submitReserve() {
        if (reservType.equals("salon")) {
            reserve("", "");
        } else {
            if (newAddress) {
                String address = EnglishNumber.convert(edtNewAdd.getText().toString());
                if (address.equals("")) {
                    new ShowMessage(context).ShowMessage_SnackBar(linerMain, "لطفا تمام فیلد های مورد نظر را پر کنید");
                    btnSubmitReserve.showNormalButton();
                } else
                    reserve(address, "");
            } else {
                reserve("", addressSelectId);
            }
        }
    }

    private void reserve(String address, String addressId) {
        conSubmitReserve submitReserve = new conSubmitReserve(context, getResources().getString(R.string.submitReserve), userId, reservType, address, addressId, itemSelectId, dateMiladi);
        submitReserve.SubmitReservs(new conSubmitReserve.submitReservsData() {
            @Override
            public void onSubmitReservsData(String response) {
                if (response.equals("درخواست با موفقیت ثبت شد")) {
                    Toast.makeText(context, response, Toast.LENGTH_SHORT).show();
                    finish();
                    Intent intent = new Intent(context, Activity_Main_BBS.class);
                    startActivity(intent);
                } else {
                    new ShowMessage(context).ShowMessage_SnackBar(linerMain, response);
                    btnSubmitReserve.showNormalButton();
                }
            }
        });
    }

    private void chooseDate() {
        PersianCalendar persianCalendar = new PersianCalendar();
        persianCalendar.setTimeZone(TimeZone.getTimeZone("GMT+3:30"));
        DatePickerDialog datePickerDialog = DatePickerDialog
                .newInstance(new DatePickerDialog.OnDateSetListener() {
                                 @Override
                                 public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                                     String day = "";
                                     CalendarTool tool = new CalendarTool();
                                     monthOfYear++;
                                     tool.setIranianDate(year, monthOfYear, dayOfMonth);

                                     if (tool.getIranianWeekDayStr().equals("جمعه")) {
                                         txtChooseDate.setText("انتخاب تاریخ");
                                         txtChooseDateHome.setText("انتخاب تاریخ");
                                         Toast.makeText(context, "شما روز تعطیل نمی توانید ثبت رزرو انجام دهید", Toast.LENGTH_SHORT).show();
                                     } else {
                                         if (String.valueOf(tool.getGregorianDay()).length() == 1) {
                                             day = 0 + String.valueOf(tool.getGregorianDay());
                                         } else {
                                             day = tool.getGregorianDay() + "";
                                         }

                                         dateMiladi = tool.getGregorianYear() + "-" + tool.getGregorianMonth() + "-" + day + "";
                                         String dateJalali = year + "/" + monthOfYear + "/" + dayOfMonth + "";

                                         txtChooseDate.setText("تاریخ انتخابی: " + dateJalali);
                                         txtChooseDateHome.setText("تاریخ انتخابی: " + dateJalali);
                                     }
                                 }
                             }, persianCalendar.getPersianYear(),
                        persianCalendar.getPersianMonth(),
                        persianCalendar.getPersianDay());
        datePickerDialog.setMinDate(persianCalendar);
        datePickerDialog.setYearRange(1398, 1399);
        datePickerDialog.setThemeDark(true);
        datePickerDialog.show(getFragmentManager(), "");
    }

    private void setDataForSpinner(final List<ModRangeTime> res) {
        List<String> data = new ArrayList<>();

        for (final ModRangeTime a : res) {
            data.add(a.rangeTime);
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context, R.layout.spinner_textview_alone, data);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnRangeTime.setAdapter(dataAdapter);
        spnRangeTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                for (ModRangeTime b : res) {
                    if (b.rangeTime.equals(adapterView.getSelectedItem().toString())) {
                        itemSelectId = b.rangeTimeId;
                        itemSelect = b.rangeTime;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spnRangeTimeHome.setAdapter(dataAdapter);
        spnRangeTimeHome.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                for (ModRangeTime b : res) {
                    if (b.rangeTime.equals(adapterView.getSelectedItem().toString())) {
                        itemSelectId = b.rangeTimeId;
                        itemSelect = b.rangeTime;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void setDataForAutoComp(final List<ModAddress> res) {
        List<String> addresses = new ArrayList<>();

        for (ModAddress a : res) {
            addresses.add(a.address);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
                android.R.layout.select_dialog_item, addresses);
        atcSearchAdd.setAdapter(adapter);
        atcSearchAdd.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                for (ModAddress b : res) {
                    if (b.address.equals(adapterView.getItemAtPosition(i).toString())) {
                        addressSelectId = b.addressId;
                    }
                }
            }
        });
    }

    private void init() {
        txtChooseDate = findViewById(R.id.txtChooseDate);
        txtChooseDateHome = findViewById(R.id.txtChooseDateHome);
        txtShowLastAdd = findViewById(R.id.txtShowLastAdd);
        atcSearchAdd = findViewById(R.id.atcSearchAdd);
        edtNewAdd = findViewById(R.id.edtNewAdd);
        chkNewAdd = findViewById(R.id.chkNewAdd);
        linerSalon = findViewById(R.id.linerSalon);
        linerHome = findViewById(R.id.linerHome);
        spnRangeTime = findViewById(R.id.spnRangeTime);
        spnRangeTimeHome = findViewById(R.id.spnRangeTimeHome);
        btnSubmitReserve = findViewById(R.id.btnSubmitReserve);
        imgHome = findViewById(R.id.imgHome);
        imgBarber = findViewById(R.id.imgBarber);
        TIatcSearchAdd = findViewById(R.id.TIatcSearchAdd);
        TIedtNewAdd = findViewById(R.id.TIedtNewAdd);
        txtChooseAddress = findViewById(R.id.txtChooseAddress);
        spinnerChooseAddress = findViewById(R.id.spinnerChooseAddress);
        linerMain = findViewById(R.id.linerMain);
        txtLastAddress = findViewById(R.id.txtLastAddress);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.txtChooseDate || view.getId() == R.id.txtChooseDateHome) {
            chooseDate();
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