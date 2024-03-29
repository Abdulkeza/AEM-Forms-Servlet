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
import com.adobe.pdfg.result.CreatePDFResult;
import com.adobe.pdfg.service.api.GeneratePDFService;

@Component(service = { Servlet.class }, property = { "sling.servlet.methods=POST",
        "sling.servlet.paths=/bin/savePdf" })
public class GenerateXdpAndPdf extends SlingAllMethodsServlet {

    private static final long serialVersionUID = 1L;

    @Reference
    GeneratePDFService generatePDFService;

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        log.error("++++++++++++++++we are starting now now +++++++++++++++++");
        String file_path = request.getParameter("save_location");

        InputStream pdf_document_is = null;
        InputStream xdp_document_is = null;
        InputStream xml_is = null;
        javax.servlet.http.Part pdf_document_part = null;
        javax.servlet.http.Part xdp_document_part = null;
        javax.servlet.http.Part xml_data_part = null;
        try {
            pdf_document_part = request.getPart("pdf_file");
            xdp_document_part = request.getPart("xdp_file"); // Get XDP file part
            xml_data_part = request.getPart("xml_data_file");

            pdf_document_is = pdf_document_part.getInputStream();
            xdp_document_is = xdp_document_part.getInputStream(); // Get XDP file input stream
            xml_is = xml_data_part.getInputStream();

            if (isXdpFile(xdp_document_part)) {
                log.error("############Checking xdp file has started######");
                // Convert XDP to dynamic PDF with the provided template path
                CreatePDFResult result = generatePDFService.createPDF2(new Document(xdp_document_is), "xdp", "pdf", null, null, null, null);
                pdf_document_is = result.getCreatedDocument().getInputStream();
                log.error("############Checking done ######");
            }

            Document data_merged_document = new Document(pdf_document_is); // Adjust according to your logic

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
