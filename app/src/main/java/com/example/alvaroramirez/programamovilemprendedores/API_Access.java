package com.example.alvaroramirez.programamovilemprendedores;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;

public class API_Access {

    private final String url_base = "https://communityapp-api.herokuapp.com/api/";

    private final String[] methods = {
            "news/create",// 0
            "news/get_news",// 1
            "news/get_news_status",// 2
            "news/delete_news",// 3
            "news/edit" //4

    };

    private final int[] responses = {HttpsURLConnection.HTTP_CREATED, HttpsURLConnection.HTTP_OK};

    int estadoRequest = -1;
    private JSONObject jsonObjectResponse = new JSONObject();
    private JSONObject jsonObjectResponseNotifs = new JSONObject();
    private JSONObject jsonObjectResponseAmigos = new JSONObject();
    private JSONObject jsonObjectResponseChats = new JSONObject();
    private JSONArray jsonArrayResponse = new JSONArray();

    private static final API_Access ourInstance = new API_Access();

    public static API_Access getInstance() {
        return ourInstance;
    }

    public API_Access() {
    }

    //----------------------------------- Método POST/PUT base ----------------------------------//
    // methodType: "POST" or "PUT"
    public boolean post_put_base(String[] keys, String[] values, int method, String methodType, int expectedResponse){
        jsonObjectResponse = new JSONObject();

        HashMap<String, String> Parameters = new HashMap<String, String>();

        int size = keys.length;
        for(int i = 0; i < size; i++){
            Parameters.put(keys[i], values[i]);
        }

        return makePOSTRequest(methods[method], methodType, true, true, Parameters, responses[expectedResponse]);
    }

    //------------------------------- Método GET/DELETE base --------------------------------//
    public boolean get_delete_base(String[] keys, String[] values, int method, String methodType, int expectedResponse){
        jsonArrayResponse = new JSONArray();

        String urlComp = "";
        int size = keys.length;
        for(int i = 0; i < size; i++){
            if(i > 0){
                urlComp += "&";
            }
            urlComp += keys[i] + "=" + values[i];
        }

        String urlEsp = methods[method] + "?" + urlComp;

        boolean result = false;

        if(methodType.equals("GET")){
            result = makeGETRequest(urlEsp, methodType, responses[expectedResponse], 0);
        }else if(methodType.equals("DELETE")){
            result = makeDELETERequest(urlEsp, methodType, responses[expectedResponse]);
        }

        return result;
    }

