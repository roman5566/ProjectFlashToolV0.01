/*    */ package org.apache.axis.components.script;
/*    */ 
/*    */ import org.apache.bsf.BSFEngine;
/*    */ import org.apache.bsf.BSFManager;
/*    */ 
/*    */ public class BSF
/*    */   implements Script
/*    */ {
/*    */   public Object run(String language, String name, String scriptStr, String methodName, Object[] argValues)
/*    */     throws Exception
/*    */   {
/* 25 */     BSFManager manager = new BSFManager();
/* 26 */     BSFEngine engine = manager.loadScriptingEngine(language);
/*    */ 
/* 28 */     manager.exec(language, "service script for '" + name + "'", 0, 0, scriptStr);
/*    */ 
/* 31 */     Object result = engine.call(null, methodName, argValues);
/* 32 */     return result;
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.components.script.BSF
 * JD-Core Version:    0.6.0
 */