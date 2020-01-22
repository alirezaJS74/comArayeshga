package ir.bbs.bbs.Internet;

import android.content.Context;
import android.content.SharedPreferences;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class conLogin {

    private Context context;
    private String url;
    private String phone;
    private String password;
    private String MY_PREFS_NAME = "arayeshgah";
    private int MODE_PRIVATE = 0;


    public conLogin(Context context , String url ,  String phone , String password){
        this.context = context;
        this.url = url;
        this.phone = phone;
        this.password = password;
    }


    public void login(final UserLoginData userLoginData){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        userLoginData.onUserLoginData(getUserData(response));
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        userLoginData.onUserLoginData("ورود با مشکل مواجه شد");
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("phone" , phone);
                params.put("password" , password);
                return params;
            }

        };

        Volley.newRequestQueue(context).add(stringRequest);
    }


    private String getUserData(String response){

        SharedPreferences.Editor editor = context.getSharedPreferences(MY_PREFS_NAME , MODE_PRIVATE).edit();

        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject jsonObjectResult = new JSONObject(jsonObject.getString("result"));

            if(jsonObjectResult.get("status").equals("ok")){
                JSONObject jsonObjectData = new JSONObject(jsonObjectResult.getString("data"));

                String id = jsonObjectData.getString("id");
                String firstName = jsonObjectData.getString("firstName");
                String lastName = jsonObjectData.getString("lastName");
                String phone = jsonObjectData.getString("phone");

                editor.putString("userId" , id);
                editor.putString("firstName" , firstName);
                editor.putString("lastName" , lastName);
                editor.putString("phone" , phone);

                editor.apply();


                return jsonObjectResult.getString("message");
            }

            return jsonObjectResult.getString("message");

        } catch (JSONException e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }





    public interface UserLoginData {
        void onUserLoginData(String response);
    }



}
