/*    */ package org.apache.axis;
/*    */ 
/*    */ import java.io.PrintStream;
/*    */ import org.apache.axis.client.Call;
/*    */ import org.apache.axis.utils.Messages;
/*    */ 
/*    */ public class Version
/*    */ {
/*    */   public static String getVersion()
/*    */   {
/* 38 */     return Messages.getMessage("axisVersion") + "\n" + Messages.getMessage("builtOn");
/*    */   }
/*    */ 
/*    */   public static String getVersionText()
/*    */   {
/* 51 */     return Messages.getMessage("axisVersionRaw") + " " + Messages.getMessage("axisBuiltOnRaw");
/*    */   }
/*    */ 
/*    */   public static void main(String[] args)
/*    */   {
/* 62 */     if (args.length != 1)
/* 63 */       System.out.println(getVersion());
/*    */     else
/*    */       try {
/* 66 */         Call call = new Call(args[0]);
/* 67 */         String result = (String)call.invoke("Version", "getVersion", null);
/*    */ 
/* 69 */         System.out.println(result);
/*    */       } catch (Exception e) {
/* 71 */         e.printStackTrace();
/*    */       }
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.Version
 * JD-Core Version:    0.6.0
 */