package com.tec.comm;

import android.os.AsyncTask;

import com.google.gson.Gson;
import com.tec.entities.Conductor;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class RegistrarConductor extends AsyncTask<Conductor, Void, Boolean> {

    private final String registrarConductorURL = "http://localhost:8080/registro-conductor";

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Boolean doInBackground(Conductor... conductors) {
        boolean registrado = false;

        Conductor conductor = conductors[0];
        Gson gson = new Gson();
        String json = gson.toJson(conductor);

        OutputStream out = null;

        try {
            System.out.println("oh hi");
            URL url = new URL(this.registrarConductorURL);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            out = new BufferedOutputStream(urlConnection.getOutputStream());

            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
            writer.write(json);
            writer.flush();
            writer.close();
            out.close();

            urlConnection.connect();

            registrado = true;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return registrado;
    }
}
