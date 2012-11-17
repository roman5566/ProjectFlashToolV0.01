/*     */ package org.apache.axis.constants;
/*     */ 
/*     */ import java.io.ObjectStreamException;
/*     */ import org.apache.axis.Constants;
/*     */ 
/*     */ public class Use extends Enum
/*     */ {
/*  33 */   private static final Type type = new Type(null);
/*     */   public static final String ENCODED_STR = "encoded";
/*     */   public static final String LITERAL_STR = "literal";
/*  38 */   public static final Use ENCODED = type.getUse("encoded");
/*  39 */   public static final Use LITERAL = type.getUse("literal");
/*     */ 
/*  41 */   public static final Use DEFAULT = ENCODED;
/*     */   private String encoding;
/*     */ 
/*     */   public static Use getDefault()
/*     */   {
/*  46 */     return (Use)type.getDefault();
/*     */   }
/*  48 */   public final String getEncoding() { return this.encoding; }
/*     */ 
/*     */   public static final Use getUse(int style) {
/*  51 */     return type.getUse(style);
/*     */   }
/*     */ 
/*     */   public static final Use getUse(String style) {
/*  55 */     return type.getUse(style);
/*     */   }
/*     */ 
/*     */   public static final Use getUse(String style, Use dephault) {
/*  59 */     return type.getUse(style, dephault);
/*     */   }
/*     */ 
/*     */   public static final boolean isValid(String style) {
/*  63 */     return type.isValid(style);
/*     */   }
/*     */ 
/*     */   public static final int size() {
/*  67 */     return type.size();
/*     */   }
/*     */ 
/*     */   public static final String[] getUses() {
/*  71 */     return type.getEnumNames();
/*     */   }
/*     */ 
/*     */   private Object readResolve() throws ObjectStreamException {
/*  75 */     return type.getUse(this.value);
/*     */   }
/*     */ 
/*     */   private Use(int value, String name, String encoding)
/*     */   {
/* 103 */     super(type, value, name);
/* 104 */     this.encoding = encoding;
/*     */   }
/*     */ 
/*     */   protected Use() {
/* 108 */     super(type, DEFAULT.getValue(), DEFAULT.getName());
/* 109 */     this.encoding = DEFAULT.getEncoding();
/*     */   }
/*     */ 
/*     */   Use(int x0, String x1, String x2, 1 x3)
/*     */   {
/*  26 */     this(x0, x1, x2);
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  43 */     type.setDefault(DEFAULT);
/*     */   }
/*     */ 
/*     */   public static class Type extends Enum.Type
/*     */   {
/*     */     private Type()
/*     */     {
/*  80 */       super(new Enum[] { new Use(0, "encoded", Constants.URI_DEFAULT_SOAP_ENC, null), new Use(1, "literal", "", null) });
/*     */     }
/*     */ 
/*     */     public final Use getUse(int style)
/*     */     {
/*  89 */       return (Use)getEnum(style);
/*     */     }
/*     */ 
/*     */     public final Use getUse(String style) {
/*  93 */       return (Use)getEnum(style);
/*     */     }
/*     */ 
/*     */     public final Use getUse(String style, Use dephault) {
/*  97 */       return (Use)getEnum(style, dephault);
/*     */     }
/*     */ 
/*     */     Type(Use.1 x0)
/*     */     {
/*  78 */       this();
/*     */     }
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.constants.Use
 * JD-Core Version:    0.6.0
 */