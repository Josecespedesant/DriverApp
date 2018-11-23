package com.tec.comm;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tec.entities.Conductor;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class InicioSesion {

    OkHttpClient client = new OkHttpClient();

    String url = "http://192.168.100.7:8080/ingreso-conductor";

    Gson gson = new Gson();

    boolean inicioExito = false;

    public boolean inicio(Conductor conductor) {
        String json = gson.toJson(conductor);
        final JsonParser jsonParser = new JsonParser();
        final JsonObject jsonObject = jsonParser.parse(json).getAsJsonObject();
        jsonObject.remove("amigos");
        json = jsonObject.toString();

        RequestBody requestBody = new FormBody.Builder()
                .add("json", json)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Content-Type", "text/plain")
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String myResponse = response.body().string();
                    myResponse = gson.toJson(myResponse);
                    JsonObject json = jsonParser.parse(myResponse).getAsJsonObject();
                    inicioExito = true;
                    // registroExitoso = json.getAsJsonPrimitive("exitoso").getAsBoolean();
                }
            }
        });
        System.out.println(inicioExito);
        return inicioExito;
    }
}
