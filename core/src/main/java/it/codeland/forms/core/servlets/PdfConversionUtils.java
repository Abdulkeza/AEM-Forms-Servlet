package it.codeland.forms.core.servlets;

import com.adobe.aemfd.docmanager.Document;
import com.adobe.fd.forms.api.FormsService;
import com.adobe.fd.forms.api.PDFFormRenderOptions;

import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

public class PdfConversionUtils {

    private static final Logger log = LoggerFactory.getLogger(PdfConversionUtils.class);

    @Reference
    FormsService formsService;
   // private FormsService formsService; 

    /**
     * Converts an XDP InputStream to a dynamic PDF InputStream.
     *
     * @param xdpInputStream 
     * @return InputStream representing the generated dynamic PDF; null in case of an error
     */
    public InputStream convertXdpToDynamicPdf(InputStream xdpInputStream) {
        try {

            log.info("++++++converting xdp started++++++");
            // Specify render options if needed
            PDFFormRenderOptions renderOptions = new PDFFormRenderOptions();
            renderOptions.setAcrobatVersion(com.adobe.fd.forms.api.AcrobatVersion.Acrobat_11);

            // Perform XDP to dynamic PDF conversion
            Document dynamicPdfDocument = formsService.renderPDFForm("/content/dam/path/to/your/template.xdp", null, renderOptions);

            // Convert the dynamic PDF Document to InputStream
            return dynamicPdfDocument.getInputStream();
        } catch (Exception e) {
            // Log the error using SLF4J
            log.error("+++++++++XDP to dynamic PDF conversion failed: {}", e.getMessage(), e);
            return null;
        }
    }


}
