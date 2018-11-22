package com.tec.comm;

import android.os.AsyncTask;

import com.google.gson.Gson;
import com.tec.entities.Conductor;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class RegistrarConductor {

    private final String registrarConductorURL = "http://192.168.100.7:8080/registrar-conductor";

    public boolean sendRegistro(Conductor conductor) {
        Gson gson = new Gson();
        String json = gson.toJson(conductor);
        OutputStream out = null;

        try {
            URL url = new URL(this.registrarConductorURL);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
            writer.write(json);
            writer.flush();
            writer.close();
            out.close();

            urlConnection.connect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }
}
