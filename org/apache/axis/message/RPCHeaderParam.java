/*    */ package org.apache.axis.message;
/*    */ 
/*    */ import javax.xml.namespace.QName;
/*    */ import org.apache.axis.MessageContext;
/*    */ import org.apache.axis.encoding.SerializationContext;
/*    */ 
/*    */ public class RPCHeaderParam extends SOAPHeaderElement
/*    */ {
/*    */   public RPCHeaderParam(RPCParam rpcParam)
/*    */   {
/* 28 */     super(rpcParam.getQName().getNamespaceURI(), rpcParam.getQName().getLocalPart(), rpcParam);
/*    */   }
/*    */ 
/*    */   protected void outputImpl(SerializationContext context)
/*    */     throws Exception
/*    */   {
/* 38 */     MessageContext msgContext = context.getMessageContext();
/*    */ 
/* 41 */     RPCParam rpcParam = (RPCParam)getObjectValue();
/* 42 */     if ((this.encodingStyle != null) && (this.encodingStyle.equals(""))) {
/* 43 */       context.registerPrefixForURI("", rpcParam.getQName().getNamespaceURI());
/*    */     }
/* 45 */     rpcParam.serialize(context);
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.message.RPCHeaderParam
 * JD-Core Version:    0.6.0
 */