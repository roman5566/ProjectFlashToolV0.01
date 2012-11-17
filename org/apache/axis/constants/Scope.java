/*     */ package org.apache.axis.constants;
/*     */ 
/*     */ import java.io.ObjectStreamException;
/*     */ 
/*     */ public class Scope extends Enum
/*     */ {
/*  26 */   private static final Type type = new Type(null);
/*     */   public static final String REQUEST_STR = "Request";
/*     */   public static final String APPLICATION_STR = "Application";
/*     */   public static final String SESSION_STR = "Session";
/*     */   public static final String FACTORY_STR = "Factory";
/*  33 */   public static final Scope REQUEST = type.getScope("Request");
/*  34 */   public static final Scope APPLICATION = type.getScope("Application");
/*  35 */   public static final Scope SESSION = type.getScope("Session");
/*  36 */   public static final Scope FACTORY = type.getScope("Factory");
/*     */ 
/*  38 */   public static final Scope DEFAULT = REQUEST;
/*     */ 
/*     */   public static Scope getDefault()
/*     */   {
/*  46 */     return (Scope)type.getDefault();
/*     */   }
/*     */   public static final Scope getScope(int scope) {
/*  49 */     return type.getScope(scope);
/*     */   }
/*     */ 
/*     */   public static final Scope getScope(String scope) {
/*  53 */     return type.getScope(scope);
/*     */   }
/*     */ 
/*     */   public static final Scope getScope(String scope, Scope dephault) {
/*  57 */     return type.getScope(scope, dephault);
/*     */   }
/*     */ 
/*     */   public static final boolean isValid(String scope) {
/*  61 */     return type.isValid(scope);
/*     */   }
/*     */ 
/*     */   public static final int size() {
/*  65 */     return type.size();
/*     */   }
/*     */ 
/*     */   public static final String[] getScopes() {
/*  69 */     return type.getEnumNames();
/*     */   }
/*     */ 
/*     */   private Object readResolve() throws ObjectStreamException {
/*  73 */     return type.getScope(this.value);
/*     */   }
/*     */ 
/*     */   private Scope(int value, String name)
/*     */   {
/* 100 */     super(type, value, name);
/*     */   }
/*     */ 
/*     */   protected Scope() {
/* 104 */     super(type, DEFAULT.getValue(), DEFAULT.getName());
/*     */   }
/*     */ 
/*     */   Scope(int x0, String x1, 1 x2)
/*     */   {
/*  25 */     this(x0, x1);
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  40 */     type.setDefault(DEFAULT);
/*     */   }
/*     */ 
/*     */   public static class Type extends Enum.Type
/*     */   {
/*     */     private Type()
/*     */     {
/*  77 */       super(new Enum[] { new Scope(0, "Request", null), new Scope(1, "Application", null), new Scope(2, "Session", null), new Scope(3, "Factory", null) });
/*     */     }
/*     */ 
/*     */     public final Scope getScope(int scope)
/*     */     {
/*  86 */       return (Scope)getEnum(scope);
/*     */     }
/*     */ 
/*     */     public final Scope getScope(String scope) {
/*  90 */       return (Scope)getEnum(scope);
/*     */     }
/*     */ 
/*     */     public final Scope getScope(String scope, Scope dephault) {
/*  94 */       return (Scope)getEnum(scope, dephault);
/*     */     }
/*     */ 
/*     */     Type(Scope.1 x0)
/*     */     {
/*  75 */       this();
/*     */     }
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.constants.Scope
 * JD-Core Version:    0.6.0
 */