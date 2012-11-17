/*     */ package org.apache.axis.handlers;
/*     */ 
/*     */ import java.io.FileWriter;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintWriter;
/*     */ import org.apache.axis.AxisFault;
/*     */ import org.apache.axis.Message;
/*     */ import org.apache.axis.MessageContext;
/*     */ import org.apache.axis.components.logger.LogFactory;
/*     */ import org.apache.axis.utils.Messages;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public class LogHandler extends BasicHandler
/*     */ {
/*  43 */   protected static Log log = LogFactory.getLog(LogHandler.class.getName());
/*     */ 
/*  46 */   long start = -1L;
/*  47 */   private boolean writeToConsole = false;
/*  48 */   private String filename = "axis.log";
/*     */ 
/*     */   public void init() {
/*  51 */     super.init();
/*     */ 
/*  53 */     Object opt = getOption("LogHandler.writeToConsole");
/*  54 */     if ((opt != null) && ((opt instanceof String)) && ("true".equalsIgnoreCase((String)opt)))
/*     */     {
/*  56 */       this.writeToConsole = true;
/*     */     }
/*  58 */     opt = getOption("LogHandler.fileName");
/*  59 */     if ((opt != null) && ((opt instanceof String)))
/*  60 */       this.filename = ((String)opt);
/*     */   }
/*     */ 
/*     */   public void invoke(MessageContext msgContext) throws AxisFault {
/*  64 */     log.debug("Enter: LogHandler::invoke");
/*  65 */     if (!msgContext.getPastPivot())
/*  66 */       this.start = System.currentTimeMillis();
/*     */     else {
/*  68 */       logMessages(msgContext);
/*     */     }
/*  70 */     log.debug("Exit: LogHandler::invoke");
/*     */   }
/*     */ 
/*     */   private void logMessages(MessageContext msgContext) throws AxisFault {
/*     */     try {
/*  75 */       PrintWriter writer = null;
/*     */ 
/*  77 */       writer = getWriter();
/*     */ 
/*  79 */       Message inMsg = msgContext.getRequestMessage();
/*  80 */       Message outMsg = msgContext.getResponseMessage();
/*     */ 
/*  82 */       writer.println("=======================================================");
/*  83 */       if (this.start != -1L) {
/*  84 */         writer.println("= " + Messages.getMessage("elapsed00", new StringBuffer().append("").append(System.currentTimeMillis() - this.start).toString()));
/*     */       }
/*     */ 
/*  87 */       writer.println("= " + Messages.getMessage("inMsg00", inMsg == null ? "null" : inMsg.getSOAPPartAsString()));
/*     */ 
/*  89 */       writer.println("= " + Messages.getMessage("outMsg00", outMsg == null ? "null" : outMsg.getSOAPPartAsString()));
/*     */ 
/*  91 */       writer.println("=======================================================");
/*     */ 
/*  94 */       if (!this.writeToConsole) {
/*  95 */         writer.close();
/*     */       }
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 100 */       log.error(Messages.getMessage("exception00"), e);
/* 101 */       throw AxisFault.makeFault(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   private PrintWriter getWriter()
/*     */     throws IOException
/*     */   {
/*     */     PrintWriter writer;
/*     */     PrintWriter writer;
/* 109 */     if (this.writeToConsole)
/*     */     {
/* 111 */       writer = new PrintWriter(System.out);
/*     */     }
/*     */     else {
/* 114 */       if (this.filename == null) {
/* 115 */         this.filename = "axis.log";
/*     */       }
/* 117 */       writer = new PrintWriter(new FileWriter(this.filename, true));
/*     */     }
/* 119 */     return writer;
/*     */   }
/*     */ 
/*     */   public void onFault(MessageContext msgContext)
/*     */   {
/*     */     try {
/* 125 */       logMessages(msgContext);
/*     */     } catch (AxisFault axisFault) {
/* 127 */       log.error(Messages.getMessage("exception00"), axisFault);
/*     */     }
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.handlers.LogHandler
 * JD-Core Version:    0.6.0
 */