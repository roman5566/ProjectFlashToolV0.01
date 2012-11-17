/*    */ package org.apache.axis.types;
/*    */ 
/*    */ import java.util.StringTokenizer;
/*    */ 
/*    */ public class Entities extends NCName
/*    */ {
/*    */   private Entity[] entities;
/*    */ 
/*    */   public Entities()
/*    */   {
/*    */   }
/*    */ 
/*    */   public Entities(String stValue)
/*    */     throws IllegalArgumentException
/*    */   {
/* 37 */     StringTokenizer tokenizer = new StringTokenizer(stValue);
/* 38 */     int count = tokenizer.countTokens();
/* 39 */     this.entities = new Entity[count];
/* 40 */     for (int i = 0; i < count; i++)
/* 41 */       this.entities[i] = new Entity(tokenizer.nextToken());
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.types.Entities
 * JD-Core Version:    0.6.0
 */