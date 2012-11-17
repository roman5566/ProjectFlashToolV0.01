/*    */ package org.apache.axis.handlers;
/*    */ 
/*    */ import java.io.Serializable;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import java.util.Map;
/*    */ import javax.xml.rpc.handler.HandlerChain;
/*    */ 
/*    */ public class HandlerInfoChainFactory
/*    */   implements Serializable
/*    */ {
/* 27 */   protected List handlerInfos = new ArrayList();
/* 28 */   protected String[] _roles = null;
/*    */ 
/*    */   public HandlerInfoChainFactory() {
/*    */   }
/*    */ 
/*    */   public HandlerInfoChainFactory(List handlerInfos) {
/* 34 */     this.handlerInfos = handlerInfos;
/*    */   }
/*    */ 
/*    */   public List getHandlerInfos() {
/* 38 */     return this.handlerInfos;
/*    */   }
/*    */ 
/*    */   public HandlerChain createHandlerChain() {
/* 42 */     HandlerChain hc = new HandlerChainImpl(this.handlerInfos);
/* 43 */     hc.setRoles(getRoles());
/* 44 */     return hc;
/*    */   }
/*    */ 
/*    */   public String[] getRoles()
/*    */   {
/* 49 */     return this._roles;
/*    */   }
/*    */ 
/*    */   public void setRoles(String[] roles) {
/* 53 */     this._roles = roles;
/*    */   }
/*    */ 
/*    */   public void init(Map map)
/*    */   {
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.handlers.HandlerInfoChainFactory
 * JD-Core Version:    0.6.0
 */