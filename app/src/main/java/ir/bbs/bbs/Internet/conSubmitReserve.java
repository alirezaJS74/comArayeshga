package ir.bbs.bbs.Internet;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class conSubmitReserve {

    private Context context;
    private String url;
    private String userId;
    private String reservType = "";
    private String address = "";
    private String addressId = "";
    private String rangeTime;
    private String date;
    private String err = "ثبت رزرو با مشکل مواجه شد";


    public conSubmitReserve(Context context , String url , String userId , String reservType , String address , String addressId , String rangeTime , String date){
        this.context = context;
        this.url = url;
        this.userId = userId;
        this.reservType = reservType;
        this.address = address;
        this.addressId = addressId;
        this.rangeTime = rangeTime;
        this.date = date;
    }

    public void SubmitReservs(final submitReservsData getReservsData){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        getReservsData.onSubmitReservsData(getSubmitReservData(response));
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        getReservsData.onSubmitReservsData(err);
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                if (reservType.equals("salon")){
                    Map<String,String> params = new HashMap<String, String>();
                    params.put("userId" , userId);
                    params.put("date" , date);
                    params.put("rangeTime" , rangeTime);
                    params.put("reservType" , reservType);
                    return params;
                }else {
                    Map<String,String> params = new HashMap<String, String>();
                    params.put("userId" , userId);
                    params.put("date" , date);
                    params.put("rangeTime" , rangeTime);
                    params.put("reservType" , reservType);
                    params.put(address.isEmpty() ? "addressId" : "address" , address.isEmpty() ? addressId : address);
                    return params;
                }

            }

        };

        Volley.newRequestQueue(context).add(stringRequest);
    }


    private String getSubmitReservData(String response){

        String msg = "";

        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject jsonObjectResult = new JSONObject(jsonObject.getString("result"));

            if(jsonObjectResult.get("status").equals("ok")){
                msg = jsonObjectResult.getString("message");
                return msg;
            }else {
                msg = jsonObjectResult.getString("message");
                return msg;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return err;
        }
    }





    public interface submitReservsData {
        void onSubmitReservsData(String response);
    }



}
