/*    */ package org.apache.axis.holders;
/*    */ 
/*    */ import javax.xml.rpc.holders.Holder;
/*    */ import org.apache.axis.attachments.OctetStream;
/*    */ 
/*    */ public final class OctetStreamHolder
/*    */   implements Holder
/*    */ {
/*    */   public OctetStream value;
/*    */ 
/*    */   public OctetStreamHolder()
/*    */   {
/*    */   }
/*    */ 
/*    */   public OctetStreamHolder(OctetStream value)
/*    */   {
/* 44 */     this.value = value;
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.holders.OctetStreamHolder
 * JD-Core Version:    0.6.0
 */