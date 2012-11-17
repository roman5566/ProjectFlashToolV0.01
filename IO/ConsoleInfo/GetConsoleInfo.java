/*    */ package IO.ConsoleInfo;
/*    */ 
/*    */ public class GetConsoleInfo
/*    */ {
/*    */   public static ConsoleTypes getConsoleTypeFromCB(int inCB)
/*    */   {
/*  7 */     CB[] cb = CB.values();
/*  8 */     for (int i = 0; i < cb.length; i++)
/*  9 */       if (cb[i].getCB() == inCB)
/* 10 */         return cb[i].getCT();
/* 11 */     return ConsoleTypes.Unknown;
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     IO.ConsoleInfo.GetConsoleInfo
 * JD-Core Version:    0.6.0
 */