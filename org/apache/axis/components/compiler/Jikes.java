/*     */ package org.apache.axis.components.compiler;
/*     */ 
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.StringTokenizer;
/*     */ import org.apache.axis.components.logger.LogFactory;
/*     */ import org.apache.axis.utils.Messages;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public class Jikes extends AbstractCompiler
/*     */ {
/*  43 */   protected static Log log = LogFactory.getLog(Jikes.class.getName());
/*     */   static final int OUTPUT_BUFFER_SIZE = 1024;
/*     */   static final int BUFFER_SIZE = 512;
/*     */ 
/*     */   protected String[] toStringArray(List arguments)
/*     */   {
/*  96 */     for (int i = 0; i < arguments.size(); i++) {
/*  97 */       String arg = (String)arguments.get(i);
/*  98 */       if (!arg.equals("-sourcepath"))
/*     */         continue;
/* 100 */       arguments.remove(i);
/* 101 */       arguments.remove(i);
/* 102 */       break;
/*     */     }
/*     */ 
/* 106 */     String[] args = new String[arguments.size() + this.fileList.size()];
/* 107 */     for (i = 0; i < arguments.size(); i++) {
/* 108 */       args[i] = ((String)arguments.get(i));
/*     */     }
/*     */ 
/* 111 */     for (int j = 0; j < this.fileList.size(); j++) {
/* 112 */       args[i] = ((String)this.fileList.get(j));
/*     */ 
/* 111 */       i++;
/*     */     }
/*     */ 
/* 115 */     return args;
/*     */   }
/*     */ 
/*     */   public boolean compile()
/*     */     throws IOException
/*     */   {
/* 123 */     List args = new ArrayList();
/*     */ 
/* 125 */     args.add("jikes");
/*     */ 
/* 127 */     args.add("+E");
/*     */ 
/* 130 */     args.add("-nowarn");
/*     */ 
/* 133 */     ByteArrayOutputStream tmpErr = new ByteArrayOutputStream(1024);
/*     */     try
/*     */     {
/* 136 */       Process p = Runtime.getRuntime().exec(toStringArray(fillArguments(args)));
/*     */ 
/* 138 */       BufferedInputStream compilerErr = new BufferedInputStream(p.getErrorStream());
/*     */ 
/* 140 */       StreamPumper errPumper = new StreamPumper(compilerErr, tmpErr);
/*     */ 
/* 142 */       errPumper.start();
/*     */ 
/* 144 */       p.waitFor();
/* 145 */       int exitValue = p.exitValue();
/*     */ 
/* 148 */       errPumper.join();
/* 149 */       compilerErr.close();
/*     */ 
/* 151 */       p.destroy();
/*     */ 
/* 153 */       tmpErr.close();
/* 154 */       this.errors = new ByteArrayInputStream(tmpErr.toByteArray());
/*     */     }
/*     */     catch (InterruptedException somethingHappened) {
/* 157 */       log.debug("Jikes.compile():SomethingHappened", somethingHappened);
/* 158 */       return false;
/*     */     }
/*     */     int exitValue;
/* 165 */     return (exitValue == 0) && (tmpErr.size() == 0);
/*     */   }
/*     */ 
/*     */   protected List parseStream(BufferedReader input)
/*     */     throws IOException
/*     */   {
/* 177 */     List errors = null;
/* 178 */     String line = null;
/* 179 */     StringBuffer buffer = null;
/*     */     while (true)
/*     */     {
/* 183 */       buffer = new StringBuffer();
/*     */ 
/* 186 */       if (line == null) line = input.readLine();
/* 187 */       if (line == null) return errors;
/* 188 */       log.debug(line);
/* 189 */       buffer.append(line);
/*     */       while (true)
/*     */       {
/* 193 */         line = input.readLine();
/*     */ 
/* 195 */         if (line == null) {
/*     */           break;
/*     */         }
/* 198 */         if ((line.length() > 0) && (line.charAt(0) != ' '))
/*     */           break;
/* 200 */         log.debug(line);
/* 201 */         buffer.append('\n');
/* 202 */         buffer.append(line);
/*     */       }
/*     */ 
/* 206 */       if (errors == null) errors = new ArrayList();
/*     */ 
/* 209 */       errors.add(parseError(buffer.toString()));
/*     */     }
/*     */   }
/*     */ 
/*     */   private CompilerError parseError(String error)
/*     */   {
/* 220 */     StringTokenizer tokens = new StringTokenizer(error, ":");
/* 221 */     String file = tokens.nextToken();
/* 222 */     if (file.length() == 1) file = file + ":" + tokens.nextToken();
/* 223 */     StringBuffer message = new StringBuffer();
/* 224 */     String type = "";
/* 225 */     int startline = 0;
/* 226 */     int startcolumn = 0;
/* 227 */     int endline = 0;
/* 228 */     int endcolumn = 0;
/*     */     try
/*     */     {
/* 231 */       startline = Integer.parseInt(tokens.nextToken());
/* 232 */       startcolumn = Integer.parseInt(tokens.nextToken());
/* 233 */       endline = Integer.parseInt(tokens.nextToken());
/* 234 */       endcolumn = Integer.parseInt(tokens.nextToken());
/*     */     }
/*     */     catch (Exception e) {
/* 237 */       message.append(Messages.getMessage("compilerFail00"));
/* 238 */       type = "error";
/* 239 */       log.error(Messages.getMessage("compilerFail00"), e);
/*     */     }
/*     */ 
/* 242 */     if ("".equals(message)) {
/* 243 */       type = tokens.nextToken().trim().toLowerCase();
/* 244 */       message.append(tokens.nextToken("\n").substring(1).trim());
/*     */ 
/* 246 */       while (tokens.hasMoreTokens()) {
/* 247 */         message.append("\n").append(tokens.nextToken());
/*     */       }
/*     */     }
/* 250 */     return new CompilerError(file, type.equals("error"), startline, startcolumn, endline, endcolumn, message.toString());
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 254 */     return Messages.getMessage("ibmJikes");
/*     */   }
/*     */ 
/*     */   private class StreamPumper extends Thread
/*     */   {
/*     */     private BufferedInputStream stream;
/*  52 */     private boolean endOfStream = false;
/*  53 */     private boolean stopSignal = false;
/*  54 */     private int SLEEP_TIME = 5;
/*     */     private OutputStream out;
/*     */ 
/*     */     public StreamPumper(BufferedInputStream is, OutputStream out)
/*     */     {
/*  58 */       this.stream = is;
/*  59 */       this.out = out;
/*     */     }
/*     */ 
/*     */     public void pumpStream() throws IOException {
/*  63 */       byte[] buf = new byte[512];
/*  64 */       if (!this.endOfStream) {
/*  65 */         int bytesRead = this.stream.read(buf, 0, 512);
/*     */ 
/*  67 */         if (bytesRead > 0)
/*  68 */           this.out.write(buf, 0, bytesRead);
/*  69 */         else if (bytesRead == -1)
/*  70 */           this.endOfStream = true;
/*     */       }
/*     */     }
/*     */ 
/*     */     public void run()
/*     */     {
/*     */       try {
/*  77 */         while (!this.endOfStream) {
/*  78 */           pumpStream();
/*  79 */           sleep(this.SLEEP_TIME);
/*     */         }
/*     */       }
/*     */       catch (Exception e)
/*     */       {
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.components.compiler.Jikes
 * JD-Core Version:    0.6.0
 */