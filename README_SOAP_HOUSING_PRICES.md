# TP SOAP adapté au projet Housing Prices

## 1. Principe

Ce module ajoute un service web SOAP Java au projet `housing_prices`.
L'opération SOAP `predictPrice` reçoit les caractéristiques principales
d'une maison et transmet la demande à l'API Python FastAPI existante.
FastAPI utilise le modèle Machine Learning sauvegardé dans `api/model.pkl`
et renvoie le prix prédit.

**Architecture :**

`SoapUI` → `Service SOAP Java (JAX-WS)` → `API Python FastAPI` → `Modèle ML`

Le projet Python et son modèle restent inchangés : le module Java agit
comme une façade SOAP.

## 2. Fichiers ajoutés

Dans `xml-project/java-maven/src/main/java/` :

- `service/IHousingPricesService.java` : contrat SOAP (`@WebService`,
  `@WebMethod`, `@WebParam`).
- `service/HousingPricesService.java` : implémentation ; appelle l'API
  FastAPI et retourne `predicted_price`.
- `serveur/ServeurJWS.java` : publie le service avec `Endpoint.publish()`.

Le `pom.xml` du projet Maven a été complété avec JAX-WS, JWS et Jackson.

## 3. Prérequis

- JDK 11 (ou version compatible avec le projet)
- Maven
- Python et les dépendances du projet (`fastapi`, `uvicorn`, `pandas`,
  `scikit-learn`, etc.)
- SoapUI

## 4. Démarrer l'API Python

Ouvrir un terminal à la racine du dépôt `housing_prices`, puis lancer :

```bash
pip install fastapi uvicorn pandas numpy scikit-learn
uvicorn api.main:app --host 127.0.0.1 --port 8000
```

Vérifier que l'API répond à `http://127.0.0.1:8000/`.
Il faut que les fichiers `api/model.pkl`, `api/columns.pkl` et
`api/training_data.pkl` soient présents.

## 5. Démarrer le serveur SOAP Java

Ouvrir le projet Maven `xml-project/java-maven` dans IntelliJ, attendre
le téléchargement des dépendances Maven, puis exécuter la classe
`serveur.ServeurJWS`.

Ou depuis le dossier `xml-project/java-maven` :

```bash
mvn clean compile
mvn exec:java -Dexec.mainClass=serveur.ServeurJWS
```

Le service est publié à :

- Endpoint : `http://localhost:8081/HousingPricesService`
- WSDL : `http://localhost:8081/HousingPricesService?wsdl`

Garder les deux serveurs (Python et Java) démarrés pendant les tests.

## 6. Tester avec SoapUI

1. Ouvrir SoapUI.
2. Créer un `New SOAP Project`.
3. Comme Initial WSDL, saisir :
   `http://localhost:8081/HousingPricesService?wsdl`
4. Ouvrir l'opération `predictPrice`.
5. Utiliser par exemple les valeurs suivantes :

   - `overallQual` : `7`
   - `grLivArea` : `1500`
   - `totalBsmtSF` : `800`
   - `yearBuilt` : `2000`
   - `yrSold` : `2010`
   - `garageCars` : `2`
   - `neighborhood` : `NAmes`

6. Envoyer la requête avec le bouton vert.
7. La réponse doit contenir une valeur numérique correspondant au prix prédit
   par le modèle. Le montant exact dépend du modèle sauvegardé.

## 7. Exemple de requête SOAP

```xml
<soapenv:Envelope
  xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
  xmlns:ser="http://service.housing.com/">
  <soapenv:Header/>
  <soapenv:Body>
    <ser:predictPrice>
      <overallQual>7</overallQual>
      <grLivArea>1500</grLivArea>
      <totalBsmtSF>800</totalBsmtSF>
      <yearBuilt>2000</yearBuilt>
      <yrSold>2010</yrSold>
      <garageCars>2</garageCars>
      <neighborhood>NAmes</neighborhood>
    </ser:predictPrice>
  </soapenv:Body>
</soapenv:Envelope>
```

## 8. Remarques importantes

- Le service SOAP ne remplace pas l'API REST : il la consomme.
- Les données reçues par SOAP sont converties en JSON pour l'API FastAPI.
- Les paramètres non transmis utilisent les valeurs par défaut de la classe
  `HouseFeatures` côté Python.
- Cette version implémente d'abord une seule opération (`predictPrice`),
  conformément à la consigne du TP qui demande de tester une première
  méthode avant d'ajouter les autres.
- La dépendance JAX-WS `2.3.1` utilise les packages `javax.*` dans le code.
  Ne pas mélanger ces imports avec `jakarta.*` de versions plus récentes.
