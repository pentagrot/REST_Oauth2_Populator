import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) throws IOException {
        if(args.length < 2){
            System.out.println("username and password not provided");
            return;
        }
        System.out.printf("username = %s, password = %s%n", args[0], args[1]);
        String accessToken = getAccessToken(args[0], args[1]);
        System.out.println("Fetched access token = \n" + accessToken);
        String patientTemplate = "{\"name\":\"Иванов Иван Иванович #%s\",\"gender\":\"male\",\"birthDate\":\"2024-01-13T18:25:43\"}";
        URL address = new URL("http://localhost:8181/patients");
        System.out.println("Patients ids : ");
        for (int i = 0; i < 100; i++) {
            System.out.println(createPatient(address, accessToken, String.format(patientTemplate, i)));
        }
    }

    private static String createPatient(URL postUrl, String accessToken, String jsonBody) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) postUrl.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Authorization", "Bearer " + accessToken);
        connection.setDoOutput(true);
        try (OutputStream outputStream = connection.getOutputStream()) {
            outputStream.write(jsonBody.getBytes(StandardCharsets.UTF_8));
        }
        return getResponceString(connection);
    }

    private static String getAccessToken(String username, String password) throws IOException {
        URL url = new URL("http://localhost:8080/realms/test_realm/protocol/openid-connect/token");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setRequestProperty("Authorization", getKeyClockClientAuthorization());
        connection.setDoOutput(true);
        try (OutputStream outputStream = connection.getOutputStream()) {
            String requestBodyTemplate = "grant_type=password&username=%s&password=%s";
            outputStream.write(String.format(requestBodyTemplate, username, password).getBytes(StandardCharsets.UTF_8));
        }
        String responseString = getResponceString(connection);
        int accessTokenIndex = responseString.indexOf("\"access_token\":\"") + 16;
        int accessTokenEndIndex = responseString.indexOf("\"", accessTokenIndex);
        return responseString.substring(accessTokenIndex, accessTokenEndIndex);
    }

    private static String getKeyClockClientAuthorization() {
        String clientId = "test_clien";
        String clientSecret = "Klw71nKQ0iL4j74ENxQVCHBkqxjL32Ky";
        String credentials = clientId + ":" + clientSecret;
        return "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes());
    }

    private static String getResponceString(HttpURLConnection connection) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        return in.lines().collect(Collectors.joining());
    }

}