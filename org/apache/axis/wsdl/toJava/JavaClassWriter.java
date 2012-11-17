/*     */ package org.apache.axis.wsdl.toJava;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintWriter;
/*     */ import org.apache.axis.Version;
/*     */ import org.apache.axis.utils.Messages;
/*     */ 
/*     */ public abstract class JavaClassWriter extends JavaWriter
/*     */ {
/*     */   protected Namespaces namespaces;
/*     */   protected String className;
/*     */   protected String packageName;
/*     */ 
/*     */   protected JavaClassWriter(Emitter emitter, String fullClassName, String type)
/*     */   {
/* 130 */     super(emitter, type);
/*     */ 
/* 132 */     this.namespaces = emitter.getNamespaces();
/* 133 */     this.packageName = Utils.getJavaPackageName(fullClassName);
/* 134 */     this.className = Utils.getJavaLocalName(fullClassName);
/*     */   }
/*     */ 
/*     */   protected String getFileName()
/*     */   {
/* 144 */     return this.namespaces.toDir(this.packageName) + this.className + ".java";
/*     */   }
/*     */ 
/*     */   protected void registerFile(String file)
/*     */   {
/* 158 */     String pkg = getPackage();
/*     */     String fqClass;
/*     */     String fqClass;
/* 160 */     if ((pkg != null) && (pkg.length() > 0))
/* 161 */       fqClass = pkg + '.' + getClassName();
/*     */     else {
/* 163 */       fqClass = getClassName();
/*     */     }
/*     */ 
/* 166 */     this.emitter.getGeneratedFileInfo().add(file, fqClass, this.type);
/*     */   }
/*     */ 
/*     */   protected void writeFileHeader(PrintWriter pw)
/*     */     throws IOException
/*     */   {
/* 178 */     writeHeaderComments(pw);
/* 179 */     writePackage(pw);
/*     */ 
/* 182 */     pw.println(getClassModifiers() + getClassText() + getClassName() + ' ' + getExtendsText() + getImplementsText() + "{");
/*     */   }
/*     */ 
/*     */   protected void writeHeaderComments(PrintWriter pw)
/*     */     throws IOException
/*     */   {
/* 194 */     String localFile = getFileName();
/* 195 */     int lastSepChar = localFile.lastIndexOf(File.separatorChar);
/*     */ 
/* 197 */     if (lastSepChar >= 0) {
/* 198 */       localFile = localFile.substring(lastSepChar + 1);
/*     */     }
/*     */ 
/* 201 */     pw.println("/**");
/* 202 */     pw.println(" * " + localFile);
/* 203 */     pw.println(" *");
/* 204 */     pw.println(" * " + Messages.getMessage("wsdlGenLine00"));
/* 205 */     pw.println(" * " + Messages.getMessage("wsdlGenLine01", Version.getVersionText()));
/*     */ 
/* 208 */     pw.println(" */");
/* 209 */     pw.println();
/*     */   }
/*     */ 
/*     */   protected void writePackage(PrintWriter pw)
/*     */     throws IOException
/*     */   {
/* 220 */     String pkg = getPackage();
/* 221 */     if ((pkg != null) && (pkg.length() > 0)) {
/* 222 */       pw.println("package " + pkg + ";");
/* 223 */       pw.println();
/*     */     }
/*     */   }
/*     */ 
/*     */   protected String getClassModifiers()
/*     */   {
/* 234 */     return "public ";
/*     */   }
/*     */ 
/*     */   protected String getClassText()
/*     */   {
/* 244 */     return "class ";
/*     */   }
/*     */ 
/*     */   protected String getExtendsText()
/*     */   {
/* 255 */     return "";
/*     */   }
/*     */ 
/*     */   protected String getImplementsText()
/*     */   {
/* 266 */     return "";
/*     */   }
/*     */ 
/*     */   protected String getPackage()
/*     */   {
/* 275 */     return this.packageName;
/*     */   }
/*     */ 
/*     */   protected String getClassName()
/*     */   {
/* 284 */     return this.className;
/*     */   }
/*     */ 
/*     */   protected void writeFileFooter(PrintWriter pw)
/*     */     throws IOException
/*     */   {
/* 294 */     super.writeFileFooter(pw);
/* 295 */     pw.println('}');
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.wsdl.toJava.JavaClassWriter
 * JD-Core Version:    0.6.0
 */