package com.tec.comm;

import android.os.AsyncTask;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class RegistrarConductor extends AsyncTask<String, String, String> {

    private final String registrarConductorURL = "http://192.168.100.7:8080/registro-conductor";

    /*
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public boolean registrarConductor(Conductor conductor) throws IOException {
        boolean registrado = false;

        System.out.println("hola");

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
    */

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... strings) { ;
        String json = strings[0];
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
        return "";
    }
}
