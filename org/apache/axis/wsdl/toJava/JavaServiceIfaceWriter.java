/*     */ package org.apache.axis.wsdl.toJava;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.PrintWriter;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import javax.wsdl.Binding;
/*     */ import javax.wsdl.Port;
/*     */ import javax.wsdl.PortType;
/*     */ import javax.wsdl.Service;
/*     */ import javax.xml.namespace.QName;
/*     */ import javax.xml.rpc.ServiceException;
/*     */ import org.apache.axis.utils.JavaUtils;
/*     */ import org.apache.axis.utils.Messages;
/*     */ import org.apache.axis.wsdl.symbolTable.BindingEntry;
/*     */ import org.apache.axis.wsdl.symbolTable.PortTypeEntry;
/*     */ import org.apache.axis.wsdl.symbolTable.ServiceEntry;
/*     */ import org.apache.axis.wsdl.symbolTable.SymbolTable;
/*     */ 
/*     */ public class JavaServiceIfaceWriter extends JavaClassWriter
/*     */ {
/*     */   private Service service;
/*     */   private SymbolTable symbolTable;
/*     */ 
/*     */   protected JavaServiceIfaceWriter(Emitter emitter, ServiceEntry sEntry, SymbolTable symbolTable)
/*     */   {
/*  54 */     super(emitter, sEntry.getName(), "service");
/*     */ 
/*  56 */     this.service = sEntry.getService();
/*  57 */     this.symbolTable = symbolTable;
/*     */   }
/*     */ 
/*     */   protected String getClassText()
/*     */   {
/*  66 */     return "interface ";
/*     */   }
/*     */ 
/*     */   protected String getExtendsText()
/*     */   {
/*  75 */     return "extends javax.xml.rpc.Service ";
/*     */   }
/*     */ 
/*     */   protected void writeFileBody(PrintWriter pw)
/*     */     throws IOException
/*     */   {
/*  87 */     writeComment(pw, this.service.getDocumentationElement(), false);
/*     */ 
/*  90 */     Map portMap = this.service.getPorts();
/*  91 */     Iterator portIterator = portMap.values().iterator();
/*     */ 
/*  94 */     while (portIterator.hasNext()) {
/*  95 */       Port p = (Port)portIterator.next();
/*  96 */       Binding binding = p.getBinding();
/*     */ 
/*  98 */       if (binding == null) {
/*  99 */         throw new IOException(Messages.getMessage("emitFailNoBinding01", new String[] { p.getName() }));
/*     */       }
/*     */ 
/* 104 */       BindingEntry bEntry = this.symbolTable.getBindingEntry(binding.getQName());
/*     */ 
/* 107 */       if (bEntry == null) {
/* 108 */         throw new IOException(Messages.getMessage("emitFailNoBindingEntry01", new String[] { binding.getQName().toString() }));
/*     */       }
/*     */ 
/* 114 */       PortTypeEntry ptEntry = this.symbolTable.getPortTypeEntry(binding.getPortType().getQName());
/*     */ 
/* 117 */       if (ptEntry == null) {
/* 118 */         throw new IOException(Messages.getMessage("emitFailNoPortType01", new String[] { binding.getPortType().getQName().toString() }));
/*     */       }
/*     */ 
/* 126 */       if (bEntry.getBindingType() != 0)
/*     */       {
/*     */         continue;
/*     */       }
/*     */ 
/* 139 */       String portName = (String)bEntry.getDynamicVar("port name:" + p.getName());
/* 140 */       if (portName == null) {
/* 141 */         portName = p.getName();
/*     */       }
/*     */ 
/* 144 */       if (!JavaUtils.isJavaId(portName)) {
/* 145 */         portName = Utils.xmlNameToJavaClass(portName);
/*     */       }
/*     */ 
/* 150 */       String bindingType = (String)bEntry.getDynamicVar(JavaBindingWriter.INTERFACE_NAME);
/*     */ 
/* 154 */       pw.println("    public java.lang.String get" + portName + "Address();");
/*     */ 
/* 156 */       pw.println();
/* 157 */       pw.println("    public " + bindingType + " get" + portName + "() throws " + ServiceException.class.getName() + ";");
/*     */ 
/* 160 */       pw.println();
/* 161 */       pw.println("    public " + bindingType + " get" + portName + "(java.net.URL portAddress) throws " + ServiceException.class.getName() + ";");
/*     */     }
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.wsdl.toJava.JavaServiceIfaceWriter
 * JD-Core Version:    0.6.0
 */