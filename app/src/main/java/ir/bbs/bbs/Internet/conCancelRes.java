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

public class conCancelRes {

    private Context context;
    private String url;
    private String reserveId;


    public conCancelRes(Context context , String url , String reserveId){
        this.context = context;
        this.url = url;
        this.reserveId = reserveId;
    }


    public void CancelRes(final CancelResData CancelResData){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        CancelResData.onCancelResData(getCancelResData(response));
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        CancelResData.onCancelResData(false);
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("reserveId" , reserveId);
                return params;
            }

        };

        Volley.newRequestQueue(context).add(stringRequest);
    }


    private Boolean getCancelResData(String response){

        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject jsonObjectResult = new JSONObject(jsonObject.getString("result"));

            if(jsonObjectResult.get("status").equals("ok")){
                return true;
            }

            return false;

        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }





    public interface CancelResData {
        void onCancelResData(Boolean response);
    }



}
