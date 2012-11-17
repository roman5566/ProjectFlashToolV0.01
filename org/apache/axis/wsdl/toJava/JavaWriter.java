/*     */ package org.apache.axis.wsdl.toJava;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStreamWriter;
/*     */ import java.io.PrintStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.util.List;
/*     */ import java.util.StringTokenizer;
/*     */ import org.apache.axis.utils.Messages;
/*     */ import org.apache.axis.wsdl.gen.Generator;
/*     */ import org.w3c.dom.Element;
/*     */ import org.w3c.dom.Node;
/*     */ 
/*     */ public abstract class JavaWriter
/*     */   implements Generator
/*     */ {
/*     */   protected static final int LINE_LENGTH = 65;
/*     */   protected Emitter emitter;
/*     */   protected String type;
/*     */ 
/*     */   protected JavaWriter(Emitter emitter, String type)
/*     */   {
/*  96 */     this.emitter = emitter;
/*  97 */     this.type = type;
/*     */   }
/*     */ 
/*     */   public void generate()
/*     */     throws IOException
/*     */   {
/* 107 */     String file = getFileName();
/*     */ 
/* 109 */     if (isFileGenerated(file)) {
/* 110 */       throw new DuplicateFileException(Messages.getMessage("duplicateFile00", file), file);
/*     */     }
/*     */ 
/* 114 */     registerFile(file);
/*     */ 
/* 116 */     if (this.emitter.isVerbose()) {
/* 117 */       String msg = verboseMessage(file);
/*     */ 
/* 119 */       if (msg != null) {
/* 120 */         System.out.println(msg);
/*     */       }
/*     */     }
/*     */ 
/* 124 */     PrintWriter pw = getPrintWriter(file);
/*     */ 
/* 126 */     writeFileHeader(pw);
/* 127 */     writeFileBody(pw);
/* 128 */     writeFileFooter(pw);
/* 129 */     closePrintWriter(pw);
/*     */   }
/*     */ 
/*     */   protected abstract String getFileName();
/*     */ 
/*     */   protected boolean isFileGenerated(String file)
/*     */   {
/* 150 */     return this.emitter.getGeneratedFileNames().contains(file);
/*     */   }
/*     */ 
/*     */   protected void registerFile(String file)
/*     */   {
/* 161 */     this.emitter.getGeneratedFileInfo().add(file, null, this.type);
/*     */   }
/*     */ 
/*     */   protected String verboseMessage(String file)
/*     */   {
/* 172 */     return Messages.getMessage("generating", file);
/*     */   }
/*     */ 
/*     */   protected PrintWriter getPrintWriter(String filename)
/*     */     throws IOException
/*     */   {
/* 185 */     File file = new File(filename);
/* 186 */     File parent = new File(file.getParent());
/*     */ 
/* 188 */     parent.mkdirs();
/*     */ 
/* 190 */     FileOutputStream out = new FileOutputStream(file);
/* 191 */     OutputStreamWriter writer = new OutputStreamWriter(out, "UTF-8");
/* 192 */     return new PrintWriter(writer);
/*     */   }
/*     */ 
/*     */   protected void writeFileHeader(PrintWriter pw)
/*     */     throws IOException
/*     */   {
/*     */   }
/*     */ 
/*     */   protected abstract void writeFileBody(PrintWriter paramPrintWriter)
/*     */     throws IOException;
/*     */ 
/*     */   protected void writeFileFooter(PrintWriter pw)
/*     */     throws IOException
/*     */   {
/*     */   }
/*     */ 
/*     */   protected void closePrintWriter(PrintWriter pw)
/*     */   {
/* 233 */     pw.close();
/*     */   }
/*     */ 
/*     */   protected String getJavadocDescriptionPart(String documentation, boolean addTab)
/*     */   {
/* 242 */     if (documentation == null) {
/* 243 */       return "";
/*     */     }
/*     */ 
/* 246 */     String doc = documentation.trim();
/*     */ 
/* 248 */     if (documentation.trim().length() == 0)
/*     */     {
/* 250 */       return doc;
/*     */     }
/*     */ 
/* 254 */     StringTokenizer st = new StringTokenizer(doc, "@");
/*     */     StringBuffer newComments;
/* 256 */     if (st.hasMoreTokens()) {
/* 257 */       String token = st.nextToken();
/* 258 */       boolean startLine = (Character.isWhitespace(token.charAt(token.length() - 1))) && (token.charAt(token.length() - 1) != '\n');
/*     */ 
/* 260 */       StringBuffer newComments = new StringBuffer(token);
/*     */ 
/* 262 */       while (st.hasMoreTokens()) {
/* 263 */         token = st.nextToken();
/*     */ 
/* 265 */         if (startLine) {
/* 266 */           newComments.append('\n');
/*     */         }
/* 268 */         newComments.append('@');
/* 269 */         startLine = Character.isWhitespace(token.charAt(token.length() - 1)) & token.charAt(token.length() - 1) != '\n';
/*     */ 
/* 272 */         newComments.append(token);
/*     */       }
/*     */     } else {
/* 275 */       newComments = new StringBuffer(doc);
/*     */     }
/* 277 */     newComments.insert(0, addTab ? "     * " : " * ");
/*     */ 
/* 281 */     int pos = newComments.toString().indexOf("*/");
/* 282 */     while (pos >= 0) {
/* 283 */       newComments.insert(pos + 1, ' ');
/* 284 */       pos = newComments.toString().indexOf("*/");
/*     */     }
/*     */ 
/* 288 */     int lineStart = 0;
/* 289 */     int newlinePos = 0;
/* 290 */     while (lineStart < newComments.length()) {
/* 291 */       newlinePos = newComments.toString().indexOf("\n", lineStart);
/* 292 */       if (newlinePos == -1) {
/* 293 */         newlinePos = newComments.length();
/*     */       }
/* 295 */       if (newlinePos - lineStart > 65)
/*     */       {
/* 297 */         lineStart += 65;
/*     */ 
/* 299 */         while ((lineStart < newComments.length()) && (!Character.isWhitespace(newComments.charAt(lineStart)))) {
/* 300 */           lineStart++;
/*     */         }
/*     */ 
/* 303 */         if (lineStart < newComments.length())
/*     */         {
/* 305 */           char next = newComments.charAt(lineStart);
/*     */ 
/* 307 */           if ((next == '\r') || (next == '\n'))
/*     */           {
/* 309 */             newComments.insert(lineStart + 1, addTab ? "     * " : " * ");
/* 310 */             lineStart += (addTab ? 8 : 4);
/*     */           } else {
/* 312 */             newComments.insert(lineStart, addTab ? "\n     * " : "\n * ");
/* 313 */             lineStart += (addTab ? 8 : 4);
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/* 319 */         while ((lineStart < newComments.length()) && (newComments.charAt(lineStart) == ' '))
/* 320 */           newComments.delete(lineStart, lineStart + 1); continue;
/*     */       }
/* 323 */       newlinePos++; if (newlinePos < newComments.length()) {
/* 324 */         newComments.insert(newlinePos, addTab ? "     * " : " * ");
/*     */       }
/* 326 */       lineStart = newlinePos;
/* 327 */       lineStart += (addTab ? 7 : 3);
/*     */     }
/*     */ 
/* 331 */     return newComments.toString();
/*     */   }
/*     */ 
/*     */   protected void writeComment(PrintWriter pw, Element element)
/*     */   {
/* 341 */     writeComment(pw, element, true);
/*     */   }
/*     */ 
/*     */   protected void writeComment(PrintWriter pw, Element element, boolean addTab)
/*     */   {
/* 353 */     if (element == null) {
/* 354 */       return;
/*     */     }
/*     */ 
/* 357 */     Node child = element.getFirstChild();
/*     */ 
/* 359 */     if (child == null) {
/* 360 */       return;
/*     */     }
/*     */ 
/* 363 */     String comment = child.getNodeValue();
/*     */ 
/* 365 */     if (comment != null) {
/* 366 */       int start = 0;
/*     */ 
/* 368 */       pw.println();
/*     */ 
/* 370 */       pw.println(addTab ? "    /**" : "/**");
/* 371 */       pw.println(getJavadocDescriptionPart(comment, addTab));
/* 372 */       pw.println(addTab ? "     */" : " */");
/*     */     }
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.wsdl.toJava.JavaWriter
 * JD-Core Version:    0.6.0
 */