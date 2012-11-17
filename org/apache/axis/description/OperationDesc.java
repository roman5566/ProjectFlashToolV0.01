/*     */ package org.apache.axis.description;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.ObjectOutputStream;
/*     */ import java.io.Serializable;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.ListIterator;
/*     */ import java.util.Map;
/*     */ import javax.wsdl.OperationType;
/*     */ import javax.xml.namespace.QName;
/*     */ import org.apache.axis.components.logger.LogFactory;
/*     */ import org.apache.axis.constants.Style;
/*     */ import org.apache.axis.constants.Use;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public class OperationDesc
/*     */   implements Serializable
/*     */ {
/*     */   public static final int MSG_METHOD_BODYARRAY = 1;
/*     */   public static final int MSG_METHOD_SOAPENVELOPE = 2;
/*     */   public static final int MSG_METHOD_ELEMENTARRAY = 3;
/*     */   public static final int MSG_METHOD_DOCUMENT = 4;
/*     */   public static final int MSG_METHOD_NONCONFORMING = -4;
/*  56 */   public static Map mepStrings = new HashMap();
/*     */   protected static Log log;
/*     */   private ServiceDesc parent;
/*  72 */   private ArrayList parameters = new ArrayList();
/*     */   private String name;
/*     */   private QName elementQName;
/*     */   private transient Method method;
/*  84 */   private Style style = null;
/*  85 */   private Use use = null;
/*     */ 
/*  88 */   private int numInParams = 0;
/*     */ 
/*  90 */   private int numOutParams = 0;
/*     */ 
/*  93 */   private String soapAction = null;
/*     */ 
/*  96 */   private ArrayList faults = null;
/*     */ 
/*  98 */   private ParameterDesc returnDesc = new ParameterDesc();
/*     */ 
/* 101 */   private int messageOperationStyle = -1;
/*     */ 
/* 104 */   private String documentation = null;
/*     */ 
/* 109 */   private OperationType mep = OperationType.REQUEST_RESPONSE;
/*     */ 
/*     */   public OperationDesc()
/*     */   {
/* 115 */     this.returnDesc.setMode(2);
/* 116 */     this.returnDesc.setIsReturn(true);
/*     */   }
/*     */ 
/*     */   public OperationDesc(String name, ParameterDesc[] parameters, QName returnQName)
/*     */   {
/* 123 */     this.name = name;
/* 124 */     this.returnDesc.setQName(returnQName);
/* 125 */     this.returnDesc.setMode(2);
/* 126 */     this.returnDesc.setIsReturn(true);
/* 127 */     for (int i = 0; i < parameters.length; i++)
/* 128 */       addParameter(parameters[i]);
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/* 136 */     return this.name;
/*     */   }
/*     */ 
/*     */   public void setName(String name)
/*     */   {
/* 143 */     this.name = name;
/*     */   }
/*     */ 
/*     */   public String getDocumentation()
/*     */   {
/* 150 */     return this.documentation;
/*     */   }
/*     */ 
/*     */   public void setDocumentation(String documentation)
/*     */   {
/* 157 */     this.documentation = documentation;
/*     */   }
/*     */ 
/*     */   public QName getReturnQName() {
/* 161 */     return this.returnDesc.getQName();
/*     */   }
/*     */ 
/*     */   public void setReturnQName(QName returnQName) {
/* 165 */     this.returnDesc.setQName(returnQName);
/*     */   }
/*     */ 
/*     */   public QName getReturnType() {
/* 169 */     return this.returnDesc.getTypeQName();
/*     */   }
/*     */ 
/*     */   public void setReturnType(QName returnType) {
/* 173 */     log.debug("@" + Integer.toHexString(hashCode()) + "setReturnType(" + returnType + ")");
/* 174 */     this.returnDesc.setTypeQName(returnType);
/*     */   }
/*     */ 
/*     */   public Class getReturnClass() {
/* 178 */     return this.returnDesc.getJavaType();
/*     */   }
/*     */ 
/*     */   public void setReturnClass(Class returnClass) {
/* 182 */     this.returnDesc.setJavaType(returnClass);
/*     */   }
/*     */ 
/*     */   public QName getElementQName() {
/* 186 */     return this.elementQName;
/*     */   }
/*     */ 
/*     */   public void setElementQName(QName elementQName) {
/* 190 */     this.elementQName = elementQName;
/*     */   }
/*     */ 
/*     */   public ServiceDesc getParent() {
/* 194 */     return this.parent;
/*     */   }
/*     */ 
/*     */   public void setParent(ServiceDesc parent) {
/* 198 */     this.parent = parent;
/*     */   }
/*     */ 
/*     */   public String getSoapAction() {
/* 202 */     return this.soapAction;
/*     */   }
/*     */ 
/*     */   public void setSoapAction(String soapAction) {
/* 206 */     this.soapAction = soapAction;
/*     */   }
/*     */ 
/*     */   public void setStyle(Style style)
/*     */   {
/* 211 */     this.style = style;
/*     */   }
/*     */ 
/*     */   public Style getStyle()
/*     */   {
/* 220 */     if (this.style == null) {
/* 221 */       if (this.parent != null) {
/* 222 */         return this.parent.getStyle();
/*     */       }
/* 224 */       return Style.DEFAULT;
/*     */     }
/*     */ 
/* 227 */     return this.style;
/*     */   }
/*     */ 
/*     */   public void setUse(Use use)
/*     */   {
/* 232 */     this.use = use;
/*     */   }
/*     */ 
/*     */   public Use getUse()
/*     */   {
/* 241 */     if (this.use == null) {
/* 242 */       if (this.parent != null) {
/* 243 */         return this.parent.getUse();
/*     */       }
/* 245 */       return Use.DEFAULT;
/*     */     }
/*     */ 
/* 248 */     return this.use;
/*     */   }
/*     */ 
/*     */   public void addParameter(ParameterDesc param)
/*     */   {
/* 255 */     param.setOrder(getNumParams());
/* 256 */     this.parameters.add(param);
/* 257 */     if ((param.getMode() == 1) || (param.getMode() == 3))
/*     */     {
/* 259 */       this.numInParams += 1;
/*     */     }
/* 261 */     if ((param.getMode() == 2) || (param.getMode() == 3))
/*     */     {
/* 263 */       this.numOutParams += 1;
/*     */     }
/* 265 */     log.debug("@" + Integer.toHexString(hashCode()) + " added parameter >" + param + "@" + Integer.toHexString(param.hashCode()) + "<total parameters:" + getNumParams());
/*     */   }
/*     */ 
/*     */   public void addParameter(QName paramName, QName xmlType, Class javaType, byte parameterMode, boolean inHeader, boolean outHeader)
/*     */   {
/* 274 */     ParameterDesc param = new ParameterDesc(paramName, parameterMode, xmlType, javaType, inHeader, outHeader);
/*     */ 
/* 277 */     addParameter(param);
/*     */   }
/*     */ 
/*     */   public ParameterDesc getParameter(int i)
/*     */   {
/* 282 */     if (this.parameters.size() <= i) {
/* 283 */       return null;
/*     */     }
/* 285 */     return (ParameterDesc)this.parameters.get(i);
/*     */   }
/*     */ 
/*     */   public ArrayList getParameters() {
/* 289 */     return this.parameters;
/*     */   }
/*     */ 
/*     */   public void setParameters(ArrayList newParameters)
/*     */   {
/* 298 */     this.parameters = new ArrayList();
/* 299 */     this.numInParams = 0;
/* 300 */     this.numOutParams = 0;
/*     */ 
/* 302 */     ListIterator li = newParameters.listIterator();
/* 303 */     while (li.hasNext())
/* 304 */       addParameter((ParameterDesc)li.next());
/*     */   }
/*     */ 
/*     */   public int getNumInParams()
/*     */   {
/* 309 */     return this.numInParams;
/*     */   }
/*     */ 
/*     */   public int getNumOutParams() {
/* 313 */     return this.numOutParams;
/*     */   }
/*     */ 
/*     */   public int getNumParams() {
/* 317 */     return this.parameters.size();
/*     */   }
/*     */ 
/*     */   public Method getMethod() {
/* 321 */     return this.method;
/*     */   }
/*     */ 
/*     */   public void setMethod(Method method) {
/* 325 */     this.method = method;
/*     */   }
/*     */ 
/*     */   public boolean isReturnHeader()
/*     */   {
/* 332 */     return this.returnDesc.isOutHeader();
/*     */   }
/*     */ 
/*     */   public void setReturnHeader(boolean value)
/*     */   {
/* 339 */     this.returnDesc.setOutHeader(value);
/*     */   }
/*     */ 
/*     */   public ParameterDesc getParamByQName(QName qname)
/*     */   {
/* 344 */     for (Iterator i = this.parameters.iterator(); i.hasNext(); ) {
/* 345 */       ParameterDesc param = (ParameterDesc)i.next();
/* 346 */       if (param.getQName().equals(qname)) {
/* 347 */         return param;
/*     */       }
/*     */     }
/* 350 */     return null;
/*     */   }
/*     */ 
/*     */   public ParameterDesc getInputParamByQName(QName qname)
/*     */   {
/* 355 */     ParameterDesc param = null;
/*     */ 
/* 357 */     param = getParamByQName(qname);
/*     */ 
/* 359 */     if ((param == null) || (param.getMode() == 2)) {
/* 360 */       param = null;
/*     */     }
/*     */ 
/* 363 */     return param;
/*     */   }
/*     */ 
/*     */   public ParameterDesc getOutputParamByQName(QName qname)
/*     */   {
/* 368 */     ParameterDesc param = null;
/*     */ 
/* 370 */     for (Iterator i = this.parameters.iterator(); i.hasNext(); ) {
/* 371 */       ParameterDesc pnext = (ParameterDesc)i.next();
/* 372 */       if ((pnext.getQName().equals(qname)) && (pnext.getMode() != 1))
/*     */       {
/* 374 */         param = pnext;
/* 375 */         break;
/*     */       }
/*     */     }
/*     */ 
/* 379 */     if (param == null) {
/* 380 */       if (null == this.returnDesc.getQName()) {
/* 381 */         param = new ParameterDesc(this.returnDesc);
/* 382 */         param.setQName(qname);
/*     */       }
/* 384 */       else if (qname.equals(this.returnDesc.getQName())) {
/* 385 */         param = this.returnDesc;
/*     */       }
/*     */     }
/*     */ 
/* 389 */     return param;
/*     */   }
/*     */ 
/*     */   public ArrayList getAllInParams()
/*     */   {
/* 401 */     ArrayList result = new ArrayList();
/* 402 */     for (Iterator i = this.parameters.iterator(); i.hasNext(); ) {
/* 403 */       ParameterDesc desc = (ParameterDesc)i.next();
/* 404 */       if (desc.getMode() != 2) {
/* 405 */         result.add(desc);
/*     */       }
/*     */     }
/* 408 */     return result;
/*     */   }
/*     */ 
/*     */   public ArrayList getAllOutParams()
/*     */   {
/* 420 */     ArrayList result = new ArrayList();
/* 421 */     for (Iterator i = this.parameters.iterator(); i.hasNext(); ) {
/* 422 */       ParameterDesc desc = (ParameterDesc)i.next();
/* 423 */       if (desc.getMode() != 1) {
/* 424 */         result.add(desc);
/*     */       }
/*     */     }
/* 427 */     return result;
/*     */   }
/*     */ 
/*     */   public ArrayList getOutParams()
/*     */   {
/* 433 */     ArrayList result = new ArrayList();
/* 434 */     for (Iterator i = this.parameters.iterator(); i.hasNext(); ) {
/* 435 */       ParameterDesc desc = (ParameterDesc)i.next();
/* 436 */       if (desc.getMode() == 2) {
/* 437 */         result.add(desc);
/*     */       }
/*     */     }
/* 440 */     return result;
/*     */   }
/*     */ 
/*     */   public void addFault(FaultDesc fault)
/*     */   {
/* 445 */     if (this.faults == null)
/* 446 */       this.faults = new ArrayList();
/* 447 */     this.faults.add(fault);
/*     */   }
/*     */ 
/*     */   public ArrayList getFaults()
/*     */   {
/* 452 */     return this.faults;
/*     */   }
/*     */ 
/*     */   public FaultDesc getFaultByClass(Class cls)
/*     */   {
/* 460 */     if ((this.faults == null) || (cls == null)) {
/* 461 */       return null;
/*     */     }
/*     */ 
/* 464 */     while (cls != null)
/*     */     {
/* 468 */       for (Iterator iterator = this.faults.iterator(); iterator.hasNext(); ) {
/* 469 */         FaultDesc desc = (FaultDesc)iterator.next();
/* 470 */         if (cls.getName().equals(desc.getClassName())) {
/* 471 */           return desc;
/*     */         }
/*     */       }
/*     */ 
/* 475 */       cls = cls.getSuperclass();
/* 476 */       if ((cls == null) || ((!cls.getName().startsWith("java.")) && (!cls.getName().startsWith("javax."))))
/*     */         continue;
/* 478 */       cls = null;
/*     */     }
/*     */ 
/* 482 */     return null;
/*     */   }
/*     */ 
/*     */   public FaultDesc getFaultByClass(Class cls, boolean checkParents)
/*     */   {
/* 490 */     if (checkParents) {
/* 491 */       return getFaultByClass(cls);
/*     */     }
/*     */ 
/* 494 */     if ((this.faults == null) || (cls == null)) {
/* 495 */       return null;
/*     */     }
/*     */ 
/* 498 */     for (Iterator iterator = this.faults.iterator(); iterator.hasNext(); ) {
/* 499 */       FaultDesc desc = (FaultDesc)iterator.next();
/* 500 */       if (cls.getName().equals(desc.getClassName())) {
/* 501 */         return desc;
/*     */       }
/*     */     }
/*     */ 
/* 505 */     return null;
/*     */   }
/*     */ 
/*     */   public FaultDesc getFaultByQName(QName qname)
/*     */   {
/*     */     Iterator iterator;
/* 514 */     if (this.faults != null) {
/* 515 */       for (iterator = this.faults.iterator(); iterator.hasNext(); ) {
/* 516 */         FaultDesc desc = (FaultDesc)iterator.next();
/* 517 */         if (qname.equals(desc.getQName())) {
/* 518 */           return desc;
/*     */         }
/*     */       }
/*     */     }
/* 522 */     return null;
/*     */   }
/*     */ 
/*     */   public FaultDesc getFaultByXmlType(QName xmlType)
/*     */   {
/*     */     Iterator iterator;
/* 529 */     if (this.faults != null) {
/* 530 */       for (iterator = this.faults.iterator(); iterator.hasNext(); ) {
/* 531 */         FaultDesc desc = (FaultDesc)iterator.next();
/* 532 */         if (xmlType.equals(desc.getXmlType())) {
/* 533 */           return desc;
/*     */         }
/*     */       }
/*     */     }
/* 537 */     return null;
/*     */   }
/*     */   public ParameterDesc getReturnParamDesc() {
/* 540 */     return this.returnDesc;
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 544 */     return toString("");
/*     */   }
/*     */   public String toString(String indent) {
/* 547 */     String text = "";
/* 548 */     text = text + indent + "name:        " + getName() + "\n";
/* 549 */     text = text + indent + "returnQName: " + getReturnQName() + "\n";
/* 550 */     text = text + indent + "returnType:  " + getReturnType() + "\n";
/* 551 */     text = text + indent + "returnClass: " + getReturnClass() + "\n";
/* 552 */     text = text + indent + "elementQName:" + getElementQName() + "\n";
/* 553 */     text = text + indent + "soapAction:  " + getSoapAction() + "\n";
/* 554 */     text = text + indent + "style:       " + getStyle().getName() + "\n";
/* 555 */     text = text + indent + "use:         " + getUse().getName() + "\n";
/* 556 */     text = text + indent + "numInParams: " + getNumInParams() + "\n";
/* 557 */     text = text + indent + "method:" + getMethod() + "\n";
/* 558 */     for (int i = 0; i < this.parameters.size(); i++) {
/* 559 */       text = text + indent + " ParameterDesc[" + i + "]:\n";
/* 560 */       text = text + indent + ((ParameterDesc)this.parameters.get(i)).toString("  ") + "\n";
/*     */     }
/* 562 */     if (this.faults != null) {
/* 563 */       for (int i = 0; i < this.faults.size(); i++) {
/* 564 */         text = text + indent + " FaultDesc[" + i + "]:\n";
/* 565 */         text = text + indent + ((FaultDesc)this.faults.get(i)).toString("  ") + "\n";
/*     */       }
/*     */     }
/* 568 */     return text;
/*     */   }
/*     */ 
/*     */   public int getMessageOperationStyle() {
/* 572 */     return this.messageOperationStyle;
/*     */   }
/*     */ 
/*     */   public void setMessageOperationStyle(int messageOperationStyle) {
/* 576 */     this.messageOperationStyle = messageOperationStyle;
/*     */   }
/*     */ 
/*     */   public OperationType getMep() {
/* 580 */     return this.mep;
/*     */   }
/*     */ 
/*     */   public void setMep(OperationType mep) {
/* 584 */     this.mep = mep;
/*     */   }
/*     */ 
/*     */   public void setMep(String mepString)
/*     */   {
/* 592 */     OperationType newMep = (OperationType)mepStrings.get(mepString);
/* 593 */     if (newMep != null)
/* 594 */       this.mep = newMep;
/*     */   }
/*     */ 
/*     */   private void writeObject(ObjectOutputStream out) throws IOException
/*     */   {
/* 599 */     out.defaultWriteObject();
/* 600 */     if (this.method != null) {
/* 601 */       out.writeObject(this.method.getDeclaringClass());
/* 602 */       out.writeObject(this.method.getName());
/* 603 */       out.writeObject(this.method.getParameterTypes());
/*     */     } else {
/* 605 */       out.writeObject(null);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
/* 610 */     in.defaultReadObject();
/* 611 */     Class clazz = (Class)in.readObject();
/* 612 */     if (clazz != null) {
/* 613 */       String methodName = (String)in.readObject();
/* 614 */       Class[] parameterTypes = (Class[])in.readObject();
/*     */       try {
/* 616 */         this.method = clazz.getMethod(methodName, parameterTypes);
/*     */       } catch (NoSuchMethodException e) {
/* 618 */         throw new IOException("Unable to deserialize the operation's method: " + methodName);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  59 */     mepStrings.put("request-response", OperationType.REQUEST_RESPONSE);
/*  60 */     mepStrings.put("oneway", OperationType.ONE_WAY);
/*  61 */     mepStrings.put("solicit-response", OperationType.SOLICIT_RESPONSE);
/*  62 */     mepStrings.put("notification", OperationType.NOTIFICATION);
/*     */ 
/*  65 */     log = LogFactory.getLog(OperationDesc.class.getName());
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.description.OperationDesc
 * JD-Core Version:    0.6.0
 */