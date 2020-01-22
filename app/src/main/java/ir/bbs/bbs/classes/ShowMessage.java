package ir.bbs.bbs.classes;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import ir.bbs.bbs.R;

public class ShowMessage {

    private Context context;

    public ShowMessage(Context context) {
        this.context = context;
    }

    public void ShowMessage_SnackBar(Object layout, String text) {
        try {

            Snackbar snackbar = Snackbar.make((View) layout, text, Snackbar.LENGTH_LONG).setAction("بستن", new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
            View sb = snackbar.getView();
            TextView tv = sb.findViewById(R.id.snackbar_text);
            tv.setTextColor(Color.WHITE);
            sb.setBackgroundColor(context.getResources().getColor(R.color.view_color_gray));
            snackbar.show();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void ShowMessage_SnackBar_NoNet(Object layout) {
        try {

            Snackbar snackbar = Snackbar.make((View) layout, R.string.AlertCheckNetAgain, Snackbar.LENGTH_LONG).setAction("بستن", new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
            View sb = snackbar.getView();
            TextView tv = sb.findViewById(R.id.snackbar_text);
            tv.setTextColor(Color.WHITE);
            sb.setBackgroundColor(context.getResources().getColor(R.color.view_color_gray));
            snackbar.show();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
