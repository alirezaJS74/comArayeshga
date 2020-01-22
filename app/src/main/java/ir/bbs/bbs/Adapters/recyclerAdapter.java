package ir.bbs.bbs.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Calendar;
import java.util.List;

import ir.bbs.bbs.R;
import ir.bbs.bbs.classes.CalendarTool;
import ir.bbs.bbs.models.ModReservs;


public class recyclerAdapter extends RecyclerView.Adapter<recyclerAdapter.ViewHolder> {

    private Context context;
    private List<ModReservs> data;
    int lastPosition = -1;

    public recyclerAdapter(Context context, List<ModReservs> data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_row_reserves, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {


        Animation animation = AnimationUtils.loadAnimation(context, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        holder.itemView.startAnimation(animation);
        lastPosition = position;

        if (data.get(position).reservType.equals("home"))
            holder.txtSR_Place.setText("محل: خونه");
        else
            holder.txtSR_Place.setText("محل: سالن");


        String[] time = data.get(position).date.split("-");

        CalendarTool tool = new CalendarTool();
        tool.setIranianDate(Integer.parseInt(time[0]), Integer.parseInt(time[1]), Integer.parseInt(time[2]));

        holder.txtSR_DateTime.setText("تاریخ و زمان: " + tool.getIranianDate() + "  " + data.get(position).rangeTime);

        if (data.get(position).reservStatus.equals("لغو شده"))
            holder.imgSR_Status.setImageResource(R.drawable.icon_warning);
        else
            holder.imgSR_Status.setImageResource(R.drawable.icon_ok_green);

    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtSR_Place;
        TextView txtSR_DateTime;
        ImageView imgSR_Status;

        ViewHolder(View view) {
            super(view);
            txtSR_Place = view.findViewById(R.id.txtSR_Place);
            txtSR_DateTime = view.findViewById(R.id.txtSR_DateTime);
            imgSR_Status = view.findViewById(R.id.imgSR_Status);
        }
    }
}