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

@Component(service = { Servlet.class }, property = { "sling.servlet.methods=post",
        "sling.servlet.paths=/bin/mergedataWithAcroform" })
public class PdfGenerationServlet2 extends SlingAllMethodsServlet {

    private static final long serialVersionUID = 1L;

    @Reference
    FormsService formsService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        String file_path = request.getParameter("save_location");

        try {
            //@desc Get XDP and XML from the request
            InputStream xdpInputStream = request.getPart("xdp_file").getInputStream();
            InputStream xmlInputStream = request.getPart("xml_data_file").getInputStream();

            //@desc Validate XML data against the Schema
            validateXmlAgainstSchema(xmlInputStream);

            //@desc Load XDP and XML documents
            Document xdpDocument = new Document(xdpInputStream);
            Document xmlDocument = new Document(xmlInputStream);

            //@desc Import XML data into XDP form
            Document mergedDocument = formsService.importData(xdpDocument, xmlDocument);

            //@desc Save the merged document as a PDF in my local pc
            mergedDocument.copyToFile(new File(file_path));

            //@desc @optional, set the response content type to "application/pdf"
            response.setContentType("application/pdf");

     
            logger.info("PDF generation successful");

        } catch (Exception e) {

            logger.error("Error during PDF generation", e);

            try {
                response.sendError(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            } catch (IOException e1) {

                logger.error("Error sending error response", e1);
            }
        }
    }


    //@desc validation
    private void validateXmlAgainstSchema(InputStream xmlInputStream) throws Exception {
        //@desc Load the XML Schema
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = schemaFactory.newSchema(new StreamSource(
                new File("/home/me/Desktop/codeland/FORMS/aem-forms-servlet-exercise/data/dataSchema.xsd")));

        //@desc validator
        Validator validator = schema.newValidator();

        //@desc Validate the actual XML data
        validator.validate(new javax.xml.transform.stream.StreamSource(xmlInputStream));
    }
}

// /bin/mergedataWithAcroform
