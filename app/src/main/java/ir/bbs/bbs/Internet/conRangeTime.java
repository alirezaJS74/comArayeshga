package ir.bbs.bbs.Internet;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ir.bbs.bbs.models.ModRangeTime;

public class conRangeTime {

    private Context context;
    private String url;


    public conRangeTime(Context context , String url){
        this.context = context;
        this.url = url;
    }


    public void rangeTime(final RangeTimeData rangeTimeData){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        rangeTimeData.onRangeTimeData(getRangeTimeData(response));
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, error.getMessage()+"", Toast.LENGTH_SHORT).show();
                        rangeTimeData.onRangeTimeData(null);
                    }
                });

        Volley.newRequestQueue(context).add(stringRequest);
    }


    private List<ModRangeTime> getRangeTimeData(String response){

        List<ModRangeTime> times = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject jsonObjectResult = new JSONObject(jsonObject.getString("result"));

            if(jsonObjectResult.get("status").equals("ok")){
                JSONArray jsonArrayData = new JSONArray(jsonObjectResult.getString("data"));

                for (int i = 0 ; i < jsonArrayData.length() ; i++){
                    JSONObject jsonObjectRangeTime = jsonArrayData.getJSONObject(i);

                    ModRangeTime data = new ModRangeTime();
                    data.rangeTimeId = jsonObjectRangeTime.getString("_id");
                    data.rangeTime = jsonObjectRangeTime.getString("rangeTime");

                    times.add(data);

                }

                return times;
            }

            return null;

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }





    public interface RangeTimeData {
        void onRangeTimeData(List<ModRangeTime> response);

    }



}
