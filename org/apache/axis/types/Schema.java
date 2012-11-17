/*     */ package org.apache.axis.types;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.lang.reflect.Array;
/*     */ import java.util.Arrays;
/*     */ import javax.xml.namespace.QName;
/*     */ import org.apache.axis.description.AttributeDesc;
/*     */ import org.apache.axis.description.FieldDesc;
/*     */ import org.apache.axis.description.TypeDesc;
/*     */ import org.apache.axis.encoding.Deserializer;
/*     */ import org.apache.axis.encoding.Serializer;
/*     */ import org.apache.axis.encoding.ser.BeanDeserializer;
/*     */ import org.apache.axis.encoding.ser.BeanSerializer;
/*     */ import org.apache.axis.message.MessageElement;
/*     */ 
/*     */ public class Schema
/*     */   implements Serializable
/*     */ {
/*     */   private MessageElement[] _any;
/*     */   private URI targetNamespace;
/*     */   private NormalizedString version;
/*     */   private Id id;
/*  65 */   private Object __equalsCalc = null;
/*     */ 
/*  94 */   private boolean __hashCodeCalc = false;
/*     */ 
/* 127 */   private static TypeDesc typeDesc = new TypeDesc(Schema.class);
/*     */ 
/*     */   public MessageElement[] get_any()
/*     */   {
/*  34 */     return this._any;
/*     */   }
/*     */ 
/*     */   public void set_any(MessageElement[] _any) {
/*  38 */     this._any = _any;
/*     */   }
/*     */ 
/*     */   public URI getTargetNamespace() {
/*  42 */     return this.targetNamespace;
/*     */   }
/*     */ 
/*     */   public void setTargetNamespace(URI targetNamespace) {
/*  46 */     this.targetNamespace = targetNamespace;
/*     */   }
/*     */ 
/*     */   public NormalizedString getVersion() {
/*  50 */     return this.version;
/*     */   }
/*     */ 
/*     */   public void setVersion(NormalizedString version) {
/*  54 */     this.version = version;
/*     */   }
/*     */ 
/*     */   public Id getId() {
/*  58 */     return this.id;
/*     */   }
/*     */ 
/*     */   public void setId(Id id) {
/*  62 */     this.id = id;
/*     */   }
/*     */ 
/*     */   public synchronized boolean equals(Object obj)
/*     */   {
/*  68 */     if (!(obj instanceof Schema)) return false;
/*  69 */     Schema other = (Schema)obj;
/*  70 */     if (obj == null) return false;
/*  71 */     if (this == obj) return true;
/*  72 */     if (this.__equalsCalc != null) {
/*  73 */       return this.__equalsCalc == obj;
/*     */     }
/*  75 */     this.__equalsCalc = obj;
/*     */ 
/*  77 */     boolean _equals = ((this._any == null) && (other.get_any() == null)) || ((this._any != null) && (Arrays.equals(this._any, other.get_any())) && (((this.targetNamespace == null) && (other.getTargetNamespace() == null)) || ((this.targetNamespace != null) && (this.targetNamespace.equals(other.getTargetNamespace())) && (((this.version == null) && (other.getVersion() == null)) || ((this.version != null) && (this.version.equals(other.getVersion())) && (((this.id == null) && (other.getId() == null)) || ((this.id != null) && (this.id.equals(other.getId())))))))));
/*     */ 
/*  90 */     this.__equalsCalc = null;
/*  91 */     return _equals;
/*     */   }
/*     */ 
/*     */   public synchronized int hashCode()
/*     */   {
/*  97 */     if (this.__hashCodeCalc) {
/*  98 */       return 0;
/*     */     }
/* 100 */     this.__hashCodeCalc = true;
/* 101 */     int _hashCode = 1;
/* 102 */     if (get_any() != null) {
/* 103 */       int i = 0;
/* 104 */       while (i < Array.getLength(get_any()))
/*     */       {
/* 106 */         Object obj = Array.get(get_any(), i);
/* 107 */         if ((obj != null) && (!obj.getClass().isArray()))
/*     */         {
/* 109 */           _hashCode += obj.hashCode();
/*     */         }
/* 105 */         i++;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 113 */     if (getTargetNamespace() != null) {
/* 114 */       _hashCode += getTargetNamespace().hashCode();
/*     */     }
/* 116 */     if (getVersion() != null) {
/* 117 */       _hashCode += getVersion().hashCode();
/*     */     }
/* 119 */     if (getId() != null) {
/* 120 */       _hashCode += getId().hashCode();
/*     */     }
/* 122 */     this.__hashCodeCalc = false;
/* 123 */     return _hashCode;
/*     */   }
/*     */ 
/*     */   public static TypeDesc getTypeDesc()
/*     */   {
/* 152 */     return typeDesc;
/*     */   }
/*     */ 
/*     */   public static Serializer getSerializer(String mechType, Class _javaType, QName _xmlType)
/*     */   {
/* 162 */     return new BeanSerializer(_javaType, _xmlType, typeDesc);
/*     */   }
/*     */ 
/*     */   public static Deserializer getDeserializer(String mechType, Class _javaType, QName _xmlType)
/*     */   {
/* 174 */     return new BeanDeserializer(_javaType, _xmlType, typeDesc);
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/* 131 */     FieldDesc field = new AttributeDesc();
/* 132 */     field.setFieldName("targetNamespace");
/* 133 */     field.setXmlName(new QName("", "targetNamespace"));
/* 134 */     field.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "anyURI"));
/* 135 */     typeDesc.addFieldDesc(field);
/* 136 */     field = new AttributeDesc();
/* 137 */     field.setFieldName("version");
/* 138 */     field.setXmlName(new QName("", "version"));
/* 139 */     field.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "normalizedString"));
/* 140 */     typeDesc.addFieldDesc(field);
/* 141 */     field = new AttributeDesc();
/* 142 */     field.setFieldName("id");
/* 143 */     field.setXmlName(new QName("", "id"));
/* 144 */     field.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "ID"));
/* 145 */     typeDesc.addFieldDesc(field);
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.types.Schema
 * JD-Core Version:    0.6.0
 */