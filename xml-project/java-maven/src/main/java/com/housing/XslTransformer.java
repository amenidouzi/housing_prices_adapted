package com.housing;

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.IOException;

/**
 * XslTransformer
 *
 * Transforms an XML document using an XSLT stylesheet.
 * Uses JAXP TransformerFactory.
 *
 * Technologies:
 *   - javax.xml.transform.TransformerFactory
 *   - javax.xml.transform.Transformer
 *   - javax.xml.transform.stream.StreamSource
 *   - javax.xml.transform.stream.StreamResult
 */
public class XslTransformer {

    /**
     * Transform an XML file using an XSL stylesheet and write output to a file.
     *
     * @param xmlPath    Path to the source XML file
     * @param xslPath    Path to the XSL stylesheet
     * @param outputPath Path to the output (HTML) file
     */
    public void transform(String xmlPath, String xslPath, String outputPath) {
        System.out.println("  📄 XML Source   : " + xmlPath);
        System.out.println("  📑 XSL Stylesheet: " + xslPath);
        System.out.println("  📝 Output HTML  : " + outputPath);
        System.out.println();

        try {
            // 1. Create output directory if it doesn't exist
            File outputFile = new File(outputPath);
            File outputDir  = outputFile.getParentFile();
            if (outputDir != null && !outputDir.exists()) {
                outputDir.mkdirs();
            }

            // 2. Create a TransformerFactory
            TransformerFactory factory = TransformerFactory.newInstance();

            // 3. Load the XSL stylesheet as a Source
            Source xslSource = new StreamSource(new File(xslPath));

            // 4. Create a Transformer from the XSL source
            Transformer transformer = factory.newTransformer(xslSource);

            // 5. Set output properties
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.METHOD, "html");

            // 6. Define the XML input source and HTML output result
            Source xmlSource = new StreamSource(new File(xmlPath));
            Result result    = new StreamResult(outputFile);

            // 7. Execute the transformation
            transformer.transform(xmlSource, result);

            System.out.println("  ✅ Transformation SUCCESS — HTML report generated at:");
            System.out.println("     " + outputFile.getAbsolutePath());

        } catch (TransformerConfigurationException e) {
            System.err.println("  ❌ Transformer configuration error: " + e.getMessage());
        } catch (TransformerException e) {
            System.err.println("  ❌ Transformation failed: " + e.getMessage());
        }
    }
}
