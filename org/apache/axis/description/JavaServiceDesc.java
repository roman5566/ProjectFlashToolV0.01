/*      */ package org.apache.axis.description;
/*      */ 
/*      */ import java.lang.reflect.InvocationTargetException;
/*      */ import java.lang.reflect.Method;
/*      */ import java.lang.reflect.Modifier;
/*      */ import java.rmi.RemoteException;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collection;
/*      */ import java.util.Collections;
/*      */ import java.util.Comparator;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.StringTokenizer;
/*      */ import javax.xml.namespace.QName;
/*      */ import javax.xml.rpc.holders.Holder;
/*      */ import javax.xml.rpc.server.ServiceLifecycle;
/*      */ import org.apache.axis.AxisFault;
/*      */ import org.apache.axis.AxisProperties;
/*      */ import org.apache.axis.AxisServiceConfig;
/*      */ import org.apache.axis.Constants;
/*      */ import org.apache.axis.InternalException;
/*      */ import org.apache.axis.components.logger.LogFactory;
/*      */ import org.apache.axis.constants.Style;
/*      */ import org.apache.axis.constants.Use;
/*      */ import org.apache.axis.encoding.DefaultTypeMappingImpl;
/*      */ import org.apache.axis.encoding.TypeMapping;
/*      */ import org.apache.axis.encoding.TypeMappingRegistry;
/*      */ import org.apache.axis.encoding.TypeMappingRegistryImpl;
/*      */ import org.apache.axis.message.SOAPBodyElement;
/*      */ import org.apache.axis.utils.JavaUtils;
/*      */ import org.apache.axis.utils.Messages;
/*      */ import org.apache.axis.utils.bytecode.ParamNameExtractor;
/*      */ import org.apache.axis.wsdl.Skeleton;
/*      */ import org.apache.axis.wsdl.fromJava.Namespaces;
/*      */ import org.apache.commons.logging.Log;
/*      */ import org.w3c.dom.Document;
/*      */ import org.w3c.dom.Element;
/*      */ 
/*      */ public class JavaServiceDesc
/*      */   implements ServiceDesc
/*      */ {
/*   62 */   protected static Log log = LogFactory.getLog(JavaServiceDesc.class.getName());
/*      */ 
/*   66 */   private String name = null;
/*      */ 
/*   69 */   private String documentation = null;
/*      */ 
/*   72 */   private Style style = Style.RPC;
/*   73 */   private Use use = Use.ENCODED;
/*      */ 
/*   78 */   private boolean useSet = false;
/*      */ 
/*   81 */   private ArrayList operations = new ArrayList();
/*      */ 
/*   84 */   private List namespaceMappings = null;
/*      */ 
/*   92 */   private String wsdlFileName = null;
/*      */ 
/*   99 */   private String endpointURL = null;
/*      */ 
/*  102 */   private HashMap properties = null;
/*      */ 
/*  105 */   private HashMap name2OperationsMap = null;
/*  106 */   private HashMap qname2OperationsMap = null;
/*  107 */   private transient HashMap method2OperationMap = new HashMap();
/*      */ 
/*  114 */   private List allowedMethods = null;
/*      */ 
/*  117 */   private List disallowedMethods = null;
/*      */ 
/*  120 */   private Class implClass = null;
/*      */ 
/*  126 */   private boolean isSkeletonClass = false;
/*      */ 
/*  129 */   private transient Method skelMethod = null;
/*      */ 
/*  134 */   private ArrayList stopClasses = null;
/*      */ 
/*  137 */   private transient HashMap method2ParamsMap = new HashMap();
/*  138 */   private OperationDesc messageServiceDefaultOp = null;
/*      */ 
/*  141 */   private ArrayList completedNames = new ArrayList();
/*      */ 
/*  144 */   private TypeMapping tm = null;
/*  145 */   private TypeMappingRegistry tmr = null;
/*      */ 
/*  147 */   private boolean haveAllSkeletonMethods = false;
/*  148 */   private boolean introspectionComplete = false;
/*      */ 
/*      */   public Style getStyle()
/*      */   {
/*  161 */     return this.style;
/*      */   }
/*      */ 
/*      */   public void setStyle(Style style) {
/*  165 */     this.style = style;
/*  166 */     if (!this.useSet)
/*      */     {
/*  168 */       this.use = (style == Style.RPC ? Use.ENCODED : Use.LITERAL);
/*      */     }
/*      */   }
/*      */ 
/*      */   public Use getUse()
/*      */   {
/*  178 */     return this.use;
/*      */   }
/*      */ 
/*      */   public void setUse(Use use) {
/*  182 */     this.useSet = true;
/*  183 */     this.use = use;
/*      */   }
/*      */ 
/*      */   public boolean isWrapped()
/*      */   {
/*  197 */     return (this.style == Style.RPC) || (this.style == Style.WRAPPED);
/*      */   }
/*      */ 
/*      */   public String getWSDLFile()
/*      */   {
/*  207 */     return this.wsdlFileName;
/*      */   }
/*      */ 
/*      */   public void setWSDLFile(String wsdlFileName)
/*      */   {
/*  217 */     this.wsdlFileName = wsdlFileName;
/*      */   }
/*      */ 
/*      */   public List getAllowedMethods() {
/*  221 */     return this.allowedMethods;
/*      */   }
/*      */ 
/*      */   public void setAllowedMethods(List allowedMethods) {
/*  225 */     this.allowedMethods = allowedMethods;
/*      */   }
/*      */ 
/*      */   public Class getImplClass() {
/*  229 */     return this.implClass;
/*      */   }
/*      */ 
/*      */   public void setImplClass(Class implClass)
/*      */   {
/*  243 */     if (this.implClass != null) {
/*  244 */       throw new IllegalArgumentException(Messages.getMessage("implAlreadySet"));
/*      */     }
/*      */ 
/*  247 */     this.implClass = implClass;
/*  248 */     if (Skeleton.class.isAssignableFrom(implClass)) {
/*  249 */       this.isSkeletonClass = true;
/*  250 */       loadSkeletonOperations();
/*      */     }
/*      */   }
/*      */ 
/*      */   private void loadSkeletonOperations() {
/*  255 */     Method method = null;
/*      */     try {
/*  257 */       method = this.implClass.getDeclaredMethod("getOperationDescs", new Class[0]);
/*      */     } catch (NoSuchMethodException e) {
/*      */     }
/*      */     catch (SecurityException e) {
/*      */     }
/*  262 */     if (method == null)
/*      */     {
/*  264 */       return;
/*      */     }
/*      */     try
/*      */     {
/*  268 */       Collection opers = (Collection)method.invoke(this.implClass, null);
/*  269 */       for (i = opers.iterator(); i.hasNext(); ) {
/*  270 */         OperationDesc skelDesc = (OperationDesc)i.next();
/*  271 */         addOperationDesc(skelDesc);
/*      */       }
/*      */     }
/*      */     catch (IllegalAccessException e)
/*      */     {
/*      */       Iterator i;
/*  274 */       if (log.isDebugEnabled()) {
/*  275 */         log.debug(Messages.getMessage("exception00"), e);
/*      */       }
/*  277 */       return;
/*      */     } catch (IllegalArgumentException e) {
/*  279 */       if (log.isDebugEnabled()) {
/*  280 */         log.debug(Messages.getMessage("exception00"), e);
/*      */       }
/*  282 */       return;
/*      */     } catch (InvocationTargetException e) {
/*  284 */       if (log.isDebugEnabled()) {
/*  285 */         log.debug(Messages.getMessage("exception00"), e);
/*      */       }
/*  287 */       return;
/*      */     }
/*  289 */     this.haveAllSkeletonMethods = true;
/*      */   }
/*      */ 
/*      */   public TypeMapping getTypeMapping() {
/*  293 */     if (this.tm == null) {
/*  294 */       return DefaultTypeMappingImpl.getSingletonDelegate();
/*      */     }
/*      */ 
/*  297 */     return this.tm;
/*      */   }
/*      */ 
/*      */   public void setTypeMapping(TypeMapping tm) {
/*  301 */     this.tm = tm;
/*      */   }
/*      */ 
/*      */   public String getName()
/*      */   {
/*  308 */     return this.name;
/*      */   }
/*      */ 
/*      */   public void setName(String name)
/*      */   {
/*  316 */     this.name = name;
/*      */   }
/*      */ 
/*      */   public String getDocumentation()
/*      */   {
/*  323 */     return this.documentation;
/*      */   }
/*      */ 
/*      */   public void setDocumentation(String documentation)
/*      */   {
/*  330 */     this.documentation = documentation;
/*      */   }
/*      */ 
/*      */   public ArrayList getStopClasses() {
/*  334 */     return this.stopClasses;
/*      */   }
/*      */ 
/*      */   public void setStopClasses(ArrayList stopClasses) {
/*  338 */     this.stopClasses = stopClasses;
/*      */   }
/*      */ 
/*      */   public List getDisallowedMethods() {
/*  342 */     return this.disallowedMethods;
/*      */   }
/*      */ 
/*      */   public void setDisallowedMethods(List disallowedMethods) {
/*  346 */     this.disallowedMethods = disallowedMethods;
/*      */   }
/*      */ 
/*      */   public void removeOperationDesc(OperationDesc operation) {
/*  350 */     this.operations.remove(operation);
/*  351 */     operation.setParent(null);
/*      */ 
/*  353 */     if (this.name2OperationsMap != null) {
/*  354 */       String name = operation.getName();
/*  355 */       ArrayList overloads = (ArrayList)this.name2OperationsMap.get(name);
/*  356 */       if (overloads != null) {
/*  357 */         overloads.remove(operation);
/*  358 */         if (overloads.size() == 0) {
/*  359 */           this.name2OperationsMap.remove(name);
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*  364 */     if (this.qname2OperationsMap != null) {
/*  365 */       QName qname = operation.getElementQName();
/*  366 */       ArrayList list = (ArrayList)this.qname2OperationsMap.get(qname);
/*  367 */       if (list != null) {
/*  368 */         list.remove(operation);
/*      */       }
/*      */     }
/*      */ 
/*  372 */     if (this.method2OperationMap != null) {
/*  373 */       Method method = operation.getMethod();
/*  374 */       if (method != null)
/*  375 */         this.method2OperationMap.remove(method);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void addOperationDesc(OperationDesc operation)
/*      */   {
/*  382 */     this.operations.add(operation);
/*  383 */     operation.setParent(this);
/*  384 */     if (this.name2OperationsMap == null) {
/*  385 */       this.name2OperationsMap = new HashMap();
/*      */     }
/*      */ 
/*  389 */     String name = operation.getName();
/*  390 */     ArrayList overloads = (ArrayList)this.name2OperationsMap.get(name);
/*  391 */     if (overloads == null) {
/*  392 */       overloads = new ArrayList();
/*  393 */       this.name2OperationsMap.put(name, overloads);
/*  394 */     } else if ((JavaUtils.isTrue(AxisProperties.getProperty("axis.ws-i.bp11.compatibility"))) && (overloads.size() > 0))
/*      */     {
/*  397 */       throw new RuntimeException(Messages.getMessage("noOverloadedOperations", name));
/*      */     }
/*  399 */     overloads.add(operation);
/*      */   }
/*      */ 
/*      */   public ArrayList getOperations()
/*      */   {
/*  410 */     loadServiceDescByIntrospection();
/*  411 */     return this.operations;
/*      */   }
/*      */ 
/*      */   public OperationDesc[] getOperationsByName(String methodName)
/*      */   {
/*  421 */     getSyncedOperationsForName(this.implClass, methodName);
/*      */ 
/*  423 */     if (this.name2OperationsMap == null) {
/*  424 */       return null;
/*      */     }
/*  426 */     ArrayList overloads = (ArrayList)this.name2OperationsMap.get(methodName);
/*  427 */     if (overloads == null) {
/*  428 */       return null;
/*      */     }
/*      */ 
/*  431 */     OperationDesc[] array = new OperationDesc[overloads.size()];
/*  432 */     return (OperationDesc[])overloads.toArray(array);
/*      */   }
/*      */ 
/*      */   public OperationDesc getOperationByName(String methodName)
/*      */   {
/*  445 */     getSyncedOperationsForName(this.implClass, methodName);
/*      */ 
/*  447 */     if (this.name2OperationsMap == null) {
/*  448 */       return null;
/*      */     }
/*  450 */     ArrayList overloads = (ArrayList)this.name2OperationsMap.get(methodName);
/*  451 */     if (overloads == null) {
/*  452 */       return null;
/*      */     }
/*      */ 
/*  455 */     return (OperationDesc)overloads.get(0);
/*      */   }
/*      */ 
/*      */   public OperationDesc getOperationByElementQName(QName qname)
/*      */   {
/*  465 */     OperationDesc[] overloads = getOperationsByQName(qname);
/*      */ 
/*  468 */     if ((overloads != null) && (overloads.length > 0)) {
/*  469 */       return overloads[0];
/*      */     }
/*  471 */     return null;
/*      */   }
/*      */ 
/*      */   public OperationDesc[] getOperationsByQName(QName qname)
/*      */   {
/*  484 */     initQNameMap();
/*      */ 
/*  486 */     ArrayList overloads = (ArrayList)this.qname2OperationsMap.get(qname);
/*  487 */     if (overloads == null)
/*      */     {
/*      */       Iterator iter;
/*  489 */       if (this.name2OperationsMap != null) {
/*  490 */         if ((isWrapped()) || ((this.style == Style.MESSAGE) && (getDefaultNamespace() == null)))
/*      */         {
/*  494 */           overloads = (ArrayList)this.name2OperationsMap.get(qname.getLocalPart());
/*      */         }
/*      */         else
/*      */         {
/*  499 */           Object ops = this.name2OperationsMap.get(qname.getLocalPart());
/*  500 */           if (ops != null) {
/*  501 */             overloads = new ArrayList((Collection)ops);
/*  502 */             for (iter = overloads.iterator(); iter.hasNext(); ) {
/*  503 */               OperationDesc operationDesc = (OperationDesc)iter.next();
/*  504 */               if (Style.WRAPPED != operationDesc.getStyle()) {
/*  505 */                 iter.remove();
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  513 */       if ((this.style == Style.MESSAGE) && (this.messageServiceDefaultOp != null)) {
/*  514 */         return new OperationDesc[] { this.messageServiceDefaultOp };
/*      */       }
/*  516 */       if (overloads == null) {
/*  517 */         return null;
/*      */       }
/*      */     }
/*  520 */     getSyncedOperationsForName(this.implClass, ((OperationDesc)overloads.get(0)).getName());
/*      */ 
/*  527 */     Collections.sort(overloads, new Comparator()
/*      */     {
/*      */       public int compare(Object o1, Object o2)
/*      */       {
/*  531 */         Method meth1 = ((OperationDesc)o1).getMethod();
/*  532 */         Method meth2 = ((OperationDesc)o2).getMethod();
/*  533 */         return meth1.getParameterTypes().length - meth2.getParameterTypes().length;
/*      */       }
/*      */     });
/*  538 */     OperationDesc[] array = new OperationDesc[overloads.size()];
/*  539 */     return (OperationDesc[])overloads.toArray(array);
/*      */   }
/*      */ 
/*      */   private synchronized void initQNameMap()
/*      */   {
/*      */     Iterator i;
/*  543 */     if (this.qname2OperationsMap == null) {
/*  544 */       loadServiceDescByIntrospection();
/*      */ 
/*  546 */       this.qname2OperationsMap = new HashMap();
/*  547 */       for (i = this.operations.iterator(); i.hasNext(); ) {
/*  548 */         OperationDesc operationDesc = (OperationDesc)i.next();
/*  549 */         QName qname = operationDesc.getElementQName();
/*  550 */         ArrayList list = (ArrayList)this.qname2OperationsMap.get(qname);
/*  551 */         if (list == null) {
/*  552 */           list = new ArrayList();
/*  553 */           this.qname2OperationsMap.put(qname, list);
/*      */         }
/*  555 */         list.add(operationDesc);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private void syncOperationToClass(OperationDesc oper, Class implClass)
/*      */   {
/*  637 */     if (oper.getMethod() != null) {
/*  638 */       return;
/*      */     }
/*      */ 
/*  642 */     Method[] methods = getMethods(implClass);
/*      */ 
/*  644 */     Method possibleMatch = null;
/*      */ 
/*  646 */     for (int i = 0; i < methods.length; i++) {
/*  647 */       Method method = methods[i];
/*  648 */       if ((!Modifier.isPublic(method.getModifiers())) || (!method.getName().equals(oper.getName())) || (this.method2OperationMap.get(method) != null))
/*      */         continue;
/*      */       Class[] paramTypes;
/*      */       boolean conversionNecessary;
/*      */       int j;
/*  652 */       if (this.style == Style.MESSAGE) {
/*  653 */         int messageOperType = checkMessageMethod(method);
/*  654 */         if (messageOperType != -4) {
/*  655 */           if (messageOperType == -1) {
/*  656 */             throw new InternalException("Couldn't match method to any of the allowable message-style patterns!");
/*      */           }
/*  658 */           oper.setMessageOperationStyle(messageOperType);
/*      */ 
/*  661 */           possibleMatch = method;
/*  662 */           break;
/*      */         }
/*      */       }
/*      */       else {
/*  666 */         paramTypes = method.getParameterTypes();
/*  667 */         if (paramTypes.length != oper.getNumParams())
/*      */         {
/*      */           continue;
/*      */         }
/*  671 */         conversionNecessary = false;
/*  672 */         for (j = 0; j < paramTypes.length; j++) {
/*  673 */           Class type = paramTypes[j];
/*  674 */           Class actualType = type;
/*  675 */           if (Holder.class.isAssignableFrom(type)) {
/*  676 */             actualType = JavaUtils.getHolderValueType(type);
/*      */           }
/*  678 */           ParameterDesc param = oper.getParameter(j);
/*  679 */           QName typeQName = param.getTypeQName();
/*  680 */           if (typeQName == null)
/*      */           {
/*  688 */             typeQName = getTypeMapping().getTypeQName(actualType);
/*  689 */             param.setTypeQName(typeQName);
/*      */           }
/*      */           else
/*      */           {
/*  697 */             Class paramClass = param.getJavaType();
/*  698 */             if ((paramClass != null) && (JavaUtils.getHolderValueType(paramClass) != null))
/*      */             {
/*  700 */               paramClass = JavaUtils.getHolderValueType(paramClass);
/*      */             }
/*  702 */             if (paramClass == null) {
/*  703 */               paramClass = getTypeMapping().getClassForQName(param.getTypeQName(), type);
/*      */             }
/*      */ 
/*  707 */             if (paramClass != null)
/*      */             {
/*  711 */               if (!JavaUtils.isConvertable(paramClass, actualType))
/*      */               {
/*      */                 break;
/*      */               }
/*  715 */               if (!actualType.isAssignableFrom(paramClass))
/*      */               {
/*  717 */                 conversionNecessary = true;
/*      */               }
/*      */ 
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/*  724 */           param.setJavaType(type);
/*      */         }
/*      */ 
/*  727 */         if (j != paramTypes.length)
/*      */         {
/*      */           continue;
/*      */         }
/*      */ 
/*  733 */         possibleMatch = method;
/*      */ 
/*  737 */         if (!conversionNecessary)
/*      */         {
/*      */           break;
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  748 */     if (possibleMatch != null) {
/*  749 */       Class returnClass = possibleMatch.getReturnType();
/*  750 */       oper.setReturnClass(returnClass);
/*      */ 
/*  752 */       QName returnType = oper.getReturnType();
/*  753 */       if (returnType == null) {
/*  754 */         oper.setReturnType(getTypeMapping().getTypeQName(returnClass));
/*      */       }
/*      */ 
/*  758 */       createFaultMetadata(possibleMatch, oper);
/*      */ 
/*  760 */       oper.setMethod(possibleMatch);
/*  761 */       this.method2OperationMap.put(possibleMatch, oper);
/*  762 */       return;
/*      */     }
/*      */ 
/*  766 */     Class superClass = implClass.getSuperclass();
/*  767 */     if ((superClass != null) && (!superClass.getName().startsWith("java.")) && (!superClass.getName().startsWith("javax.")) && ((this.stopClasses == null) || (!this.stopClasses.contains(superClass.getName()))))
/*      */     {
/*  772 */       syncOperationToClass(oper, superClass);
/*      */     }
/*      */ 
/*  776 */     if (oper.getMethod() == null) {
/*  777 */       InternalException ie = new InternalException(Messages.getMessage("serviceDescOperSync00", oper.getName(), implClass.getName()));
/*      */ 
/*  781 */       throw ie;
/*      */     }
/*      */   }
/*      */ 
/*      */   private Method[] getMethods(Class implClass) {
/*  786 */     if (implClass.isInterface())
/*      */     {
/*  788 */       List methodsList = new ArrayList();
/*  789 */       Method[] methods = implClass.getMethods();
/*  790 */       if (methods != null) {
/*  791 */         for (int i = 0; i < methods.length; i++) {
/*  792 */           String declaringClass = methods[i].getDeclaringClass().getName();
/*  793 */           if ((declaringClass.startsWith("java.")) || (declaringClass.startsWith("javax.")))
/*      */             continue;
/*  795 */           methodsList.add(methods[i]);
/*      */         }
/*      */       }
/*      */ 
/*  799 */       return (Method[])methodsList.toArray(new Method[0]);
/*      */     }
/*  801 */     return implClass.getDeclaredMethods();
/*      */   }
/*      */ 
/*      */   private int checkMessageMethod(Method method)
/*      */   {
/*  808 */     Class[] params = method.getParameterTypes();
/*      */ 
/*  810 */     if (params.length == 1) {
/*  811 */       if ((params[0] == new Element[0].getClass()) && (method.getReturnType() == new Element[0].getClass()))
/*      */       {
/*  813 */         return 3;
/*      */       }
/*      */ 
/*  816 */       if ((params[0] == new SOAPBodyElement[0].getClass()) && (method.getReturnType() == new SOAPBodyElement[0].getClass()))
/*      */       {
/*  818 */         return 1;
/*      */       }
/*      */ 
/*  821 */       if ((params[0] == Document.class) && (method.getReturnType() == Document.class))
/*      */       {
/*  823 */         return 4;
/*      */       }
/*  825 */     } else if ((params.length == 2) && (
/*  826 */       ((params[0] == org.apache.axis.message.SOAPEnvelope.class) && (params[1] == org.apache.axis.message.SOAPEnvelope.class)) || ((params[0] == javax.xml.soap.SOAPEnvelope.class) && (params[1] == javax.xml.soap.SOAPEnvelope.class) && (method.getReturnType() == Void.TYPE))))
/*      */     {
/*  831 */       return 2;
/*      */     }
/*      */ 
/*  834 */     if ((null != this.allowedMethods) && (!this.allowedMethods.isEmpty())) {
/*  835 */       throw new InternalException(Messages.getMessage("badMsgMethodParams", method.getName()));
/*      */     }
/*  837 */     return -4;
/*      */   }
/*      */ 
/*      */   public void loadServiceDescByIntrospection()
/*      */   {
/*  846 */     loadServiceDescByIntrospection(this.implClass);
/*      */ 
/*  850 */     this.completedNames = null;
/*      */   }
/*      */ 
/*      */   public void loadServiceDescByIntrospection(Class implClass)
/*      */   {
/*  858 */     if ((this.introspectionComplete) || (implClass == null)) {
/*  859 */       return;
/*      */     }
/*      */ 
/*  863 */     this.implClass = implClass;
/*  864 */     if (Skeleton.class.isAssignableFrom(implClass)) {
/*  865 */       this.isSkeletonClass = true;
/*  866 */       loadSkeletonOperations();
/*      */     }
/*      */ 
/*  872 */     AxisServiceConfig axisConfig = null;
/*      */     try {
/*  874 */       Method method = implClass.getDeclaredMethod("getAxisServiceConfig", new Class[0]);
/*      */ 
/*  876 */       if ((method != null) && (Modifier.isStatic(method.getModifiers()))) {
/*  877 */         axisConfig = (AxisServiceConfig)method.invoke(null, null);
/*      */       }
/*      */     }
/*      */     catch (Exception e)
/*      */     {
/*      */     }
/*  883 */     if (axisConfig != null) {
/*  884 */       String allowedMethodsStr = axisConfig.getAllowedMethods();
/*  885 */       if ((allowedMethodsStr != null) && (!"*".equals(allowedMethodsStr))) {
/*  886 */         ArrayList methodList = new ArrayList();
/*  887 */         StringTokenizer tokenizer = new StringTokenizer(allowedMethodsStr, " ,");
/*      */ 
/*  889 */         while (tokenizer.hasMoreTokens()) {
/*  890 */           methodList.add(tokenizer.nextToken());
/*      */         }
/*  892 */         setAllowedMethods(methodList);
/*      */       }
/*      */     }
/*      */ 
/*  896 */     loadServiceDescByIntrospectionRecursive(implClass);
/*      */ 
/*  899 */     for (Iterator iterator = this.operations.iterator(); iterator.hasNext(); ) {
/*  900 */       OperationDesc operation = (OperationDesc)iterator.next();
/*  901 */       if (operation.getMethod() == null) {
/*  902 */         throw new InternalException(Messages.getMessage("badWSDDOperation", operation.getName(), "" + operation.getNumParams()));
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  909 */     if ((this.style == Style.MESSAGE) && (this.operations.size() == 1)) {
/*  910 */       this.messageServiceDefaultOp = ((OperationDesc)this.operations.get(0));
/*      */     }
/*      */ 
/*  913 */     this.introspectionComplete = true;
/*      */   }
/*      */ 
/*      */   private boolean isServiceLifeCycleMethod(Class implClass, Method m)
/*      */   {
/*  922 */     if (ServiceLifecycle.class.isAssignableFrom(implClass)) {
/*  923 */       String methodName = m.getName();
/*      */ 
/*  925 */       if (methodName.equals("init"))
/*      */       {
/*  928 */         Class[] classes = m.getParameterTypes();
/*  929 */         if ((classes != null) && (classes.length == 1) && (classes[0] == Object.class) && (m.getReturnType() == Void.TYPE))
/*      */         {
/*  933 */           return true;
/*      */         }
/*  935 */       } else if (methodName.equals("destroy"))
/*      */       {
/*  938 */         Class[] classes = m.getParameterTypes();
/*  939 */         if ((classes != null) && (classes.length == 0) && (m.getReturnType() == Void.TYPE))
/*      */         {
/*  942 */           return true;
/*      */         }
/*      */       }
/*      */     }
/*  946 */     return false;
/*      */   }
/*      */ 
/*      */   private void loadServiceDescByIntrospectionRecursive(Class implClass)
/*      */   {
/*  954 */     if (Skeleton.class.equals(implClass)) {
/*  955 */       return;
/*      */     }
/*      */ 
/*  958 */     Method[] methods = getMethods(implClass);
/*      */ 
/*  960 */     for (int i = 0; i < methods.length; i++) {
/*  961 */       if ((Modifier.isPublic(methods[i].getModifiers())) && (!isServiceLifeCycleMethod(implClass, methods[i]))) {
/*  962 */         getSyncedOperationsForName(implClass, methods[i].getName());
/*      */       }
/*      */     }
/*      */ 
/*  966 */     if (implClass.isInterface()) {
/*  967 */       Class[] superClasses = implClass.getInterfaces();
/*  968 */       for (int i = 0; i < superClasses.length; i++) {
/*  969 */         Class superClass = superClasses[i];
/*  970 */         if ((superClass.getName().startsWith("java.")) || (superClass.getName().startsWith("javax.")) || ((this.stopClasses != null) && (this.stopClasses.contains(superClass.getName()))))
/*      */         {
/*      */           continue;
/*      */         }
/*  974 */         loadServiceDescByIntrospectionRecursive(superClass);
/*      */       }
/*      */     }
/*      */     else {
/*  978 */       Class superClass = implClass.getSuperclass();
/*  979 */       if ((superClass != null) && (!superClass.getName().startsWith("java.")) && (!superClass.getName().startsWith("javax.")) && ((this.stopClasses == null) || (!this.stopClasses.contains(superClass.getName()))))
/*      */       {
/*  984 */         loadServiceDescByIntrospectionRecursive(superClass);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void loadServiceDescByIntrospection(Class cls, TypeMapping tm)
/*      */   {
/*  997 */     this.implClass = cls;
/*  998 */     this.tm = tm;
/*      */ 
/* 1000 */     if (Skeleton.class.isAssignableFrom(this.implClass)) {
/* 1001 */       this.isSkeletonClass = true;
/* 1002 */       loadSkeletonOperations();
/*      */     }
/*      */ 
/* 1005 */     loadServiceDescByIntrospection();
/*      */   }
/*      */ 
/*      */   private void getSyncedOperationsForName(Class implClass, String methodName)
/*      */   {
/* 1015 */     if ((this.isSkeletonClass) && (
/* 1016 */       (methodName.equals("getOperationDescByName")) || (methodName.equals("getOperationDescs"))))
/*      */     {
/* 1018 */       return;
/*      */     }
/*      */ 
/* 1023 */     if (implClass == null) {
/* 1024 */       return;
/*      */     }
/*      */ 
/* 1027 */     if ((this.completedNames == null) || (this.completedNames.contains(methodName))) {
/* 1028 */       return;
/*      */     }
/*      */ 
/* 1031 */     if ((this.allowedMethods != null) && (!this.allowedMethods.contains(methodName)))
/*      */     {
/* 1033 */       return;
/*      */     }
/* 1035 */     if ((this.disallowedMethods != null) && (this.disallowedMethods.contains(methodName)))
/*      */     {
/* 1037 */       return;
/*      */     }
/*      */ 
/* 1042 */     if ((this.isSkeletonClass) && (!this.haveAllSkeletonMethods))
/*      */     {
/* 1045 */       if (this.skelMethod == null)
/*      */       {
/*      */         try {
/* 1048 */           this.skelMethod = implClass.getDeclaredMethod("getOperationDescByName", new Class[] { String.class });
/*      */         }
/*      */         catch (NoSuchMethodException e) {
/*      */         }
/*      */         catch (SecurityException e) {
/*      */         }
/* 1054 */         if (this.skelMethod == null)
/*      */         {
/* 1056 */           return;
/*      */         }
/*      */       }
/*      */       try {
/* 1060 */         List skelList = (List)this.skelMethod.invoke(implClass, new Object[] { methodName });
/*      */ 
/* 1063 */         if (skelList != null) {
/* 1064 */           Iterator i = skelList.iterator();
/* 1065 */           while (i.hasNext())
/* 1066 */             addOperationDesc((OperationDesc)i.next());
/*      */         }
/*      */       }
/*      */       catch (IllegalAccessException e) {
/* 1070 */         if (log.isDebugEnabled()) {
/* 1071 */           log.debug(Messages.getMessage("exception00"), e);
/*      */         }
/* 1073 */         return;
/*      */       } catch (IllegalArgumentException e) {
/* 1075 */         if (log.isDebugEnabled()) {
/* 1076 */           log.debug(Messages.getMessage("exception00"), e);
/*      */         }
/* 1078 */         return;
/*      */       } catch (InvocationTargetException e) {
/* 1080 */         if (log.isDebugEnabled()) {
/* 1081 */           log.debug(Messages.getMessage("exception00"), e);
/*      */         }
/* 1083 */         return;
/*      */       }
/*      */     }
/*      */     Iterator i;
/* 1089 */     if (this.name2OperationsMap != null) {
/* 1090 */       ArrayList currentOverloads = (ArrayList)this.name2OperationsMap.get(methodName);
/*      */ 
/* 1092 */       if (currentOverloads != null)
/*      */       {
/* 1094 */         for (i = currentOverloads.iterator(); i.hasNext(); ) {
/* 1095 */           OperationDesc oper = (OperationDesc)i.next();
/* 1096 */           if (oper.getMethod() == null) {
/* 1097 */             syncOperationToClass(oper, implClass);
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1112 */     createOperationsForName(implClass, methodName);
/*      */ 
/* 1115 */     this.completedNames.add(methodName);
/*      */   }
/*      */ 
/* 1119 */   private String getUniqueOperationName(String name) { int i = 1;
/*      */     String candidate;
/*      */     do candidate = name + i++;
/* 1123 */     while (this.name2OperationsMap.get(candidate) != null);
/*      */ 
/* 1125 */     return candidate;
/*      */   }
/*      */ 
/*      */   private void createOperationsForName(Class implClass, String methodName)
/*      */   {
/* 1137 */     if ((this.isSkeletonClass) && (
/* 1138 */       (methodName.equals("getOperationDescByName")) || (methodName.equals("getOperationDescs"))))
/*      */     {
/* 1140 */       return;
/*      */     }
/*      */ 
/* 1143 */     Method[] methods = getMethods(implClass);
/*      */ 
/* 1145 */     for (int i = 0; i < methods.length; i++) {
/* 1146 */       Method method = methods[i];
/* 1147 */       if ((!Modifier.isPublic(method.getModifiers())) || (!method.getName().equals(methodName)) || (isServiceLifeCycleMethod(implClass, method))) {
/*      */         continue;
/*      */       }
/* 1150 */       createOperationForMethod(method);
/*      */     }
/*      */ 
/* 1154 */     Class superClass = implClass.getSuperclass();
/* 1155 */     if ((superClass != null) && (!superClass.getName().startsWith("java.")) && (!superClass.getName().startsWith("javax.")) && ((this.stopClasses == null) || (!this.stopClasses.contains(superClass.getName()))))
/*      */     {
/* 1160 */       createOperationsForName(superClass, methodName);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void createOperationForMethod(Method method)
/*      */   {
/* 1176 */     if (this.method2OperationMap.get(method) != null) {
/* 1177 */       return;
/*      */     }
/*      */ 
/* 1180 */     Class[] paramTypes = method.getParameterTypes();
/*      */ 
/* 1185 */     ArrayList overloads = this.name2OperationsMap == null ? null : (ArrayList)this.name2OperationsMap.get(method.getName());
/*      */ 
/* 1187 */     if ((overloads != null) && (!overloads.isEmpty()))
/*      */     {
/* 1190 */       for (int i = 0; i < overloads.size(); i++) {
/* 1191 */         OperationDesc op = (OperationDesc)overloads.get(i);
/* 1192 */         Method checkMethod = op.getMethod();
/* 1193 */         if (checkMethod != null) {
/* 1194 */           Class[] others = checkMethod.getParameterTypes();
/* 1195 */           if (paramTypes.length == others.length) {
/* 1196 */             int j = 0;
/* 1197 */             while ((j < others.length) && 
/* 1198 */               (others[j].equals(paramTypes[j]))) {
/* 1197 */               j++;
/*      */             }
/*      */ 
/* 1202 */             if (j == others.length) {
/* 1203 */               return;
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/* 1209 */     boolean isWSICompliant = JavaUtils.isTrue(AxisProperties.getProperty("axis.ws-i.bp11.compatibility"));
/*      */ 
/* 1213 */     OperationDesc operation = new OperationDesc();
/*      */ 
/* 1218 */     String name = method.getName();
/* 1219 */     if ((isWSICompliant) && (this.name2OperationsMap != null)) {
/* 1220 */       Collection methodNames = this.name2OperationsMap.keySet();
/* 1221 */       name = JavaUtils.getUniqueValue(methodNames, name);
/*      */     }
/* 1223 */     operation.setName(name);
/* 1224 */     String defaultNS = "";
/* 1225 */     if ((this.namespaceMappings != null) && (!this.namespaceMappings.isEmpty()))
/*      */     {
/* 1228 */       defaultNS = (String)this.namespaceMappings.get(0);
/*      */     }
/* 1230 */     if (defaultNS.length() == 0) {
/* 1231 */       defaultNS = Namespaces.makeNamespace(method.getDeclaringClass().getName());
/*      */     }
/* 1233 */     operation.setElementQName(new QName(defaultNS, name));
/* 1234 */     operation.setMethod(method);
/*      */ 
/* 1238 */     if (this.style == Style.MESSAGE) {
/* 1239 */       int messageOperType = checkMessageMethod(method);
/* 1240 */       if (messageOperType == -4) return;
/* 1241 */       if (messageOperType == -1) {
/* 1242 */         throw new InternalException("Couldn't match method to any of the allowable message-style patterns!");
/*      */       }
/* 1244 */       operation.setMessageOperationStyle(messageOperType);
/* 1245 */       operation.setReturnClass(Object.class);
/* 1246 */       operation.setReturnType(Constants.XSD_ANYTYPE);
/*      */     }
/*      */     else {
/* 1249 */       Class retClass = method.getReturnType();
/* 1250 */       operation.setReturnClass(retClass);
/* 1251 */       QName typeQName = getTypeQName(retClass);
/* 1252 */       operation.setReturnType(typeQName);
/*      */ 
/* 1254 */       String[] paramNames = getParamNames(method);
/*      */ 
/* 1256 */       for (int k = 0; k < paramTypes.length; k++) {
/* 1257 */         Class type = paramTypes[k];
/* 1258 */         ParameterDesc paramDesc = new ParameterDesc();
/*      */ 
/* 1261 */         String paramNamespace = this.style == Style.RPC ? "" : operation.getElementQName().getNamespaceURI();
/*      */ 
/* 1265 */         if ((paramNames != null) && (paramNames[k] != null) && (paramNames[k].length() > 0))
/*      */         {
/* 1267 */           paramDesc.setQName(new QName(paramNamespace, paramNames[k]));
/*      */         }
/* 1269 */         else paramDesc.setQName(new QName(paramNamespace, "in" + k));
/*      */ 
/* 1275 */         Class heldClass = JavaUtils.getHolderValueType(type);
/* 1276 */         if (heldClass != null) {
/* 1277 */           paramDesc.setMode(3);
/* 1278 */           paramDesc.setTypeQName(getTypeQName(heldClass));
/*      */         } else {
/* 1280 */           paramDesc.setMode(1);
/* 1281 */           paramDesc.setTypeQName(getTypeQName(type));
/*      */         }
/* 1283 */         paramDesc.setJavaType(type);
/* 1284 */         operation.addParameter(paramDesc);
/*      */       }
/*      */     }
/*      */ 
/* 1288 */     createFaultMetadata(method, operation);
/*      */ 
/* 1290 */     addOperationDesc(operation);
/* 1291 */     this.method2OperationMap.put(method, operation);
/*      */   }
/*      */ 
/*      */   private QName getTypeQName(Class javaClass)
/*      */   {
/* 1296 */     TypeMapping tm = getTypeMapping();
/*      */     QName typeQName;
/*      */     QName typeQName;
/* 1297 */     if (this.style == Style.RPC) {
/* 1298 */       typeQName = tm.getTypeQName(javaClass);
/*      */     } else {
/* 1300 */       typeQName = tm.getTypeQNameExact(javaClass);
/* 1301 */       if ((typeQName == null) && (javaClass.isArray()))
/* 1302 */         typeQName = tm.getTypeQName(javaClass.getComponentType());
/*      */       else {
/* 1304 */         typeQName = tm.getTypeQName(javaClass);
/*      */       }
/*      */     }
/* 1307 */     return typeQName;
/*      */   }
/*      */ 
/*      */   private void createFaultMetadata(Method method, OperationDesc operation)
/*      */   {
/* 1312 */     Class[] exceptionTypes = method.getExceptionTypes();
/*      */ 
/* 1314 */     for (int i = 0; i < exceptionTypes.length; i++)
/*      */     {
/* 1318 */       Class ex = exceptionTypes[i];
/* 1319 */       if ((ex == RemoteException.class) || (ex == AxisFault.class) || (ex.getName().startsWith("java.")) || (ex.getName().startsWith("javax.")))
/*      */       {
/*      */         continue;
/*      */       }
/*      */ 
/* 1360 */       FaultDesc fault = operation.getFaultByClass(ex, false);
/*      */       boolean isNew;
/*      */       boolean isNew;
/* 1364 */       if (fault == null) {
/* 1365 */         fault = new FaultDesc();
/* 1366 */         isNew = true;
/*      */       } else {
/* 1368 */         isNew = false;
/*      */       }
/*      */ 
/* 1374 */       QName xmlType = fault.getXmlType();
/* 1375 */       if (xmlType == null) {
/* 1376 */         fault.setXmlType(getTypeMapping().getTypeQName(ex));
/*      */       }
/*      */ 
/* 1380 */       String pkgAndClsName = ex.getName();
/* 1381 */       if (fault.getClassName() == null) {
/* 1382 */         fault.setClassName(pkgAndClsName);
/*      */       }
/* 1384 */       if (fault.getName() == null) {
/* 1385 */         String name = pkgAndClsName.substring(pkgAndClsName.lastIndexOf('.') + 1, pkgAndClsName.length());
/*      */ 
/* 1388 */         fault.setName(name);
/*      */       }
/*      */ 
/* 1393 */       if (fault.getParameters() == null) {
/* 1394 */         if (xmlType == null) {
/* 1395 */           xmlType = getTypeMapping().getTypeQName(ex);
/*      */         }
/* 1397 */         QName qname = fault.getQName();
/* 1398 */         if (qname == null) {
/* 1399 */           qname = new QName("", "fault");
/*      */         }
/* 1401 */         ParameterDesc param = new ParameterDesc(qname, 1, xmlType);
/*      */ 
/* 1405 */         param.setJavaType(ex);
/* 1406 */         ArrayList exceptionParams = new ArrayList();
/* 1407 */         exceptionParams.add(param);
/* 1408 */         fault.setParameters(exceptionParams);
/*      */       }
/*      */ 
/* 1412 */       if (fault.getQName() == null) {
/* 1413 */         fault.setQName(new QName(pkgAndClsName));
/*      */       }
/*      */ 
/* 1416 */       if (!isNew)
/*      */         continue;
/* 1418 */       operation.addFault(fault);
/*      */     }
/*      */   }
/*      */ 
/*      */   private String[] getParamNames(Method method)
/*      */   {
/* 1425 */     synchronized (this.method2ParamsMap) {
/* 1426 */       String[] paramNames = (String[])this.method2ParamsMap.get(method);
/* 1427 */       if (paramNames != null)
/* 1428 */         return paramNames;
/* 1429 */       paramNames = ParamNameExtractor.getParameterNamesFromDebugInfo(method);
/* 1430 */       this.method2ParamsMap.put(method, paramNames);
/* 1431 */       return paramNames;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setNamespaceMappings(List namespaces) {
/* 1436 */     this.namespaceMappings = namespaces;
/*      */   }
/*      */ 
/*      */   public String getDefaultNamespace() {
/* 1440 */     if ((this.namespaceMappings == null) || (this.namespaceMappings.isEmpty()))
/* 1441 */       return null;
/* 1442 */     return (String)this.namespaceMappings.get(0);
/*      */   }
/*      */ 
/*      */   public void setDefaultNamespace(String namespace) {
/* 1446 */     if (this.namespaceMappings == null)
/* 1447 */       this.namespaceMappings = new ArrayList();
/* 1448 */     this.namespaceMappings.add(0, namespace);
/*      */   }
/*      */ 
/*      */   public void setProperty(String name, Object value) {
/* 1452 */     if (this.properties == null) {
/* 1453 */       this.properties = new HashMap();
/*      */     }
/* 1455 */     this.properties.put(name, value);
/*      */   }
/*      */ 
/*      */   public Object getProperty(String name) {
/* 1459 */     if (this.properties == null) {
/* 1460 */       return null;
/*      */     }
/* 1462 */     return this.properties.get(name);
/*      */   }
/*      */ 
/*      */   public String getEndpointURL() {
/* 1466 */     return this.endpointURL;
/*      */   }
/*      */ 
/*      */   public void setEndpointURL(String endpointURL) {
/* 1470 */     this.endpointURL = endpointURL;
/*      */   }
/*      */ 
/*      */   public TypeMappingRegistry getTypeMappingRegistry() {
/* 1474 */     if (this.tmr == null) {
/* 1475 */       this.tmr = new TypeMappingRegistryImpl(false);
/*      */     }
/* 1477 */     return this.tmr;
/*      */   }
/*      */ 
/*      */   public void setTypeMappingRegistry(TypeMappingRegistry tmr) {
/* 1481 */     this.tmr = tmr;
/*      */   }
/*      */ 
/*      */   public boolean isInitialized() {
/* 1485 */     return this.implClass != null;
/*      */   }
/*      */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.description.JavaServiceDesc
 * JD-Core Version:    0.6.0
 */