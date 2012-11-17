/*    */ package org.apache.axis.schema;
/*    */ 
/*    */ import java.io.Serializable;
/*    */ import javax.xml.namespace.QName;
/*    */ import org.apache.axis.encoding.TypeMappingImpl;
/*    */ 
/*    */ public abstract interface SchemaVersion extends Serializable
/*    */ {
/* 31 */   public static final SchemaVersion SCHEMA_1999 = new SchemaVersion1999();
/* 32 */   public static final SchemaVersion SCHEMA_2000 = new SchemaVersion2000();
/* 33 */   public static final SchemaVersion SCHEMA_2001 = new SchemaVersion2001();
/*    */ 
/*    */   public abstract QName getNilQName();
/*    */ 
/*    */   public abstract String getXsiURI();
/*    */ 
/*    */   public abstract String getXsdURI();
/*    */ 
/*    */   public abstract void registerSchemaSpecificTypes(TypeMappingImpl paramTypeMappingImpl);
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.schema.SchemaVersion
 * JD-Core Version:    0.6.0
 */