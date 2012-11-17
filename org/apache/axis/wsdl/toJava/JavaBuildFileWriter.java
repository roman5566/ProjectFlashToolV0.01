/*     */ package org.apache.axis.wsdl.toJava;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.PrintWriter;
/*     */ import java.util.StringTokenizer;
/*     */ import javax.wsdl.Definition;
/*     */ import org.apache.axis.wsdl.symbolTable.SymbolTable;
/*     */ 
/*     */ public class JavaBuildFileWriter extends JavaWriter
/*     */ {
/*     */   protected Definition definition;
/*     */   protected SymbolTable symbolTable;
/*     */ 
/*     */   public JavaBuildFileWriter(Emitter emitter, Definition definition, SymbolTable symbolTable)
/*     */   {
/*  59 */     super(emitter, "build");
/*     */ 
/*  61 */     this.definition = definition;
/*  62 */     this.symbolTable = symbolTable;
/*     */   }
/*     */ 
/*     */   protected String getFileName() {
/*  66 */     String dir = this.emitter.getOutputDir();
/*  67 */     if (dir == null) {
/*  68 */       dir = ".";
/*     */     }
/*  70 */     return dir + "/build.xml";
/*     */   }
/*     */ 
/*     */   protected void writeFileBody(PrintWriter out) throws IOException {
/*  74 */     out.write("<?xml version=\"1.0\"?>\n");
/*     */ 
/*  76 */     out.write("<project basedir=\".\" default=\"jar\">\n");
/*  77 */     out.write("    <property name=\"src\" location=\".\"/>\n");
/*  78 */     out.write("    <property name=\"build.classes\" location=\"classes\"/>\n");
/*     */ 
/*  80 */     out.write("    <path id=\"classpath\">\n");
/*  81 */     StringTokenizer tok = getClasspathComponets();
/*  82 */     while (tok.hasMoreTokens()) {
/*  83 */       out.write("        <pathelement location=\"" + tok.nextToken() + "\"/>\n");
/*     */     }
/*  85 */     out.write("    </path>\n");
/*     */ 
/*  87 */     out.write("    <target name=\"compile\">\n");
/*  88 */     out.write("       <mkdir dir=\"${build.classes}\"/>\n");
/*  89 */     out.write("        <javac destdir=\"${build.classes}\" debug=\"on\">\n");
/*  90 */     out.write("            <classpath refid=\"classpath\" />\n");
/*  91 */     out.write("            <src path=\"${src}\"/>\n");
/*  92 */     out.write("        </javac>\n");
/*  93 */     out.write("    </target>\n");
/*     */ 
/*  95 */     out.write("    <target name=\"jar\" depends=\"compile\">\n");
/*  96 */     out.write("        <copy todir=\"${build.classes}\">\n");
/*  97 */     out.write("            <fileset dir=\".\" casesensitive=\"yes\" >\n");
/*  98 */     out.write("                <include name=\"**/*.wsdd\"/>\n");
/*  99 */     out.write("            </fileset>\n");
/* 100 */     out.write("        </copy>\n");
/*     */ 
/* 102 */     out.write("        <jar jarfile=\"" + getJarFileName(this.symbolTable.getWSDLURI()) + ".jar\" basedir=\"${build.classes}\" >\n");
/* 103 */     out.write("        <include name=\"**\" />\n");
/* 104 */     out.write("        <manifest>\n");
/* 105 */     out.write("            <section name=\"org/apache/ws4j2ee\">\n");
/* 106 */     out.write("            <attribute name=\"Implementation-Title\" value=\"Apache Axis\"/>\n");
/* 107 */     out.write("            <attribute name=\"Implementation-Vendor\" value=\"Apache Web Services\"/>\n");
/* 108 */     out.write("            </section>\n");
/* 109 */     out.write("        </manifest>\n");
/* 110 */     out.write("        </jar>\n");
/* 111 */     out.write("        <delete dir=\"${build.classes}\"/>\n");
/* 112 */     out.write("    </target>\n");
/* 113 */     out.write("</project>\n");
/* 114 */     out.close();
/*     */   }
/*     */ 
/*     */   private StringTokenizer getClasspathComponets() {
/* 118 */     String classpath = System.getProperty("java.class.path");
/* 119 */     String spearator = ";";
/* 120 */     if (classpath.indexOf(';') < 0)
/*     */     {
/* 122 */       spearator = ":";
/*     */     }
/*     */ 
/* 125 */     return new StringTokenizer(classpath, spearator);
/*     */   }
/*     */ 
/*     */   private String getJarFileName(String wsdlFile) {
/* 129 */     int index = 0;
/* 130 */     if ((index = wsdlFile.lastIndexOf("/")) > 0) {
/* 131 */       wsdlFile = wsdlFile.substring(index + 1);
/*     */     }
/* 133 */     if ((index = wsdlFile.lastIndexOf("?")) > 0) {
/* 134 */       wsdlFile = wsdlFile.substring(0, index);
/*     */     }
/* 136 */     if ((index = wsdlFile.indexOf('.')) != -1) {
/* 137 */       return wsdlFile.substring(0, index);
/*     */     }
/* 139 */     return wsdlFile;
/*     */   }
/*     */ 
/*     */   public void generate()
/*     */     throws IOException
/*     */   {
/* 147 */     if (this.emitter.isBuildFileWanted())
/* 148 */       super.generate();
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.wsdl.toJava.JavaBuildFileWriter
 * JD-Core Version:    0.6.0
 */