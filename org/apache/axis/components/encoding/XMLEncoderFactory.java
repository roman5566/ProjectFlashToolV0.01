/*     */ package org.apache.axis.components.encoding;
/*     */ 
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import org.apache.axis.components.logger.LogFactory;
/*     */ import org.apache.axis.utils.JavaUtils;
/*     */ import org.apache.axis.utils.Messages;
/*     */ import org.apache.commons.discovery.ResourceNameIterator;
/*     */ import org.apache.commons.discovery.resource.ClassLoaders;
/*     */ import org.apache.commons.discovery.resource.names.DiscoverServiceNames;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public class XMLEncoderFactory
/*     */ {
/*  37 */   protected static Log log = LogFactory.getLog(XMLEncoderFactory.class.getName());
/*     */   public static final String ENCODING_UTF_8 = "UTF-8";
/*     */   public static final String ENCODING_UTF_16 = "UTF-16";
/*     */   public static final String DEFAULT_ENCODING = "UTF-8";
/*  43 */   private static Map encoderMap = new HashMap();
/*     */   private static final String PLUGABLE_PROVIDER_FILENAME = "org.apache.axis.components.encoding.XMLEncoder";
/*     */ 
/*     */   public static XMLEncoder getDefaultEncoder()
/*     */   {
/*     */     try
/*     */     {
/*  65 */       return getEncoder("UTF-8");
/*     */     } catch (UnsupportedEncodingException e) {
/*     */     }
/*  68 */     throw new IllegalStateException(Messages.getMessage("unsupportedDefaultEncoding00", "UTF-8"));
/*     */   }
/*     */ 
/*     */   public static XMLEncoder getEncoder(String encoding)
/*     */     throws UnsupportedEncodingException
/*     */   {
/*  79 */     XMLEncoder encoder = (XMLEncoder)encoderMap.get(encoding);
/*  80 */     if (encoder == null) {
/*  81 */       encoder = new DefaultXMLEncoder(encoding);
/*  82 */       encoderMap.put(encoding, encoder);
/*     */     }
/*  84 */     return encoder;
/*     */   }
/*     */ 
/*     */   private static void loadPluggableEncoders()
/*     */   {
/* 104 */     ClassLoader clzLoader = XMLEncoder.class.getClassLoader();
/* 105 */     ClassLoaders loaders = new ClassLoaders();
/* 106 */     loaders.put(clzLoader);
/* 107 */     DiscoverServiceNames dsn = new DiscoverServiceNames(loaders);
/* 108 */     ResourceNameIterator iter = dsn.findResourceNames("org.apache.axis.components.encoding.XMLEncoder");
/* 109 */     while (iter.hasNext()) {
/* 110 */       String className = iter.nextResourceName();
/*     */       try {
/* 112 */         Object o = Class.forName(className).newInstance();
/* 113 */         if ((o instanceof XMLEncoder)) {
/* 114 */           XMLEncoder encoder = (XMLEncoder)o;
/* 115 */           encoderMap.put(encoder.getEncoding(), encoder);
/* 116 */           encoderMap.put(encoder.getEncoding().toLowerCase(), encoder);
/*     */         }
/*     */       } catch (Exception e) {
/* 119 */         String msg = e + JavaUtils.LS + JavaUtils.stackToString(e);
/* 120 */         log.info(Messages.getMessage("exception01", msg));
/* 121 */       }continue;
/*     */     }
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  47 */     encoderMap.put("UTF-8", new UTF8Encoder());
/*  48 */     encoderMap.put("UTF-16", new UTF16Encoder());
/*  49 */     encoderMap.put("UTF-8".toLowerCase(), new UTF8Encoder());
/*  50 */     encoderMap.put("UTF-16".toLowerCase(), new UTF16Encoder());
/*     */     try {
/*  52 */       loadPluggableEncoders();
/*     */     } catch (Throwable t) {
/*  54 */       String msg = t + JavaUtils.LS + JavaUtils.stackToString(t);
/*  55 */       log.info(Messages.getMessage("exception01", msg));
/*     */     }
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.components.encoding.XMLEncoderFactory
 * JD-Core Version:    0.6.0
 */