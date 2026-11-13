package serveur;


import jakarta.xml.ws.Endpoint;
import service.HousingPricesService;


/**
 * Publie le service SOAP Housing Prices.
 */
public class ServeurJWS {

    public static void main(String[] args) {
        String url = "http://127.0.0.1:8081/HousingPricesService";
        Endpoint.publish(url, new HousingPricesService());
        System.out.println("Service publié sur : " + url);
        System.out.println("WSDL : " + url + "?wsdl");
        System.out.println("Laissez cette fenêtre ouverte pendant les tests SoapUI.");
    }
}
