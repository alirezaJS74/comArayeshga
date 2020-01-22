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

public class conChangePass {

    private Context context;
    private String url;
    private String userId;
    private String password;


    public conChangePass(Context context , String url , String userId , String password){
        this.context = context;
        this.url = url;
        this.userId = userId;
        this.password = password;
    }


    public void ChangePass(final ChangePassData changePassData){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        changePassData.onChangePassData(ChangePassData(response));
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        changePassData.onChangePassData(false);
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("userId" , userId);
                params.put("password" , password);
                return params;
            }

        };

        Volley.newRequestQueue(context).add(stringRequest);
    }


    private Boolean ChangePassData(String response){

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





    public interface ChangePassData {
        void onChangePassData(Boolean response);
    }



}
