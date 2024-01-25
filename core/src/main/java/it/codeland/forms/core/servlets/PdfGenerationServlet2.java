package it.codeland.forms.core.servlets;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import com.adobe.aemfd.docmanager.Document;
import com.adobe.fd.forms.api.FormsService;

@Component(service = { Servlet.class }, property = { "sling.servlet.methods=POST",
        "sling.servlet.paths=/bin/savePdf" })
public class PdfGenerationServlet2 extends SlingAllMethodsServlet {

    private static final long serialVersionUID = 1L;

    @Reference
    FormsService formsService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) {

        String file_path = request.getParameter("save_location");

        try {

            InputStream xdpInputStream = request.getPart("xdp_file").getInputStream();
            InputStream xmlInputStream = request.getPart("xml_data_file").getInputStream();

            // @desc Validate XML data against the Schema
            validateXmlAgainstSchema(xmlInputStream);


            Document xdpDocument = new Document(xdpInputStream);
            Document xmlDocument = new Document(xmlInputStream);


             // Log XDP and XML content for debugging
            logDocumentContent("XDP Document", xdpDocument);
            logDocumentContent("XML Document", xmlDocument);

            logger.info("######### before merge document #######");

            Document mergedDocument = formsService.importData(xdpDocument, xmlDocument);

            logger.error("&&&&&&&&&&& after merge document &&&&&&&&&&&&&&");

            mergedDocument.copyToFile(new File(file_path));

            response.setContentType("application/pdf");
            logger.info("PDF generation successful");

        } catch (Exception e) {

            logger.error("oooops! Error during PDF generation", e);

            try {
                response.sendError(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            } catch (IOException e1) {

                logger.error("Error sending error response", e1);
            }
        }

    }


    // @desc validation
    private void validateXmlAgainstSchema(InputStream xmlInputStream) throws Exception {
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = schemaFactory.newSchema(new StreamSource(
                new File("/home/me/Desktop/codeland/FORMS/aem-forms-servlet-exercise/data/dataSchema.xsd")));

        Validator validator = schema.newValidator();

        validator.validate(new javax.xml.transform.stream.StreamSource(xmlInputStream));
    }


private void logDocumentContent(String documentName, Document document) {
    logger.info(documentName + " Content: " + document); 
}
}

