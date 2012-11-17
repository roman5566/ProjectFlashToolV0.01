/*     */ package org.apache.axis.client.async;
/*     */ 
/*     */ import org.apache.axis.constants.Enum;
/*     */ import org.apache.axis.constants.Enum.Type;
/*     */ 
/*     */ public class Status extends Enum
/*     */ {
/*  30 */   private static final Type type = new Type(null);
/*     */   public static final String NONE_STR = "none";
/*     */   public static final String INTERRUPTED_STR = "interrupted";
/*     */   public static final String COMPLETED_STR = "completed";
/*     */   public static final String EXCEPTION_STR = "exception";
/*  55 */   public static final Status NONE = type.getStatus("none");
/*     */ 
/*  60 */   public static final Status INTERRUPTED = type.getStatus("interrupted");
/*     */ 
/*  65 */   public static final Status COMPLETED = type.getStatus("completed");
/*     */ 
/*  70 */   public static final Status EXCEPTION = type.getStatus("exception");
/*     */ 
/*  75 */   public static final Status DEFAULT = NONE;
/*     */ 
/*     */   public static Status getDefault()
/*     */   {
/*  87 */     return (Status)type.getDefault();
/*     */   }
/*     */ 
/*     */   public static final Status getStatus(int style)
/*     */   {
/*  97 */     return type.getStatus(style);
/*     */   }
/*     */ 
/*     */   public static final Status getStatus(String style)
/*     */   {
/* 107 */     return type.getStatus(style);
/*     */   }
/*     */ 
/*     */   public static final Status getStatus(String style, Status dephault)
/*     */   {
/* 118 */     return type.getStatus(style, dephault);
/*     */   }
/*     */ 
/*     */   public static final boolean isValid(String style)
/*     */   {
/* 128 */     return type.isValid(style);
/*     */   }
/*     */ 
/*     */   public static final int size()
/*     */   {
/* 137 */     return type.size();
/*     */   }
/*     */ 
/*     */   public static final String[] getUses()
/*     */   {
/* 146 */     return type.getEnumNames();
/*     */   }
/*     */ 
/*     */   private Status(int value, String name)
/*     */   {
/* 207 */     super(type, value, name);
/*     */   }
/*     */ 
/*     */   Status(int x0, String x1, 1 x2)
/*     */   {
/*  25 */     this(x0, x1);
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  78 */     type.setDefault(DEFAULT);
/*     */   }
/*     */ 
/*     */   public static class Type extends Enum.Type
/*     */   {
/*     */     private Type()
/*     */     {
/* 162 */       super(new Enum[] { new Status(0, "none", null), new Status(1, "interrupted", null), new Status(2, "completed", null), new Status(3, "exception", null) });
/*     */     }
/*     */ 
/*     */     public final Status getStatus(int status)
/*     */     {
/* 175 */       return (Status)getEnum(status);
/*     */     }
/*     */ 
/*     */     public final Status getStatus(String status)
/*     */     {
/* 185 */       return (Status)getEnum(status);
/*     */     }
/*     */ 
/*     */     public final Status getStatus(String status, Status dephault)
/*     */     {
/* 196 */       return (Status)getEnum(status, dephault);
/*     */     }
/*     */ 
/*     */     Type(Status.1 x0)
/*     */     {
/* 155 */       this();
/*     */     }
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.client.async.Status
 * JD-Core Version:    0.6.0
 */