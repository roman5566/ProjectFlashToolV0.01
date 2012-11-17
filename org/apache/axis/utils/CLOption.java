/*     */ package org.apache.axis.utils;
/*     */ 
/*     */ import java.util.Arrays;
/*     */ 
/*     */ public final class CLOption
/*     */ {
/*     */   public static final int TEXT_ARGUMENT = 0;
/*     */   private final int m_id;
/*     */   private String[] m_arguments;
/*     */ 
/*     */   public final String getArgument()
/*     */   {
/*  45 */     return getArgument(0);
/*     */   }
/*     */ 
/*     */   public final String getArgument(int index)
/*     */   {
/*  57 */     if ((null == this.m_arguments) || (index < 0) || (index >= this.m_arguments.length))
/*     */     {
/*  59 */       return null;
/*     */     }
/*     */ 
/*  63 */     return this.m_arguments[index];
/*     */   }
/*     */ 
/*     */   public final int getId()
/*     */   {
/*  76 */     return this.m_id;
/*     */   }
/*     */ 
/*     */   public CLOption(int id)
/*     */   {
/*  86 */     this.m_id = id;
/*     */   }
/*     */ 
/*     */   public CLOption(String argument)
/*     */   {
/*  96 */     this(0);
/*  97 */     addArgument(argument);
/*     */   }
/*     */ 
/*     */   public final void addArgument(String argument)
/*     */   {
/* 107 */     if (null == this.m_arguments) { this.m_arguments = new String[] { argument };
/*     */     } else
/*     */     {
/* 110 */       String[] arguments = new String[this.m_arguments.length + 1];
/* 111 */       System.arraycopy(this.m_arguments, 0, arguments, 0, this.m_arguments.length);
/* 112 */       arguments[this.m_arguments.length] = argument;
/* 113 */       this.m_arguments = arguments;
/*     */     }
/*     */   }
/*     */ 
/*     */   public final int getArgumentCount()
/*     */   {
/* 122 */     if (null == this.m_arguments)
/*     */     {
/* 124 */       return 0;
/*     */     }
/*     */ 
/* 128 */     return this.m_arguments.length;
/*     */   }
/*     */ 
/*     */   public final String toString()
/*     */   {
/* 139 */     StringBuffer sb = new StringBuffer();
/* 140 */     sb.append("[Option ");
/* 141 */     sb.append((char)this.m_id);
/*     */ 
/* 143 */     if (null != this.m_arguments)
/*     */     {
/* 145 */       sb.append(", ");
/* 146 */       sb.append(Arrays.asList(this.m_arguments));
/*     */     }
/*     */ 
/* 149 */     sb.append(" ]");
/*     */ 
/* 151 */     return sb.toString();
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.utils.CLOption
 * JD-Core Version:    0.6.0
 */