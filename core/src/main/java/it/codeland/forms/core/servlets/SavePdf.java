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

@Component(service = { Servlet.class }, property = { "sling.servlet.methods=post",
        "sling.servlet.paths=/bin/save_me" })
public class SavePdf extends SlingAllMethodsServlet {

    private static final long serialVersionUID = 1L;

    @Reference
    FormsService formsService;

    @Reference
    PdfConversionUtils pdfConversionUtils; // Inject or instantiate appropriately

    private static final Logger log = LoggerFactory.getLogger(SavePdf.class);

    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        String file_path = request.getParameter("save_location");
    
        InputStream pdf_document_is = null;
        InputStream xml_is = null;
        javax.servlet.http.Part pdf_document_part = null;
        javax.servlet.http.Part xml_data_part = null;
        try {
            pdf_document_part = request.getPart("pdf_file");
            xml_data_part = request.getPart("xml_data_file");
            pdf_document_is = pdf_document_part.getInputStream();
            xml_is = xml_data_part.getInputStream();
    
            // Check if the uploaded file is an XDP file
            if (isXdpFile(pdf_document_part)) {
                log.info("############Checking xdp file has started######");
                // Convert XDP to dynamic PDF
                pdf_document_is = pdfConversionUtils.convertXdpToDynamicPdf(pdf_document_is);
            }
    
            Document data_merged_document = formsService.importData(new Document(pdf_document_is),
                    new Document(xml_is));
    
            data_merged_document.copyToFile(new File(file_path));
    
            byte[] pdfByteArray = convertInputStreamToByteArray(data_merged_document.getInputStream());
    
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "inline; filename=\"" + file_path + "\"");
    
            // PDF content to the response
            try (ServletOutputStream out = response.getOutputStream()) {
                out.write(pdfByteArray);
            }
    
        } catch (Exception e) {
            // Log the error using SLF4J
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

    // Check if the file is an XDP file
    private boolean isXdpFile(javax.servlet.http.Part part) {
        return part.getContentType().equalsIgnoreCase("application/vnd.adobe.xdp+xml");
    }
}
