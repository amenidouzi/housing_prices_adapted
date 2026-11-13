package com.housing;

/**
 * TP1 SOA - Main Entry Point
 *
 * This class orchestrates:
 *   1. XML/XSD Validation using JAXP SchemaFactory + Validator
 *   2. XPath Interrogation using XPathFactory + XPathExpression
 *   3. XSL Transformation using TransformerFactory
 *
 * Domain: Ames Housing Prices (Kaggle dataset)
 */
public class Main {

    public static void main(String[] args) {

        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║     TP1 SOA - Housing Prices XML Project                 ║");
        System.out.println("║     Faculté des Sciences de Sfax - LSI3 2025-2026        ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");
        System.out.println();

        // ── Paths to resources ──────────────────────────────────────────────────
        String xmlPath = "../../xml/housing_prices.xml";
        String xsdPath = "../../xsd/housing_prices.xsd";
        String xslPath = "../../xsl/housing_prices.xsl";
        String outputHtml = "../../output/housing_prices_report.html";

        // ── Step 1: XML/XSD Validation ─────────────────────────────────────────
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("  STEP 1 — XML/XSD Validation");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        XmlValidator validator = new XmlValidator();
        boolean isValid = validator.validate(xmlPath, xsdPath);
        System.out.println();

        if (!isValid) {
            System.err.println("⚠  XML validation failed. Please fix the XML/XSD before querying.");
        }

        // ── Step 2: XPath Interrogation ────────────────────────────────────────
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("  STEP 2 — XPath Interrogation");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        XPathQuery xpathQuery = new XPathQuery(xmlPath);
        xpathQuery.runAllQueries();
        System.out.println();

        // ── Step 3: XSL Transformation ─────────────────────────────────────────
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("  STEP 3 — XSL Transformation → HTML Report");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        XslTransformer transformer = new XslTransformer();
        transformer.transform(xmlPath, xslPath, outputHtml);
        System.out.println();

        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║  ✓  All steps completed successfully!                    ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");
    }
}
