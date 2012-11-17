/*     */ package org.apache.axis.wsdl.toJava;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStreamWriter;
/*     */ import java.io.PrintWriter;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import javax.wsdl.Definition;
/*     */ import javax.wsdl.Port;
/*     */ import javax.wsdl.Service;
/*     */ import javax.xml.namespace.QName;
/*     */ import org.apache.axis.utils.Messages;
/*     */ import org.apache.axis.wsdl.symbolTable.SymbolTable;
/*     */ 
/*     */ public class JavaUndeployWriter extends JavaWriter
/*     */ {
/*     */   protected Definition definition;
/*     */ 
/*     */   public JavaUndeployWriter(Emitter emitter, Definition definition, SymbolTable notUsed)
/*     */   {
/*  51 */     super(emitter, "undeploy");
/*     */ 
/*  53 */     this.definition = definition;
/*     */   }
/*     */ 
/*     */   public void generate()
/*     */     throws IOException
/*     */   {
/*  64 */     if (this.emitter.isServerSide())
/*  65 */       super.generate();
/*     */   }
/*     */ 
/*     */   protected String getFileName()
/*     */   {
/*  77 */     String dir = this.emitter.getNamespaces().getAsDir(this.definition.getTargetNamespace());
/*     */ 
/*  80 */     return dir + "undeploy.wsdd";
/*     */   }
/*     */ 
/*     */   protected void writeFileHeader(PrintWriter pw)
/*     */     throws IOException
/*     */   {
/*  91 */     pw.println(Messages.getMessage("deploy01"));
/*  92 */     pw.println(Messages.getMessage("deploy02"));
/*  93 */     pw.println(Messages.getMessage("deploy04"));
/*  94 */     pw.println(Messages.getMessage("deploy05"));
/*  95 */     pw.println(Messages.getMessage("deploy06"));
/*  96 */     pw.println(Messages.getMessage("deploy08"));
/*  97 */     pw.println(Messages.getMessage("deploy09"));
/*  98 */     pw.println();
/*  99 */     pw.println("<undeployment");
/* 100 */     pw.println("    xmlns=\"http://xml.apache.org/axis/wsdd/\">");
/*     */   }
/*     */ 
/*     */   protected void writeFileBody(PrintWriter pw)
/*     */     throws IOException
/*     */   {
/* 110 */     writeDeployServices(pw);
/* 111 */     pw.println("</undeployment>");
/*     */   }
/*     */ 
/*     */   protected void writeDeployServices(PrintWriter pw)
/*     */     throws IOException
/*     */   {
/* 123 */     Map serviceMap = this.definition.getServices();
/*     */ 
/* 125 */     Iterator mapIterator = serviceMap.values().iterator();
/* 126 */     while (mapIterator.hasNext()) {
/* 127 */       Service myService = (Service)mapIterator.next();
/*     */ 
/* 129 */       pw.println();
/* 130 */       pw.println("  <!-- " + Messages.getMessage("wsdlService00", myService.getQName().getLocalPart()) + " -->");
/*     */ 
/* 134 */       pw.println();
/*     */ 
/* 136 */       Iterator portIterator = myService.getPorts().values().iterator();
/* 137 */       while (portIterator.hasNext()) {
/* 138 */         Port myPort = (Port)portIterator.next();
/*     */ 
/* 140 */         writeDeployPort(pw, myPort);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void writeDeployPort(PrintWriter pw, Port port)
/*     */     throws IOException
/*     */   {
/* 154 */     String serviceName = port.getName();
/*     */ 
/* 156 */     pw.println("  <service name=\"" + serviceName + "\"/>");
/*     */   }
/*     */ 
/*     */   protected PrintWriter getPrintWriter(String filename)
/*     */     throws IOException
/*     */   {
/* 168 */     File file = new File(filename);
/* 169 */     File parent = new File(file.getParent());
/*     */ 
/* 171 */     parent.mkdirs();
/*     */ 
/* 173 */     FileOutputStream out = new FileOutputStream(file);
/* 174 */     OutputStreamWriter writer = new OutputStreamWriter(out, "UTF-8");
/*     */ 
/* 176 */     return new PrintWriter(writer);
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.wsdl.toJava.JavaUndeployWriter
 * JD-Core Version:    0.6.0
 */