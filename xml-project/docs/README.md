# TP1 SOA — Housing Prices XML Project

**Matière :** Architecture SOA et Services Web  
**Filière :** LSI3 — Faculté des Sciences de Sfax  
**Année Universitaire :** 2025-2026  
**Étudiant(e) :** Islem Bouaziz  

---

## 1. Domaine Choisi : Prix des Maisons (Ames Housing)

### Justification du choix

Le domaine choisi est le **marché immobilier résidentiel de la ville d'Ames, Iowa (USA)**, issu du célèbre dataset Kaggle *"House Prices: Advanced Regression Techniques"*.

Ce domaine est riche en données structurées avec :
- Des **entités hiérarchiques** claires (localisation, construction, qualité, surface, pièces…)
- Des **types variés** : entiers, chaînes, dates, booléens, enums
- Des **contraintes métier** naturelles (qualité de 1 à 10, zones de zonage définies, etc.)
- Une pertinence réelle pour des systèmes SOA (services de valorisation immobilière)

---

## 2. Modélisation XML

### Entités principales

| Entité | Description |
|--------|-------------|
| `housingDataset` | Racine : contient métadonnées + liste des maisons |
| `metadata` | Informations sur la source du dataset |
| `house` | Une maison, identifiée par un attribut `id` (ex: `H001`) |
| `location` | Quartier, zonage, surface du terrain |
| `construction` | Année de construction, style, fondation, toit |
| `quality` | Scores de qualité globale et des équipements |
| `areas` | Surfaces en pieds carrés (salon, cave, garage…) |
| `rooms` | Nombre de pièces, chambres, salles de bain, cheminées |
| `utilities` | Climatisation, chauffage, électricité |
| `sale` | Date de vente, type, condition, **prix de vente** |

### Structure XML (extrait)

```xml
<house id="H001">
  <location>
    <neighborhood>CollgCr</neighborhood>
    <msZoning>RL</msZoning>
    <lotArea>8450</lotArea>
    <street>Pave</street>
  </location>
  <construction>
    <yearBuilt>2003</yearBuilt>
    <houseStyle>2Story</houseStyle>
    <foundation>PConc</foundation>
    ...
  </construction>
  <quality>
    <overallQual>7</overallQual>
    <exterQual>Gd</exterQual>
    ...
  </quality>
  ...
  <sale>
    <salePrice>208500</salePrice>
  </sale>
</house>
```

---

## 3. Schéma XSD

### Contraintes implémentées

| Élément/Type | Contrainte |
|---|---|
| `house/@id` | Pattern : `H\d{3}` (H + 3 chiffres) |
| `msZoning` | Enumération : `RL`, `RM`, `RH`, `C`, `FV`, `I`, `A`, `RP` |
| `houseStyle` | Enumération : `1Story`, `2Story`, `1.5Fin`, `2Story`, etc. |
| `foundation` | Enumération : `BrkTil`, `CBlock`, `PConc`, `Slab`, `Stone`, `Wood` |
| `overallQual` / `overallCond` | Entier borné : `1..10` |
| `exterQual`, `kitchenQual`, `bsmtQual`, `heatingQC` | Enumération : `Ex`, `Gd`, `TA`, `Fa`, `Po` |
| `centralAir` | Enumération : `Y`, `N` |
| `electrical` | Enumération : `SBrkr`, `FuseA`, `FuseF`, `FuseP`, `Mix` |
| `moSold` | Entier borné : `1..12` |
| `yearBuilt`, `yearRemodAdd`, `yrSold` | Entier borné : `1800..2025` |
| `salePrice`, `lotArea`, `grLivArea` | `xs:positiveInteger` |
| `openPorchSF`, `woodDeckSF`, `garageArea` | `xs:nonNegativeInteger` |
| `saleCondition` | Enumération : `Normal`, `Abnorml`, `AdjLand`, `Alloca`, `Family`, `Partial` |

---

## 4. Projet Maven Java

### Structure

```
java-maven/
├── pom.xml
└── src/
    └── main/
        └── java/
            └── com/housing/
                ├── Main.java           ← Point d'entrée
                ├── XmlValidator.java   ← Validation XML/XSD
                ├── XPathQuery.java     ← Interrogation XPath
                └── XslTransformer.java ← Transformation XSLT
```

