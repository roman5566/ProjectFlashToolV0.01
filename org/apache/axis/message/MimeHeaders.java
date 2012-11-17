/*    */ package org.apache.axis.message;
/*    */ 
/*    */ import java.io.Externalizable;
/*    */ import java.io.IOException;
/*    */ import java.io.ObjectInput;
/*    */ import java.io.ObjectOutput;
/*    */ import java.util.Iterator;
/*    */ import javax.xml.soap.MimeHeader;
/*    */ 
/*    */ public class MimeHeaders extends javax.xml.soap.MimeHeaders
/*    */   implements Externalizable
/*    */ {
/*    */   public MimeHeaders()
/*    */   {
/*    */   }
/*    */ 
/*    */   public MimeHeaders(javax.xml.soap.MimeHeaders h)
/*    */   {
/* 34 */     Iterator iterator = h.getAllHeaders();
/* 35 */     while (iterator.hasNext()) {
/* 36 */       MimeHeader hdr = (MimeHeader)iterator.next();
/* 37 */       addHeader(hdr.getName(), hdr.getValue());
/*    */     }
/*    */   }
/*    */ 
/*    */   public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
/* 42 */     int size = in.readInt();
/* 43 */     for (int i = 0; i < size; i++) {
/* 44 */       Object key = in.readObject();
/* 45 */       Object value = in.readObject();
/* 46 */       addHeader((String)key, (String)value);
/*    */     }
/*    */   }
/*    */ 
/*    */   public void writeExternal(ObjectOutput out) throws IOException {
/* 51 */     out.writeInt(getHeaderSize());
/* 52 */     Iterator iterator = getAllHeaders();
/* 53 */     while (iterator.hasNext()) {
/* 54 */       MimeHeader hdr = (MimeHeader)iterator.next();
/* 55 */       out.writeObject(hdr.getName());
/* 56 */       out.writeObject(hdr.getValue());
/*    */     }
/*    */   }
/*    */ 
/*    */   private int getHeaderSize() {
/* 61 */     int size = 0;
/* 62 */     Iterator iterator = getAllHeaders();
/* 63 */     while (iterator.hasNext()) {
/* 64 */       iterator.next();
/* 65 */       size++;
/*    */     }
/* 67 */     return size;
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.message.MimeHeaders
 * JD-Core Version:    0.6.0
 */