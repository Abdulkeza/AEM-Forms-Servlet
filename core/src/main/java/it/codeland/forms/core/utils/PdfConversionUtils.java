package it.codeland.forms.core.utils;

import com.adobe.aemfd.docmanager.Document;
import com.adobe.pdfg.exception.ConversionException;
import com.adobe.pdfg.exception.FileFormatNotSupportedException;
import com.adobe.pdfg.exception.InvalidParameterException;
import com.adobe.pdfg.result.CreatePDFResult;
import com.adobe.pdfg.service.api.GeneratePDFService;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

@Component(service = PdfConversionUtils.class)
public class PdfConversionUtils {

    private static final Logger log = LoggerFactory.getLogger(PdfConversionUtils.class);

    @Reference
    private GeneratePDFService generatePDFService;

    public InputStream convertXdpToDynamicPdf(InputStream xdpInputStream, String xdpTemplatePath) {
        log.info("Converting XDP to dynamic PDF started...");

        try {
            // Create Document object from InputStream
            Document xdpDocument = new Document(xdpInputStream);

            // Invoke createPDF2 method with Document object
            CreatePDFResult result = generatePDFService.createPDF2(xdpDocument, "xdp", "pdf", xdpTemplatePath, null, null, null);
            Document createdDocument = result.getCreatedDocument();
            if (createdDocument != null) {
                return createdDocument.getInputStream();
            } else {
                log.error("Conversion result returned a null document.");
                return null;
            }
        } catch (ConversionException | InvalidParameterException | FileFormatNotSupportedException | IOException e) {
            log.error("XDP to dynamic PDF conversion failed: {}", e.getMessage(), e);
            return null;
        }
    }
}
