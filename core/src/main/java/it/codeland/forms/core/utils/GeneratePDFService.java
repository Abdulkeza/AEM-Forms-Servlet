package it.codeland.forms.core.utils;

import com.adobe.aemfd.docmanager.Document;
import com.adobe.pdfg.exception.ConversionException;
import com.adobe.pdfg.exception.FileFormatNotSupportedException;
import com.adobe.pdfg.exception.InvalidParameterException;
import com.adobe.pdfg.result.CreatePDFResult;
import com.adobe.pdfg.result.ExportPDFResult;
import com.adobe.pdfg.result.HtmlToPdfResult;
import com.adobe.pdfg.result.OptimizePDFResult;

import java.util.Map;

public interface GeneratePDFService {
   Map createPDF(Document var1, String var2, String var3, String var4, String var5, Document var6, Document var7) throws ConversionException, InvalidParameterException, FileFormatNotSupportedException;

   CreatePDFResult createPDF2(Document var1, String var2, String var3, String var4, String var5, Document var6, Document var7) throws ConversionException, InvalidParameterException, FileFormatNotSupportedException;

   HtmlToPdfResult htmlFileToPdf(Document var1, String var2, String var3, Document var4, Document var5) throws ConversionException, InvalidParameterException, FileFormatNotSupportedException;

   Map htmlToPdf(String var1, String var2, String var3, Document var4, Document var5) throws ConversionException, InvalidParameterException, FileFormatNotSupportedException;

   HtmlToPdfResult htmlToPdf2(String var1, String var2, String var3, Document var4, Document var5) throws ConversionException, InvalidParameterException, FileFormatNotSupportedException;

   Map exportPDF(Document var1, String var2, String var3, Document var4) throws ConversionException, InvalidParameterException, FileFormatNotSupportedException;

   ExportPDFResult exportPDF2(Document var1, String var2, String var3, Document var4) throws ConversionException, InvalidParameterException, FileFormatNotSupportedException;

   ExportPDFResult exportPDF3(Document var1, String var2, String var3, Document var4, Document var5) throws ConversionException, InvalidParameterException, FileFormatNotSupportedException;

   OptimizePDFResult optimizePDF(Document var1, String var2, Document var3) throws ConversionException, InvalidParameterException, FileFormatNotSupportedException;

   void updateGeneralConfig(Map<String, String[]> var1);
}
