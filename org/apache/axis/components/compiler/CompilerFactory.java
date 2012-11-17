/*    */ package org.apache.axis.components.compiler;
/*    */ 
/*    */ import org.apache.axis.AxisProperties;
/*    */ import org.apache.axis.components.logger.LogFactory;
/*    */ import org.apache.axis.utils.Messages;
/*    */ import org.apache.commons.logging.Log;
/*    */ 
/*    */ public class CompilerFactory
/*    */ {
/* 32 */   protected static Log log = LogFactory.getLog(CompilerFactory.class.getName());
/*    */ 
/*    */   public static Compiler getCompiler()
/*    */   {
/* 43 */     Compiler compiler = (Compiler)AxisProperties.newInstance(Compiler.class);
/*    */ 
/* 48 */     if (compiler == null) {
/* 49 */       log.debug(Messages.getMessage("defaultCompiler"));
/* 50 */       compiler = new Javac();
/*    */     }
/*    */ 
/* 53 */     log.debug("axis.Compiler:" + compiler.getClass().getName());
/*    */ 
/* 55 */     return compiler;
/*    */   }
/*    */ 
/*    */   static
/*    */   {
/* 36 */     AxisProperties.setClassOverrideProperty(Compiler.class, "axis.Compiler");
/*    */ 
/* 38 */     AxisProperties.setClassDefault(Compiler.class, "org.apache.axis.components.compiler.Javac");
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.components.compiler.CompilerFactory
 * JD-Core Version:    0.6.0
 */