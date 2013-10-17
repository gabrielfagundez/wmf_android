package com.whereismyfriend;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;

public class Comunicador {

	
	
	
	public String[] postLogin(String user, String pass) {
		
		String res_id="";
		String res_name="";
		String res_codigo="";
		
	    // Create a new HttpClient and Post Header
	    HttpClient httpclient = new DefaultHttpClient();
	    HttpPost httppost = new HttpPost("http://developmentpis.azurewebsites.net/api/Users/Login");
	    try {
	        // Add your data
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	        nameValuePairs.add(new BasicNameValuePair("Mail", user));
	        nameValuePairs.add(new BasicNameValuePair("Password", pass));
	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

	        // Execute HTTP Post Request
	        HttpResponse response = httpclient.execute(httppost);
	        //Obtengo el codigo de la respuesta http
	        int response_code = response.getStatusLine().getStatusCode();
	        //Obtengo el nombre de usuario
	        if (response_code==200){
	        	BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
		        String json = reader.readLine();
		        JSONTokener tokener = new JSONTokener(json);
		        try {
					JSONObject finalResult = new JSONObject(tokener);
			        res_name =finalResult.get("Name").toString();
			        res_id =finalResult.get("Id").toString();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        	
	        }
	        
	        res_codigo=Integer.toString(response_code);
	        String[] result= {res_codigo, res_id, res_name};
	        return result;

	    } catch (ClientProtocolException e) {
	        // TODO Auto-generated catch block
	    	String[] result={"-1"};
	    	return result;
	    } catch (IOException e) {
	        // TODO Auto-generated catch block
	    	String[] result={"-1"};
	    	return result;
	    }
	} 
	
	public String[] getFriends(String id) {

		String[] result = {"-1","-1"};
		 //crear un ArrayList bidimensional de enteros vac�o
        //Realmente se crea un ArrayList de ArrayLists de strings
       // ArrayList<ArrayList<String>> array = new ArrayList<ArrayList<String>>();
		
	    // Create a new HttpClient and Post Header
	    HttpClient httpclient = new DefaultHttpClient();
	    HttpGet httpGet = new HttpGet("http://developmentpis.azurewebsites.net/api/Friends/GetAllFriends/" + id);
		try {
		
		HttpResponse response = httpclient.execute(httpGet);
		
		 //Obtengo el codigo de la respuesta http
        int response_code = response.getStatusLine().getStatusCode();
        result[0] = Integer.toString(response_code);
        if (response_code==200){
        	
			BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
	        String json = reader.readLine();
	        result[1]=json;
		
        }
		
		return result;
		
		} catch (ClientProtocolException e) {
	        // TODO Auto-generated catch block
	    	String[] result2={"-1"};
	    	return result2;
	    } catch (IOException e) {
	        // TODO Auto-generated catch block
	    	String[] result2={"-1"};
	    	return result2;
	    }
		
	}

	public void logout(View view){
		SharedPreferences pref = view.getContext().getSharedPreferences("prefs",Context.MODE_PRIVATE);
		pref.edit().putBoolean("log_in", false).commit();
		pref.edit().putString("user_name", "").commit();
		pref.edit().putString("user_id", "").commit();
        // go to previous screen when app icon in action bar is clicked
        Intent intent = new Intent(view.getContext(), MainActivity.class);
        view.getContext().startActivity(intent);
	}

}