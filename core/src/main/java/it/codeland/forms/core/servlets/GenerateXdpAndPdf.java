package it.codeland.forms.core.servlets;

import javax.servlet.Servlet;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.aemfd.docmanager.Document;
import com.adobe.fd.forms.api.FormsService;

import it.codeland.forms.core.utils.PdfConversionUtils;

@Component(service = { Servlet.class }, property = { "sling.servlet.methods=POST",
        "sling.servlet.paths=/bin/savePdf" })
public class GenerateXdpAndPdf extends SlingAllMethodsServlet {

    private static final long serialVersionUID = 1L;

    @Reference
    FormsService formsService;

    @Reference
    PdfConversionUtils pdfConversionUtils;

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        log.error("++++++++++++++++we are starting now now +++++++++++++++++");
        String file_path = request.getParameter("save_location");
        String xdpTemplatePath = request.getParameter("xdp_template_path");

        InputStream pdf_document_is = null;
        InputStream xml_is = null;
        // InputStream xdp_document_is = null;
        javax.servlet.http.Part pdf_document_part = null;
        javax.servlet.http.Part xml_data_part = null;
        // javax.servlet.http.Part xdp_document_part = null;
        try {
            pdf_document_part = request.getPart("pdf_file");
            xml_data_part = request.getPart("xml_data_file");

            // xdp_document_part = request.getPart("xdp_file");
            log.info("############ooops, we good now ######");
            pdf_document_is = pdf_document_part.getInputStream();
            xml_is = xml_data_part.getInputStream();
            // xdp_document_is = xdp_document_part.getInputStream();

            if (isXdpFile(pdf_document_part)) {
                log.error("############Checking xdp file has started######");
                // Convert XDP to dynamic PDF with the provided template path
                pdf_document_is = pdfConversionUtils.convertXdpToDynamicPdf(pdf_document_is, xdpTemplatePath);
            }

            Document data_merged_document = formsService.importData(new Document(pdf_document_is),
                    new Document(xml_is));

            data_merged_document.copyToFile(new File(file_path));

            byte[] pdfByteArray = convertInputStreamToByteArray(data_merged_document.getInputStream());

            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "inline; filename=\"" + file_path + "\"");

            try (ServletOutputStream out = response.getOutputStream()) {
                out.write(pdfByteArray);
            }

        } catch (Exception e) {
            log.error("Error in SavePdf servlet: {}", e.getMessage(), e);

            try {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            } catch (IOException e1) {
                log.error("Error sending error response: {}", e1.getMessage(), e1);
            }
        }
    }

    private byte[] convertInputStreamToByteArray(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int bytesRead;

        while ((bytesRead = inputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, bytesRead);
        }

        return byteArrayOutputStream.toByteArray();
    }

    private boolean isXdpFile(javax.servlet.http.Part part) {
        return part.getContentType().equalsIgnoreCase("application/vnd.adobe.xdp+xml");
    }
}
