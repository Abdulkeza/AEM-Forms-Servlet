package it.codeland.forms.core.utils;

import com.adobe.aemfd.docmanager.Document;
import com.adobe.fd.forms.api.FormsService;
import com.adobe.fd.forms.api.PDFFormRenderOptions;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

@Component(service = PdfConversionUtils.class)
public class PdfConversionUtils {

    private static final Logger log = LoggerFactory.getLogger(PdfConversionUtils.class);

    @Reference
    private FormsService formsService;

    /**
     * @param xdpInputStream
     * @param xdpTemplatePath
     * @return
     */
    public InputStream convertXdpToDynamicPdf(InputStream xdpInputStream, String xdpTemplatePath) {
        log.error("++++++converting xdp started++++++");
        try {
            PDFFormRenderOptions renderOptions = new PDFFormRenderOptions();
            renderOptions.setAcrobatVersion(com.adobe.fd.forms.api.AcrobatVersion.Acrobat_11);

            Document dynamicPdfDocument = formsService.renderPDFForm(xdpTemplatePath, null, renderOptions);

            return dynamicPdfDocument.getInputStream();
        } catch (Exception e) {
            log.error("+++++++++XDP to dynamic PDF conversion failed: {}", e.getMessage(), e);
            return null;
        }
    }
}
