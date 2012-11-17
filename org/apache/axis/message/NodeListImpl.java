/*    */ package org.apache.axis.message;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.Collections;
/*    */ import java.util.List;
/*    */ import org.w3c.dom.Node;
/*    */ import org.w3c.dom.NodeList;
/*    */ 
/*    */ class NodeListImpl
/*    */   implements NodeList
/*    */ {
/*    */   List mNodes;
/* 37 */   public static final NodeList EMPTY_NODELIST = new NodeListImpl(Collections.EMPTY_LIST);
/*    */ 
/*    */   NodeListImpl()
/*    */   {
/* 44 */     this.mNodes = new ArrayList();
/*    */   }
/*    */ 
/*    */   NodeListImpl(List nodes) {
/* 48 */     this();
/* 49 */     this.mNodes.addAll(nodes);
/*    */   }
/*    */ 
/*    */   void addNode(Node node) {
/* 53 */     this.mNodes.add(node);
/*    */   }
/*    */ 
/*    */   void addNodeList(NodeList nodes) {
/* 57 */     for (int i = 0; i < nodes.getLength(); i++)
/* 58 */       this.mNodes.add(nodes.item(i));
/*    */   }
/*    */ 
/*    */   public Node item(int index)
/*    */   {
/* 69 */     if ((this.mNodes != null) && (this.mNodes.size() > index)) {
/* 70 */       return (Node)this.mNodes.get(index);
/*    */     }
/* 72 */     return null;
/*    */   }
/*    */ 
/*    */   public int getLength()
/*    */   {
/* 77 */     return this.mNodes.size();
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.message.NodeListImpl
 * JD-Core Version:    0.6.0
 */