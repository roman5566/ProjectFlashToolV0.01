/*     */ package org.apache.axis.wsdl;
/*     */ 
/*     */ import java.util.HashMap;
/*     */ import javax.xml.namespace.QName;
/*     */ import javax.xml.rpc.ParameterMode;
/*     */ 
/*     */ public class SkeletonImpl
/*     */   implements Skeleton
/*     */ {
/*  28 */   private static HashMap table = null;
/*     */ 
/*     */   public SkeletonImpl()
/*     */   {
/*  35 */     if (table == null)
/*  36 */       table = new HashMap();
/*     */   }
/*     */ 
/*     */   public void add(String operation, QName[] names, ParameterMode[] modes, String inputNamespace, String outputNamespace, String soapAction)
/*     */   {
/*  98 */     table.put(operation, new MetaInfo(names, modes, inputNamespace, outputNamespace, soapAction));
/*     */   }
/*     */ 
/*     */   public void add(String operation, String[] names, ParameterMode[] modes, String inputNamespace, String outputNamespace, String soapAction)
/*     */   {
/* 118 */     QName[] qnames = new QName[names.length];
/*     */ 
/* 120 */     for (int i = 0; i < names.length; i++) {
/* 121 */       QName qname = new QName(null, names[i]);
/*     */ 
/* 123 */       qnames[i] = qname;
/*     */     }
/*     */ 
/* 126 */     add(operation, qnames, modes, inputNamespace, outputNamespace, soapAction);
/*     */   }
/*     */ 
/*     */   public QName getParameterName(String operationName, int n)
/*     */   {
/* 141 */     MetaInfo value = (MetaInfo)table.get(operationName);
/*     */ 
/* 143 */     if ((value == null) || (value.names == null) || (value.names.length <= n + 1))
/*     */     {
/* 145 */       return null;
/*     */     }
/*     */ 
/* 148 */     return value.names[(n + 1)];
/*     */   }
/*     */ 
/*     */   public ParameterMode getParameterMode(String operationName, int n)
/*     */   {
/* 162 */     MetaInfo value = (MetaInfo)table.get(operationName);
/*     */ 
/* 164 */     if ((value == null) || (value.modes == null) || (value.modes.length <= n + 1))
/*     */     {
/* 166 */       return null;
/*     */     }
/*     */ 
/* 169 */     return value.modes[(n + 1)];
/*     */   }
/*     */ 
/*     */   public String getInputNamespace(String operationName)
/*     */   {
/* 181 */     MetaInfo value = (MetaInfo)table.get(operationName);
/*     */ 
/* 183 */     if (value == null) {
/* 184 */       return null;
/*     */     }
/*     */ 
/* 187 */     return value.inputNamespace;
/*     */   }
/*     */ 
/*     */   public String getOutputNamespace(String operationName)
/*     */   {
/* 199 */     MetaInfo value = (MetaInfo)table.get(operationName);
/*     */ 
/* 201 */     if (value == null) {
/* 202 */       return null;
/*     */     }
/*     */ 
/* 205 */     return value.outputNamespace;
/*     */   }
/*     */ 
/*     */   public String getSOAPAction(String operationName)
/*     */   {
/* 217 */     MetaInfo value = (MetaInfo)table.get(operationName);
/*     */ 
/* 219 */     if (value == null) {
/* 220 */       return null;
/*     */     }
/*     */ 
/* 223 */     return value.soapAction;
/*     */   }
/*     */ 
/*     */   class MetaInfo
/*     */   {
/*     */     QName[] names;
/*     */     ParameterMode[] modes;
/*     */     String inputNamespace;
/*     */     String outputNamespace;
/*     */     String soapAction;
/*     */ 
/*     */     MetaInfo(QName[] names, ParameterMode[] modes, String inputNamespace, String outputNamespace, String soapAction)
/*     */     {
/*  74 */       this.names = names;
/*  75 */       this.modes = modes;
/*  76 */       this.inputNamespace = inputNamespace;
/*  77 */       this.outputNamespace = outputNamespace;
/*  78 */       this.soapAction = soapAction;
/*     */     }
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.wsdl.SkeletonImpl
 * JD-Core Version:    0.6.0
 */