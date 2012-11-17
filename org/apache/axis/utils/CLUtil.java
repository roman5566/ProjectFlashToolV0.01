/*     */ package org.apache.axis.utils;
/*     */ 
/*     */ public final class CLUtil
/*     */ {
/*     */   private static final int MAX_DESCRIPTION_COLUMN_LENGTH = 60;
/*     */ 
/*     */   public static final StringBuffer describeOptions(CLOptionDescriptor[] options)
/*     */   {
/*  40 */     StringBuffer sb = new StringBuffer();
/*     */ 
/*  42 */     for (int i = 0; i < options.length; i++)
/*     */     {
/*  44 */       char ch = (char)options[i].getId();
/*  45 */       String name = options[i].getName();
/*  46 */       String description = options[i].getDescription();
/*  47 */       int flags = options[i].getFlags();
/*  48 */       boolean argumentRequired = (flags & 0x2) == 2;
/*     */ 
/*  51 */       boolean twoArgumentsRequired = (flags & 0x10) == 16;
/*     */ 
/*  54 */       boolean needComma = false;
/*  55 */       if (twoArgumentsRequired) {
/*  56 */         argumentRequired = true;
/*     */       }
/*  58 */       sb.append('\t');
/*     */ 
/*  60 */       if (Character.isLetter(ch))
/*     */       {
/*  62 */         sb.append("-");
/*  63 */         sb.append(ch);
/*  64 */         needComma = true;
/*     */       }
/*     */ 
/*  67 */       if (null != name)
/*     */       {
/*  69 */         if (needComma)
/*     */         {
/*  71 */           sb.append(", ");
/*     */         }
/*     */ 
/*  74 */         sb.append("--");
/*  75 */         sb.append(name);
/*  76 */         if (argumentRequired)
/*     */         {
/*  78 */           sb.append(" <argument>");
/*     */         }
/*  80 */         if (twoArgumentsRequired)
/*     */         {
/*  82 */           sb.append("=<value>");
/*     */         }
/*  84 */         sb.append(JavaUtils.LS);
/*     */       }
/*     */ 
/*  87 */       if (null == description)
/*     */         continue;
/*  89 */       while (description.length() > 60)
/*     */       {
/*  91 */         String descriptionPart = description.substring(0, 60);
/*     */ 
/*  93 */         description = description.substring(60);
/*     */ 
/*  95 */         sb.append("\t\t");
/*  96 */         sb.append(descriptionPart);
/*  97 */         sb.append(JavaUtils.LS);
/*     */       }
/*     */ 
/* 100 */       sb.append("\t\t");
/* 101 */       sb.append(description);
/* 102 */       sb.append(JavaUtils.LS);
/*     */     }
/*     */ 
/* 105 */     return sb;
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.utils.CLUtil
 * JD-Core Version:    0.6.0
 */