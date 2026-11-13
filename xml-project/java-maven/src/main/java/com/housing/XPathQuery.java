package com.housing;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.*;
import java.io.File;

/**
 * XPathQuery
 *
 * Demonstrates XPath interrogation of the Housing Prices XML document.
 * Uses JAXP XPathFactory and XPathExpression to extract data.
 *
 * Technologies:
 *   - javax.xml.parsers.DocumentBuilderFactory
 *   - javax.xml.xpath.XPathFactory
 *   - javax.xml.xpath.XPathExpression
 *   - org.w3c.dom.Document / NodeList
 *
 * Example XPath queries:
 *   Q1. Get all house IDs
 *   Q2. Get all neighborhoods
 *   Q3. Find houses with overall quality >= 7
 *   Q4. Find houses sold in Normal condition
 *   Q5. Get the most expensive house
 *   Q6. Find houses built after 2000
 *   Q7. Count houses with central air
 *   Q8. Find houses in CollgCr neighborhood
 */
public class XPathQuery {

    private final Document document;

    /**
     * Constructor: parse the XML document once, reuse for all queries.
     *
     * @param xmlFilePath path to the XML file
     */
    public XPathQuery(String xmlFilePath) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            this.document = builder.parse(new File(xmlFilePath));
            System.out.println("  ✅ XML document parsed successfully.");
            System.out.println();
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse XML: " + e.getMessage(), e);
        }
    }

    /**
     * Run all example XPath queries and print results.
     */
    public void runAllQueries() {
        XPathFactory xpathFactory = XPathFactory.newInstance();
        XPath xpath = xpathFactory.newXPath();

        // ─── Query 1: All house IDs ────────────────────────────────────────────
        printSectionHeader("Q1 — All House IDs");
        executeStringListQuery(xpath, "//house/@id", "House ID");

        // ─── Query 2: All unique neighborhoods ────────────────────────────────
        printSectionHeader("Q2 — All Neighborhoods");
        executeStringListQuery(xpath, "//house/location/neighborhood", "Neighborhood");

        // ─── Query 3: Houses with overall quality >= 7 ─────────────────────────
        printSectionHeader("Q3 — Houses with Overall Quality >= 7");
        executeHouseQuery(xpath,
            "//house[quality/overallQual >= 7]",
            "House ID", "Neighborhood", "OverallQual", "SalePrice");

        // ─── Query 4: Houses sold under Normal condition ────────────────────────
        printSectionHeader("Q4 — Houses Sold under 'Normal' Condition");
        executeHouseQuery(xpath,
            "//house[sale/saleCondition = 'Normal']",
            "House ID", "Neighborhood", "SaleCondition", "SalePrice");

        // ─── Query 5: Most expensive house ────────────────────────────────────
        printSectionHeader("Q5 — Most Expensive House");
        try {
            XPathExpression expr = xpath.compile(
                "//house[sale/salePrice = max(//house/sale/salePrice)]"
            );
            NodeList nodes = (NodeList) expr.evaluate(document, XPathConstants.NODESET);
            if (nodes.getLength() > 0) {
                Node house = nodes.item(0);
                String id   = xpathValue(xpath, "@id", house);
                String nbhd = xpathValue(xpath, "location/neighborhood", house);
                String price = xpathValue(xpath, "sale/salePrice", house);
                System.out.printf("  → House %s in %s — $%s%n", id, nbhd, price);
            }
        } catch (XPathExpressionException e) {
            System.err.println("  ⚠ XPath error: " + e.getMessage());
        }

        // ─── Query 6: Houses built after year 2000 ────────────────────────────
        printSectionHeader("Q6 — Houses Built After Year 2000");
        executeHouseQuery(xpath,
            "//house[construction/yearBuilt > 2000]",
            "House ID", "Neighborhood", "YearBuilt", "SalePrice");

        // ─── Query 7: Count houses with central air ────────────────────────────
        printSectionHeader("Q7 — Count Houses WITH Central Air");
        try {
            XPathExpression expr = xpath.compile("count(//house[utilities/centralAir = 'Y'])");
            double count = (double) expr.evaluate(document, XPathConstants.NUMBER);
            System.out.printf("  → %d houses have central air conditioning.%n", (int) count);
        } catch (XPathExpressionException e) {
            System.err.println("  ⚠ XPath error: " + e.getMessage());
        }

        // ─── Query 8: Houses in CollgCr neighborhood ──────────────────────────
        printSectionHeader("Q8 — Houses in 'CollgCr' Neighborhood");
        executeHouseQuery(xpath,
            "//house[location/neighborhood = 'CollgCr']",
            "House ID", "YearBuilt", "OverallQual", "SalePrice");

        // ─── Query 9: Average sale price ──────────────────────────────────────
        printSectionHeader("Q9 — Average Sale Price");
        try {
            XPathExpression exprSum   = xpath.compile("sum(//house/sale/salePrice)");
            XPathExpression exprCount = xpath.compile("count(//house)");
            double sum   = (double) exprSum.evaluate(document, XPathConstants.NUMBER);
            double count = (double) exprCount.evaluate(document, XPathConstants.NUMBER);
            System.out.printf("  → Average sale price: $%.2f%n", sum / count);
        } catch (XPathExpressionException e) {
            System.err.println("  ⚠ XPath error: " + e.getMessage());
        }

        // ─── Query 10: Houses with garage >= 3 cars ───────────────────────────
        printSectionHeader("Q10 — Houses with Garage Capacity >= 3 Cars");
        executeHouseQuery(xpath,
            "//house[rooms/garageCars >= 3]",
            "House ID", "Neighborhood", "GarageCars", "SalePrice");
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private void executeStringListQuery(XPath xpath, String expression, String label) {
        try {
            XPathExpression expr = xpath.compile(expression);
            NodeList nodes = (NodeList) expr.evaluate(document, XPathConstants.NODESET);
            if (nodes.getLength() == 0) {
                System.out.println("  (no results)");
            } else {
                for (int i = 0; i < nodes.getLength(); i++) {
                    System.out.printf("  %s: %s%n", label, nodes.item(i).getNodeValue() != null
                        ? nodes.item(i).getNodeValue() : nodes.item(i).getTextContent());
                }
            }
            System.out.println();
        } catch (XPathExpressionException e) {
            System.err.println("  ⚠ XPath error: " + e.getMessage());
        }
    }

    private void executeHouseQuery(XPath xpath, String expression,
                                   String col1, String col2, String col3, String col4) {
        try {
            XPathExpression expr = xpath.compile(expression);
            NodeList nodes = (NodeList) expr.evaluate(document, XPathConstants.NODESET);
            System.out.printf("  Found %d house(s):%n", nodes.getLength());
            for (int i = 0; i < nodes.getLength(); i++) {
                Node house = nodes.item(i);
                String id    = xpathValue(xpath, "@id", house);
                String nbhd  = xpathValue(xpath, "location/neighborhood", house);
                String qual  = xpathValue(xpath, "quality/overallQual", house);
                String year  = xpathValue(xpath, "construction/yearBuilt", house);
                String cond  = xpathValue(xpath, "sale/saleCondition", house);
                String cars  = xpathValue(xpath, "rooms/garageCars", house);
                String price = xpathValue(xpath, "sale/salePrice", house);

                // Pick display value based on col3 label
                String val3 = col3.equals("OverallQual") ? qual
                    : col3.equals("YearBuilt") ? year
                    : col3.equals("SaleCondition") ? cond
                    : col3.equals("GarageCars") ? cars
                    : col3;

                System.out.printf("    [%s] %s — %s: %-8s | $%s%n", id, nbhd, col3, val3, price);
            }
            System.out.println();
        } catch (XPathExpressionException e) {
            System.err.println("  ⚠ XPath error: " + e.getMessage());
        }
    }

    private String xpathValue(XPath xpath, String expr, Node context) {
        try {
            return xpath.evaluate(expr, context);
        } catch (XPathExpressionException e) {
            return "N/A";
        }
    }

    private void printSectionHeader(String title) {
        System.out.println("  ┌─ " + title);
    }
}
