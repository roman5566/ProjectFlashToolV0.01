/*     */ package org.apache.axis.wsdl.symbolTable;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import javax.wsdl.Binding;
/*     */ import javax.wsdl.Operation;
/*     */ import javax.wsdl.extensions.soap.SOAPFault;
/*     */ import org.apache.axis.constants.Style;
/*     */ import org.apache.axis.constants.Use;
/*     */ 
/*     */ public class BindingEntry extends SymTabEntry
/*     */ {
/*     */   public static final int TYPE_SOAP = 0;
/*     */   public static final int TYPE_HTTP_GET = 1;
/*     */   public static final int TYPE_HTTP_POST = 2;
/*     */   public static final int TYPE_UNKNOWN = 3;
/*     */   public static final int USE_ENCODED = 0;
/*     */   public static final int USE_LITERAL = 1;
/*     */   private Binding binding;
/*     */   private int bindingType;
/*     */   private Style bindingStyle;
/*     */   private boolean hasLiteral;
/*     */   private HashMap attributes;
/*  76 */   private HashMap parameters = new HashMap();
/*     */ 
/*  81 */   private HashMap faults = new HashMap();
/*     */   private Map mimeTypes;
/*     */   private Map headerParts;
/*  99 */   private ArrayList dimeOps = new ArrayList();
/*     */   public static final int NO_HEADER = 0;
/*     */   public static final int IN_HEADER = 1;
/*     */   public static final int OUT_HEADER = 2;
/*     */ 
/*     */   public BindingEntry(Binding binding, int bindingType, Style bindingStyle, boolean hasLiteral, HashMap attributes, Map mimeTypes, Map headerParts)
/*     */   {
/* 118 */     super(binding.getQName());
/*     */ 
/* 120 */     this.binding = binding;
/* 121 */     this.bindingType = bindingType;
/* 122 */     this.bindingStyle = bindingStyle;
/* 123 */     this.hasLiteral = hasLiteral;
/*     */ 
/* 125 */     if (attributes == null)
/* 126 */       this.attributes = new HashMap();
/*     */     else {
/* 128 */       this.attributes = attributes;
/*     */     }
/*     */ 
/* 131 */     if (mimeTypes == null)
/* 132 */       this.mimeTypes = new HashMap();
/*     */     else {
/* 134 */       this.mimeTypes = mimeTypes;
/*     */     }
/*     */ 
/* 137 */     if (headerParts == null)
/* 138 */       this.headerParts = new HashMap();
/*     */     else
/* 140 */       this.headerParts = headerParts;
/*     */   }
/*     */ 
/*     */   public BindingEntry(Binding binding)
/*     */   {
/* 165 */     super(binding.getQName());
/*     */ 
/* 167 */     this.binding = binding;
/* 168 */     this.bindingType = 3;
/* 169 */     this.bindingStyle = Style.DOCUMENT;
/* 170 */     this.hasLiteral = false;
/* 171 */     this.attributes = new HashMap();
/* 172 */     this.mimeTypes = new HashMap();
/* 173 */     this.headerParts = new HashMap();
/*     */   }
/*     */ 
/*     */   public Parameters getParameters(Operation operation)
/*     */   {
/* 183 */     return (Parameters)this.parameters.get(operation);
/*     */   }
/*     */ 
/*     */   public HashMap getParameters()
/*     */   {
/* 192 */     return this.parameters;
/*     */   }
/*     */ 
/*     */   public void setParameters(HashMap parameters)
/*     */   {
/* 201 */     this.parameters = parameters;
/*     */   }
/*     */ 
/*     */   public MimeInfo getMIMEInfo(String operationName, String parameterName)
/*     */   {
/* 214 */     Map opMap = (Map)this.mimeTypes.get(operationName);
/*     */ 
/* 216 */     if (opMap == null) {
/* 217 */       return null;
/*     */     }
/* 219 */     return (MimeInfo)opMap.get(parameterName);
/*     */   }
/*     */ 
/*     */   public Map getMIMETypes()
/*     */   {
/* 229 */     return this.mimeTypes;
/*     */   }
/*     */ 
/*     */   public void setMIMEInfo(String operationName, String parameterName, String type, String dims)
/*     */   {
/* 243 */     Map opMap = (Map)this.mimeTypes.get(operationName);
/*     */ 
/* 245 */     if (opMap == null) {
/* 246 */       opMap = new HashMap();
/*     */ 
/* 248 */       this.mimeTypes.put(operationName, opMap);
/*     */     }
/*     */ 
/* 251 */     opMap.put(parameterName, new MimeInfo(type, dims));
/*     */   }
/*     */ 
/*     */   public void setOperationDIME(String operationName)
/*     */   {
/* 261 */     if (this.dimeOps.indexOf(operationName) == -1)
/* 262 */       this.dimeOps.add(operationName);
/*     */   }
/*     */ 
/*     */   public boolean isOperationDIME(String operationName)
/*     */   {
/* 273 */     return this.dimeOps.indexOf(operationName) >= 0;
/*     */   }
/*     */ 
/*     */   public boolean isInHeaderPart(String operationName, String partName)
/*     */   {
/* 284 */     return (headerPart(operationName, partName) & 0x1) > 0;
/*     */   }
/*     */ 
/*     */   public boolean isOutHeaderPart(String operationName, String partName)
/*     */   {
/* 295 */     return (headerPart(operationName, partName) & 0x2) > 0;
/*     */   }
/*     */ 
/*     */   private int headerPart(String operationName, String partName)
/*     */   {
/* 317 */     Map opMap = (Map)this.headerParts.get(operationName);
/*     */ 
/* 319 */     if (opMap == null) {
/* 320 */       return 0;
/*     */     }
/* 322 */     Integer I = (Integer)opMap.get(partName);
/*     */ 
/* 324 */     return I == null ? 0 : I.intValue();
/*     */   }
/*     */ 
/*     */   public Map getHeaderParts()
/*     */   {
/* 336 */     return this.headerParts;
/*     */   }
/*     */ 
/*     */   public void setHeaderPart(String operationName, String partName, int headerFlags)
/*     */   {
/* 349 */     Map opMap = (Map)this.headerParts.get(operationName);
/*     */ 
/* 351 */     if (opMap == null) {
/* 352 */       opMap = new HashMap();
/*     */ 
/* 354 */       this.headerParts.put(operationName, opMap);
/*     */     }
/*     */ 
/* 357 */     Integer I = (Integer)opMap.get(partName);
/* 358 */     int i = I == null ? headerFlags : I.intValue() | headerFlags;
/*     */ 
/* 362 */     opMap.put(partName, new Integer(i));
/*     */   }
/*     */ 
/*     */   public Binding getBinding()
/*     */   {
/* 371 */     return this.binding;
/*     */   }
/*     */ 
/*     */   public int getBindingType()
/*     */   {
/* 381 */     return this.bindingType;
/*     */   }
/*     */ 
/*     */   protected void setBindingType(int bindingType)
/*     */   {
/* 391 */     if ((bindingType >= 0) && (bindingType <= 3));
/* 394 */     this.bindingType = bindingType;
/*     */   }
/*     */ 
/*     */   public Style getBindingStyle()
/*     */   {
/* 403 */     return this.bindingStyle;
/*     */   }
/*     */ 
/*     */   protected void setBindingStyle(Style bindingStyle)
/*     */   {
/* 412 */     this.bindingStyle = bindingStyle;
/*     */   }
/*     */ 
/*     */   public boolean hasLiteral()
/*     */   {
/* 421 */     return this.hasLiteral;
/*     */   }
/*     */ 
/*     */   protected void setHasLiteral(boolean hasLiteral)
/*     */   {
/* 430 */     this.hasLiteral = hasLiteral;
/*     */   }
/*     */ 
/*     */   public Use getInputBodyType(Operation operation)
/*     */   {
/* 441 */     OperationAttr attr = (OperationAttr)this.attributes.get(operation);
/*     */ 
/* 443 */     if (attr == null) {
/* 444 */       return Use.ENCODED;
/*     */     }
/* 446 */     return attr.getInputBodyType();
/*     */   }
/*     */ 
/*     */   protected void setInputBodyType(Operation operation, Use inputBodyType)
/*     */   {
/* 458 */     OperationAttr attr = (OperationAttr)this.attributes.get(operation);
/*     */ 
/* 460 */     if (attr == null) {
/* 461 */       attr = new OperationAttr();
/*     */ 
/* 463 */       this.attributes.put(operation, attr);
/*     */     }
/*     */ 
/* 466 */     attr.setInputBodyType(inputBodyType);
/*     */ 
/* 468 */     if (inputBodyType == Use.LITERAL)
/* 469 */       setHasLiteral(true);
/*     */   }
/*     */ 
/*     */   public Use getOutputBodyType(Operation operation)
/*     */   {
/* 481 */     OperationAttr attr = (OperationAttr)this.attributes.get(operation);
/*     */ 
/* 483 */     if (attr == null) {
/* 484 */       return Use.ENCODED;
/*     */     }
/* 486 */     return attr.getOutputBodyType();
/*     */   }
/*     */ 
/*     */   protected void setOutputBodyType(Operation operation, Use outputBodyType)
/*     */   {
/* 498 */     OperationAttr attr = (OperationAttr)this.attributes.get(operation);
/*     */ 
/* 500 */     if (attr == null) {
/* 501 */       attr = new OperationAttr();
/*     */ 
/* 503 */       this.attributes.put(operation, attr);
/*     */     }
/*     */ 
/* 506 */     attr.setOutputBodyType(outputBodyType);
/*     */ 
/* 508 */     if (outputBodyType == Use.LITERAL)
/* 509 */       setHasLiteral(true);
/*     */   }
/*     */ 
/*     */   protected void setBodyType(Operation operation, Use bodyType, boolean input)
/*     */   {
/* 525 */     if (input)
/* 526 */       setInputBodyType(operation, bodyType);
/*     */     else
/* 528 */       setOutputBodyType(operation, bodyType);
/*     */   }
/*     */ 
/*     */   public Use getFaultBodyType(Operation operation, String faultName)
/*     */   {
/* 541 */     OperationAttr attr = (OperationAttr)this.attributes.get(operation);
/*     */ 
/* 543 */     if (attr == null) {
/* 544 */       return Use.ENCODED;
/*     */     }
/* 546 */     HashMap m = attr.getFaultBodyTypeMap();
/* 547 */     SOAPFault soapFault = (SOAPFault)m.get(faultName);
/*     */ 
/* 550 */     if (soapFault == null) {
/* 551 */       return Use.ENCODED;
/*     */     }
/*     */ 
/* 554 */     String use = soapFault.getUse();
/*     */ 
/* 556 */     if ("literal".equals(use)) {
/* 557 */       return Use.LITERAL;
/*     */     }
/*     */ 
/* 560 */     return Use.ENCODED;
/*     */   }
/*     */ 
/*     */   public HashMap getFaults()
/*     */   {
/* 570 */     return this.faults;
/*     */   }
/*     */ 
/*     */   public void setFaults(HashMap faults)
/*     */   {
/* 579 */     this.faults = faults;
/*     */   }
/*     */ 
/*     */   public Set getOperations()
/*     */   {
/* 588 */     return this.attributes.keySet();
/*     */   }
/*     */ 
/*     */   protected void setFaultBodyTypeMap(Operation operation, HashMap faultBodyTypeMap)
/*     */   {
/* 600 */     OperationAttr attr = (OperationAttr)this.attributes.get(operation);
/*     */ 
/* 602 */     if (attr == null) {
/* 603 */       attr = new OperationAttr();
/*     */ 
/* 605 */       this.attributes.put(operation, attr);
/*     */     }
/*     */ 
/* 608 */     attr.setFaultBodyTypeMap(faultBodyTypeMap);
/*     */   }
/*     */ 
/*     */   protected static class OperationAttr
/*     */   {
/*     */     private Use inputBodyType;
/*     */     private Use outputBodyType;
/*     */     private HashMap faultBodyTypeMap;
/*     */ 
/*     */     public OperationAttr(Use inputBodyType, Use outputBodyType, HashMap faultBodyTypeMap)
/*     */     {
/* 636 */       this.inputBodyType = inputBodyType;
/* 637 */       this.outputBodyType = outputBodyType;
/* 638 */       this.faultBodyTypeMap = faultBodyTypeMap;
/*     */     }
/*     */ 
/*     */     public OperationAttr()
/*     */     {
/* 646 */       this.inputBodyType = Use.ENCODED;
/* 647 */       this.outputBodyType = Use.ENCODED;
/* 648 */       this.faultBodyTypeMap = null;
/*     */     }
/*     */ 
/*     */     public Use getInputBodyType()
/*     */     {
/* 657 */       return this.inputBodyType;
/*     */     }
/*     */ 
/*     */     protected void setInputBodyType(Use inputBodyType)
/*     */     {
/* 666 */       this.inputBodyType = inputBodyType;
/*     */     }
/*     */ 
/*     */     public Use getOutputBodyType()
/*     */     {
/* 675 */       return this.outputBodyType;
/*     */     }
/*     */ 
/*     */     protected void setOutputBodyType(Use outputBodyType)
/*     */     {
/* 684 */       this.outputBodyType = outputBodyType;
/*     */     }
/*     */ 
/*     */     public HashMap getFaultBodyTypeMap()
/*     */     {
/* 693 */       return this.faultBodyTypeMap;
/*     */     }
/*     */ 
/*     */     protected void setFaultBodyTypeMap(HashMap faultBodyTypeMap)
/*     */     {
/* 702 */       this.faultBodyTypeMap = faultBodyTypeMap;
/*     */     }
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.wsdl.symbolTable.BindingEntry
 * JD-Core Version:    0.6.0
 */