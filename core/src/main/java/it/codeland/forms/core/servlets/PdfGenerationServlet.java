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

import com.adobe.aemfd.docmanager.Document;
import com.adobe.fd.forms.api.FormsService;

@Component(service = { Servlet.class }, property = { "sling.servlet.methods=post",
        "sling.servlet.paths=/bin/generatePdf" })
public class PdfGenerationServlet extends SlingAllMethodsServlet {

    private static final long serialVersionUID = 1L;

    @Reference
    FormsService formsService;

    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        String file_path = request.getParameter("save_location");

        java.io.InputStream pdf_document_is = null;
        java.io.InputStream xml_is = null;
        javax.servlet.http.Part pdf_document_part = null;
        javax.servlet.http.Part xml_data_part = null;
        try {
            pdf_document_part = request.getPart("pdf_file");
            xml_data_part = request.getPart("xml_data_file");
            pdf_document_is = pdf_document_part.getInputStream();
            xml_is = xml_data_part.getInputStream();
            Document data_merged_document = formsService.importData(new Document(pdf_document_is),
                    new Document(xml_is));

            // Save the document to the specified file path
            data_merged_document.copyToFile(new File(file_path));

            // Convert PDF content to a byte array
            byte[] pdfByteArray = convertInputStreamToByteArray(data_merged_document.getInputStream());

            // Set the content type and headers for PDF response
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "inline; filename=\"" + file_path + "\"");

            // Write the PDF content to the response
            try (ServletOutputStream out = response.getOutputStream()) {
                out.write(pdfByteArray);
            }

        } catch (Exception e) {
            try {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            } catch (IOException e1) {
                e1.printStackTrace();
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
}
