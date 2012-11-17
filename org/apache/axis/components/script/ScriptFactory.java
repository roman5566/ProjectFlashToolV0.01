/*    */ package org.apache.axis.components.script;
/*    */ 
/*    */ import org.apache.axis.AxisProperties;
/*    */ import org.apache.axis.components.logger.LogFactory;
/*    */ import org.apache.commons.logging.Log;
/*    */ 
/*    */ public class ScriptFactory
/*    */ {
/* 29 */   protected static Log log = LogFactory.getLog(ScriptFactory.class.getName());
/*    */ 
/*    */   public static Script getScript()
/*    */   {
/* 45 */     Script script = (Script)AxisProperties.newInstance(Script.class);
/* 46 */     log.debug("axis.Script: " + script.getClass().getName());
/* 47 */     return script;
/*    */   }
/*    */ 
/*    */   static
/*    */   {
/* 33 */     AxisProperties.setClassOverrideProperty(Script.class, "axis.Script");
/*    */ 
/* 35 */     AxisProperties.setClassDefaults(class$org$apache$axis$components$script$Script, new String[] { "org.apache.axis.components.script.BSF" });
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.components.script.ScriptFactory
 * JD-Core Version:    0.6.0
 */