    /////////////////////// GET Respuesta del servidor: JSONObject ////////////////////////////////
    public JSONObject getJsonObjectResponse(){
        Log.d("estado: ", "" + estadoRequest);
        return jsonObjectResponse;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////// GET Respuesta del servidor: JSONObject ////////////////////////////////
    public JSONObject getJsonObjectResponseNotifs(){
        Log.d("estado: ", "" + estadoRequest);
        return jsonObjectResponseNotifs;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////// GET Respuesta del servidor: JSONObject ////////////////////////////////
    public JSONObject getJsonObjectResponseAmigos(){
        Log.d("estado: ", "" + estadoRequest);
        return jsonObjectResponseAmigos;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////// GET Respuesta del servidor: JSONObject ////////////////////////////////
    public JSONObject getJsonObjectResponseChats(){
        Log.d("estado: ", "" + estadoRequest);
        return jsonObjectResponseChats;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////



    /////////////////////////////////////////////// Métodos que ejecutan las solicitudes ////////////////////////////////////////////
    // Solicitud para POSTs
    private boolean makePOSTRequest(String urlEsp, String metodo, boolean doInput, boolean doOutput, HashMap<String, String> Parametros, int responseCode){
        String result = "";
        URL url;
        HttpsURLConnection httpsURLConnection;

        try {
            url = new URL(url_base + urlEsp);
            httpsURLConnection = (HttpsURLConnection) url.openConnection();

            //crea el objeto JSON para enviar los parámetros
            JSONObject parametros = new JSONObject();
            for(String s: Parametros.keySet()){
                parametros.put(s, Parametros.get(s));
            }

            //DEFINE PARAMETROS DE CONEXION
            httpsURLConnection.setReadTimeout(15000);
            httpsURLConnection.setConnectTimeout(15000);
            httpsURLConnection.setRequestMethod(metodo);
            httpsURLConnection.setDoInput(doInput);
            httpsURLConnection.setDoOutput(doOutput);

            //Obtiene el resultado de la solicitud
            OutputStream outputStream = httpsURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            String pars = jsonToString(parametros);
            //String g = httpsURLConnection.g
            bufferedWriter.write(pars);
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();

            int rCode = httpsURLConnection.getResponseCode();
            if(responseCode == rCode){
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpsURLConnection.getInputStream()));
                StringBuffer stringBuffer = new StringBuffer("");
                String linea = "";
                while((linea = bufferedReader.readLine()) != null){
                    stringBuffer.append(linea);
                    break;
                }
                bufferedReader.close();
                result = stringBuffer.toString();
            }else{
                result = "Error " + responseCode;
            }

            jsonObjectResponse = new JSONObject(result);
            return true;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private String jsonToString(JSONObject params) throws JSONException, UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        Iterator<String> iterator = params.keys();
        while ((iterator.hasNext())){
            String key = iterator.next();
            Object value = params.get(key);

            if(first){
                //result.append("?");
                first = false;
            }else {
                result.append("&");
            }

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));
        }
        return result.toString();
    }

    // Solicitud para GETs
    //devolver_en= 0:JsonObjectResponse; 1:JsonObjectResponseNotifs; 2:JsonObjectResponseAmigos
    private boolean makeGETRequest(String urlEsp, String metodo, int responseCode, int devolver_en) {
        String result = "";
        URL url;
        HttpsURLConnection httpsURLConnection;

        try {
            url = new URL(url_base + urlEsp);
            httpsURLConnection = (HttpsURLConnection) url.openConnection();

            //DEFINE PARAMETROS DE CONEXION
            httpsURLConnection.setReadTimeout(15000);
            httpsURLConnection.setConnectTimeout(15000);
            httpsURLConnection.setRequestMethod(metodo);

            //Connect to our url
            httpsURLConnection.connect();

            //Create a new InputStreamReader
            InputStreamReader streamReader = new
                    InputStreamReader(httpsURLConnection.getInputStream());

            //Create a new buffered reader and String Builder
            BufferedReader reader = new BufferedReader(streamReader);
            StringBuilder stringBuilder = new StringBuilder();

            //Check if the line we are reading is not null
            int rCode = httpsURLConnection.getResponseCode();
            if (responseCode == rCode) {
                String inputLine = "";
                while ((inputLine = reader.readLine()) != null) {
                    stringBuilder.append(inputLine);
                }
                result = stringBuilder.toString();
            } else {
                result = "Error " + responseCode;
            }


            //Close our InputStream and Buffered reader
            reader.close();
            streamReader.close();

            switch(devolver_en){
                case 0:
                    jsonObjectResponse = new JSONObject(result);
                    break;
                case 1:
                    jsonObjectResponseNotifs = new JSONObject(result);
                    break;
                case 2:
                    jsonObjectResponseAmigos = new JSONObject(result);
                    break;
                case 3:
                    jsonObjectResponseChats = new JSONObject(result);
                    break;
            }

            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Solicitud para DELETEs
    private boolean makeDELETERequest(String urlEsp, String metodo, int responseCode){
        URL url;
        HttpsURLConnection httpsURLConnection;

        try {
            url = new URL(url_base + urlEsp);
            httpsURLConnection = (HttpsURLConnection) url.openConnection();
            httpsURLConnection.setReadTimeout(15000);
            httpsURLConnection.setConnectTimeout(15000);
            httpsURLConnection.setRequestMethod("DELETE");
            httpsURLConnection.setDoOutput(false);
            httpsURLConnection.setDoInput(true);
            httpsURLConnection.connect();

            int r = httpsURLConnection.getResponseCode();
            String rm = httpsURLConnection.getContent().toString();
            String rC = Integer.toString(httpsURLConnection.getResponseCode());
            if(r==200){
                return true;
            } else{
                Log.e("httpsURLConnection", rm);
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return false;
    }
}
