package it.codeland.forms.core.utils;


import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XDPToPDFConverter {

    private static final Logger log = LoggerFactory.getLogger(PdfConversionUtils.class);

    /**
     * @param xdpInputStream
     * @return
     */
    public InputStream convertXdpToDynamicPdf(InputStream xdpInputStream) {
        log.error("++++++converting xdp started++++++");
        try {
            // Read XDP file as bytes
            byte[] xdpBytes = readInputStream(xdpInputStream);

            // Encode XDP bytes to Base64
            String base64Xdp = Base64.getEncoder().encodeToString(xdpBytes);

            // Decode Base64 to bytes
            byte[] pdfBytes = Base64.getDecoder().decode(base64Xdp);

            // Create Document from bytes (assuming it's a PDF document)
            return new ByteArrayInputStream(pdfBytes);
        } catch (Exception e) {
            log.error("+++++++++XDP to dynamic PDF conversion failed: {}", e.getMessage(), e);
            return null;
        }
    }

    private byte[] readInputStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[1024];
        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        buffer.flush();
        return buffer.toByteArray();
    }
}
