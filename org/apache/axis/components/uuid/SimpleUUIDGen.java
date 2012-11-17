/*     */ package org.apache.axis.components.uuid;
/*     */ 
/*     */ import java.math.BigInteger;
/*     */ import java.security.SecureRandom;
/*     */ import java.util.Random;
/*     */ 
/*     */ public class SimpleUUIDGen
/*     */   implements UUIDGen
/*     */ {
/*  40 */   private static final BigInteger countStart = new BigInteger("-12219292800000");
/*  41 */   private static final int clock_sequence = new Random().nextInt(16384);
/*     */   private static final byte ZERO = 48;
/*     */   private static final byte ONE = 49;
/*  44 */   private static Random secureRandom = null;
/*     */ 
/*     */   private static final String leftZeroPadString(String bitString, int len)
/*     */   {
/*  78 */     if (bitString.length() < len) {
/*  79 */       int nbExtraZeros = len - bitString.length();
/*  80 */       StringBuffer extraZeros = new StringBuffer();
/*  81 */       for (int i = 0; i < nbExtraZeros; i++) {
/*  82 */         extraZeros.append("0");
/*     */       }
/*  84 */       extraZeros.append(bitString);
/*  85 */       bitString = extraZeros.toString();
/*     */     }
/*  87 */     return bitString;
/*     */   }
/*     */ 
/*     */   public String nextUUID()
/*     */   {
/* 107 */     BigInteger current = BigInteger.valueOf(System.currentTimeMillis());
/*     */ 
/* 110 */     BigInteger countMillis = current.subtract(countStart);
/*     */ 
/* 113 */     BigInteger count = countMillis.multiply(BigInteger.valueOf(10000L));
/* 114 */     byte[] bits = leftZeroPadString(count.toString(2), 60).getBytes();
/*     */ 
/* 117 */     byte[] time_low = new byte[32];
/* 118 */     for (int i = 0; i < 32; i++) {
/* 119 */       time_low[i] = bits[(bits.length - i - 1)];
/*     */     }
/*     */ 
/* 122 */     byte[] time_mid = new byte[16];
/* 123 */     for (int i = 0; i < 16; i++) {
/* 124 */       time_mid[i] = bits[(bits.length - 32 - i - 1)];
/*     */     }
/*     */ 
/* 127 */     byte[] time_hi_and_version = new byte[16];
/* 128 */     for (int i = 0; i < 12; i++) {
/* 129 */       time_hi_and_version[i] = bits[(bits.length - 48 - i - 1)];
/*     */     }
/* 131 */     time_hi_and_version[12] = 49;
/* 132 */     time_hi_and_version[13] = 48;
/* 133 */     time_hi_and_version[14] = 48;
/* 134 */     time_hi_and_version[15] = 48;
/*     */ 
/* 137 */     BigInteger clockSequence = BigInteger.valueOf(clock_sequence);
/* 138 */     byte[] clock_bits = leftZeroPadString(clockSequence.toString(2), 14).getBytes();
/* 139 */     byte[] clock_seq_low = new byte[8];
/* 140 */     for (int i = 0; i < 8; i++) {
/* 141 */       clock_seq_low[i] = clock_bits[(clock_bits.length - i - 1)];
/*     */     }
/*     */ 
/* 145 */     byte[] clock_seq_hi_and_reserved = new byte[8];
/* 146 */     for (int i = 0; i < 6; i++) {
/* 147 */       clock_seq_hi_and_reserved[i] = clock_bits[(clock_bits.length - 8 - i - 1)];
/*     */     }
/* 149 */     clock_seq_hi_and_reserved[6] = 48;
/* 150 */     clock_seq_hi_and_reserved[7] = 49;
/*     */ 
/* 152 */     String timeLow = Long.toHexString(new BigInteger(new String(reverseArray(time_low)), 2).longValue());
/* 153 */     timeLow = leftZeroPadString(timeLow, 8);
/*     */ 
/* 155 */     String timeMid = Long.toHexString(new BigInteger(new String(reverseArray(time_mid)), 2).longValue());
/* 156 */     timeMid = leftZeroPadString(timeMid, 4);
/*     */ 
/* 158 */     String timeHiAndVersion = Long.toHexString(new BigInteger(new String(reverseArray(time_hi_and_version)), 2).longValue());
/* 159 */     timeHiAndVersion = leftZeroPadString(timeHiAndVersion, 4);
/*     */ 
/* 161 */     String clockSeqHiAndReserved = Long.toHexString(new BigInteger(new String(reverseArray(clock_seq_hi_and_reserved)), 2).longValue());
/* 162 */     clockSeqHiAndReserved = leftZeroPadString(clockSeqHiAndReserved, 2);
/*     */ 
/* 164 */     String clockSeqLow = Long.toHexString(new BigInteger(new String(reverseArray(clock_seq_low)), 2).longValue());
/* 165 */     clockSeqLow = leftZeroPadString(clockSeqLow, 2);
/*     */ 
/* 167 */     long nodeValue = secureRandom.nextLong();
/* 168 */     nodeValue = Math.abs(nodeValue);
/* 169 */     while (nodeValue > 140737488355328L) {
/* 170 */       nodeValue = secureRandom.nextLong();
/* 171 */       nodeValue = Math.abs(nodeValue);
/*     */     }
/*     */ 
/* 174 */     BigInteger nodeInt = BigInteger.valueOf(nodeValue);
/*     */ 
/* 176 */     byte[] node_bits = leftZeroPadString(nodeInt.toString(2), 47).getBytes();
/* 177 */     byte[] node = new byte[48];
/* 178 */     for (int i = 0; i < 47; i++) {
/* 179 */       node[i] = node_bits[(node_bits.length - i - 1)];
/*     */     }
/* 181 */     node[47] = 49;
/* 182 */     String theNode = Long.toHexString(new BigInteger(new String(reverseArray(node)), 2).longValue());
/* 183 */     theNode = leftZeroPadString(theNode, 12);
/*     */ 
/* 185 */     StringBuffer result = new StringBuffer(timeLow);
/* 186 */     result.append("-");
/* 187 */     result.append(timeMid);
/* 188 */     result.append("-");
/* 189 */     result.append(timeHiAndVersion);
/* 190 */     result.append("-");
/* 191 */     result.append(clockSeqHiAndReserved);
/* 192 */     result.append(clockSeqLow);
/* 193 */     result.append("-");
/* 194 */     result.append(theNode);
/* 195 */     return result.toString().toUpperCase();
/*     */   }
/*     */ 
/*     */   private static byte[] reverseArray(byte[] bits) {
/* 199 */     byte[] result = new byte[bits.length];
/* 200 */     for (int i = 0; i < result.length; i++) {
/* 201 */       result[i] = bits[(result.length - 1 - i)];
/*     */     }
/* 203 */     return result;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*     */     try
/*     */     {
/*  62 */       secureRandom = SecureRandom.getInstance("SHA1PRNG", "SUN");
/*     */     } catch (Exception e) {
/*  64 */       secureRandom = new Random();
/*     */     }
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.components.uuid.SimpleUUIDGen
 * JD-Core Version:    0.6.0
 */