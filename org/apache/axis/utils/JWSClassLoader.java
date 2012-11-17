/*    */ package org.apache.axis.utils;
/*    */ 
/*    */ import java.io.ByteArrayOutputStream;
/*    */ import java.io.FileInputStream;
/*    */ import java.io.FileNotFoundException;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ 
/*    */ public class JWSClassLoader extends ClassLoader
/*    */ {
/* 35 */   private String classFile = null;
/* 36 */   private String name = null;
/*    */ 
/*    */   public JWSClassLoader(String name, ClassLoader cl, String classFile)
/*    */     throws FileNotFoundException, IOException
/*    */   {
/* 53 */     super(cl);
/*    */ 
/* 55 */     this.name = (name + ".class");
/* 56 */     this.classFile = classFile;
/*    */ 
/* 58 */     FileInputStream fis = new FileInputStream(classFile);
/* 59 */     ByteArrayOutputStream baos = new ByteArrayOutputStream();
/* 60 */     byte[] buf = new byte[1024];
/* 61 */     for (int i = 0; (i = fis.read(buf)) != -1; )
/* 62 */       baos.write(buf, 0, i);
/* 63 */     fis.close();
/* 64 */     baos.close();
/*    */ 
/* 68 */     byte[] data = baos.toByteArray();
/* 69 */     defineClass(name, data, 0, data.length);
/*    */ 
/* 71 */     ClassUtils.setClassLoader(name, this);
/*    */   }
/*    */ 
/*    */   public InputStream getResourceAsStream(String resourceName)
/*    */   {
/*    */     try
/*    */     {
/* 84 */       if (resourceName.equals(this.name))
/* 85 */         return new FileInputStream(this.classFile);
/*    */     }
/*    */     catch (FileNotFoundException e) {
/*    */     }
/* 89 */     return null;
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.utils.JWSClassLoader
 * JD-Core Version:    0.6.0
 */