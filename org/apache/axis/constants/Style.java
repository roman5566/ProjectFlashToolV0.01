/*     */ package org.apache.axis.constants;
/*     */ 
/*     */ import java.io.ObjectStreamException;
/*     */ import javax.xml.namespace.QName;
/*     */ import org.apache.axis.deployment.wsdd.WSDDConstants;
/*     */ 
/*     */ public class Style extends Enum
/*     */ {
/*  83 */   private static final Type type = new Type(null);
/*     */   public static final String RPC_STR = "rpc";
/*     */   public static final String DOCUMENT_STR = "document";
/*     */   public static final String WRAPPED_STR = "wrapped";
/*     */   public static final String MESSAGE_STR = "message";
/*  91 */   public static final Style RPC = type.getStyle("rpc");
/*  92 */   public static final Style DOCUMENT = type.getStyle("document");
/*  93 */   public static final Style WRAPPED = type.getStyle("wrapped");
/*  94 */   public static final Style MESSAGE = type.getStyle("message");
/*     */ 
/*  96 */   public static final Style DEFAULT = RPC;
/*     */   private QName provider;
/*     */ 
/*     */   public static Style getDefault()
/*     */   {
/* 103 */     return (Style)type.getDefault();
/*     */   }
/* 105 */   public final QName getProvider() { return this.provider; }
/*     */ 
/*     */   public static final Style getStyle(int style) {
/* 108 */     return type.getStyle(style);
/*     */   }
/*     */ 
/*     */   public static final Style getStyle(String style) {
/* 112 */     return type.getStyle(style);
/*     */   }
/*     */ 
/*     */   public static final Style getStyle(String style, Style dephault) {
/* 116 */     return type.getStyle(style, dephault);
/*     */   }
/*     */ 
/*     */   public static final boolean isValid(String style) {
/* 120 */     return type.isValid(style);
/*     */   }
/*     */ 
/*     */   public static final int size() {
/* 124 */     return type.size();
/*     */   }
/*     */ 
/*     */   public static final String[] getStyles() {
/* 128 */     return type.getEnumNames();
/*     */   }
/*     */ 
/*     */   private Object readResolve() throws ObjectStreamException {
/* 132 */     return type.getStyle(this.value);
/*     */   }
/*     */ 
/*     */   private Style(int value, String name, QName provider)
/*     */   {
/* 163 */     super(type, value, name);
/* 164 */     this.provider = provider;
/*     */   }
/*     */ 
/*     */   protected Style() {
/* 168 */     super(type, DEFAULT.getValue(), DEFAULT.getName());
/* 169 */     this.provider = DEFAULT.getProvider();
/*     */   }
/*     */ 
/*     */   Style(int x0, String x1, QName x2, 1 x3)
/*     */   {
/*  81 */     this(x0, x1, x2);
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  98 */     type.setDefault(DEFAULT);
/*     */   }
/*     */ 
/*     */   public static class Type extends Enum.Type
/*     */   {
/*     */     private Type()
/*     */     {
/* 137 */       super(new Enum[] { new Style(0, "rpc", WSDDConstants.QNAME_JAVARPC_PROVIDER, null), new Style(1, "document", WSDDConstants.QNAME_JAVARPC_PROVIDER, null), new Style(2, "wrapped", WSDDConstants.QNAME_JAVARPC_PROVIDER, null), new Style(3, "message", WSDDConstants.QNAME_JAVAMSG_PROVIDER, null) });
/*     */     }
/*     */ 
/*     */     public final Style getStyle(int style)
/*     */     {
/* 150 */       return (Style)getEnum(style);
/*     */     }
/*     */ 
/*     */     public final Style getStyle(String style) {
/* 154 */       return (Style)getEnum(style);
/*     */     }
/*     */ 
/*     */     public final Style getStyle(String style, Style dephault) {
/* 158 */       return (Style)getEnum(style, dephault);
/*     */     }
/*     */ 
/*     */     Type(Style.1 x0)
/*     */     {
/* 135 */       this();
/*     */     }
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.constants.Style
 * JD-Core Version:    0.6.0
 */