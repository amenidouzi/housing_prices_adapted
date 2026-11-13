package service;
import jakarta.jws.WebService;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;

/**
 * Contrat SOAP du service Housing Prices.
 * La première opération expose le modèle de prédiction Python existant.
 */
@WebService(
    name = "HousingPricesPort",
    targetNamespace = "http://service.housing.com/"
)
public interface IHousingPricesService {

    @WebMethod(
        operationName = "predictPrice",
        action = "http://service.housing.com/predictPrice"
    )
    double predictPrice(
        @WebParam(name = "overallQual") int overallQual,
        @WebParam(name = "grLivArea") int grLivArea,
        @WebParam(name = "totalBsmtSF") int totalBsmtSF,
        @WebParam(name = "yearBuilt") int yearBuilt,
        @WebParam(name = "yrSold") int yrSold,
        @WebParam(name = "garageCars") int garageCars,
        @WebParam(name = "neighborhood") String neighborhood
    );
}
