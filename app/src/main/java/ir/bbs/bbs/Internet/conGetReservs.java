package ir.bbs.bbs.Internet;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ir.bbs.bbs.models.ModReservs;

public class conGetReservs {

    private Context context;
    private String url;
    private String userId;
    private String reservFlag = "";


    public conGetReservs(Context context , String url , String userId){
        this.context = context;
        this.url = url;
        this.userId = userId;
    }

    public conGetReservs(Context context , String url , String userId , String reservFlag){
        this.context = context;
        this.url = url;
        this.userId = userId;
        this.reservFlag = reservFlag;
    }

    public void GetReservs(final GetReservsData getReservsData){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        getReservsData.onGetReservsData(getReservData(response));
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        getReservsData.onGetReservsData(null);
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                if (reservFlag.isEmpty()){
                    Map<String,String> params = new HashMap<String, String>();
                    params.put("userId" , userId);
                    return params;
                }

                Map<String,String> params = new HashMap<String, String>();
                params.put("userId" , userId);
                params.put("reservFlag" , reservFlag);
                return params;
            }

        };

        Volley.newRequestQueue(context).add(stringRequest);
    }


    private List<ModReservs> getReservData(String response){

        List<ModReservs> lstReservData = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject jsonObjectResult = new JSONObject(jsonObject.getString("result"));

            if(jsonObjectResult.get("status").equals("ok")){
                JSONArray jsonArrayData = jsonObjectResult.getJSONArray("data");

                for (int i = 0 ; i < jsonArrayData.length() ; i++){
                    JSONObject jsonObjectData = jsonArrayData.getJSONObject(i);

                    String reservId = jsonObjectData.getString("_id");
                    String date = jsonObjectData.getString("date").substring(0 , 10);

                    String location = jsonObjectData.getString("location");

                    JSONObject jsonObjectRangeTime = jsonObjectData.getJSONObject("rangeTime");
                    String rangeTime = jsonObjectRangeTime.getString("rangeTime");

                    JSONObject jsonObjectReservType = jsonObjectData.getJSONObject("reservType");
                    String reservType = jsonObjectReservType.getString("reservType");

                    JSONObject jsonObjectReservStatus = jsonObjectData.getJSONObject("reservStatus");
                    String reservStatus = jsonObjectReservStatus.getString("status");

                    ModReservs data = new ModReservs();
                    data.id = reservId;
                    data.location = location;
                    data.reservType = reservType;
                    data.rangeTime = rangeTime;
                    data.reservStatus = reservStatus;
                    data.date = date;

                    lstReservData.add(data);


                }

                return lstReservData;

            }



        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }





    public interface GetReservsData {
        void onGetReservsData(List<ModReservs> response);
    }



}
