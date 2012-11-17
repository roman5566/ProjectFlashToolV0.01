/*     */ package org.apache.axis.utils.bytecode;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class ChainedParamReader
/*     */ {
/*  37 */   private List chain = new ArrayList();
/*  38 */   private List clsChain = new ArrayList();
/*  39 */   private Map methodToParamMap = new HashMap();
/*     */ 
/*     */   public ChainedParamReader(Class cls)
/*     */     throws IOException
/*     */   {
/*  47 */     ParamReader reader = new ParamReader(cls);
/*  48 */     this.chain.add(reader);
/*  49 */     this.clsChain.add(cls);
/*     */   }
/*     */ 
/*     */   public String[] getParameterNames(Constructor ctor)
/*     */   {
/*  63 */     return ((ParamReader)this.chain.get(0)).getParameterNames(ctor);
/*     */   }
/*     */ 
/*     */   public String[] getParameterNames(Method method)
/*     */   {
/*  78 */     if (this.methodToParamMap.containsKey(method)) {
/*  79 */       return (String[])this.methodToParamMap.get(method);
/*     */     }
/*     */ 
/*  82 */     String[] ret = null;
/*  83 */     for (Iterator it = this.chain.iterator(); it.hasNext(); ) {
/*  84 */       ParamReader reader = (ParamReader)it.next();
/*  85 */       ret = reader.getParameterNames(method);
/*  86 */       if (ret != null) {
/*  87 */         this.methodToParamMap.put(method, ret);
/*  88 */         return ret;
/*     */       }
/*     */     }
/*     */ 
/*  92 */     Class cls = (Class)this.clsChain.get(this.chain.size() - 1);
/*  93 */     while (cls.getSuperclass() != null) {
/*  94 */       Class superClass = cls.getSuperclass();
/*     */       try {
/*  96 */         ParamReader _reader = new ParamReader(superClass);
/*  97 */         this.chain.add(_reader);
/*  98 */         this.clsChain.add(cls);
/*  99 */         ret = _reader.getParameterNames(method);
/* 100 */         if (ret != null) {
/* 101 */           this.methodToParamMap.put(method, ret);
/* 102 */           return ret;
/*     */         }
/*     */       }
/*     */       catch (IOException e) {
/* 106 */         return null;
/*     */       }
/*     */     }
/* 109 */     this.methodToParamMap.put(method, ret);
/* 110 */     return null;
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.utils.bytecode.ChainedParamReader
 * JD-Core Version:    0.6.0
 */