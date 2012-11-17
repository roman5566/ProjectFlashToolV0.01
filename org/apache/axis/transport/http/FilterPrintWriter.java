/*     */ package org.apache.axis.transport.http;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.io.PrintWriter;
/*     */ import javax.servlet.http.HttpServletResponse;
/*     */ 
/*     */ public class FilterPrintWriter extends PrintWriter
/*     */ {
/*  36 */   private PrintWriter _writer = null;
/*  37 */   private HttpServletResponse _response = null;
/*  38 */   private static OutputStream _sink = new NullOutputStream();
/*     */ 
/*     */   public FilterPrintWriter(HttpServletResponse aResponse) {
/*  41 */     super(_sink);
/*  42 */     this._response = aResponse;
/*     */   }
/*     */ 
/*     */   private PrintWriter getPrintWriter() {
/*  46 */     if (this._writer == null) {
/*     */       try {
/*  48 */         this._writer = this._response.getWriter();
/*     */       } catch (IOException e) {
/*  50 */         throw new RuntimeException(e.toString());
/*     */       }
/*     */     }
/*  53 */     return this._writer;
/*     */   }
/*     */ 
/*     */   public void write(int i) {
/*  57 */     getPrintWriter().write(i);
/*     */   }
/*     */ 
/*     */   public void write(char[] chars) {
/*  61 */     getPrintWriter().write(chars);
/*     */   }
/*     */ 
/*     */   public void write(char[] chars, int i, int i1) {
/*  65 */     getPrintWriter().write(chars, i, i1);
/*     */   }
/*     */ 
/*     */   public void write(String string) {
/*  69 */     getPrintWriter().write(string);
/*     */   }
/*     */ 
/*     */   public void write(String string, int i, int i1) {
/*  73 */     getPrintWriter().write(string, i, i1);
/*     */   }
/*     */ 
/*     */   public void flush() {
/*  77 */     getPrintWriter().flush();
/*     */   }
/*     */ 
/*     */   public void close() {
/*  81 */     getPrintWriter().close();
/*     */   }
/*     */ 
/*     */   public boolean checkError() {
/*  85 */     return getPrintWriter().checkError();
/*     */   }
/*     */ 
/*     */   public void print(boolean b) {
/*  89 */     getPrintWriter().print(b);
/*     */   }
/*     */ 
/*     */   public void print(char c) {
/*  93 */     getPrintWriter().print(c);
/*     */   }
/*     */ 
/*     */   public void print(int i) {
/*  97 */     getPrintWriter().print(i);
/*     */   }
/*     */ 
/*     */   public void print(long l) {
/* 101 */     getPrintWriter().print(l);
/*     */   }
/*     */ 
/*     */   public void print(float v) {
/* 105 */     getPrintWriter().print(v);
/*     */   }
/*     */ 
/*     */   public void print(double v) {
/* 109 */     getPrintWriter().print(v);
/*     */   }
/*     */ 
/*     */   public void print(char[] chars) {
/* 113 */     getPrintWriter().print(chars);
/*     */   }
/*     */ 
/*     */   public void print(String string) {
/* 117 */     getPrintWriter().print(string);
/*     */   }
/*     */ 
/*     */   public void print(Object object) {
/* 121 */     getPrintWriter().print(object);
/*     */   }
/*     */ 
/*     */   public void println() {
/* 125 */     getPrintWriter().println();
/*     */   }
/*     */ 
/*     */   public void println(boolean b) {
/* 129 */     getPrintWriter().println(b);
/*     */   }
/*     */ 
/*     */   public void println(char c) {
/* 133 */     getPrintWriter().println(c);
/*     */   }
/*     */ 
/*     */   public void println(int i) {
/* 137 */     getPrintWriter().println(i);
/*     */   }
/*     */ 
/*     */   public void println(long l) {
/* 141 */     getPrintWriter().println(l);
/*     */   }
/*     */ 
/*     */   public void println(float v) {
/* 145 */     getPrintWriter().println(v);
/*     */   }
/*     */ 
/*     */   public void println(double v) {
/* 149 */     getPrintWriter().println(v);
/*     */   }
/*     */ 
/*     */   public void println(char[] chars) {
/* 153 */     getPrintWriter().println(chars);
/*     */   }
/*     */ 
/*     */   public void println(String string) {
/* 157 */     getPrintWriter().println(string);
/*     */   }
/*     */ 
/*     */   public void println(Object object) {
/* 161 */     getPrintWriter().println(object);
/*     */   }
/*     */ 
/*     */   public static class NullOutputStream extends OutputStream
/*     */   {
/*     */     public void write(int b)
/*     */     {
/*     */     }
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.transport.http.FilterPrintWriter
 * JD-Core Version:    0.6.0
 */