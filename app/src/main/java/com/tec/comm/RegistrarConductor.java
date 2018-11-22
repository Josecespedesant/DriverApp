package com.tec.comm;

import android.os.Build;
import android.support.annotation.RequiresApi;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tec.entities.Conductor;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class RegistrarConductor {

    private final String registrarConductorURL = "http://192.168.100.7:8080/registro-conductor";

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public boolean registrarConductor(Conductor conductor) throws IOException {
        boolean registrado = false;

        HttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost(registrarConductorURL);

        Gson gson = new Gson();
        String json = gson.toJson(conductor);

        List<NameValuePair> params = new ArrayList<NameValuePair>(2);
        params.add(new BasicNameValuePair("json", json));
        httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

        //Execute and get the response.
        HttpResponse response = httpclient.execute(httppost);
        HttpEntity entity = response.getEntity();

        if (entity != null) {
            try (InputStream instream = entity.getContent()) {
                InputStreamReader inputStreamReader = new InputStreamReader(instream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                StringBuilder stringBuilder = new StringBuilder();
                String bufferedStrChunk = null;
                while ((bufferedStrChunk = bufferedReader.readLine()) != null) {
                    stringBuilder.append(bufferedStrChunk);
                }

                JsonParser jsonParser = new JsonParser();
                String respuesta = stringBuilder.toString();
                JsonObject respuestaJson = jsonParser.parse(respuesta).getAsJsonObject();
                registrado = respuestaJson.getAsJsonPrimitive("exitoso").getAsBoolean();
            }
        }
        return registrado;
    }
}
