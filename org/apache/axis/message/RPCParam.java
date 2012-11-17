/*     */ package org.apache.axis.message;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.ObjectOutputStream;
/*     */ import java.io.Serializable;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.ArrayList;
/*     */ import javax.xml.namespace.QName;
/*     */ import javax.xml.soap.SOAPElement;
/*     */ import javax.xml.soap.SOAPException;
/*     */ import org.apache.axis.Constants;
/*     */ import org.apache.axis.MessageContext;
/*     */ import org.apache.axis.components.logger.LogFactory;
/*     */ import org.apache.axis.constants.Style;
/*     */ import org.apache.axis.description.OperationDesc;
/*     */ import org.apache.axis.description.ParameterDesc;
/*     */ import org.apache.axis.encoding.SerializationContext;
/*     */ import org.apache.axis.utils.JavaUtils;
/*     */ import org.apache.axis.utils.Messages;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public class RPCParam extends MessageElement
/*     */   implements Serializable
/*     */ {
/*  45 */   protected static Log log = LogFactory.getLog(RPCParam.class.getName());
/*     */ 
/*  48 */   private Object value = null;
/*  49 */   private int countSetCalls = 0;
/*     */   private ParameterDesc paramDesc;
/*  59 */   private Boolean wantXSIType = null;
/*     */   private static Method valueSetMethod;
/*     */ 
/*     */   public RPCParam(String name, Object value)
/*     */   {
/*  76 */     this(new QName("", name), value);
/*     */   }
/*     */ 
/*     */   public RPCParam(QName qname, Object value)
/*     */   {
/*  81 */     super(qname);
/*  82 */     if ((value instanceof String))
/*     */       try {
/*  84 */         addTextNode((String)value);
/*     */       } catch (SOAPException e) {
/*  86 */         throw new RuntimeException(Messages.getMessage("cannotCreateTextNode00"));
/*     */       }
/*     */     else
/*  89 */       this.value = value;
/*     */   }
/*     */ 
/*     */   public RPCParam(String namespace, String name, Object value)
/*     */   {
/*  95 */     this(new QName(namespace, name), value);
/*     */   }
/*     */ 
/*     */   public void setRPCCall(RPCElement call)
/*     */   {
/* 100 */     this.parent = call;
/*     */   }
/*     */ 
/*     */   public Object getObjectValue()
/*     */   {
/* 105 */     return this.value;
/*     */   }
/*     */ 
/*     */   public void setObjectValue(Object value)
/*     */   {
/* 110 */     this.value = value;
/*     */   }
/*     */ 
/*     */   public void set(Object newValue)
/*     */   {
/* 122 */     this.countSetCalls += 1;
/*     */ 
/* 125 */     if (this.countSetCalls == 1) {
/* 126 */       this.value = newValue;
/* 127 */       return;
/*     */     }
/*     */ 
/* 131 */     if (this.countSetCalls == 2) {
/* 132 */       ArrayList list = new ArrayList();
/* 133 */       list.add(this.value);
/* 134 */       this.value = list;
/*     */     }
/*     */ 
/* 137 */     ((ArrayList)this.value).add(newValue);
/*     */   }
/*     */ 
/*     */   public static Method getValueSetMethod()
/*     */   {
/* 142 */     return valueSetMethod;
/*     */   }
/*     */ 
/*     */   public ParameterDesc getParamDesc() {
/* 146 */     return this.paramDesc;
/*     */   }
/*     */ 
/*     */   public void setParamDesc(ParameterDesc paramDesc) {
/* 150 */     this.paramDesc = paramDesc;
/*     */   }
/*     */ 
/*     */   public void setXSITypeGeneration(Boolean value) {
/* 154 */     this.wantXSIType = value;
/*     */   }
/*     */ 
/*     */   public Boolean getXSITypeGeneration() {
/* 158 */     return this.wantXSIType;
/*     */   }
/*     */ 
/*     */   public void serialize(SerializationContext context)
/*     */     throws IOException
/*     */   {
/* 169 */     Class javaType = this.value == null ? null : this.value.getClass();
/* 170 */     QName xmlType = null;
/*     */ 
/* 173 */     Boolean sendNull = Boolean.TRUE;
/* 174 */     if (this.paramDesc != null) {
/* 175 */       if (javaType == null) {
/* 176 */         javaType = this.paramDesc.getJavaType() != null ? this.paramDesc.getJavaType() : javaType;
/*     */       }
/* 178 */       else if (!javaType.equals(this.paramDesc.getJavaType())) {
/* 179 */         Class clazz = JavaUtils.getPrimitiveClass(javaType);
/* 180 */         if (((clazz == null) || (!clazz.equals(this.paramDesc.getJavaType()))) && 
/* 181 */           (!javaType.equals(JavaUtils.getHolderValueType(this.paramDesc.getJavaType()))))
/*     */         {
/* 186 */           this.wantXSIType = Boolean.TRUE;
/*     */         }
/*     */       }
/*     */ 
/* 190 */       xmlType = this.paramDesc.getTypeQName();
/* 191 */       QName itemQName = this.paramDesc.getItemQName();
/* 192 */       if (itemQName == null) {
/* 193 */         MessageContext mc = context.getMessageContext();
/* 194 */         if ((mc != null) && (mc.getOperation() != null) && (mc.getOperation().getStyle() == Style.DOCUMENT)) {
/* 195 */           itemQName = Constants.QNAME_LITERAL_ITEM;
/*     */         }
/*     */       }
/* 198 */       context.setItemQName(itemQName);
/*     */ 
/* 200 */       QName itemType = this.paramDesc.getItemType();
/* 201 */       context.setItemType(itemType);
/*     */ 
/* 205 */       if ((this.paramDesc.isOmittable()) && (!this.paramDesc.isNillable()))
/* 206 */         sendNull = Boolean.FALSE;
/*     */     }
/* 208 */     context.serialize(getQName(), null, this.value, xmlType, sendNull, this.wantXSIType);
/*     */   }
/*     */ 
/*     */   private void writeObject(ObjectOutputStream out)
/*     */     throws IOException
/*     */   {
/* 217 */     if (getQName() == null) {
/* 218 */       out.writeBoolean(false);
/*     */     } else {
/* 220 */       out.writeBoolean(true);
/* 221 */       out.writeObject(getQName().getNamespaceURI());
/* 222 */       out.writeObject(getQName().getLocalPart());
/*     */     }
/* 224 */     out.defaultWriteObject();
/*     */   }
/*     */ 
/*     */   private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
/*     */   {
/* 229 */     if (in.readBoolean()) {
/* 230 */       setQName(new QName((String)in.readObject(), (String)in.readObject()));
/*     */     }
/*     */ 
/* 233 */     in.defaultReadObject();
/*     */   }
/*     */ 
/*     */   protected void outputImpl(SerializationContext context) throws Exception {
/* 237 */     serialize(context);
/*     */   }
/*     */ 
/*     */   public String getValue() {
/* 241 */     return getValueDOM();
/*     */   }
/*     */ 
/*     */   public SOAPElement addTextNode(String s)
/*     */     throws SOAPException
/*     */   {
/* 248 */     this.value = s;
/* 249 */     return super.addTextNode(s);
/*     */   }
/*     */ 
/*     */   public void setValue(String value)
/*     */   {
/* 255 */     this.value = value;
/* 256 */     super.setValue(value);
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  63 */     Class cls = RPCParam.class;
/*     */     try {
/*  65 */       valueSetMethod = cls.getMethod("set", new Class[] { Object.class });
/*     */     } catch (NoSuchMethodException e) {
/*  67 */       log.error(Messages.getMessage("noValue00", "" + e));
/*  68 */       throw new RuntimeException(e.getMessage());
/*     */     }
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.message.RPCParam
 * JD-Core Version:    0.6.0
 */