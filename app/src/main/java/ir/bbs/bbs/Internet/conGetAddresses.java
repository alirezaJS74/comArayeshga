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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ir.bbs.bbs.models.ModAddress;

public class conGetAddresses {

    private Context context;
    private String url;
    private String userId;


    public conGetAddresses(Context context , String url , String userId){
        this.context = context;
        this.url = url;
        this.userId = userId;
    }


    public void GetAddresses(final GetAddressesData GetAddressesData){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        GetAddressesData.onGetAddressesData(getCancelResData(response));
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, error.getMessage()+"", Toast.LENGTH_SHORT).show();
                        GetAddressesData.onGetAddressesData(null);
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("userId" , userId);
                return params;
            }

        };

        Volley.newRequestQueue(context).add(stringRequest);
    }


    private List<ModAddress> getCancelResData(String response){

        List<ModAddress> addressList = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject jsonObjectResult = new JSONObject(jsonObject.getString("result"));
            JSONArray jsonArrayData = jsonObjectResult.getJSONArray("data");

            if(jsonObjectResult.get("status").equals("ok")){

                for (int i = 0 ; i < jsonArrayData.length() ; i++){
                    JSONObject object = jsonArrayData.getJSONObject(i);

                    ModAddress data = new ModAddress();
                    data.addressId = object.getString("_id");
                    data.address = object.getString("address");

                    addressList.add(data);
                }

                return addressList;
            }else {
                return null;
            }


        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }





    public interface GetAddressesData {
        void onGetAddressesData(List<ModAddress> response);
    }



}
