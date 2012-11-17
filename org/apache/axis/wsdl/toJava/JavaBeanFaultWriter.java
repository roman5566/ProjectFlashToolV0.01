/*     */ package org.apache.axis.wsdl.toJava;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.PrintWriter;
/*     */ import java.util.Collections;
/*     */ import java.util.HashSet;
/*     */ import java.util.Set;
/*     */ import java.util.Vector;
/*     */ import org.apache.axis.wsdl.symbolTable.TypeEntry;
/*     */ 
/*     */ public class JavaBeanFaultWriter extends JavaBeanWriter
/*     */ {
/*     */   public static final Set RESERVED_PROPERTY_NAMES;
/*     */ 
/*     */   protected JavaBeanFaultWriter(Emitter emitter, TypeEntry type, Vector elements, TypeEntry extendType, Vector attributes, JavaWriter helper)
/*     */   {
/*  80 */     super(emitter, type, elements, extendType, attributes, helper);
/*     */ 
/*  86 */     this.enableDefaultConstructor = true;
/*     */ 
/*  89 */     this.enableFullConstructor = true;
/*     */ 
/*  92 */     this.enableSetters = true;
/*     */   }
/*     */ 
/*     */   protected String getExtendsText()
/*     */   {
/* 103 */     String extendsText = super.getExtendsText();
/*     */ 
/* 105 */     if (extendsText.equals(""))
/*     */     {
/* 109 */       extendsText = " extends org.apache.axis.AxisFault ";
/*     */     }
/*     */ 
/* 112 */     return extendsText;
/*     */   }
/*     */ 
/*     */   protected void writeFileFooter(PrintWriter pw)
/*     */     throws IOException
/*     */   {
/* 131 */     pw.println();
/* 132 */     pw.println("    /**");
/* 133 */     pw.println("     * Writes the exception data to the faultDetails");
/* 134 */     pw.println("     */");
/* 135 */     pw.println("    public void writeDetails(javax.xml.namespace.QName qname, org.apache.axis.encoding.SerializationContext context) throws java.io.IOException {");
/*     */ 
/* 137 */     pw.println("        context.serialize(qname, null, this);");
/* 138 */     pw.println("    }");
/* 139 */     super.writeFileFooter(pw);
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  46 */     Set temp = new HashSet();
/*     */ 
/*  48 */     temp.add("cause");
/*  49 */     temp.add("message");
/*  50 */     temp.add("localizedMessage");
/*  51 */     temp.add("stackTrace");
/*     */ 
/*  53 */     temp.add("faultActor");
/*  54 */     temp.add("faultCode");
/*  55 */     temp.add("faultDetails");
/*  56 */     temp.add("faultNode");
/*  57 */     temp.add("faultReason");
/*  58 */     temp.add("faultRole");
/*  59 */     temp.add("faultString");
/*  60 */     temp.add("faultSubCodes");
/*  61 */     temp.add("headers");
/*  62 */     RESERVED_PROPERTY_NAMES = Collections.unmodifiableSet(temp);
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.wsdl.toJava.JavaBeanFaultWriter
 * JD-Core Version:    0.6.0
 */