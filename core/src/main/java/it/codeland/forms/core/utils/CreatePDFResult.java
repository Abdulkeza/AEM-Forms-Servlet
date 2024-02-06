package it.codeland.forms.core.utils;

import com.adobe.aemfd.docmanager.Document;

import java.io.Serializable;

public class CreatePDFResult implements Serializable {
   private static final long serialVersionUID = -5702887287056202551L;
   private Document createdDocument = null;
   private Document logDocument = null;

   public CreatePDFResult() {
   }

   public Document getCreatedDocument() {
      return this.createdDocument;
   }

   public Document getLogDocument() {
      return this.logDocument;
   }

   public void setCreatedDocument(Document createdDocument) {
      this.createdDocument = createdDocument;
   }

   public void setLogDocument(Document logDocument) {
      this.logDocument = logDocument;
   }
}
