/*     */ package org.apache.axis.encoding.ser;
/*     */ 
/*     */ import java.lang.reflect.Modifier;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.StringTokenizer;
/*     */ import javax.xml.namespace.QName;
/*     */ import org.apache.axis.Constants;
/*     */ import org.apache.axis.components.logger.LogFactory;
/*     */ import org.apache.axis.encoding.DeserializationContext;
/*     */ import org.apache.axis.encoding.Deserializer;
/*     */ import org.apache.axis.encoding.DeserializerImpl;
/*     */ import org.apache.axis.encoding.DeserializerTarget;
/*     */ import org.apache.axis.encoding.TypeMapping;
/*     */ import org.apache.axis.message.MessageElement;
/*     */ import org.apache.axis.message.SOAPHandler;
/*     */ import org.apache.axis.soap.SOAPConstants;
/*     */ import org.apache.axis.utils.ClassUtils;
/*     */ import org.apache.axis.utils.JavaUtils;
/*     */ import org.apache.axis.utils.JavaUtils.ConvertCache;
/*     */ import org.apache.axis.utils.Messages;
/*     */ import org.apache.axis.wsdl.symbolTable.SchemaUtils;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.xml.sax.Attributes;
/*     */ import org.xml.sax.SAXException;
/*     */ 
/*     */ public class ArrayDeserializer extends DeserializerImpl
/*     */ {
/*  55 */   protected static Log log = LogFactory.getLog(ArrayDeserializer.class.getName());
/*     */   public QName arrayType;
/*     */   public int curIndex;
/*     */   QName defaultItemType;
/*     */   int length;
/*     */   Class arrayClass;
/*     */   ArrayList mDimLength;
/*     */   ArrayList mDimFactor;
/*     */   SOAPConstants soapConstants;
/*     */ 
/*     */   public ArrayDeserializer()
/*     */   {
/*  58 */     this.arrayType = null;
/*  59 */     this.curIndex = 0;
/*     */ 
/*  62 */     this.arrayClass = null;
/*  63 */     this.mDimLength = null;
/*  64 */     this.mDimFactor = null;
/*  65 */     this.soapConstants = SOAPConstants.SOAP11_CONSTANTS;
/*     */   }
/*     */ 
/*     */   public void onStartElement(String namespace, String localName, String prefix, Attributes attributes, DeserializationContext context)
/*     */     throws SAXException
/*     */   {
/* 109 */     if (log.isDebugEnabled()) {
/* 110 */       log.debug("Enter: ArrayDeserializer::startElement()");
/*     */     }
/*     */ 
/* 113 */     this.soapConstants = context.getSOAPConstants();
/*     */ 
/* 117 */     QName typeQName = context.getTypeFromAttributes(namespace, localName, attributes);
/*     */ 
/* 120 */     if (typeQName == null) {
/* 121 */       typeQName = getDefaultType();
/*     */     }
/*     */ 
/* 124 */     if ((typeQName != null) && (Constants.equals(Constants.SOAP_ARRAY, typeQName)))
/*     */     {
/* 126 */       typeQName = null;
/*     */     }
/*     */ 
/* 130 */     QName arrayTypeValue = context.getQNameFromString(Constants.getValue(attributes, Constants.URIS_SOAP_ENC, this.soapConstants.getAttrItemType()));
/*     */ 
/* 138 */     String dimString = null;
/* 139 */     QName innerQName = null;
/* 140 */     String innerDimString = "";
/* 141 */     if (arrayTypeValue != null) {
/* 142 */       if (this.soapConstants != SOAPConstants.SOAP12_CONSTANTS)
/*     */       {
/* 145 */         String arrayTypeValueNamespaceURI = arrayTypeValue.getNamespaceURI();
/*     */ 
/* 147 */         String arrayTypeValueLocalPart = arrayTypeValue.getLocalPart();
/*     */ 
/* 150 */         int leftBracketIndex = arrayTypeValueLocalPart.lastIndexOf('[');
/*     */ 
/* 152 */         int rightBracketIndex = arrayTypeValueLocalPart.lastIndexOf(']');
/*     */ 
/* 154 */         if ((leftBracketIndex == -1) || (rightBracketIndex == -1) || (rightBracketIndex < leftBracketIndex))
/*     */         {
/* 157 */           throw new IllegalArgumentException(Messages.getMessage("badArrayType00", "" + arrayTypeValue));
/*     */         }
/*     */ 
/* 162 */         dimString = arrayTypeValueLocalPart.substring(leftBracketIndex + 1, rightBracketIndex);
/*     */ 
/* 165 */         arrayTypeValueLocalPart = arrayTypeValueLocalPart.substring(0, leftBracketIndex);
/*     */ 
/* 169 */         if (arrayTypeValueLocalPart.endsWith("]")) {
/* 170 */           this.defaultItemType = Constants.SOAP_ARRAY;
/* 171 */           int bracket = arrayTypeValueLocalPart.indexOf("[");
/* 172 */           innerQName = new QName(arrayTypeValueNamespaceURI, arrayTypeValueLocalPart.substring(0, bracket));
/*     */ 
/* 174 */           innerDimString = arrayTypeValueLocalPart.substring(bracket);
/*     */         } else {
/* 176 */           this.defaultItemType = new QName(arrayTypeValueNamespaceURI, arrayTypeValueLocalPart);
/*     */         }
/*     */       }
/*     */       else
/*     */       {
/* 181 */         String arraySizeValue = attributes.getValue(this.soapConstants.getEncodingURI(), "arraySize");
/* 182 */         int leftStarIndex = arraySizeValue.lastIndexOf('*');
/*     */ 
/* 185 */         if (leftStarIndex != -1)
/*     */         {
/* 187 */           if ((leftStarIndex != 0) || (arraySizeValue.length() != 1))
/*     */           {
/* 189 */             if (leftStarIndex == arraySizeValue.length() - 1) {
/* 190 */               throw new IllegalArgumentException(Messages.getMessage("badArraySize00", "" + arraySizeValue));
/*     */             }
/*     */ 
/* 195 */             dimString = arraySizeValue.substring(leftStarIndex + 2);
/* 196 */             innerQName = arrayTypeValue;
/* 197 */             innerDimString = arraySizeValue.substring(0, leftStarIndex + 1);
/*     */           }
/*     */         }
/* 200 */         else dimString = arraySizeValue;
/*     */ 
/* 203 */         if ((innerDimString == null) || (innerDimString.length() == 0))
/* 204 */           this.defaultItemType = arrayTypeValue;
/*     */         else {
/* 206 */           this.defaultItemType = Constants.SOAP_ARRAY12;
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 212 */     if ((this.defaultItemType == null) && (typeQName == null)) {
/* 213 */       Class destClass = context.getDestinationClass();
/* 214 */       if ((destClass == null) || (!destClass.isArray()))
/*     */       {
/* 217 */         this.defaultItemType = Constants.XSD_ANYTYPE;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 222 */     this.arrayClass = null;
/* 223 */     if (typeQName != null) {
/* 224 */       this.arrayClass = context.getTypeMapping().getClassForQName(typeQName);
/*     */     }
/*     */ 
/* 228 */     if ((typeQName == null) || (this.arrayClass == null))
/*     */     {
/* 231 */       Class arrayItemClass = null;
/* 232 */       QName compQName = this.defaultItemType;
/*     */ 
/* 235 */       String dims = "[]";
/* 236 */       if (innerQName != null) {
/* 237 */         compQName = innerQName;
/*     */ 
/* 239 */         if (this.soapConstants == SOAPConstants.SOAP12_CONSTANTS)
/*     */         {
/* 241 */           int offset = 0;
/* 242 */           while ((offset = innerDimString.indexOf('*', offset)) != -1) {
/* 243 */             dims = dims + "[]";
/* 244 */             offset++;
/*     */           }
/*     */         }
/*     */         else {
/* 248 */           dims = dims + innerDimString;
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 253 */       arrayItemClass = context.getTypeMapping().getClassForQName(compQName);
/* 254 */       if (arrayItemClass != null)
/*     */       {
/*     */         try
/*     */         {
/* 259 */           String loadableArrayClassName = JavaUtils.getLoadableClassName(JavaUtils.getTextClassName(arrayItemClass.getName()) + dims);
/*     */ 
/* 261 */           this.arrayClass = ClassUtils.forName(loadableArrayClassName, true, arrayItemClass.getClassLoader());
/*     */         }
/*     */         catch (Exception e)
/*     */         {
/* 265 */           throw new SAXException(Messages.getMessage("noComponent00", "" + this.defaultItemType));
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 271 */     if (this.arrayClass == null) {
/* 272 */       this.arrayClass = context.getDestinationClass();
/*     */     }
/*     */ 
/* 275 */     if (this.arrayClass == null) {
/* 276 */       throw new SAXException(Messages.getMessage("noComponent00", "" + this.defaultItemType));
/*     */     }
/*     */ 
/* 280 */     if ((dimString == null) || (dimString.length() == 0))
/*     */     {
/* 282 */       this.value = new ArrayListExtension(this.arrayClass);
/*     */     }
/*     */     else {
/*     */       try
/*     */       {
/*     */         StringTokenizer tokenizer;
/*     */         StringTokenizer tokenizer;
/* 287 */         if (this.soapConstants == SOAPConstants.SOAP12_CONSTANTS)
/* 288 */           tokenizer = new StringTokenizer(dimString);
/*     */         else {
/* 290 */           tokenizer = new StringTokenizer(dimString, "[],");
/*     */         }
/*     */ 
/* 293 */         this.length = Integer.parseInt(tokenizer.nextToken());
/* 294 */         if (tokenizer.hasMoreTokens())
/*     */         {
/* 300 */           this.mDimLength = new ArrayList();
/* 301 */           this.mDimLength.add(new Integer(this.length));
/*     */ 
/* 303 */           while (tokenizer.hasMoreTokens()) {
/* 304 */             this.mDimLength.add(new Integer(Integer.parseInt(tokenizer.nextToken())));
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/* 312 */         ArrayList list = new ArrayListExtension(this.arrayClass, this.length);
/*     */ 
/* 320 */         this.value = list;
/*     */       }
/*     */       catch (NumberFormatException e)
/*     */       {
/* 325 */         throw new IllegalArgumentException(Messages.getMessage("badInteger00", dimString));
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 331 */     String offset = Constants.getValue(attributes, Constants.URIS_SOAP_ENC, "offset");
/*     */ 
/* 334 */     if (offset != null) {
/* 335 */       if (this.soapConstants == SOAPConstants.SOAP12_CONSTANTS) {
/* 336 */         throw new SAXException(Messages.getMessage("noSparseArray"));
/*     */       }
/*     */ 
/* 339 */       int leftBracketIndex = offset.lastIndexOf('[');
/* 340 */       int rightBracketIndex = offset.lastIndexOf(']');
/*     */ 
/* 342 */       if ((leftBracketIndex == -1) || (rightBracketIndex == -1) || (rightBracketIndex < leftBracketIndex))
/*     */       {
/* 346 */         throw new SAXException(Messages.getMessage("badOffset00", offset));
/*     */       }
/*     */ 
/* 350 */       this.curIndex = convertToIndex(offset.substring(leftBracketIndex + 1, rightBracketIndex), "badOffset00");
/*     */     }
/*     */ 
/* 356 */     if (log.isDebugEnabled())
/* 357 */       log.debug("Exit: ArrayDeserializer::startElement()");
/*     */   }
/*     */ 
/*     */   public SOAPHandler onStartChild(String namespace, String localName, String prefix, Attributes attributes, DeserializationContext context)
/*     */     throws SAXException
/*     */   {
/* 380 */     if (log.isDebugEnabled()) {
/* 381 */       log.debug("Enter: ArrayDeserializer.onStartChild()");
/*     */     }
/*     */ 
/* 386 */     if (attributes != null) {
/* 387 */       String pos = Constants.getValue(attributes, Constants.URIS_SOAP_ENC, "position");
/*     */ 
/* 391 */       if (pos != null) {
/* 392 */         if (this.soapConstants == SOAPConstants.SOAP12_CONSTANTS) {
/* 393 */           throw new SAXException(Messages.getMessage("noSparseArray"));
/*     */         }
/*     */ 
/* 396 */         int leftBracketIndex = pos.lastIndexOf('[');
/* 397 */         int rightBracketIndex = pos.lastIndexOf(']');
/*     */ 
/* 399 */         if ((leftBracketIndex == -1) || (rightBracketIndex == -1) || (rightBracketIndex < leftBracketIndex))
/*     */         {
/* 403 */           throw new SAXException(Messages.getMessage("badPosition00", pos));
/*     */         }
/*     */ 
/* 407 */         this.curIndex = convertToIndex(pos.substring(leftBracketIndex + 1, rightBracketIndex), "badPosition00");
/*     */       }
/*     */ 
/* 415 */       if (context.isNil(attributes)) {
/* 416 */         setChildValue(null, new Integer(this.curIndex++));
/* 417 */         return null;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 422 */     QName itemType = context.getTypeFromAttributes(namespace, localName, attributes);
/*     */ 
/* 427 */     Deserializer dSer = null;
/* 428 */     if ((itemType != null) && (context.getCurElement().getHref() == null)) {
/* 429 */       dSer = context.getDeserializerForType(itemType);
/*     */     }
/*     */ 
/* 432 */     if (dSer == null)
/*     */     {
/* 434 */       QName defaultType = this.defaultItemType;
/* 435 */       Class javaType = null;
/* 436 */       if ((this.arrayClass != null) && (this.arrayClass.isArray()) && (defaultType == null))
/*     */       {
/* 439 */         javaType = this.arrayClass.getComponentType();
/* 440 */         defaultType = context.getTypeMapping().getTypeQName(javaType);
/*     */       }
/*     */ 
/* 452 */       if ((itemType == null) && (dSer == null) && 
/* 453 */         (defaultType != null) && (SchemaUtils.isSimpleSchemaType(defaultType))) {
/* 454 */         dSer = context.getDeserializer(javaType, defaultType);
/*     */       }
/*     */ 
/* 462 */       if (dSer == null) {
/* 463 */         dSer = new DeserializerImpl();
/*     */ 
/* 465 */         if (itemType == null) {
/* 466 */           dSer.setDefaultType(defaultType);
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 474 */     dSer.registerValueTarget(new DeserializerTarget(this, new Integer(this.curIndex)));
/*     */ 
/* 479 */     addChildDeserializer(dSer);
/*     */ 
/* 481 */     this.curIndex += 1;
/*     */ 
/* 485 */     context.setDestinationClass(this.arrayClass.getComponentType());
/*     */ 
/* 487 */     if (log.isDebugEnabled()) {
/* 488 */       log.debug("Exit: ArrayDeserializer.onStartChild()");
/*     */     }
/*     */ 
/* 491 */     return (SOAPHandler)dSer;
/*     */   }
/*     */ 
/*     */   public void onEndChild(String namespace, String localName, DeserializationContext context) throws SAXException
/*     */   {
/* 496 */     context.setDestinationClass(this.arrayClass);
/*     */   }
/*     */ 
/*     */   public void characters(char[] chars, int i, int i1) throws SAXException {
/* 500 */     for (int idx = i; i < i1; i++)
/* 501 */       if (!Character.isWhitespace(chars[idx]))
/* 502 */         throw new SAXException(Messages.getMessage("charsInArray"));
/*     */   }
/*     */ 
/*     */   public void setChildValue(Object value, Object hint)
/*     */     throws SAXException
/*     */   {
/* 523 */     if (log.isDebugEnabled()) {
/* 524 */       log.debug("Enter: ArrayDeserializer::setValue(" + value + ", " + hint + ")");
/*     */     }
/* 526 */     ArrayList list = (ArrayList)this.value;
/* 527 */     int offset = ((Integer)hint).intValue();
/*     */ 
/* 529 */     if (this.mDimLength == null)
/*     */     {
/* 532 */       while (list.size() <= offset) {
/* 533 */         list.add(null);
/*     */       }
/*     */ 
/* 536 */       list.set(offset, value);
/*     */     }
/*     */     else
/*     */     {
/* 542 */       ArrayList mDimIndex = toMultiIndex(offset);
/*     */ 
/* 545 */       for (int i = 0; i < this.mDimLength.size(); i++) {
/* 546 */         int length = ((Integer)this.mDimLength.get(i)).intValue();
/* 547 */         int index = ((Integer)mDimIndex.get(i)).intValue();
/* 548 */         while (list.size() < length) {
/* 549 */           list.add(null);
/*     */         }
/*     */ 
/* 553 */         if (i < this.mDimLength.size() - 1) {
/* 554 */           if (list.get(index) == null) {
/* 555 */             list.set(index, new ArrayList());
/*     */           }
/* 557 */           list = (ArrayList)list.get(index);
/*     */         } else {
/* 559 */           list.set(index, value);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void valueComplete()
/*     */     throws SAXException
/*     */   {
/* 573 */     if (componentsReady()) {
/*     */       try {
/* 575 */         if (this.arrayClass != null) {
/* 576 */           this.value = JavaUtils.convert(this.value, this.arrayClass);
/*     */         }
/*     */       }
/*     */       catch (RuntimeException e)
/*     */       {
/*     */       }
/*     */     }
/* 583 */     super.valueComplete();
/*     */   }
/*     */ 
/*     */   private int convertToIndex(String text, String exceptKey)
/*     */     throws SAXException
/*     */   {
/* 600 */     StringTokenizer tokenizer = new StringTokenizer(text, "[],");
/* 601 */     int index = 0;
/*     */     try {
/* 603 */       if (this.mDimLength == null)
/*     */       {
/* 605 */         index = Integer.parseInt(tokenizer.nextToken());
/* 606 */         if (tokenizer.hasMoreTokens()) {
/* 607 */           throw new SAXException(Messages.getMessage(exceptKey, text));
/*     */         }
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/* 613 */         int dim = -1;
/* 614 */         ArrayList work = new ArrayList();
/* 615 */         while (tokenizer.hasMoreTokens())
/*     */         {
/* 618 */           dim++;
/* 619 */           if (dim >= this.mDimLength.size()) {
/* 620 */             throw new SAXException(Messages.getMessage(exceptKey, text));
/*     */           }
/*     */ 
/* 624 */           int workIndex = Integer.parseInt(tokenizer.nextToken());
/*     */ 
/* 627 */           if ((workIndex < 0) || (workIndex >= ((Integer)this.mDimLength.get(dim)).intValue()))
/*     */           {
/* 630 */             throw new SAXException(Messages.getMessage(exceptKey, text));
/*     */           }
/*     */ 
/* 633 */           work.add(new Integer(workIndex));
/*     */         }
/* 635 */         index = toSingleIndex(work);
/*     */       }
/*     */     } catch (SAXException e) {
/* 638 */       throw e;
/*     */     } catch (Exception e) {
/* 640 */       throw new SAXException(Messages.getMessage(exceptKey, text));
/*     */     }
/* 642 */     return index;
/*     */   }
/*     */ 
/*     */   private ArrayList toMultiIndex(int single)
/*     */   {
/* 651 */     if (this.mDimLength == null) {
/* 652 */       return null;
/*     */     }
/*     */ 
/* 655 */     if (this.mDimFactor == null) {
/* 656 */       this.mDimFactor = new ArrayList();
/* 657 */       for (int i = 0; i < this.mDimLength.size(); i++) {
/* 658 */         int factor = 1;
/* 659 */         for (int j = i + 1; j < this.mDimLength.size(); j++) {
/* 660 */           factor *= ((Integer)this.mDimLength.get(j)).intValue();
/*     */         }
/* 662 */         this.mDimFactor.add(new Integer(factor));
/*     */       }
/*     */     }
/*     */ 
/* 666 */     ArrayList rc = new ArrayList();
/* 667 */     for (int i = 0; i < this.mDimLength.size(); i++) {
/* 668 */       int factor = ((Integer)this.mDimFactor.get(i)).intValue();
/* 669 */       rc.add(new Integer(single / factor));
/* 670 */       single %= factor;
/*     */     }
/* 672 */     return rc;
/*     */   }
/*     */ 
/*     */   private int toSingleIndex(ArrayList indexArray)
/*     */   {
/* 681 */     if ((this.mDimLength == null) || (indexArray == null)) {
/* 682 */       return -1;
/*     */     }
/*     */ 
/* 685 */     if (this.mDimFactor == null) {
/* 686 */       this.mDimFactor = new ArrayList();
/* 687 */       for (int i = 0; i < this.mDimLength.size(); i++) {
/* 688 */         int factor = 1;
/* 689 */         for (int j = i + 1; j < this.mDimLength.size(); j++) {
/* 690 */           factor *= ((Integer)this.mDimLength.get(j)).intValue();
/*     */         }
/* 692 */         this.mDimFactor.add(new Integer(factor));
/*     */       }
/*     */     }
/*     */ 
/* 696 */     int single = 0;
/* 697 */     for (int i = 0; i < indexArray.size(); i++) {
/* 698 */       single += ((Integer)this.mDimFactor.get(i)).intValue() * ((Integer)indexArray.get(i)).intValue();
/*     */     }
/*     */ 
/* 701 */     return single;
/*     */   }
/*     */ 
/*     */   public class ArrayListExtension extends ArrayList
/*     */     implements JavaUtils.ConvertCache
/*     */   {
/* 713 */     private HashMap table = null;
/* 714 */     private Class arrayClass = null;
/*     */ 
/*     */     ArrayListExtension(Class arrayClass)
/*     */     {
/* 720 */       this.arrayClass = arrayClass;
/*     */ 
/* 723 */       if ((arrayClass == null) || (arrayClass.isInterface()) || (Modifier.isAbstract(arrayClass.getModifiers())))
/*     */       {
/* 727 */         arrayClass = null;
/*     */       }
/*     */     }
/*     */ 
/*     */     ArrayListExtension(Class arrayClass, int length) {
/* 732 */       super();
/* 733 */       this.arrayClass = arrayClass;
/*     */ 
/* 736 */       if ((arrayClass == null) || (arrayClass.isInterface()) || (Modifier.isAbstract(arrayClass.getModifiers())))
/*     */       {
/* 740 */         arrayClass = null;
/*     */       }
/*     */     }
/*     */ 
/*     */     public void setConvertedValue(Class cls, Object value)
/*     */     {
/* 747 */       if (this.table == null)
/* 748 */         this.table = new HashMap();
/* 749 */       this.table.put(cls, value);
/*     */     }
/*     */ 
/*     */     public Object getConvertedValue(Class cls)
/*     */     {
/* 755 */       if (this.table == null)
/* 756 */         return null;
/* 757 */       return this.table.get(cls);
/*     */     }
/*     */ 
/*     */     public Class getDestClass()
/*     */     {
/* 764 */       return this.arrayClass;
/*     */     }
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.encoding.ser.ArrayDeserializer
 * JD-Core Version:    0.6.0
 */