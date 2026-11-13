<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:output method="html" encoding="UTF-8" indent="yes"/>

    <!-- ============================================================ -->
    <!-- Root Template -->
    <!-- ============================================================ -->
    <xsl:template match="/">
        <html>
            <head>
                <title>Housing Prices Dataset</title>
                <style>
                    body {
                        font-family: 'Segoe UI', Arial, sans-serif;
                        background: linear-gradient(135deg, #1a1a2e 0%, #16213e 50%, #0f3460 100%);
                        color: #e0e0e0;
                        margin: 0;
                        padding: 20px;
                        min-height: 100vh;
                    }
                    h1 {
                        text-align: center;
                        color: #00d2ff;
                        font-size: 2.2em;
                        text-shadow: 0 0 20px rgba(0, 210, 255, 0.5);
                        margin-bottom: 5px;
                    }
                    .subtitle {
                        text-align: center;
                        color: #a0a0c0;
                        margin-bottom: 30px;
                        font-size: 1em;
                    }
                    .metadata-box {
                        background: rgba(255,255,255,0.05);
                        border: 1px solid rgba(0, 210, 255, 0.2);
                        border-radius: 12px;
                        padding: 20px;
                        margin-bottom: 30px;
                        max-width: 900px;
                        margin-left: auto;
                        margin-right: auto;
                    }
                    .metadata-box h2 {
                        color: #00d2ff;
                        margin-top: 0;
                    }
                    .metadata-box p {
                        margin: 5px 0;
                        color: #c0c0d0;
                    }
                    table {
                        width: 100%;
                        border-collapse: collapse;
                        max-width: 1200px;
                        margin: 0 auto 30px auto;
                        background: rgba(255,255,255,0.03);
                        border-radius: 12px;
                        overflow: hidden;
                        box-shadow: 0 4px 30px rgba(0, 0, 0, 0.3);
                    }
                    th {
                        background: rgba(0, 210, 255, 0.15);
                        color: #00d2ff;
                        padding: 14px 10px;
                        text-align: left;
                        font-size: 0.85em;
                        text-transform: uppercase;
                        letter-spacing: 0.5px;
                        border-bottom: 1px solid rgba(0, 210, 255, 0.3);
                    }
                    td {
                        padding: 12px 10px;
                        border-bottom: 1px solid rgba(255,255,255,0.05);
                        font-size: 0.9em;
                        color: #d0d0e0;
                    }
                    tr:hover td {
                        background: rgba(0, 210, 255, 0.05);
                    }
                    .price-tag {
                        color: #00ffaa;
                        font-weight: bold;
                        font-size: 1em;
                    }
                    .qual-badge {
                        display: inline-block;
                        padding: 2px 8px;
                        border-radius: 20px;
                        font-size: 0.75em;
                        font-weight: bold;
                    }
                    .qual-high  { background: rgba(0,255,100,0.2); color: #00ff88; }
                    .qual-mid   { background: rgba(255,200,0,0.2); color: #ffc800; }
                    .qual-low   { background: rgba(255,80,80,0.2); color: #ff5050; }
                    .house-id   { color: #a0c4ff; font-weight: bold; }
                    .section-title {
                        text-align: center;
                        color: #00d2ff;
                        font-size: 1.3em;
                        margin: 30px 0 15px 0;
                        letter-spacing: 1px;
                    }
                    .stats-grid {
                        display: grid;
                        grid-template-columns: repeat(4, 1fr);
                        gap: 15px;
                        max-width: 900px;
                        margin: 0 auto 30px auto;
                    }
                    .stat-card {
                        background: rgba(255,255,255,0.05);
                        border: 1px solid rgba(0, 210, 255, 0.2);
                        border-radius: 10px;
                        padding: 15px;
                        text-align: center;
                    }
                    .stat-value { font-size: 1.5em; color: #00d2ff; font-weight: bold; }
                    .stat-label { font-size: 0.75em; color: #a0a0c0; margin-top: 5px; }
                </style>
            </head>
            <body>
                <h1>&#127968; Ames Housing Prices Dataset</h1>

                <!-- Metadata section -->
                <xsl:apply-templates select="housingDataset/metadata"/>

                <!-- Summary stats -->
                <p class="section-title">&#128202; Dataset Overview</p>
                <div class="stats-grid">
                    <div class="stat-card">
                        <div class="stat-value"><xsl:value-of select="count(housingDataset/houses/house)"/></div>
                        <div class="stat-label">Houses Listed</div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-value">
                            $<xsl:value-of select="format-number(sum(housingDataset/houses/house/sale/salePrice) div count(housingDataset/houses/house), '#,##0')"/>
                        </div>
                        <div class="stat-label">Average Price</div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-value">
                            <xsl:value-of select="format-number(sum(housingDataset/houses/house/areas/grLivArea) div count(housingDataset/houses/house), '#,##0')"/> sqft
                        </div>
                        <div class="stat-label">Avg Living Area</div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-value">
                            <xsl:value-of select="count(housingDataset/houses/house[utilities/centralAir='Y'])"/>/<xsl:value-of select="count(housingDataset/houses/house)"/>
                        </div>
                        <div class="stat-label">With Central Air</div>
                    </div>
                </div>

                <!-- Houses Table -->
                <p class="section-title">&#127968; House Records</p>
                <table>
                    <tr>
                        <th>ID</th>
                        <th>Neighborhood</th>
                        <th>Year Built</th>
                        <th>Style</th>
                        <th>Overall Qual</th>
                        <th>Living Area (sqft)</th>
                        <th>Rooms</th>
                        <th>Bedrooms</th>
                        <th>Bathrooms</th>
                        <th>Garage Cars</th>
                        <th>Central Air</th>
                        <th>Year Sold</th>
                        <th>Sale Condition</th>
                        <th>Sale Price</th>
                    </tr>
                    <xsl:apply-templates select="housingDataset/houses/house"/>
                </table>
            </body>
        </html>
    </xsl:template>

    <!-- ============================================================ -->
    <!-- Metadata Template -->
    <!-- ============================================================ -->
    <xsl:template match="metadata">
        <div class="metadata-box">
            <h2>&#128196; Dataset Information</h2>
            <p><strong>Title:</strong> <xsl:value-of select="title"/></p>
            <p><strong>Description:</strong> <xsl:value-of select="description"/></p>
            <p><strong>Source:</strong> <xsl:value-of select="source"/></p>
            <p><strong>Period:</strong> <xsl:value-of select="year"/></p>
            <p><strong>Total Records:</strong> <xsl:value-of select="totalRecords"/></p>
        </div>
    </xsl:template>

    <!-- ============================================================ -->
    <!-- House Row Template -->
    <!-- ============================================================ -->
    <xsl:template match="house">
        <tr>
            <!-- House ID -->
            <td><span class="house-id"><xsl:value-of select="@id"/></span></td>

            <!-- Neighborhood -->
            <td><xsl:value-of select="location/neighborhood"/></td>

            <!-- Year Built -->
            <td><xsl:value-of select="construction/yearBuilt"/></td>

            <!-- House Style -->
            <td><xsl:value-of select="construction/houseStyle"/></td>

            <!-- Overall Quality with color badge -->
            <td>
                <xsl:choose>
                    <xsl:when test="quality/overallQual &gt;= 8">
                        <span class="qual-badge qual-high"><xsl:value-of select="quality/overallQual"/>/10</span>
                    </xsl:when>
                    <xsl:when test="quality/overallQual &gt;= 6">
                        <span class="qual-badge qual-mid"><xsl:value-of select="quality/overallQual"/>/10</span>
                    </xsl:when>
                    <xsl:otherwise>
                        <span class="qual-badge qual-low"><xsl:value-of select="quality/overallQual"/>/10</span>
                    </xsl:otherwise>
                </xsl:choose>
            </td>

            <!-- Living Area -->
            <td><xsl:value-of select="format-number(areas/grLivArea, '#,##0')"/></td>

            <!-- Total Rooms -->
            <td><xsl:value-of select="rooms/totRmsAbvGrd"/></td>

            <!-- Bedrooms -->
            <td><xsl:value-of select="rooms/bedroomAbvGr"/></td>

            <!-- Full + Half Bathrooms -->
            <td><xsl:value-of select="rooms/fullBath"/> full / <xsl:value-of select="rooms/halfBath"/> half</td>

            <!-- Garage Cars -->
            <td><xsl:value-of select="rooms/garageCars"/></td>

            <!-- Central Air -->
            <td>
                <xsl:choose>
                    <xsl:when test="utilities/centralAir = 'Y'">&#9989; Yes</xsl:when>
                    <xsl:otherwise>&#10060; No</xsl:otherwise>
                </xsl:choose>
            </td>

            <!-- Year Sold -->
            <td><xsl:value-of select="sale/yrSold"/></td>

            <!-- Sale Condition -->
            <td><xsl:value-of select="sale/saleCondition"/></td>

            <!-- Sale Price -->
            <td>
                <span class="price-tag">$<xsl:value-of select="format-number(sale/salePrice, '#,##0')"/></span>
            </td>
        </tr>
    </xsl:template>

</xsl:stylesheet>
