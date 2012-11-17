/*    */ package org.apache.axis.message;
/*    */ 
/*    */ import org.w3c.dom.CharacterData;
/*    */ import org.w3c.dom.Comment;
/*    */ import org.w3c.dom.DOMException;
/*    */ import org.w3c.dom.Node;
/*    */ import org.w3c.dom.NodeList;
/*    */ 
/*    */ public class CommentImpl extends Text
/*    */   implements javax.xml.soap.Text, Comment
/*    */ {
/*    */   public CommentImpl(String text)
/*    */   {
/* 36 */     super(text);
/*    */   }
/*    */ 
/*    */   public boolean isComment() {
/* 40 */     return true;
/*    */   }
/*    */ 
/*    */   public org.w3c.dom.Text splitText(int offset) throws DOMException {
/* 44 */     int length = this.textRep.getLength();
/*    */ 
/* 48 */     String tailData = this.textRep.substringData(offset, length);
/* 49 */     this.textRep.deleteData(offset, length);
/*    */ 
/* 52 */     javax.xml.soap.Text tailText = new CommentImpl(tailData);
/* 53 */     Node myParent = getParentNode();
/* 54 */     if (myParent != null) {
/* 55 */       NodeList brothers = myParent.getChildNodes();
/*    */ 
/* 57 */       for (int i = 0; i < brothers.getLength(); i++) {
/* 58 */         if (brothers.item(i).equals(this)) {
/* 59 */           myParent.insertBefore(tailText, this);
/* 60 */           return tailText;
/*    */         }
/*    */       }
/*    */     }
/* 64 */     return tailText;
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.message.CommentImpl
 * JD-Core Version:    0.6.0
 */