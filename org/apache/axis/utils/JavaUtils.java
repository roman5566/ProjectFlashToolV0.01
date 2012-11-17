/*      */ package org.apache.axis.utils;
/*      */ 
/*      */ import java.awt.Image;
/*      */ import java.beans.Introspector;
/*      */ import java.io.ByteArrayOutputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.PrintWriter;
/*      */ import java.io.StringWriter;
/*      */ import java.lang.reflect.Array;
/*      */ import java.lang.reflect.Field;
/*      */ import java.lang.reflect.Method;
/*      */ import java.text.Collator;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Calendar;
/*      */ import java.util.Collection;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.Hashtable;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Locale;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import java.util.WeakHashMap;
/*      */ import javax.activation.DataHandler;
/*      */ import javax.xml.rpc.holders.Holder;
/*      */ import javax.xml.soap.SOAPException;
/*      */ import javax.xml.transform.Source;
/*      */ import javax.xml.transform.stream.StreamSource;
/*      */ import org.apache.axis.attachments.AttachmentPart;
/*      */ import org.apache.axis.attachments.OctetStream;
/*      */ import org.apache.axis.components.image.ImageIO;
/*      */ import org.apache.axis.components.image.ImageIOFactory;
/*      */ import org.apache.axis.components.logger.LogFactory;
/*      */ import org.apache.axis.types.Day;
/*      */ import org.apache.axis.types.Duration;
/*      */ import org.apache.axis.types.Entities;
/*      */ import org.apache.axis.types.Entity;
/*      */ import org.apache.axis.types.HexBinary;
/*      */ import org.apache.axis.types.IDRef;
/*      */ import org.apache.axis.types.IDRefs;
/*      */ import org.apache.axis.types.Id;
/*      */ import org.apache.axis.types.Language;
/*      */ import org.apache.axis.types.Month;
/*      */ import org.apache.axis.types.MonthDay;
/*      */ import org.apache.axis.types.NCName;
/*      */ import org.apache.axis.types.NMToken;
/*      */ import org.apache.axis.types.NMTokens;
/*      */ import org.apache.axis.types.Name;
/*      */ import org.apache.axis.types.NegativeInteger;
/*      */ import org.apache.axis.types.NonNegativeInteger;
/*      */ import org.apache.axis.types.NonPositiveInteger;
/*      */ import org.apache.axis.types.NormalizedString;
/*      */ import org.apache.axis.types.PositiveInteger;
/*      */ import org.apache.axis.types.Time;
/*      */ import org.apache.axis.types.Token;
/*      */ import org.apache.axis.types.URI;
/*      */ import org.apache.axis.types.UnsignedByte;
/*      */ import org.apache.axis.types.UnsignedInt;
/*      */ import org.apache.axis.types.UnsignedLong;
/*      */ import org.apache.axis.types.UnsignedShort;
/*      */ import org.apache.axis.types.Year;
/*      */ import org.apache.axis.types.YearMonth;
/*      */ import org.apache.commons.logging.Log;
/*      */ 
/*      */ public class JavaUtils
/*      */ {
/*   64 */   protected static Log log = LogFactory.getLog(JavaUtils.class.getName());
/*      */   public static final char NL = '\n';
/*      */   public static final char CR = '\r';
/*   74 */   public static final String LS = System.getProperty("line.separator", new Character('\n').toString());
/*      */ 
/*  697 */   static final String[] keywords = { "abstract", "assert", "boolean", "break", "byte", "case", "catch", "char", "class", "const", "continue", "default", "do", "double", "else", "extends", "false", "final", "finally", "float", "for", "goto", "if", "implements", "import", "instanceof", "int", "interface", "long", "native", "new", "null", "package", "private", "protected", "public", "return", "short", "static", "strictfp", "super", "switch", "synchronized", "this", "throw", "throws", "transient", "true", "try", "void", "volatile", "while" };
/*      */ 
/*  713 */   static final Collator englishCollator = Collator.getInstance(Locale.ENGLISH);
/*      */   static final char keywordPrefix = '_';
/* 1051 */   private static WeakHashMap enumMap = new WeakHashMap();
/*      */ 
/* 1283 */   private static boolean checkForAttachmentSupport = true;
/* 1284 */   private static boolean attachmentSupportEnabled = false;
/*      */ 
/*      */   public static Class getWrapperClass(Class primitive)
/*      */   {
/*   80 */     if (primitive == Integer.TYPE)
/*   81 */       return Integer.class;
/*   82 */     if (primitive == Short.TYPE)
/*   83 */       return Short.class;
/*   84 */     if (primitive == Boolean.TYPE)
/*   85 */       return Boolean.class;
/*   86 */     if (primitive == Byte.TYPE)
/*   87 */       return Byte.class;
/*   88 */     if (primitive == Long.TYPE)
/*   89 */       return Long.class;
/*   90 */     if (primitive == Double.TYPE)
/*   91 */       return Double.class;
/*   92 */     if (primitive == Float.TYPE)
/*   93 */       return Float.class;
/*   94 */     if (primitive == Character.TYPE) {
/*   95 */       return Character.class;
/*      */     }
/*   97 */     return null;
/*      */   }
/*      */ 
/*      */   public static String getWrapper(String primitive)
/*      */   {
/*  102 */     if (primitive.equals("int"))
/*  103 */       return "Integer";
/*  104 */     if (primitive.equals("short"))
/*  105 */       return "Short";
/*  106 */     if (primitive.equals("boolean"))
/*  107 */       return "Boolean";
/*  108 */     if (primitive.equals("byte"))
/*  109 */       return "Byte";
/*  110 */     if (primitive.equals("long"))
/*  111 */       return "Long";
/*  112 */     if (primitive.equals("double"))
/*  113 */       return "Double";
/*  114 */     if (primitive.equals("float"))
/*  115 */       return "Float";
/*  116 */     if (primitive.equals("char")) {
/*  117 */       return "Character";
/*      */     }
/*  119 */     return null;
/*      */   }
/*      */ 
/*      */   public static Class getPrimitiveClass(Class wrapper)
/*      */   {
/*  124 */     if (wrapper == Integer.class)
/*  125 */       return Integer.TYPE;
/*  126 */     if (wrapper == Short.class)
/*  127 */       return Short.TYPE;
/*  128 */     if (wrapper == Boolean.class)
/*  129 */       return Boolean.TYPE;
/*  130 */     if (wrapper == Byte.class)
/*  131 */       return Byte.TYPE;
/*  132 */     if (wrapper == Long.class)
/*  133 */       return Long.TYPE;
/*  134 */     if (wrapper == Double.class)
/*  135 */       return Double.TYPE;
/*  136 */     if (wrapper == Float.class)
/*  137 */       return Float.TYPE;
/*  138 */     if (wrapper == Character.class) {
/*  139 */       return Character.TYPE;
/*      */     }
/*  141 */     return null;
/*      */   }
/*      */ 
/*      */   public static Class getPrimitiveClassFromName(String primitive) {
/*  145 */     if (primitive.equals("int"))
/*  146 */       return Integer.TYPE;
/*  147 */     if (primitive.equals("short"))
/*  148 */       return Short.TYPE;
/*  149 */     if (primitive.equals("boolean"))
/*  150 */       return Boolean.TYPE;
/*  151 */     if (primitive.equals("byte"))
/*  152 */       return Byte.TYPE;
/*  153 */     if (primitive.equals("long"))
/*  154 */       return Long.TYPE;
/*  155 */     if (primitive.equals("double"))
/*  156 */       return Double.TYPE;
/*  157 */     if (primitive.equals("float"))
/*  158 */       return Float.TYPE;
/*  159 */     if (primitive.equals("char")) {
/*  160 */       return Character.TYPE;
/*      */     }
/*  162 */     return null;
/*      */   }
/*      */ 
/*      */   public static boolean isBasic(Class javaType)
/*      */   {
/*  172 */     return (javaType.isPrimitive()) || (javaType == String.class) || (javaType == Boolean.class) || (javaType == Float.class) || (javaType == Double.class) || (Number.class.isAssignableFrom(javaType)) || (javaType == Day.class) || (javaType == Duration.class) || (javaType == Entities.class) || (javaType == Entity.class) || (javaType == HexBinary.class) || (javaType == Id.class) || (javaType == IDRef.class) || (javaType == IDRefs.class) || (javaType == Language.class) || (javaType == Month.class) || (javaType == MonthDay.class) || (javaType == Name.class) || (javaType == NCName.class) || (javaType == NegativeInteger.class) || (javaType == NMToken.class) || (javaType == NMTokens.class) || (javaType == NonNegativeInteger.class) || (javaType == NonPositiveInteger.class) || (javaType == NormalizedString.class) || (javaType == PositiveInteger.class) || (javaType == Time.class) || (javaType == Token.class) || (javaType == UnsignedByte.class) || (javaType == UnsignedInt.class) || (javaType == UnsignedLong.class) || (javaType == UnsignedShort.class) || (javaType == URI.class) || (javaType == Year.class) || (javaType == YearMonth.class);
/*      */   }
/*      */ 
/*      */   public static Object convert(Object arg, Class destClass)
/*      */   {
/*  237 */     if (destClass == null) {
/*  238 */       return arg;
/*      */     }
/*      */ 
/*  241 */     Class argHeldType = null;
/*  242 */     if (arg != null) {
/*  243 */       argHeldType = getHolderValueType(arg.getClass());
/*      */     }
/*      */ 
/*  246 */     if ((arg != null) && (argHeldType == null) && (destClass.isAssignableFrom(arg.getClass()))) {
/*  247 */       return arg;
/*      */     }
/*      */ 
/*  250 */     if (log.isDebugEnabled()) {
/*  251 */       String clsName = "null";
/*  252 */       if (arg != null) clsName = arg.getClass().getName();
/*  253 */       log.debug(Messages.getMessage("convert00", clsName, destClass.getName()));
/*      */     }
/*      */ 
/*  257 */     Object destValue = null;
/*  258 */     if ((arg instanceof ConvertCache)) {
/*  259 */       destValue = ((ConvertCache)arg).getConvertedValue(destClass);
/*  260 */       if (destValue != null) {
/*  261 */         return destValue;
/*      */       }
/*      */     }
/*      */ 
/*  265 */     Class destHeldType = getHolderValueType(destClass);
/*      */ 
/*  268 */     if (((arg instanceof HexBinary)) && (destClass == new byte[0].getClass()))
/*      */     {
/*  270 */       return ((HexBinary)arg).getBytes();
/*  271 */     }if (((arg instanceof byte[])) && (destClass == HexBinary.class))
/*      */     {
/*  273 */       return new HexBinary((byte[])arg);
/*      */     }
/*      */ 
/*  277 */     if (((arg instanceof Calendar)) && (destClass == java.util.Date.class)) {
/*  278 */       return ((Calendar)arg).getTime();
/*      */     }
/*  280 */     if (((arg instanceof java.util.Date)) && (destClass == Calendar.class)) {
/*  281 */       Calendar calendar = Calendar.getInstance();
/*  282 */       calendar.setTime((java.util.Date)arg);
/*  283 */       return calendar;
/*      */     }
/*      */ 
/*  287 */     if (((arg instanceof Calendar)) && (destClass == java.sql.Date.class)) {
/*  288 */       return new java.sql.Date(((Calendar)arg).getTime().getTime());
/*      */     }
/*      */ 
/*  292 */     if (((arg instanceof HashMap)) && (destClass == Hashtable.class)) {
/*  293 */       return new Hashtable((HashMap)arg);
/*      */     }
/*      */ 
/*  297 */     if ((isAttachmentSupported()) && (((arg instanceof InputStream)) || ((arg instanceof AttachmentPart)) || ((arg instanceof DataHandler)))) {
/*      */       try
/*      */       {
/*  300 */         String destName = destClass.getName();
/*  301 */         if ((destClass == String.class) || (destClass == OctetStream.class) || (destClass == new byte[0].getClass()) || (destClass == Image.class) || (destClass == Source.class) || (destClass == DataHandler.class) || (destName.equals("javax.mail.internet.MimeMultipart")))
/*      */         {
/*  308 */           DataHandler handler = null;
/*  309 */           if ((arg instanceof AttachmentPart)) {
/*  310 */             handler = ((AttachmentPart)arg).getDataHandler();
/*      */           }
/*  312 */           else if ((arg instanceof DataHandler)) {
/*  313 */             handler = (DataHandler)arg;
/*      */           }
/*  315 */           if (destClass == Image.class)
/*      */           {
/*  320 */             InputStream is = handler.getInputStream();
/*  321 */             if (is.available() == 0) {
/*  322 */               return null;
/*      */             }
/*      */ 
/*  325 */             ImageIO imageIO = ImageIOFactory.getImageIO();
/*  326 */             if (imageIO != null) {
/*  327 */               return getImageFromStream(is);
/*      */             }
/*      */ 
/*  330 */             log.info(Messages.getMessage("needImageIO"));
/*  331 */             return arg;
/*      */           }
/*      */ 
/*  335 */           if (destClass == Source.class)
/*      */           {
/*  339 */             return new StreamSource(handler.getInputStream());
/*      */           }
/*  341 */           if ((destClass == OctetStream.class) || (destClass == new byte[0].getClass())) {
/*  342 */             InputStream in = null;
/*  343 */             if ((arg instanceof InputStream))
/*  344 */               in = (InputStream)arg;
/*      */             else {
/*  346 */               in = handler.getInputStream();
/*      */             }
/*  348 */             ByteArrayOutputStream baos = new ByteArrayOutputStream();
/*  349 */             int byte1 = -1;
/*  350 */             while ((byte1 = in.read()) != -1)
/*  351 */               baos.write(byte1);
/*  352 */             return new OctetStream(baos.toByteArray());
/*      */           }
/*  354 */           if (destClass == DataHandler.class) {
/*  355 */             return handler;
/*      */           }
/*      */ 
/*  358 */           return handler.getContent();
/*      */         }
/*      */ 
/*      */       }
/*      */       catch (IOException ioe)
/*      */       {
/*      */       }
/*      */       catch (SOAPException se)
/*      */       {
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  371 */     if ((arg != null) && (destClass.isArray()) && (!destClass.getComponentType().equals(Object.class)) && (destClass.getComponentType().isAssignableFrom(arg.getClass())))
/*      */     {
/*  375 */       Object array = Array.newInstance(destClass.getComponentType(), 1);
/*      */ 
/*  377 */       Array.set(array, 0, arg);
/*  378 */       return array;
/*      */     }
/*      */ 
/*  382 */     if ((arg != null) && (destClass.isArray())) {
/*  383 */       Object newArg = ArrayUtil.convertObjectToArray(arg, destClass);
/*  384 */       if ((newArg == null) || ((newArg != ArrayUtil.NON_CONVERTABLE) && (newArg != arg)))
/*      */       {
/*  386 */         return newArg;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  391 */     if ((arg != null) && (arg.getClass().isArray())) {
/*  392 */       Object newArg = ArrayUtil.convertArrayToObject(arg, destClass);
/*  393 */       if (newArg != null) {
/*  394 */         return newArg;
/*      */       }
/*      */     }
/*      */ 
/*  398 */     if ((!(arg instanceof Collection)) && ((arg == null) || (!arg.getClass().isArray())) && (((destHeldType == null) && (argHeldType == null)) || ((destHeldType != null) && (argHeldType != null))))
/*      */     {
/*  402 */       return arg;
/*      */     }
/*      */ 
/*  406 */     if (destHeldType != null)
/*      */     {
/*  408 */       Object newArg = convert(arg, destHeldType);
/*  409 */       Object argHolder = null;
/*      */       try {
/*  411 */         argHolder = destClass.newInstance();
/*  412 */         setHolderValue(argHolder, newArg);
/*  413 */         return argHolder;
/*      */       } catch (Exception e) {
/*  415 */         return arg;
/*      */       }
/*      */     }
/*  417 */     if (argHeldType != null) {
/*      */       try
/*      */       {
/*  420 */         Object newArg = getHolderValue(arg);
/*  421 */         return convert(newArg, destClass);
/*      */       } catch (HolderException e) {
/*  423 */         return arg;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  430 */     if (((arg instanceof ConvertCache)) && (((ConvertCache)arg).getDestClass() != destClass))
/*      */     {
/*  432 */       Class hintClass = ((ConvertCache)arg).getDestClass();
/*  433 */       if ((hintClass != null) && (hintClass.isArray()) && (destClass.isArray()) && (destClass.isAssignableFrom(hintClass)))
/*      */       {
/*  437 */         destClass = hintClass;
/*  438 */         destValue = ((ConvertCache)arg).getConvertedValue(destClass);
/*  439 */         if (destValue != null) {
/*  440 */           return destValue;
/*      */         }
/*      */       }
/*      */     }
/*  444 */     if (arg == null) {
/*  445 */       return arg;
/*      */     }
/*      */ 
/*  449 */     int length = 0;
/*  450 */     if (arg.getClass().isArray())
/*  451 */       length = Array.getLength(arg);
/*      */     else {
/*  453 */       length = ((Collection)arg).size();
/*      */     }
/*  455 */     if (destClass.isArray()) {
/*  456 */       if (destClass.getComponentType().isPrimitive())
/*      */       {
/*  458 */         Object array = Array.newInstance(destClass.getComponentType(), length);
/*      */ 
/*  461 */         if (arg.getClass().isArray()) {
/*  462 */           for (int i = 0; i < length; i++)
/*  463 */             Array.set(array, i, Array.get(arg, i));
/*      */         }
/*      */         else {
/*  466 */           int idx = 0;
/*  467 */           Iterator i = ((Collection)arg).iterator();
/*  468 */           while (i.hasNext()) {
/*  469 */             Array.set(array, idx++, i.next());
/*      */           }
/*      */         }
/*  472 */         destValue = array;
/*      */       }
/*      */       else
/*      */       {
/*      */         try {
/*  477 */           array = (Object[])Array.newInstance(destClass.getComponentType(), length);
/*      */         }
/*      */         catch (Exception e)
/*      */         {
/*      */           Object[] array;
/*  480 */           return arg;
/*      */         }
/*      */         Object[] array;
/*  484 */         if (arg.getClass().isArray()) {
/*  485 */           for (int i = 0; i < length; i++)
/*  486 */             array[i] = convert(Array.get(arg, i), destClass.getComponentType());
/*      */         }
/*      */         else
/*      */         {
/*  490 */           int idx = 0;
/*  491 */           Iterator i = ((Collection)arg).iterator();
/*  492 */           while (i.hasNext()) {
/*  493 */             array[(idx++)] = convert(i.next(), destClass.getComponentType());
/*      */           }
/*      */         }
/*      */ 
/*  497 */         destValue = array;
/*      */       }
/*      */     }
/*  500 */     else if (Collection.class.isAssignableFrom(destClass)) {
/*  501 */       Collection newList = null;
/*      */       try
/*      */       {
/*  505 */         if ((destClass == Collection.class) || (destClass == List.class))
/*  506 */           newList = new ArrayList();
/*  507 */         else if (destClass == Set.class)
/*  508 */           newList = new HashSet();
/*      */         else
/*  510 */           newList = (Collection)destClass.newInstance();
/*      */       }
/*      */       catch (Exception e)
/*      */       {
/*  514 */         return arg;
/*      */       }
/*      */ 
/*  517 */       if (arg.getClass().isArray()) {
/*  518 */         for (int j = 0; j < length; j++)
/*  519 */           newList.add(Array.get(arg, j));
/*      */       }
/*      */       else {
/*  522 */         Iterator j = ((Collection)arg).iterator();
/*  523 */         while (j.hasNext()) {
/*  524 */           newList.add(j.next());
/*      */         }
/*      */       }
/*  527 */       destValue = newList;
/*      */     }
/*      */     else {
/*  530 */       destValue = arg;
/*      */     }
/*      */ 
/*  534 */     if ((arg instanceof ConvertCache)) {
/*  535 */       ((ConvertCache)arg).setConvertedValue(destClass, destValue);
/*      */     }
/*  537 */     return destValue;
/*      */   }
/*      */ 
/*      */   public static boolean isConvertable(Object obj, Class dest)
/*      */   {
/*  542 */     return isConvertable(obj, dest, false);
/*      */   }
/*      */ 
/*      */   public static boolean isConvertable(Object obj, Class dest, boolean isEncoded)
/*      */   {
/*  547 */     Class src = null;
/*      */ 
/*  549 */     if (obj != null) {
/*  550 */       if ((obj instanceof Class))
/*  551 */         src = (Class)obj;
/*      */       else {
/*  553 */         src = obj.getClass();
/*      */       }
/*      */     }
/*  556 */     else if (!dest.isPrimitive()) {
/*  557 */       return true;
/*      */     }
/*      */ 
/*  560 */     if (dest == null) {
/*  561 */       return false;
/*      */     }
/*  563 */     if (src != null)
/*      */     {
/*  565 */       if (dest.isAssignableFrom(src)) {
/*  566 */         return true;
/*      */       }
/*      */ 
/*  569 */       if ((Map.class.isAssignableFrom(dest)) && (Map.class.isAssignableFrom(src)))
/*      */       {
/*  571 */         return true;
/*      */       }
/*      */ 
/*  575 */       if (getWrapperClass(src) == dest)
/*  576 */         return true;
/*  577 */       if (getWrapperClass(dest) == src) {
/*  578 */         return true;
/*      */       }
/*      */ 
/*  581 */       if (((Collection.class.isAssignableFrom(src)) || (src.isArray())) && ((Collection.class.isAssignableFrom(dest)) || (dest.isArray())) && ((src.getComponentType() == Object.class) || (src.getComponentType() == null) || (dest.getComponentType() == Object.class) || (dest.getComponentType() == null) || (isConvertable(src.getComponentType(), dest.getComponentType()))))
/*      */       {
/*  588 */         return true;
/*      */       }
/*      */ 
/*  592 */       if ((!isEncoded) && (dest.isArray()) && (dest.getComponentType().isAssignableFrom(src)))
/*      */       {
/*  595 */         return true;
/*      */       }
/*  597 */       if (((src == HexBinary.class) && (dest == new byte[0].getClass())) || ((src == new byte[0].getClass()) && (dest == HexBinary.class)))
/*      */       {
/*  599 */         return true;
/*      */       }
/*      */ 
/*  602 */       if ((Calendar.class.isAssignableFrom(src)) && (dest == java.util.Date.class)) {
/*  603 */         return true;
/*      */       }
/*      */ 
/*  606 */       if ((java.util.Date.class.isAssignableFrom(src)) && (dest == Calendar.class)) {
/*  607 */         return true;
/*      */       }
/*      */ 
/*  610 */       if ((Calendar.class.isAssignableFrom(src)) && (dest == java.sql.Date.class)) {
/*  611 */         return true;
/*      */       }
/*      */     }
/*  614 */     Class destHeld = getHolderValueType(dest);
/*      */ 
/*  616 */     if (src == null) {
/*  617 */       return destHeld != null;
/*      */     }
/*  619 */     if ((destHeld != null) && (
/*  620 */       (destHeld.isAssignableFrom(src)) || (isConvertable(src, destHeld)))) {
/*  621 */       return true;
/*      */     }
/*      */ 
/*  625 */     Class srcHeld = getHolderValueType(src);
/*  626 */     if ((srcHeld != null) && (
/*  627 */       (dest.isAssignableFrom(srcHeld)) || (isConvertable(srcHeld, dest)))) {
/*  628 */       return true;
/*      */     }
/*      */ 
/*  633 */     if (dest.getName().equals("javax.activation.DataHandler")) {
/*  634 */       String name = src.getName();
/*  635 */       if ((src == String.class) || (src == Image.class) || (src == OctetStream.class) || (name.equals("javax.mail.internet.MimeMultipart")) || (name.equals("javax.xml.transform.Source")))
/*      */       {
/*  640 */         return true;
/*      */       }
/*      */     }
/*  643 */     if (src.getName().equals("javax.activation.DataHandler")) {
/*  644 */       if (dest == new byte[0].getClass())
/*  645 */         return true;
/*  646 */       if ((dest.isArray()) && (dest.getComponentType() == new byte[0].getClass())) {
/*  647 */         return true;
/*      */       }
/*      */     }
/*  650 */     if (dest.getName().equals("javax.activation.DataHandler")) {
/*  651 */       if (src == new Object[0].getClass())
/*  652 */         return true;
/*  653 */       if ((src.isArray()) && (src.getComponentType() == new Object[0].getClass())) {
/*  654 */         return true;
/*      */       }
/*      */     }
/*  657 */     if (((obj instanceof InputStream)) && 
/*  658 */       (dest == OctetStream.class)) {
/*  659 */       return true;
/*      */     }
/*      */ 
/*  662 */     if (src.isPrimitive()) {
/*  663 */       return isConvertable(getWrapperClass(src), dest);
/*      */     }
/*      */ 
/*  667 */     if ((dest.isArray()) && 
/*  668 */       (ArrayUtil.isConvertable(src, dest) == true)) {
/*  669 */       return true;
/*      */     }
/*      */ 
/*  675 */     return (src.isArray()) && 
/*  674 */       (ArrayUtil.isConvertable(src, dest) == true);
/*      */   }
/*      */ 
/*      */   public static Image getImageFromStream(InputStream is)
/*      */   {
/*      */     try
/*      */     {
/*  683 */       return ImageIOFactory.getImageIO().loadImage(is);
/*      */     } catch (Throwable t) {
/*      */     }
/*  686 */     return null;
/*      */   }
/*      */ 
/*      */   public static boolean isJavaId(String id)
/*      */   {
/*  725 */     if ((id == null) || (id.equals("")) || (isJavaKeyword(id)))
/*  726 */       return false;
/*  727 */     if (!Character.isJavaIdentifierStart(id.charAt(0)))
/*  728 */       return false;
/*  729 */     for (int i = 1; i < id.length(); i++)
/*  730 */       if (!Character.isJavaIdentifierPart(id.charAt(i)))
/*  731 */         return false;
/*  732 */     return true;
/*      */   }
/*      */ 
/*      */   public static boolean isJavaKeyword(String keyword)
/*      */   {
/*  740 */     return Arrays.binarySearch(keywords, keyword, englishCollator) >= 0;
/*      */   }
/*      */ 
/*      */   public static String makeNonJavaKeyword(String keyword)
/*      */   {
/*  748 */     return '_' + keyword;
/*      */   }
/*      */ 
/*      */   public static String getLoadableClassName(String text)
/*      */   {
/*  756 */     if ((text == null) || (text.indexOf("[") < 0) || (text.charAt(0) == '['))
/*      */     {
/*  759 */       return text;
/*  760 */     }String className = text.substring(0, text.indexOf("["));
/*  761 */     if (className.equals("byte"))
/*  762 */       className = "B";
/*  763 */     else if (className.equals("char"))
/*  764 */       className = "C";
/*  765 */     else if (className.equals("double"))
/*  766 */       className = "D";
/*  767 */     else if (className.equals("float"))
/*  768 */       className = "F";
/*  769 */     else if (className.equals("int"))
/*  770 */       className = "I";
/*  771 */     else if (className.equals("long"))
/*  772 */       className = "J";
/*  773 */     else if (className.equals("short"))
/*  774 */       className = "S";
/*  775 */     else if (className.equals("boolean"))
/*  776 */       className = "Z";
/*      */     else
/*  778 */       className = "L" + className + ";";
/*  779 */     int i = text.indexOf("]");
/*  780 */     while (i > 0) {
/*  781 */       className = "[" + className;
/*  782 */       i = text.indexOf("]", i + 1);
/*      */     }
/*  784 */     return className;
/*      */   }
/*      */ 
/*      */   public static String getTextClassName(String text)
/*      */   {
/*  792 */     if ((text == null) || (text.indexOf("[") != 0))
/*      */     {
/*  794 */       return text;
/*  795 */     }String className = "";
/*  796 */     int index = 0;
/*      */ 
/*  798 */     while ((index < text.length()) && (text.charAt(index) == '[')) {
/*  799 */       index++;
/*  800 */       className = className + "[]";
/*      */     }
/*  802 */     if (index < text.length()) {
/*  803 */       if (text.charAt(index) == 'B')
/*  804 */         className = "byte" + className;
/*  805 */       else if (text.charAt(index) == 'C')
/*  806 */         className = "char" + className;
/*  807 */       else if (text.charAt(index) == 'D')
/*  808 */         className = "double" + className;
/*  809 */       else if (text.charAt(index) == 'F')
/*  810 */         className = "float" + className;
/*  811 */       else if (text.charAt(index) == 'I')
/*  812 */         className = "int" + className;
/*  813 */       else if (text.charAt(index) == 'J')
/*  814 */         className = "long" + className;
/*  815 */       else if (text.charAt(index) == 'S')
/*  816 */         className = "short" + className;
/*  817 */       else if (text.charAt(index) == 'Z')
/*  818 */         className = "boolean" + className;
/*      */       else {
/*  820 */         className = text.substring(index + 1, text.indexOf(";")) + className;
/*      */       }
/*      */     }
/*  823 */     return className;
/*      */   }
/*      */ 
/*      */   public static String xmlNameToJava(String name)
/*      */   {
/*  837 */     if ((name == null) || (name.equals(""))) {
/*  838 */       return name;
/*      */     }
/*  840 */     char[] nameArray = name.toCharArray();
/*  841 */     int nameLen = name.length();
/*  842 */     StringBuffer result = new StringBuffer(nameLen);
/*  843 */     boolean wordStart = false;
/*      */ 
/*  846 */     int i = 0;
/*      */ 
/*  848 */     while ((i < nameLen) && ((isPunctuation(nameArray[i])) || (!Character.isJavaIdentifierStart(nameArray[i]))))
/*      */     {
/*  850 */       i++;
/*      */     }
/*  852 */     if (i < nameLen)
/*      */     {
/*  856 */       result.append(nameArray[i]);
/*      */ 
/*  858 */       wordStart = (!Character.isLetter(nameArray[i])) && (nameArray[i] != "_".charAt(0));
/*      */     }
/*  863 */     else if (Character.isJavaIdentifierPart(nameArray[0])) {
/*  864 */       result.append("_" + nameArray[0]);
/*      */     }
/*      */     else
/*      */     {
/*  870 */       result.append("_" + nameArray.length);
/*      */     }
/*      */ 
/*  879 */     for (i++; i < nameLen; i++) {
/*  880 */       char c = nameArray[i];
/*      */ 
/*  884 */       if ((isPunctuation(c)) || (!Character.isJavaIdentifierPart(c))) {
/*  885 */         wordStart = true;
/*      */       }
/*      */       else {
/*  888 */         if ((wordStart) && (Character.isLowerCase(c))) {
/*  889 */           result.append(Character.toUpperCase(c));
/*      */         }
/*      */         else {
/*  892 */           result.append(c);
/*      */         }
/*      */ 
/*  898 */         wordStart = (!Character.isLetter(c)) && (c != "_".charAt(0));
/*      */       }
/*      */     }
/*      */ 
/*  902 */     String newName = result.toString();
/*      */ 
/*  906 */     if (Character.isUpperCase(newName.charAt(0))) {
/*  907 */       newName = Introspector.decapitalize(newName);
/*      */     }
/*      */ 
/*  910 */     if (isJavaKeyword(newName)) {
/*  911 */       newName = makeNonJavaKeyword(newName);
/*      */     }
/*  913 */     return newName;
/*      */   }
/*      */ 
/*      */   private static boolean isPunctuation(char c)
/*      */   {
/*  921 */     return ('-' == c) || ('.' == c) || (':' == c) || ('·' == c) || ('·' == c) || ('۝' == c) || ('۞' == c);
/*      */   }
/*      */ 
/*      */   public static final String replace(String name, String oldT, String newT)
/*      */   {
/*  943 */     if (name == null) return "";
/*      */ 
/*  947 */     StringBuffer sb = new StringBuffer(name.length() * 2);
/*      */ 
/*  949 */     int len = oldT.length();
/*      */     try {
/*  951 */       int start = 0;
/*  952 */       int i = name.indexOf(oldT, start);
/*      */ 
/*  954 */       while (i >= 0) {
/*  955 */         sb.append(name.substring(start, i));
/*  956 */         sb.append(newT);
/*  957 */         start = i + len;
/*  958 */         i = name.indexOf(oldT, start);
/*      */       }
/*  960 */       if (start < name.length())
/*  961 */         sb.append(name.substring(start));
/*      */     }
/*      */     catch (NullPointerException e) {
/*      */     }
/*  965 */     return new String(sb);
/*      */   }
/*      */ 
/*      */   public static Class getHolderValueType(Class type)
/*      */   {
/*  975 */     if (type != null) {
/*  976 */       Class[] intf = type.getInterfaces();
/*  977 */       boolean isHolder = false;
/*  978 */       for (int i = 0; (i < intf.length) && (!isHolder); i++) {
/*  979 */         if (intf[i] == Holder.class) {
/*  980 */           isHolder = true;
/*      */         }
/*      */       }
/*  983 */       if (!isHolder) {
/*  984 */         return null;
/*      */       }
/*      */       Field field;
/*      */       try
/*      */       {
/*  990 */         field = type.getField("value");
/*      */       }
/*      */       catch (Exception e)
/*      */       {
/*      */         Field field;
/*  992 */         field = null;
/*      */       }
/*  994 */       if (field != null) {
/*  995 */         return field.getType();
/*      */       }
/*      */     }
/*  998 */     return null;
/*      */   }
/*      */ 
/*      */   public static Object getHolderValue(Object holder)
/*      */     throws JavaUtils.HolderException
/*      */   {
/* 1007 */     if (!(holder instanceof Holder))
/* 1008 */       throw new HolderException(Messages.getMessage("badHolder00"));
/*      */     try
/*      */     {
/* 1011 */       Field valueField = holder.getClass().getField("value");
/* 1012 */       return valueField.get(holder); } catch (Exception e) {
/*      */     }
/* 1014 */     throw new HolderException(Messages.getMessage("exception01", e.getMessage()));
/*      */   }
/*      */ 
/*      */   public static void setHolderValue(Object holder, Object value)
/*      */     throws JavaUtils.HolderException
/*      */   {
/* 1024 */     if (!(holder instanceof Holder))
/* 1025 */       throw new HolderException(Messages.getMessage("badHolder00"));
/*      */     try
/*      */     {
/* 1028 */       Field valueField = holder.getClass().getField("value");
/* 1029 */       if (valueField.getType().isPrimitive()) {
/* 1030 */         if (value != null)
/*      */         {
/* 1033 */           valueField.set(holder, value);
/*      */         }
/*      */       } else valueField.set(holder, value); 
/*      */     }
/*      */     catch (Exception e)
/*      */     {
/* 1038 */       throw new HolderException(Messages.getMessage("exception01", e.getMessage()));
/*      */     }
/*      */   }
/*      */ 
/*      */   public static boolean isEnumClass(Class cls)
/*      */   {
/* 1061 */     Boolean b = (Boolean)enumMap.get(cls);
/* 1062 */     if (b == null) {
/* 1063 */       b = isEnumClassSub(cls) ? Boolean.TRUE : Boolean.FALSE;
/* 1064 */       synchronized (enumMap) {
/* 1065 */         enumMap.put(cls, b);
/*      */       }
/*      */     }
/* 1068 */     return b.booleanValue();
/*      */   }
/*      */ 
/*      */   private static boolean isEnumClassSub(Class cls) {
/*      */     try {
/* 1073 */       Method[] methods = cls.getMethods();
/* 1074 */       Method getValueMethod = null;
/* 1075 */       Method fromValueMethod = null;
/* 1076 */       Method setValueMethod = null; Method fromStringMethod = null;
/*      */ 
/* 1080 */       for (int i = 0; i < methods.length; i++) {
/* 1081 */         String name = methods[i].getName();
/*      */ 
/* 1083 */         if ((name.equals("getValue")) && (methods[i].getParameterTypes().length == 0))
/*      */         {
/* 1085 */           getValueMethod = methods[i];
/* 1086 */         } else if (name.equals("fromString")) {
/* 1087 */           Object[] params = methods[i].getParameterTypes();
/* 1088 */           if ((params.length == 1) && (params[0] == String.class))
/*      */           {
/* 1090 */             fromStringMethod = methods[i];
/*      */           }
/* 1092 */         } else if ((name.equals("fromValue")) && (methods[i].getParameterTypes().length == 1))
/*      */         {
/* 1094 */           fromValueMethod = methods[i]; } else {
/* 1095 */           if ((!name.equals("setValue")) || (methods[i].getParameterTypes().length != 1))
/*      */             continue;
/* 1097 */           setValueMethod = methods[i];
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1104 */       if ((null != getValueMethod) && (null != fromStringMethod))
/*      */       {
/* 1109 */         return (null == setValueMethod) || (setValueMethod.getParameterTypes().length != 1) || (getValueMethod.getReturnType() != setValueMethod.getParameterTypes()[0]);
/*      */       }
/*      */ 
/* 1114 */       return false;
/*      */     } catch (SecurityException e) {
/*      */     }
/* 1117 */     return false;
/*      */   }
/*      */ 
/*      */   public static String stackToString(Throwable e)
/*      */   {
/* 1122 */     StringWriter sw = new StringWriter(1024);
/* 1123 */     PrintWriter pw = new PrintWriter(sw);
/* 1124 */     e.printStackTrace(pw);
/* 1125 */     pw.close();
/* 1126 */     return sw.toString();
/*      */   }
/*      */ 
/*      */   public static final boolean isTrue(String value)
/*      */   {
/* 1137 */     return !isFalseExplicitly(value);
/*      */   }
/*      */ 
/*      */   public static final boolean isTrueExplicitly(String value)
/*      */   {
/* 1145 */     return (value != null) && ((value.equalsIgnoreCase("true")) || (value.equals("1")) || (value.equalsIgnoreCase("yes")));
/*      */   }
/*      */ 
/*      */   public static final boolean isTrueExplicitly(Object value, boolean defaultVal)
/*      */   {
/* 1160 */     if (value == null) return defaultVal;
/* 1161 */     if ((value instanceof Boolean)) {
/* 1162 */       return ((Boolean)value).booleanValue();
/*      */     }
/* 1164 */     if ((value instanceof Integer)) {
/* 1165 */       return ((Integer)value).intValue() != 0;
/*      */     }
/* 1167 */     if ((value instanceof String)) {
/* 1168 */       return isTrueExplicitly((String)value);
/*      */     }
/* 1170 */     return true;
/*      */   }
/*      */ 
/*      */   public static final boolean isTrueExplicitly(Object value) {
/* 1174 */     return isTrueExplicitly(value, false);
/*      */   }
/*      */ 
/*      */   public static final boolean isTrue(Object value, boolean defaultVal)
/*      */   {
/* 1186 */     return !isFalseExplicitly(value, !defaultVal);
/*      */   }
/*      */ 
/*      */   public static final boolean isTrue(Object value) {
/* 1190 */     return isTrue(value, false);
/*      */   }
/*      */ 
/*      */   public static final boolean isFalse(String value)
/*      */   {
/* 1201 */     return isFalseExplicitly(value);
/*      */   }
/*      */ 
/*      */   public static final boolean isFalseExplicitly(String value)
/*      */   {
/* 1209 */     return (value == null) || (value.equalsIgnoreCase("false")) || (value.equals("0")) || (value.equalsIgnoreCase("no"));
/*      */   }
/*      */ 
/*      */   public static final boolean isFalseExplicitly(Object value, boolean defaultVal)
/*      */   {
/* 1224 */     if (value == null) return defaultVal;
/* 1225 */     if ((value instanceof Boolean)) {
/* 1226 */       return !((Boolean)value).booleanValue();
/*      */     }
/* 1228 */     if ((value instanceof Integer)) {
/* 1229 */       return ((Integer)value).intValue() == 0;
/*      */     }
/* 1231 */     if ((value instanceof String)) {
/* 1232 */       return isFalseExplicitly((String)value);
/*      */     }
/* 1234 */     return false;
/*      */   }
/*      */ 
/*      */   public static final boolean isFalseExplicitly(Object value) {
/* 1238 */     return isFalseExplicitly(value, true);
/*      */   }
/*      */ 
/*      */   public static final boolean isFalse(Object value, boolean defaultVal)
/*      */   {
/* 1250 */     return isFalseExplicitly(value, defaultVal);
/*      */   }
/*      */ 
/*      */   public static final boolean isFalse(Object value) {
/* 1254 */     return isFalse(value, true);
/*      */   }
/*      */ 
/*      */   public static String mimeToJava(String mime)
/*      */   {
/* 1261 */     if (("image/gif".equals(mime)) || ("image/jpeg".equals(mime))) {
/* 1262 */       return "java.awt.Image";
/*      */     }
/* 1264 */     if ("text/plain".equals(mime)) {
/* 1265 */       return "java.lang.String";
/*      */     }
/* 1267 */     if (("text/xml".equals(mime)) || ("application/xml".equals(mime))) {
/* 1268 */       return "javax.xml.transform.Source";
/*      */     }
/* 1270 */     if (("application/octet-stream".equals(mime)) || ("application/octetstream".equals(mime)))
/*      */     {
/* 1272 */       return "org.apache.axis.attachments.OctetStream";
/*      */     }
/* 1274 */     if ((mime != null) && (mime.startsWith("multipart/"))) {
/* 1275 */       return "javax.mail.internet.MimeMultipart";
/*      */     }
/*      */ 
/* 1278 */     return "javax.activation.DataHandler";
/*      */   }
/*      */ 
/*      */   public static synchronized boolean isAttachmentSupported()
/*      */   {
/* 1293 */     if (checkForAttachmentSupport)
/*      */     {
/* 1295 */       checkForAttachmentSupport = false;
/*      */       try
/*      */       {
/* 1300 */         ClassUtils.forName("javax.activation.DataHandler");
/* 1301 */         ClassUtils.forName("javax.mail.internet.MimeMultipart");
/* 1302 */         attachmentSupportEnabled = true;
/*      */       } catch (Throwable t) {
/*      */       }
/* 1305 */       log.debug(Messages.getMessage("attachEnabled") + "  " + attachmentSupportEnabled);
/*      */ 
/* 1307 */       if (!attachmentSupportEnabled) {
/* 1308 */         log.warn(Messages.getMessage("attachDisabled"));
/*      */       }
/*      */     }
/*      */ 
/* 1312 */     return attachmentSupportEnabled;
/*      */   }
/*      */ 
/*      */   public static String getUniqueValue(Collection values, String initValue)
/*      */   {
/* 1322 */     if (!values.contains(initValue)) {
/* 1323 */       return initValue;
/*      */     }
/*      */ 
/* 1327 */     StringBuffer unqVal = new StringBuffer(initValue);
/* 1328 */     int beg = unqVal.length();
/* 1329 */     while (Character.isDigit(unqVal.charAt(beg - 1))) {
/* 1330 */       beg--;
/*      */     }
/* 1332 */     if (beg == unqVal.length())
/* 1333 */       unqVal.append('1');
/*      */     int end;
/* 1335 */     int cur = end = unqVal.length() - 1;
/*      */ 
/* 1337 */     while (values.contains(unqVal.toString()))
/*      */     {
/* 1339 */       if (unqVal.charAt(cur) < '9') {
/* 1340 */         unqVal.setCharAt(cur, (char)(unqVal.charAt(cur) + '\001')); continue;
/*      */       }
/*      */ 
/* 1345 */       while (cur-- > beg) {
/* 1346 */         if (unqVal.charAt(cur) < '9') {
/* 1347 */           unqVal.setCharAt(cur, (char)(unqVal.charAt(cur) + '\001'));
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1354 */       if (cur < beg) {
/* 1355 */         cur++; unqVal.insert(cur, '1'); end++;
/*      */       }
/* 1357 */       while (cur < end) {
/* 1358 */         cur++; unqVal.setCharAt(cur, '0');
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1365 */     return unqVal.toString();
/*      */   }
/*      */ 
/*      */   public static class HolderException extends Exception
/*      */   {
/*      */     public HolderException(String msg)
/*      */     {
/* 1043 */       super();
/*      */     }
/*      */   }
/*      */ 
/*      */   public static abstract interface ConvertCache
/*      */   {
/*      */     public abstract void setConvertedValue(Class paramClass, Object paramObject);
/*      */ 
/*      */     public abstract Object getConvertedValue(Class paramClass);
/*      */ 
/*      */     public abstract Class getDestClass();
/*      */   }
/*      */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.utils.JavaUtils
 * JD-Core Version:    0.6.0
 */