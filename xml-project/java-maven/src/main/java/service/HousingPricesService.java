package service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.jws.WebService;
import jakarta.xml.ws.WebServiceException;
import jakarta.xml.ws.Endpoint;
import jakarta.jws.WebMethod;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Implémentation SOAP.
 * Elle délègue la prédiction au service REST FastAPI déjà présent
 * dans le projet Python : POST http://127.0.0.1:8000/predict.
 */
@WebService(
    endpointInterface = "service.IHousingPricesService",
    serviceName = "HousingPricesService",
    portName = "HousingPricesPort",
    targetNamespace = "http://service.housing.com/"
)
public class HousingPricesService implements IHousingPricesService {

    private static final String DEFAULT_API_URL =
            "http://127.0.0.1:8000/predict";

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @WebMethod
    public double predictPrice(
            int overallQual,
            int grLivArea,
            int totalBsmtSF,
            int yearBuilt,
            int yrSold,
            int garageCars,
            String neighborhood) {

        try {
            String apiUrl = System.getenv().getOrDefault(
                    "HOUSING_API_URL", DEFAULT_API_URL);

            Map<String, Object> input = new LinkedHashMap<>();
            input.put("OverallQual", overallQual);
            input.put("GrLivArea", grLivArea);
            input.put("TotalBsmtSF", totalBsmtSF);
            input.put("YearBuilt", yearBuilt);
            input.put("YrSold", yrSold);
            input.put("GarageCars", garageCars);
            input.put("Neighborhood",
                    neighborhood == null || neighborhood.isBlank()
                            ? "NAmes" : neighborhood);

            String json = objectMapper.writeValueAsString(input);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .timeout(Duration.ofSeconds(30))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = httpClient.send(
                    request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new WebServiceException(
                        "L'API Python a retourné HTTP "
                                + response.statusCode() + " : "
                                + response.body());
            }

            JsonNode result = objectMapper.readTree(response.body());
            JsonNode price = result.get("predicted_price");

            if (price == null || price.isNull()) {
                throw new WebServiceException(
                        "La réponse de l'API ne contient pas predicted_price.");
            }

            return price.asDouble();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new WebServiceException(
                    "Appel interrompu vers l'API Housing Prices.", e);
        } catch (IOException e) {
            throw new WebServiceException(
                    "Impossible de contacter l'API Python. "
                    + "Vérifiez qu'elle est démarrée sur le port 8000.", e);
        }
    }
}
