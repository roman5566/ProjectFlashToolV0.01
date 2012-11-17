/*     */ package org.apache.axis.utils.bytecode;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.Member;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Modifier;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import org.apache.axis.utils.Messages;
/*     */ 
/*     */ public class ParamReader extends ClassReader
/*     */ {
/*     */   private String methodName;
/*  45 */   private Map methods = new HashMap();
/*     */   private Class[] paramTypes;
/*     */ 
/*     */   public ParamReader(Class c)
/*     */     throws IOException
/*     */   {
/*  55 */     this(getBytes(c));
/*     */   }
/*     */ 
/*     */   public ParamReader(byte[] b)
/*     */     throws IOException
/*     */   {
/*  64 */     super(b, findAttributeReaders(ParamReader.class));
/*     */ 
/*  67 */     if (readInt() != -889275714)
/*     */     {
/*  69 */       throw new IOException(Messages.getMessage("badClassFile00"));
/*     */     }
/*     */ 
/*  72 */     readShort();
/*  73 */     readShort();
/*     */ 
/*  75 */     readCpool();
/*     */ 
/*  77 */     readShort();
/*  78 */     readShort();
/*  79 */     readShort();
/*     */ 
/*  81 */     int count = readShort();
/*  82 */     for (int i = 0; i < count; i++) {
/*  83 */       readShort();
/*     */     }
/*     */ 
/*  86 */     count = readShort();
/*  87 */     for (int i = 0; i < count; i++) {
/*  88 */       readShort();
/*  89 */       readShort();
/*  90 */       readShort();
/*  91 */       skipAttributes();
/*     */     }
/*     */ 
/*  94 */     count = readShort();
/*  95 */     for (int i = 0; i < count; i++) {
/*  96 */       readShort();
/*  97 */       int m = readShort();
/*  98 */       String name = resolveUtf8(m);
/*  99 */       int d = readShort();
/* 100 */       this.methodName = (name + resolveUtf8(d));
/* 101 */       readAttributes();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void readCode()
/*     */     throws IOException
/*     */   {
/* 108 */     readShort();
/* 109 */     int maxLocals = readShort();
/*     */ 
/* 111 */     MethodInfo info = new MethodInfo(maxLocals);
/* 112 */     if ((this.methods != null) && (this.methodName != null))
/*     */     {
/* 114 */       this.methods.put(this.methodName, info);
/*     */     }
/*     */ 
/* 117 */     skipFully(readInt());
/* 118 */     skipFully(8 * readShort());
/*     */ 
/* 121 */     readAttributes();
/*     */   }
/*     */ 
/*     */   public String[] getParameterNames(Constructor ctor)
/*     */   {
/* 133 */     this.paramTypes = ctor.getParameterTypes();
/* 134 */     return getParameterNames(ctor, this.paramTypes);
/*     */   }
/*     */ 
/*     */   public String[] getParameterNames(Method method)
/*     */   {
/* 146 */     this.paramTypes = method.getParameterTypes();
/* 147 */     return getParameterNames(method, this.paramTypes);
/*     */   }
/*     */ 
/*     */   protected String[] getParameterNames(Member member, Class[] paramTypes)
/*     */   {
/* 152 */     MethodInfo info = (MethodInfo)this.methods.get(getSignature(member, paramTypes));
/*     */ 
/* 157 */     if (info != null) {
/* 158 */       String[] paramNames = new String[paramTypes.length];
/* 159 */       int j = Modifier.isStatic(member.getModifiers()) ? 0 : 1;
/*     */ 
/* 161 */       boolean found = false;
/* 162 */       for (int i = 0; i < paramNames.length; i++) {
/* 163 */         if (info.names[j] != null) {
/* 164 */           found = true;
/* 165 */           paramNames[i] = info.names[j];
/*     */         }
/* 167 */         j++;
/* 168 */         if ((paramTypes[i] != Double.TYPE) && (paramTypes[i] != Long.TYPE))
/*     */           continue;
/* 170 */         j++;
/*     */       }
/*     */ 
/* 174 */       if (found) {
/* 175 */         return paramNames;
/*     */       }
/* 177 */       return null;
/*     */     }
/*     */ 
/* 180 */     return null;
/*     */   }
/*     */ 
/*     */   private MethodInfo getMethodInfo()
/*     */   {
/* 198 */     MethodInfo info = null;
/* 199 */     if ((this.methods != null) && (this.methodName != null))
/*     */     {
/* 201 */       info = (MethodInfo)this.methods.get(this.methodName);
/*     */     }
/* 203 */     return info;
/*     */   }
/*     */ 
/*     */   public void readLocalVariableTable()
/*     */     throws IOException
/*     */   {
/* 211 */     int len = readShort();
/* 212 */     MethodInfo info = getMethodInfo();
/* 213 */     for (int j = 0; j < len; j++) {
/* 214 */       readShort();
/* 215 */       readShort();
/* 216 */       int nameIndex = readShort();
/* 217 */       readShort();
/* 218 */       int index = readShort();
/* 219 */       if (info != null)
/* 220 */         info.names[index] = resolveUtf8(nameIndex);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class MethodInfo
/*     */   {
/*     */     String[] names;
/*     */     int maxLocals;
/*     */ 
/*     */     public MethodInfo(int maxLocals)
/*     */     {
/* 191 */       this.maxLocals = maxLocals;
/* 192 */       this.names = new String[maxLocals];
/*     */     }
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.utils.bytecode.ParamReader
 * JD-Core Version:    0.6.0
 */