### Dépendances Maven

| Dépendance | Version | Usage |
|---|---|---|
| Saxon-HE | 12.4 | Processeur XPath 2.0 / XSLT 2.0 |
| Xerces | 2.12.2 | Parseur XML + validateur XSD |
| JUnit Jupiter | 5.10.1 | Tests unitaires |

### Commandes

```bash
# Compiler le projet
mvn compile

# Exécuter le programme principal
mvn exec:java -Dexec.mainClass="com.housing.Main"

# Lancer les tests
mvn test

# Créer le jar exécutable
mvn package
java -jar target/housing-prices-xml-1.0.0-jar-with-dependencies.jar
```

---

## 5. Requêtes XPath — Exemples

| # | Expression XPath | Description | Résultat |
|---|---|---|---|
| Q1 | `//house/@id` | Tous les IDs | H001..H010 |
| Q2 | `//house/location/neighborhood` | Tous les quartiers | CollgCr, Veenker... |
| Q3 | `//house[quality/overallQual >= 7]` | Maisons qualité ≥ 7 | 7 maisons |
| Q4 | `//house[sale/saleCondition = 'Normal']` | Ventes normales | 8 maisons |
| Q5 | `//house[sale/salePrice = max(...)]` | La plus chère | H007 — $307,000 |
| Q6 | `//house[construction/yearBuilt > 2000]` | Construites après 2000 | 3 maisons |
| Q7 | `count(//house[utilities/centralAir = 'Y'])` | Avec climatisation | 9 |
| Q8 | `//house[location/neighborhood = 'CollgCr']` | Dans CollgCr | H001, H003 |
| Q9 | `sum(//sale/salePrice) div count(//house)` | Prix moyen | $190,140 |
| Q10 | `//house[rooms/garageCars >= 3]` | Garage ≥ 3 voitures | H004, H005 |

---

## 6. Transformation XSLT

Le fichier `xsl/housing_prices.xsl` transforme le document XML en un **rapport HTML** avec :
- Un tableau de données complet (toutes les maisons)
- Un panneau de statistiques (moyenne, max, min)
- Des badges colorés pour la qualité (vert = excellent, jaune = moyen, rouge = faible)
- Un design moderne en dark mode

---

## 7. Service Web SOAP

Le WSDL `soap/HousingPricesService.wsdl` décrit un service SOAP avec 4 opérations :

| Opération | Description |
|---|---|
| `getPredictedPrice` | Prédit le prix à partir des caractéristiques |
| `getHouseById` | Récupère une maison par son ID |
| `getHousesByNeighborhood` | Liste toutes les maisons d'un quartier |
| `validateHouseXml` | Valide un fragment XML maison contre le XSD |

---

## 8. Structure du projet

```
xml-project/
├── xml/
│   └── housing_prices.xml          ← Document XML principal
├── xsd/
│   └── housing_prices.xsd          ← Schéma de validation
├── xsl/
│   └── housing_prices.xsl          ← Feuille de style XSLT
├── xpath/
│   └── xpath_queries.xml           ← Catalogue de requêtes XPath
├── soap/
│   ├── HousingPricesService.wsdl   ← Description du service SOAP
│   └── soap_examples.xml           ← Exemples de messages SOAP
├── java-maven/
│   ├── pom.xml                     ← Configuration Maven
│   └── src/main/java/com/housing/
│       ├── Main.java
│       ├── XmlValidator.java
│       ├── XPathQuery.java
│       └── XslTransformer.java
└── docs/
    └── README.md                   ← Ce document
```

---

## 9. Validation en ligne

Pour valider le XML/XSD en ligne :
🔗 [https://www.freeformatter.com/xml-validator-xsd.html](https://www.freeformatter.com/xml-validator-xsd.html)

Pour tester les requêtes XPath :
🔗 [https://www.freeformatter.com/xpath-tester.html](https://www.freeformatter.com/xpath-tester.html)

Pour tester XSLT :
🔗 [https://www.freeformatter.com/xsl-transformer.html](https://www.freeformatter.com/xsl-transformer.html)
