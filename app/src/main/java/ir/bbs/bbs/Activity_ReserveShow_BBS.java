package ir.bbs.bbs;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.transition.Explode;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ir.bbs.bbs.Adapters.recyclerAdapter;
import ir.bbs.bbs.Internet.conGetReservs;
import ir.bbs.bbs.models.ModReservs;

public class Activity_ReserveShow_BBS extends AppCompatActivity {
    private RecyclerView rycShowReserves;
    private TextView txtNoData;

    private Context context = this;
    private SharedPreferences prefs;
    private String MY_PREFS_NAME = "arayeshgah";
    private int MODE_PRIVATE = 0;
    private String userId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserve_show_bbs);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            explode();

        prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        userId = prefs.getString("userId", "");

        init();

        conGetReservs getReservs = new conGetReservs(context, getResources().getString(R.string.getReservs), userId);
        getReservs.GetReservs(new conGetReservs.GetReservsData() {
            @Override
            public void onGetReservsData(List<ModReservs> response) {
                if (response == null || response.size() == 0) {
                    txtNoData.setVisibility(View.VISIBLE);
                } else {
                    recyclerAdapter adapter = new recyclerAdapter(context, response);
                    rycShowReserves.setAdapter(adapter);
                    rycShowReserves.setLayoutManager(new LinearLayoutManager(context));
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

    private void init() {
        rycShowReserves = findViewById(R.id.rycShowReserves);
        txtNoData = findViewById(R.id.txtNoData);
    }
}
