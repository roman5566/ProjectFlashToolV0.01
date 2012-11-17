/*    */ package IO.ConsoleInfo;
/*    */ 
/*    */ public enum CB
/*    */ {
/*  5 */   CB1888(1888, ConsoleTypes.Xenon), 
/*  6 */   CB1902(1902, ConsoleTypes.Xenon), 
/*  7 */   CB1903(1903, ConsoleTypes.Xenon), 
/*  8 */   CB1920(1920, ConsoleTypes.Xenon), 
/*  9 */   CB1921(1921, ConsoleTypes.Xenon), 
/* 10 */   CB1922(1922, ConsoleTypes.Xenon), 
/* 11 */   CB1923(1923, ConsoleTypes.Xenon), 
/* 12 */   CB1940(1940, ConsoleTypes.Xenon), 
/* 13 */   CB8192(8192, ConsoleTypes.Xenon), 
/* 14 */   CB4558(4558, ConsoleTypes.Zephyr), 
/* 15 */   CB4571(4571, ConsoleTypes.Zephyr), 
/* 16 */   CB4578(4578, ConsoleTypes.Zephyr), 
/* 17 */   CB4579(4579, ConsoleTypes.Zephyr), 
/* 18 */   CB5761(5761, ConsoleTypes.Falcon), 
/* 19 */   CB5766(5766, ConsoleTypes.Falcon), 
/* 20 */   CB5770(5770, ConsoleTypes.Falcon), 
/* 21 */   CB5771(5771, ConsoleTypes.Falcon), 
/* 22 */   CB6712(6712, ConsoleTypes.Jasper), 
/* 23 */   CB6723(6723, ConsoleTypes.Jasper), 
/* 24 */   CB6750(6750, ConsoleTypes.Jasper), 
/* 25 */   CB6751(6751, ConsoleTypes.Jasper), 
/* 26 */   CB9188(9188, ConsoleTypes.Trinity);
/*    */ 
/*    */   private ConsoleTypes CT;
/*    */   private int CB;
/*    */ 
/* 31 */   private CB(int CB, ConsoleTypes CT) { this.CB = CB;
/* 32 */     this.CT = CT; }
/*    */ 
/*    */   public ConsoleTypes getCT()
/*    */   {
/* 36 */     return this.CT;
/*    */   }
/*    */ 
/*    */   public int getCB() {
/* 40 */     return this.CB;
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     IO.ConsoleInfo.CB
 * JD-Core Version:    0.6.0
 */