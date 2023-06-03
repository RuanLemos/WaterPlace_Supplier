package waterplace.finalproj.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class viacepUtil {
    public static int CEPcheck(int CEP) {
        int cep = CEP;

        try {
            String cepString = String.valueOf(cep);
            URL url = new URL("https://viacep.com.br/ws/" + cepString + "/json/");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                String jsonResponse = response.toString();
                // Checa se o CEP é valido
                if (jsonResponse.contains("\"erro\": true")) {
                    //System.out.println("CEP Invalido.");
                } else {
                    //System.out.println("CEP Valido.");

                }
            } else {
                //System.out.println("Erro na request, código do erro: " + responseCode);
            }

            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }
}