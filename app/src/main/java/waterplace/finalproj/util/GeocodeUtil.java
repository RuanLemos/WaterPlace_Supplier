package waterplace.finalproj.util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class GeocodeUtil {
    public static double[] geocode(String address, int cep) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<double[]> futureResult = executor.submit(new GeocodeTask(address, cep));

        try {
            return futureResult.get();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }

        return null;
    }

    private static class GeocodeTask implements Callable<double[]> {
        private String address;
        private int cep;

        public GeocodeTask(String address, int cep) {
            this.address = address;
            this.cep = cep;
        }

        @Override
        public double[] call() throws Exception {
            String url = "https://photon.komoot.io/api/?";

            if (address != null) {
                url += "q=" + URLEncoder.encode(address, "UTF-8");
            } else {

            }

            if (cep > 0) {
                if (address != null) {
                    url += "&";
                }
                url += "postal_code=" + URLEncoder.encode(Integer.toString(cep), "UTF-8");
            }

            HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
            con.setRequestMethod("GET");
            con.setDoInput(true);

            BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            String jsonResponse = response.toString();
            JSONObject responseObject = new JSONObject(jsonResponse);

            JSONArray features = responseObject.getJSONArray("features");

            if (features.length() > 0) {
                JSONObject firstFeature = features.getJSONObject(0);
                JSONObject geometry = firstFeature.getJSONObject("geometry");
                JSONArray coordinates = geometry.getJSONArray("coordinates");

                double longitude = coordinates.getDouble(0);
                double latitude = coordinates.getDouble(1);
                return new double[]{latitude, longitude};
            }

            return null;
        }
    }
}