/*     */ package org.apache.axis.components.uuid;
/*     */ 
/*     */ import java.security.SecureRandom;
/*     */ import java.util.Random;
/*     */ 
/*     */ public class FastUUIDGen
/*     */   implements UUIDGen
/*     */ {
/*     */   private static Random secureRandom;
/*     */   private static String nodeStr;
/*     */   private static int clockSequence;
/*  33 */   private long lastTime = 0L;
/*     */ 
/*     */   private static String getNodeHexValue()
/*     */   {
/*  61 */     long node = 0L;
/*  62 */     long nodeValue = 0L;
/*  63 */     while ((node = getBitsValue(nodeValue, 47, 47)) == 0L) {
/*  64 */       nodeValue = secureRandom.nextLong();
/*     */     }
/*  66 */     node |= 140737488355328L;
/*  67 */     return leftZeroPadString(Long.toHexString(node), 12);
/*     */   }
/*     */ 
/*     */   private static int getClockSequence() {
/*  71 */     return secureRandom.nextInt(16384);
/*     */   }
/*     */ 
/*     */   public String nextUUID() {
/*  75 */     long time = System.currentTimeMillis();
/*     */ 
/*  77 */     long timestamp = time * 10000L;
/*  78 */     timestamp += 122192927672762368L;
/*  79 */     timestamp += 327237632L;
/*     */ 
/*  81 */     synchronized (this) {
/*  82 */       if (time - this.lastTime <= 0L) {
/*  83 */         clockSequence = clockSequence + 1 & 0x3FFF;
/*     */       }
/*  85 */       this.lastTime = time;
/*     */     }
/*     */ 
/*  88 */     long timeLow = getBitsValue(timestamp, 32, 32);
/*  89 */     long timeMid = getBitsValue(timestamp, 48, 16);
/*  90 */     long timeHi = getBitsValue(timestamp, 64, 16) | 0x1000;
/*     */ 
/*  92 */     long clockSeqLow = getBitsValue(clockSequence, 8, 8);
/*  93 */     long clockSeqHi = getBitsValue(clockSequence, 16, 8) | 0x80;
/*     */ 
/*  95 */     String timeLowStr = leftZeroPadString(Long.toHexString(timeLow), 8);
/*  96 */     String timeMidStr = leftZeroPadString(Long.toHexString(timeMid), 4);
/*  97 */     String timeHiStr = leftZeroPadString(Long.toHexString(timeHi), 4);
/*     */ 
/*  99 */     String clockSeqHiStr = leftZeroPadString(Long.toHexString(clockSeqHi), 2);
/* 100 */     String clockSeqLowStr = leftZeroPadString(Long.toHexString(clockSeqLow), 2);
/*     */ 
/* 102 */     StringBuffer result = new StringBuffer(36);
/* 103 */     result.append(timeLowStr).append("-");
/* 104 */     result.append(timeMidStr).append("-");
/* 105 */     result.append(timeHiStr).append("-");
/* 106 */     result.append(clockSeqHiStr).append(clockSeqLowStr);
/* 107 */     result.append("-").append(nodeStr);
/*     */ 
/* 109 */     return result.toString();
/*     */   }
/*     */ 
/*     */   private static long getBitsValue(long value, int startBit, int bitLen) {
/* 113 */     return value << 64 - startBit >>> 64 - bitLen;
/*     */   }
/*     */ 
/*     */   private static final String leftZeroPadString(String bitString, int len) {
/* 117 */     if (bitString.length() < len) {
/* 118 */       int nbExtraZeros = len - bitString.length();
/* 119 */       StringBuffer extraZeros = new StringBuffer();
/* 120 */       for (int i = 0; i < nbExtraZeros; i++) {
/* 121 */         extraZeros.append("0");
/*     */       }
/* 123 */       extraZeros.append(bitString);
/* 124 */       bitString = extraZeros.toString();
/*     */     }
/* 126 */     return bitString;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*     */     try
/*     */     {
/*  51 */       secureRandom = SecureRandom.getInstance("SHA1PRNG", "SUN");
/*     */     } catch (Exception e) {
/*  53 */       secureRandom = new Random();
/*     */     }
/*     */ 
/*  56 */     nodeStr = getNodeHexValue();
/*  57 */     clockSequence = getClockSequence();
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.components.uuid.FastUUIDGen
 * JD-Core Version:    0.6.0
 */