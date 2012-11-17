/*     */ package org.apache.axis.utils.bytecode;
/*     */ 
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.EOFException;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Member;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import org.apache.axis.utils.Messages;
/*     */ 
/*     */ public class ClassReader extends ByteArrayInputStream
/*     */ {
/*     */   private static final int CONSTANT_Class = 7;
/*     */   private static final int CONSTANT_Fieldref = 9;
/*     */   private static final int CONSTANT_Methodref = 10;
/*     */   private static final int CONSTANT_InterfaceMethodref = 11;
/*     */   private static final int CONSTANT_String = 8;
/*     */   private static final int CONSTANT_Integer = 3;
/*     */   private static final int CONSTANT_Float = 4;
/*     */   private static final int CONSTANT_Long = 5;
/*     */   private static final int CONSTANT_Double = 6;
/*     */   private static final int CONSTANT_NameAndType = 12;
/*     */   private static final int CONSTANT_Utf8 = 1;
/*     */   private int[] cpoolIndex;
/*     */   private Object[] cpool;
/*     */   private Map attrMethods;
/*     */ 
/*     */   protected static byte[] getBytes(Class c)
/*     */     throws IOException
/*     */   {
/*  81 */     InputStream fin = c.getResourceAsStream('/' + c.getName().replace('.', '/') + ".class");
/*  82 */     if (fin == null) {
/*  83 */       throw new IOException(Messages.getMessage("cantLoadByecode", c.getName()));
/*     */     }try {
/*  87 */       ByteArrayOutputStream out = new ByteArrayOutputStream();
/*  88 */       byte[] buf = new byte[1024];
/*     */       int actual;
/*     */       do { actual = fin.read(buf);
/*  92 */         if (actual > 0)
/*  93 */           out.write(buf, 0, actual);
/*     */       }
/*  95 */       while (actual > 0);
/*  96 */       byte[] arrayOfByte1 = out.toByteArray();
/*     */       return arrayOfByte1; } finally { fin.close(); } throw localObject;
/*     */   }
/*     */ 
/*     */   static String classDescriptorToName(String desc)
/*     */   {
/* 103 */     return desc.replace('/', '.');
/*     */   }
/*     */ 
/*     */   protected static Map findAttributeReaders(Class c) {
/* 107 */     HashMap map = new HashMap();
/* 108 */     Method[] methods = c.getMethods();
/*     */ 
/* 110 */     for (int i = 0; i < methods.length; i++) {
/* 111 */       String name = methods[i].getName();
/* 112 */       if ((name.startsWith("read")) && (methods[i].getReturnType() == Void.TYPE)) {
/* 113 */         map.put(name.substring(4), methods[i]);
/*     */       }
/*     */     }
/*     */ 
/* 117 */     return map;
/*     */   }
/*     */ 
/*     */   protected static String getSignature(Member method, Class[] paramTypes)
/*     */   {
/* 124 */     StringBuffer b = new StringBuffer((method instanceof Method) ? method.getName() : "<init>");
/* 125 */     b.append('(');
/*     */ 
/* 127 */     for (int i = 0; i < paramTypes.length; i++) {
/* 128 */       addDescriptor(b, paramTypes[i]);
/*     */     }
/*     */ 
/* 131 */     b.append(')');
/* 132 */     if ((method instanceof Method))
/* 133 */       addDescriptor(b, ((Method)method).getReturnType());
/* 134 */     else if ((method instanceof Constructor)) {
/* 135 */       addDescriptor(b, Void.TYPE);
/*     */     }
/*     */ 
/* 138 */     return b.toString();
/*     */   }
/*     */ 
/*     */   private static void addDescriptor(StringBuffer b, Class c) {
/* 142 */     if (c.isPrimitive()) {
/* 143 */       if (c == Void.TYPE)
/* 144 */         b.append('V');
/* 145 */       else if (c == Integer.TYPE)
/* 146 */         b.append('I');
/* 147 */       else if (c == Boolean.TYPE)
/* 148 */         b.append('Z');
/* 149 */       else if (c == Byte.TYPE)
/* 150 */         b.append('B');
/* 151 */       else if (c == Short.TYPE)
/* 152 */         b.append('S');
/* 153 */       else if (c == Long.TYPE)
/* 154 */         b.append('J');
/* 155 */       else if (c == Character.TYPE)
/* 156 */         b.append('C');
/* 157 */       else if (c == Float.TYPE)
/* 158 */         b.append('F');
/* 159 */       else if (c == Double.TYPE) b.append('D'); 
/*     */     }
/* 160 */     else if (c.isArray()) {
/* 161 */       b.append('[');
/* 162 */       addDescriptor(b, c.getComponentType());
/*     */     } else {
/* 164 */       b.append('L').append(c.getName().replace('.', '/')).append(';');
/*     */     }
/*     */   }
/*     */ 
/*     */   protected final int readShort()
/*     */   {
/* 173 */     return read() << 8 | read();
/*     */   }
/*     */ 
/*     */   protected final int readInt()
/*     */   {
/* 180 */     return read() << 24 | read() << 16 | read() << 8 | read();
/*     */   }
/*     */ 
/*     */   protected void skipFully(int n)
/*     */     throws IOException
/*     */   {
/* 187 */     while (n > 0) {
/* 188 */       int c = (int)skip(n);
/* 189 */       if (c <= 0)
/* 190 */         throw new EOFException(Messages.getMessage("unexpectedEOF00"));
/* 191 */       n -= c;
/*     */     }
/*     */   }
/*     */ 
/*     */   protected final Member resolveMethod(int index) throws IOException, ClassNotFoundException, NoSuchMethodException {
/* 196 */     int oldPos = this.pos;
/*     */     try {
/* 198 */       Member m = (Member)this.cpool[index];
/* 199 */       if (m == null) {
/* 200 */         this.pos = this.cpoolIndex[index];
/* 201 */         owner = resolveClass(readShort());
/* 202 */         NameAndType nt = resolveNameAndType(readShort());
/* 203 */         String signature = nt.name + nt.type;
/*     */         Member localMember1;
/* 204 */         if (nt.name.equals("<init>")) {
/* 205 */           Constructor[] ctors = owner.getConstructors();
/* 206 */           for (int i = 0; i < ctors.length; i++) {
/* 207 */             String sig = getSignature(ctors[i], ctors[i].getParameterTypes());
/* 208 */             if (sig.equals(signature))
/*     */             {
/*     */               Constructor tmp146_145 = ctors[i]; m = tmp146_145; this.cpool[index] = tmp146_145;
/* 210 */               localMember1 = m;
/*     */               return localMember1;
/*     */             }
/*     */           }
/*     */         }
/*     */         else
/*     */         {
/* 214 */           Method[] methods = owner.getDeclaredMethods();
/* 215 */           for (int i = 0; i < methods.length; i++) {
/* 216 */             String sig = getSignature(methods[i], methods[i].getParameterTypes());
/* 217 */             if (sig.equals(signature))
/*     */             {
/*     */               Method tmp225_224 = methods[i]; m = tmp225_224; this.cpool[index] = tmp225_224;
/* 219 */               localMember1 = m;
/*     */               return localMember1;
/*     */             }
/*     */           }
/*     */         }
/* 223 */         throw new NoSuchMethodException(signature);
/*     */       }
/* 225 */       Class owner = m;
/*     */       return owner; } finally { this.pos = oldPos; } throw localObject;
/*     */   }
/*     */ 
/*     */   protected final Field resolveField(int i)
/*     */     throws IOException, ClassNotFoundException, NoSuchFieldException
/*     */   {
/* 233 */     int oldPos = this.pos;
/*     */     try {
/* 235 */       Field f = (Field)this.cpool[i];
/* 236 */       if (f == null) {
/* 237 */         this.pos = this.cpoolIndex[i];
/* 238 */         owner = resolveClass(readShort());
/* 239 */         NameAndType nt = resolveNameAndType(readShort());
/*     */         Field tmp64_61 = owner.getDeclaredField(nt.name); f = tmp64_61; this.cpool[i] = tmp64_61;
/*     */       }
/* 242 */       Class owner = f;
/*     */       return owner; } finally { this.pos = oldPos; } throw localObject;
/*     */   }
/*     */ 
/*     */   protected final NameAndType resolveNameAndType(int i)
/*     */     throws IOException
/*     */   {
/* 259 */     int oldPos = this.pos;
/*     */     try {
/* 261 */       NameAndType nt = (NameAndType)this.cpool[i];
/* 262 */       if (nt == null) {
/* 263 */         this.pos = this.cpoolIndex[i];
/* 264 */         name = resolveUtf8(readShort());
/* 265 */         String type = resolveUtf8(readShort());
/*     */          tmp65_62 = new NameAndType(name, type); nt = tmp65_62; this.cpool[i] = tmp65_62;
/*     */       }
/* 268 */       String name = nt;
/*     */       return name; } finally { this.pos = oldPos; } throw localObject;
/*     */   }
/*     */ 
/*     */   protected final Class resolveClass(int i)
/*     */     throws IOException, ClassNotFoundException
/*     */   {
/* 276 */     int oldPos = this.pos;
/*     */     try {
/* 278 */       Class c = (Class)this.cpool[i];
/* 279 */       if (c == null) {
/* 280 */         this.pos = this.cpoolIndex[i];
/* 281 */         name = resolveUtf8(readShort());
/*     */         Class tmp52_49 = Class.forName(classDescriptorToName(name)); c = tmp52_49; this.cpool[i] = tmp52_49;
/*     */       }
/* 284 */       String name = c;
/*     */       return name; } finally { this.pos = oldPos; } throw localObject;
/*     */   }
/*     */ 
/*     */   protected final String resolveUtf8(int i) throws IOException
/*     */   {
/* 291 */     int oldPos = this.pos;
/*     */     try {
/* 293 */       String s = (String)this.cpool[i];
/* 294 */       if (s == null) {
/* 295 */         this.pos = this.cpoolIndex[i];
/* 296 */         len = readShort();
/* 297 */         skipFully(len);
/*     */          tmp68_65 = new String(this.buf, this.pos - len, len, "utf-8"); s = tmp68_65; this.cpool[i] = tmp68_65;
/*     */       }
/* 300 */       int len = s;
/*     */       return len; } finally { this.pos = oldPos; } throw localObject;
/*     */   }
/*     */ 
/*     */   protected final void readCpool() throws IOException
/*     */   {
/* 307 */     int count = readShort();
/* 308 */     this.cpoolIndex = new int[count];
/* 309 */     this.cpool = new Object[count];
/* 310 */     for (int i = 1; i < count; i++) {
/* 311 */       int c = read();
/* 312 */       this.cpoolIndex[i] = this.pos;
/* 313 */       switch (c)
/*     */       {
/*     */       case 9:
/*     */       case 10:
/*     */       case 11:
/*     */       case 12:
/* 320 */         readShort();
/*     */       case 7:
/*     */       case 8:
/* 326 */         readShort();
/* 327 */         break;
/*     */       case 5:
/*     */       case 6:
/* 332 */         readInt();
/*     */ 
/* 336 */         i++;
/*     */       case 3:
/*     */       case 4:
/* 342 */         readInt();
/* 343 */         break;
/*     */       case 1:
/* 347 */         int len = readShort();
/* 348 */         skipFully(len);
/* 349 */         break;
/*     */       case 2:
/*     */       default:
/* 353 */         throw new IllegalStateException(Messages.getMessage("unexpectedBytes00"));
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   protected final void skipAttributes() throws IOException
/*     */   {
/* 360 */     int count = readShort();
/* 361 */     for (int i = 0; i < count; i++) {
/* 362 */       readShort();
/* 363 */       skipFully(readInt());
/*     */     }
/*     */   }
/*     */ 
/*     */   protected final void readAttributes()
/*     */     throws IOException
/*     */   {
/* 373 */     int count = readShort();
/* 374 */     for (int i = 0; i < count; i++) {
/* 375 */       int nameIndex = readShort();
/* 376 */       int attrLen = readInt();
/* 377 */       int curPos = this.pos;
/*     */ 
/* 379 */       String attrName = resolveUtf8(nameIndex);
/*     */ 
/* 381 */       Method m = (Method)this.attrMethods.get(attrName);
/*     */ 
/* 383 */       if (m != null) {
/*     */         try {
/* 385 */           m.invoke(this, new Object[0]);
/*     */         } catch (IllegalAccessException e) {
/* 387 */           this.pos = curPos;
/* 388 */           skipFully(attrLen);
/*     */         } catch (InvocationTargetException e) {
/*     */           try {
/* 391 */             throw e.getTargetException();
/*     */           } catch (Error ex) {
/* 393 */             throw ex;
/*     */           } catch (RuntimeException ex) {
/* 395 */             throw ex;
/*     */           } catch (IOException ex) {
/* 397 */             throw ex;
/*     */           } catch (Throwable ex) {
/* 399 */             this.pos = curPos;
/* 400 */             skipFully(attrLen);
/*     */           }
/*     */         }
/*     */       }
/*     */       else
/* 405 */         skipFully(attrLen);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void readCode()
/*     */     throws IOException
/*     */   {
/* 415 */     readShort();
/* 416 */     readShort();
/* 417 */     skipFully(readInt());
/* 418 */     skipFully(8 * readShort());
/*     */ 
/* 422 */     readAttributes();
/*     */   }
/*     */ 
/*     */   protected ClassReader(byte[] buf, Map attrMethods) {
/* 426 */     super(buf);
/*     */ 
/* 428 */     this.attrMethods = attrMethods;
/*     */   }
/*     */ 
/*     */   private static class NameAndType
/*     */   {
/*     */     String name;
/*     */     String type;
/*     */ 
/*     */     public NameAndType(String name, String type)
/*     */     {
/* 253 */       this.name = name;
/* 254 */       this.type = type;
/*     */     }
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.utils.bytecode.ClassReader
 * JD-Core Version:    0.6.0
 */