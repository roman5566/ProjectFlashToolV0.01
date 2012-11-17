/*     */ package org.apache.axis.utils;
/*     */ 
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.io.OutputStreamWriter;
/*     */ import java.io.Reader;
/*     */ import java.io.StringWriter;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.io.Writer;
/*     */ import java.net.HttpURLConnection;
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.ProtocolException;
/*     */ import java.net.URL;
/*     */ import java.net.URLConnection;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Stack;
/*     */ import javax.xml.namespace.QName;
/*     */ import javax.xml.parsers.DocumentBuilder;
/*     */ import javax.xml.parsers.DocumentBuilderFactory;
/*     */ import javax.xml.parsers.ParserConfigurationException;
/*     */ import javax.xml.parsers.SAXParser;
/*     */ import javax.xml.parsers.SAXParserFactory;
/*     */ import javax.xml.soap.SOAPException;
/*     */ import javax.xml.transform.Source;
/*     */ import javax.xml.transform.dom.DOMSource;
/*     */ import javax.xml.transform.sax.SAXSource;
/*     */ import javax.xml.transform.stream.StreamSource;
/*     */ import org.apache.axis.AxisEngine;
/*     */ import org.apache.axis.AxisProperties;
/*     */ import org.apache.axis.InternalException;
/*     */ import org.apache.axis.Message;
/*     */ import org.apache.axis.MessageContext;
/*     */ import org.apache.axis.components.encoding.XMLEncoder;
/*     */ import org.apache.axis.components.encoding.XMLEncoderFactory;
/*     */ import org.apache.axis.components.logger.LogFactory;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.w3c.dom.Attr;
/*     */ import org.w3c.dom.CharacterData;
/*     */ import org.w3c.dom.Document;
/*     */ import org.w3c.dom.Element;
/*     */ import org.w3c.dom.NamedNodeMap;
/*     */ import org.w3c.dom.Node;
/*     */ import org.w3c.dom.NodeList;
/*     */ import org.w3c.dom.Text;
/*     */ import org.xml.sax.ErrorHandler;
/*     */ import org.xml.sax.InputSource;
/*     */ import org.xml.sax.SAXException;
/*     */ import org.xml.sax.SAXParseException;
/*     */ import org.xml.sax.XMLReader;
/*     */ import org.xml.sax.helpers.DefaultHandler;
/*     */ 
/*     */ public class XMLUtils
/*     */ {
/*  76 */   protected static Log log = LogFactory.getLog(XMLUtils.class.getName());
/*     */   public static final String httpAuthCharEncoding = "ISO-8859-1";
/*     */   private static final String saxParserFactoryProperty = "javax.xml.parsers.SAXParserFactory";
/*  83 */   private static DocumentBuilderFactory dbf = getDOMFactory();
/*     */   private static SAXParserFactory saxFactory;
/*  85 */   private static Stack saxParsers = new Stack();
/*  86 */   private static DefaultHandler doNothingContentHandler = new DefaultHandler();
/*     */ 
/*  88 */   private static String EMPTY = "";
/*  89 */   private static ByteArrayInputStream bais = new ByteArrayInputStream(EMPTY.getBytes());
/*     */ 
/*  91 */   private static boolean tryReset = true;
/*     */ 
/*  93 */   protected static boolean enableParserReuse = false;
/*     */ 
/* 106 */   private static ThreadLocalDocumentBuilder documentBuilder = new ThreadLocalDocumentBuilder(null);
/*     */ 
/*     */   public static String xmlEncodeString(String orig)
/*     */   {
/* 130 */     XMLEncoder encoder = getXMLEncoder(MessageContext.getCurrentContext());
/* 131 */     return encoder.encode(orig);
/*     */   }
/*     */ 
/*     */   public static XMLEncoder getXMLEncoder(MessageContext msgContext)
/*     */   {
/* 139 */     return getXMLEncoder(getEncoding(null, msgContext));
/*     */   }
/*     */ 
/*     */   public static XMLEncoder getXMLEncoder(String encoding)
/*     */   {
/* 147 */     XMLEncoder encoder = null;
/*     */     try {
/* 149 */       encoder = XMLEncoderFactory.getEncoder(encoding);
/*     */     } catch (Exception e) {
/* 151 */       log.error(Messages.getMessage("exception00"), e);
/* 152 */       encoder = XMLEncoderFactory.getDefaultEncoder();
/*     */     }
/* 154 */     return encoder;
/*     */   }
/*     */ 
/*     */   public static String getEncoding(MessageContext msgContext)
/*     */   {
/* 162 */     XMLEncoder encoder = getXMLEncoder(msgContext);
/* 163 */     return encoder.getEncoding();
/*     */   }
/*     */ 
/*     */   public static String getEncoding()
/*     */   {
/* 171 */     XMLEncoder encoder = getXMLEncoder(MessageContext.getCurrentContext());
/* 172 */     return encoder.getEncoding();
/*     */   }
/*     */ 
/*     */   public static void initSAXFactory(String factoryClassName, boolean namespaceAware, boolean validating)
/*     */   {
/* 192 */     if (factoryClassName != null)
/*     */       try {
/* 194 */         saxFactory = (SAXParserFactory)Class.forName(factoryClassName).newInstance();
/*     */ 
/* 200 */         if (System.getProperty("javax.xml.parsers.SAXParserFactory") == null)
/* 201 */           System.setProperty("javax.xml.parsers.SAXParserFactory", factoryClassName);
/*     */       }
/*     */       catch (Exception e)
/*     */       {
/* 205 */         log.error(Messages.getMessage("exception00"), e);
/* 206 */         saxFactory = null;
/*     */       }
/*     */     else {
/* 209 */       saxFactory = SAXParserFactory.newInstance();
/*     */     }
/* 211 */     saxFactory.setNamespaceAware(namespaceAware);
/* 212 */     saxFactory.setValidating(validating);
/*     */ 
/* 215 */     saxParsers.clear();
/*     */   }
/*     */   private static DocumentBuilderFactory getDOMFactory() {
/*     */     DocumentBuilderFactory dbf;
/*     */     try {
/* 221 */       DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
/* 222 */       dbf.setNamespaceAware(true);
/*     */     }
/*     */     catch (Exception e) {
/* 225 */       log.error(Messages.getMessage("exception00"), e);
/* 226 */       dbf = null;
/*     */     }
/* 228 */     return dbf;
/*     */   }
/*     */ 
/*     */   public static DocumentBuilder getDocumentBuilder()
/*     */     throws ParserConfigurationException
/*     */   {
/* 237 */     return (DocumentBuilder)documentBuilder.get();
/*     */   }
/*     */ 
/*     */   public static void releaseDocumentBuilder(DocumentBuilder db)
/*     */   {
/*     */     try
/*     */     {
/* 246 */       db.setErrorHandler(null);
/*     */     } catch (Throwable t) {
/* 248 */       log.debug("Failed to set ErrorHandler to null on DocumentBuilder", t);
/*     */     }
/*     */     try
/*     */     {
/* 252 */       db.setEntityResolver(null);
/*     */     } catch (Throwable t) {
/* 254 */       log.debug("Failed to set EntityResolver to null on DocumentBuilder", t);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static synchronized SAXParser getSAXParser()
/*     */   {
/* 264 */     if ((enableParserReuse) && (!saxParsers.empty())) {
/* 265 */       return (SAXParser)saxParsers.pop();
/*     */     }
/*     */     try
/*     */     {
/* 269 */       SAXParser parser = saxFactory.newSAXParser();
/* 270 */       XMLReader reader = parser.getXMLReader();
/*     */       try
/*     */       {
/* 277 */         reader.setEntityResolver(new DefaultEntityResolver());
/*     */       } catch (Throwable t) {
/* 279 */         log.debug("Failed to set EntityResolver on DocumentBuilder", t);
/*     */       }
/* 281 */       reader.setFeature("http://xml.org/sax/features/namespace-prefixes", false);
/* 282 */       return parser;
/*     */     } catch (ParserConfigurationException e) {
/* 284 */       log.error(Messages.getMessage("parserConfigurationException00"), e);
/* 285 */       return null;
/*     */     } catch (SAXException se) {
/* 287 */       log.error(Messages.getMessage("SAXException00"), se);
/* 288 */     }return null;
/*     */   }
/*     */ 
/*     */   public static void releaseSAXParser(SAXParser parser)
/*     */   {
/* 297 */     if ((!tryReset) || (!enableParserReuse)) return;
/*     */ 
/*     */     try
/*     */     {
/* 301 */       XMLReader xmlReader = parser.getXMLReader();
/* 302 */       if (null != xmlReader) {
/* 303 */         xmlReader.setContentHandler(doNothingContentHandler);
/* 304 */         xmlReader.setDTDHandler(doNothingContentHandler);
/*     */         try {
/* 306 */           xmlReader.setEntityResolver(doNothingContentHandler);
/*     */         } catch (Throwable t) {
/* 308 */           log.debug("Failed to set EntityResolver on DocumentBuilder", t);
/*     */         }
/*     */         try {
/* 311 */           xmlReader.setErrorHandler(doNothingContentHandler);
/*     */         } catch (Throwable t) {
/* 313 */           log.debug("Failed to set ErrorHandler on DocumentBuilder", t);
/*     */         }
/*     */ 
/* 316 */         synchronized (XMLUtils.class) {
/* 317 */           saxParsers.push(parser);
/*     */         }
/*     */       }
/*     */ 
/* 321 */       tryReset = false;
/*     */     }
/*     */     catch (SAXException e) {
/* 324 */       tryReset = false;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static Document newDocument()
/*     */     throws ParserConfigurationException
/*     */   {
/* 335 */     DocumentBuilder db = null;
/*     */     try {
/* 337 */       db = getDocumentBuilder();
/* 338 */       Document doc = db.newDocument();
/* 339 */       Document localDocument1 = doc;
/*     */       return localDocument1;
/*     */     }
/*     */     finally
/*     */     {
/* 341 */       if (db != null)
/* 342 */         releaseDocumentBuilder(db); 
/* 342 */     }throw localObject;
/*     */   }
/*     */ 
/*     */   public static Document newDocument(InputSource inp)
/*     */     throws ParserConfigurationException, SAXException, IOException
/*     */   {
/* 356 */     DocumentBuilder db = null;
/*     */     try {
/* 358 */       db = getDocumentBuilder();
/*     */       try {
/* 360 */         db.setEntityResolver(new DefaultEntityResolver());
/*     */       } catch (Throwable t) {
/* 362 */         log.debug("Failed to set EntityResolver on DocumentBuilder", t);
/*     */       }
/*     */       try {
/* 365 */         db.setErrorHandler(new ParserErrorHandler());
/*     */       } catch (Throwable t) {
/* 367 */         log.debug("Failed to set ErrorHandler on DocumentBuilder", t);
/*     */       }
/* 369 */       Document doc = db.parse(inp);
/* 370 */       Document localDocument1 = doc;
/*     */       return localDocument1;
/*     */     }
/*     */     finally
/*     */     {
/* 372 */       if (db != null)
/* 373 */         releaseDocumentBuilder(db); 
/* 373 */     }throw localObject;
/*     */   }
/*     */ 
/*     */   public static Document newDocument(InputStream inp)
/*     */     throws ParserConfigurationException, SAXException, IOException
/*     */   {
/* 388 */     return newDocument(new InputSource(inp));
/*     */   }
/*     */ 
/*     */   public static Document newDocument(String uri)
/*     */     throws ParserConfigurationException, SAXException, IOException
/*     */   {
/* 403 */     return newDocument(uri, null, null);
/*     */   }
/*     */ 
/*     */   public static Document newDocument(String uri, String username, String password)
/*     */     throws ParserConfigurationException, SAXException, IOException
/*     */   {
/* 419 */     InputSource ins = getInputSourceFromURI(uri, username, password);
/* 420 */     Document doc = newDocument(ins);
/*     */ 
/* 422 */     if (ins.getByteStream() != null)
/* 423 */       ins.getByteStream().close();
/* 424 */     else if (ins.getCharacterStream() != null) {
/* 425 */       ins.getCharacterStream().close();
/*     */     }
/* 427 */     return doc;
/*     */   }
/*     */ 
/*     */   private static String privateElementToString(Element element, boolean omitXMLDecl)
/*     */   {
/* 433 */     return DOM2Writer.nodeToString(element, omitXMLDecl);
/*     */   }
/*     */ 
/*     */   public static String ElementToString(Element element)
/*     */   {
/* 442 */     return privateElementToString(element, true);
/*     */   }
/*     */ 
/*     */   public static String DocumentToString(Document doc)
/*     */   {
/* 451 */     return privateElementToString(doc.getDocumentElement(), false);
/*     */   }
/*     */ 
/*     */   public static String PrettyDocumentToString(Document doc) {
/* 455 */     StringWriter sw = new StringWriter();
/* 456 */     PrettyElementToWriter(doc.getDocumentElement(), sw);
/* 457 */     return sw.toString();
/*     */   }
/*     */ 
/*     */   public static void privateElementToWriter(Element element, Writer writer, boolean omitXMLDecl, boolean pretty)
/*     */   {
/* 463 */     DOM2Writer.serializeAsXML(element, writer, omitXMLDecl, pretty);
/*     */   }
/*     */ 
/*     */   public static void ElementToStream(Element element, OutputStream out) {
/* 467 */     Writer writer = getWriter(out);
/* 468 */     privateElementToWriter(element, writer, true, false);
/*     */   }
/*     */ 
/*     */   public static void PrettyElementToStream(Element element, OutputStream out) {
/* 472 */     Writer writer = getWriter(out);
/* 473 */     privateElementToWriter(element, writer, true, true);
/*     */   }
/*     */ 
/*     */   public static void ElementToWriter(Element element, Writer writer) {
/* 477 */     privateElementToWriter(element, writer, true, false);
/*     */   }
/*     */ 
/*     */   public static void PrettyElementToWriter(Element element, Writer writer) {
/* 481 */     privateElementToWriter(element, writer, true, true);
/*     */   }
/*     */ 
/*     */   public static void DocumentToStream(Document doc, OutputStream out) {
/* 485 */     Writer writer = getWriter(out);
/* 486 */     privateElementToWriter(doc.getDocumentElement(), writer, false, false);
/*     */   }
/*     */ 
/*     */   public static void PrettyDocumentToStream(Document doc, OutputStream out) {
/* 490 */     Writer writer = getWriter(out);
/* 491 */     privateElementToWriter(doc.getDocumentElement(), writer, false, true);
/*     */   }
/*     */ 
/*     */   private static Writer getWriter(OutputStream os) {
/* 495 */     Writer writer = null;
/*     */     try {
/* 497 */       writer = new OutputStreamWriter(os, "UTF-8");
/*     */     } catch (UnsupportedEncodingException uee) {
/* 499 */       log.error(Messages.getMessage("exception00"), uee);
/* 500 */       writer = new OutputStreamWriter(os);
/*     */     }
/* 502 */     return writer;
/*     */   }
/*     */ 
/*     */   public static void DocumentToWriter(Document doc, Writer writer) {
/* 506 */     privateElementToWriter(doc.getDocumentElement(), writer, false, false);
/*     */   }
/*     */ 
/*     */   public static void PrettyDocumentToWriter(Document doc, Writer writer) {
/* 510 */     privateElementToWriter(doc.getDocumentElement(), writer, false, true);
/*     */   }
/*     */ 
/*     */   public static Element StringToElement(String namespace, String name, String string)
/*     */   {
/*     */     try
/*     */     {
/* 522 */       Document doc = newDocument();
/* 523 */       Element element = doc.createElementNS(namespace, name);
/* 524 */       Text text = doc.createTextNode(string);
/* 525 */       element.appendChild(text);
/* 526 */       return element;
/*     */     }
/*     */     catch (ParserConfigurationException e) {
/*     */     }
/* 530 */     throw new InternalException(e);
/*     */   }
/*     */ 
/*     */   public static String getInnerXMLString(Element element)
/*     */   {
/* 542 */     String elementString = ElementToString(element);
/*     */ 
/* 544 */     int start = elementString.indexOf(">") + 1;
/* 545 */     int end = elementString.lastIndexOf("</");
/* 546 */     if (end > 0) {
/* 547 */       return elementString.substring(start, end);
/*     */     }
/* 549 */     return null;
/*     */   }
/*     */ 
/*     */   public static String getPrefix(String uri, Node e) {
/* 553 */     while ((e != null) && (e.getNodeType() == 1)) {
/* 554 */       NamedNodeMap attrs = e.getAttributes();
/* 555 */       for (int n = 0; n < attrs.getLength(); n++) {
/* 556 */         Attr a = (Attr)attrs.item(n);
/*     */         String name;
/* 558 */         if (((name = a.getName()).startsWith("xmlns:")) && (a.getNodeValue().equals(uri)))
/*     */         {
/* 560 */           return name.substring(6);
/*     */         }
/*     */       }
/* 563 */       e = e.getParentNode();
/*     */     }
/* 565 */     return null;
/*     */   }
/*     */ 
/*     */   public static String getNamespace(String prefix, Node e, Node stopNode)
/*     */   {
/* 581 */     while ((e != null) && (e.getNodeType() == 1)) {
/* 582 */       Attr attr = null;
/* 583 */       if (prefix == null)
/* 584 */         attr = ((Element)e).getAttributeNode("xmlns");
/*     */       else {
/* 586 */         attr = ((Element)e).getAttributeNodeNS("http://www.w3.org/2000/xmlns/", prefix);
/*     */       }
/*     */ 
/* 589 */       if (attr != null) return attr.getValue();
/* 590 */       if (e == stopNode)
/* 591 */         return null;
/* 592 */       e = e.getParentNode();
/*     */     }
/* 594 */     return null;
/*     */   }
/*     */ 
/*     */   public static String getNamespace(String prefix, Node e) {
/* 598 */     return getNamespace(prefix, e, null);
/*     */   }
/*     */ 
/*     */   public static QName getQNameFromString(String str, Node e)
/*     */   {
/* 608 */     return getQNameFromString(str, e, false);
/*     */   }
/*     */ 
/*     */   public static QName getFullQNameFromString(String str, Node e)
/*     */   {
/* 618 */     return getQNameFromString(str, e, true);
/*     */   }
/*     */   private static QName getQNameFromString(String str, Node e, boolean defaultNS) {
/* 621 */     if ((str == null) || (e == null)) {
/* 622 */       return null;
/*     */     }
/* 624 */     int idx = str.indexOf(':');
/* 625 */     if (idx > -1) {
/* 626 */       String prefix = str.substring(0, idx);
/* 627 */       String ns = getNamespace(prefix, e);
/* 628 */       if (ns == null)
/* 629 */         return null;
/* 630 */       return new QName(ns, str.substring(idx + 1));
/*     */     }
/* 632 */     if (defaultNS) {
/* 633 */       String ns = getNamespace(null, e);
/* 634 */       if (ns != null)
/* 635 */         return new QName(ns, str);
/*     */     }
/* 637 */     return new QName("", str);
/*     */   }
/*     */ 
/*     */   public static String getStringForQName(QName qname, Element e)
/*     */   {
/* 647 */     String uri = qname.getNamespaceURI();
/* 648 */     String prefix = getPrefix(uri, e);
/* 649 */     if (prefix == null) {
/* 650 */       int i = 1;
/* 651 */       prefix = "ns" + i;
/* 652 */       while (getNamespace(prefix, e) != null) {
/* 653 */         i++;
/* 654 */         prefix = "ns" + i;
/*     */       }
/* 656 */       e.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:" + prefix, uri);
/*     */     }
/*     */ 
/* 659 */     return prefix + ":" + qname.getLocalPart();
/*     */   }
/*     */ 
/*     */   public static String getChildCharacterData(Element parentEl)
/*     */   {
/* 672 */     if (parentEl == null) {
/* 673 */       return null;
/*     */     }
/* 675 */     Node tempNode = parentEl.getFirstChild();
/* 676 */     StringBuffer strBuf = new StringBuffer();
/*     */ 
/* 679 */     while (tempNode != null) {
/* 680 */       switch (tempNode.getNodeType()) { case 3:
/*     */       case 4:
/* 682 */         CharacterData charData = (CharacterData)tempNode;
/* 683 */         strBuf.append(charData.getData());
/*     */       }
/*     */ 
/* 686 */       tempNode = tempNode.getNextSibling();
/*     */     }
/* 688 */     return strBuf.toString();
/*     */   }
/*     */ 
/*     */   public static InputSource getInputSourceFromURI(String uri)
/*     */   {
/* 737 */     return new InputSource(uri);
/*     */   }
/*     */ 
/*     */   public static InputSource sourceToInputSource(Source source)
/*     */   {
/* 746 */     if ((source instanceof SAXSource))
/* 747 */       return ((SAXSource)source).getInputSource();
/* 748 */     if ((source instanceof DOMSource)) {
/* 749 */       ByteArrayOutputStream baos = new ByteArrayOutputStream();
/* 750 */       Node node = ((DOMSource)source).getNode();
/* 751 */       if ((node instanceof Document)) {
/* 752 */         node = ((Document)node).getDocumentElement();
/*     */       }
/* 754 */       Element domElement = (Element)node;
/* 755 */       ElementToStream(domElement, baos);
/* 756 */       InputSource isource = new InputSource(source.getSystemId());
/* 757 */       isource.setByteStream(new ByteArrayInputStream(baos.toByteArray()));
/* 758 */       return isource;
/* 759 */     }if ((source instanceof StreamSource)) {
/* 760 */       StreamSource ss = (StreamSource)source;
/* 761 */       InputSource isource = new InputSource(ss.getSystemId());
/* 762 */       isource.setByteStream(ss.getInputStream());
/* 763 */       isource.setCharacterStream(ss.getReader());
/* 764 */       isource.setPublicId(ss.getPublicId());
/* 765 */       return isource;
/*     */     }
/* 767 */     return getInputSourceFromURI(source.getSystemId());
/*     */   }
/*     */ 
/*     */   private static InputSource getInputSourceFromURI(String uri, String username, String password)
/*     */     throws IOException, ProtocolException, UnsupportedEncodingException
/*     */   {
/* 790 */     URL wsdlurl = null;
/*     */     try {
/* 792 */       wsdlurl = new URL(uri);
/*     */     }
/*     */     catch (MalformedURLException e)
/*     */     {
/* 796 */       return new InputSource(uri);
/*     */     }
/*     */ 
/* 800 */     if ((username == null) && (wsdlurl.getUserInfo() == null)) {
/* 801 */       return new InputSource(uri);
/*     */     }
/*     */ 
/* 805 */     if (!wsdlurl.getProtocol().startsWith("http")) {
/* 806 */       return new InputSource(uri);
/*     */     }
/*     */ 
/* 809 */     URLConnection connection = wsdlurl.openConnection();
/*     */ 
/* 811 */     if (!(connection instanceof HttpURLConnection))
/*     */     {
/* 813 */       return new InputSource(uri);
/*     */     }
/* 815 */     HttpURLConnection uconn = (HttpURLConnection)connection;
/* 816 */     String userinfo = wsdlurl.getUserInfo();
/* 817 */     uconn.setRequestMethod("GET");
/* 818 */     uconn.setAllowUserInteraction(false);
/* 819 */     uconn.setDefaultUseCaches(false);
/* 820 */     uconn.setDoInput(true);
/* 821 */     uconn.setDoOutput(false);
/* 822 */     uconn.setInstanceFollowRedirects(true);
/* 823 */     uconn.setUseCaches(false);
/*     */ 
/* 826 */     String auth = null;
/* 827 */     if (userinfo != null)
/* 828 */       auth = userinfo;
/* 829 */     else if (username != null) {
/* 830 */       auth = username + ":" + password;
/*     */     }
/*     */ 
/* 833 */     if (auth != null) {
/* 834 */       uconn.setRequestProperty("Authorization", "Basic " + base64encode(auth.getBytes("ISO-8859-1")));
/*     */     }
/*     */ 
/* 839 */     uconn.connect();
/*     */ 
/* 841 */     return new InputSource(uconn.getInputStream());
/*     */   }
/*     */ 
/*     */   public static final String base64encode(byte[] bytes) {
/* 845 */     return new String(Base64.encode(bytes));
/*     */   }
/*     */ 
/*     */   public static InputSource getEmptyInputSource() {
/* 849 */     return new InputSource(bais);
/*     */   }
/*     */ 
/*     */   public static Node findNode(Node node, QName name)
/*     */   {
/* 860 */     if ((name.getNamespaceURI().equals(node.getNamespaceURI())) && (name.getLocalPart().equals(node.getLocalName())))
/*     */     {
/* 862 */       return node;
/* 863 */     }NodeList children = node.getChildNodes();
/* 864 */     for (int i = 0; i < children.getLength(); i++) {
/* 865 */       Node ret = findNode(children.item(i), name);
/* 866 */       if (ret != null)
/* 867 */         return ret;
/*     */     }
/* 869 */     return null;
/*     */   }
/*     */ 
/*     */   public static void normalize(Node node)
/*     */   {
/* 878 */     if (node.getNodeType() == 3) {
/* 879 */       String data = ((Text)node).getData();
/* 880 */       if (data.length() > 0) {
/* 881 */         char ch = data.charAt(data.length() - 1);
/* 882 */         if ((ch == '\n') || (ch == '\r') || (ch == ' ')) {
/* 883 */           String data2 = trim(data);
/* 884 */           ((Text)node).setData(data2);
/*     */         }
/*     */       }
/*     */     }
/* 888 */     for (Node currentChild = node.getFirstChild(); currentChild != null; currentChild = currentChild.getNextSibling())
/* 889 */       normalize(currentChild);
/*     */   }
/*     */ 
/*     */   public static String trim(String str)
/*     */   {
/* 894 */     if (str.length() == 0) {
/* 895 */       return str;
/*     */     }
/*     */ 
/* 898 */     if (str.length() == 1) {
/* 899 */       if (("\r".equals(str)) || ("\n".equals(str))) {
/* 900 */         return "";
/*     */       }
/* 902 */       return str;
/*     */     }
/*     */ 
/* 906 */     int lastIdx = str.length() - 1;
/* 907 */     char last = str.charAt(lastIdx);
/* 908 */     while ((lastIdx > 0) && (
/* 909 */       (last == '\n') || (last == '\r') || (last == ' ')))
/*     */     {
/* 911 */       lastIdx--;
/* 912 */       last = str.charAt(lastIdx);
/*     */     }
/* 914 */     if (lastIdx == 0)
/* 915 */       return "";
/* 916 */     return str.substring(0, lastIdx);
/*     */   }
/*     */ 
/*     */   public static Element[] asElementArray(List list)
/*     */   {
/* 927 */     Element[] elements = new Element[list.size()];
/*     */ 
/* 929 */     int i = 0;
/* 930 */     Iterator detailIter = list.iterator();
/* 931 */     while (detailIter.hasNext()) {
/* 932 */       elements[(i++)] = ((Element)detailIter.next());
/*     */     }
/*     */ 
/* 935 */     return elements;
/*     */   }
/*     */ 
/*     */   public static String getEncoding(Message message, MessageContext msgContext)
/*     */   {
/* 940 */     return getEncoding(message, msgContext, XMLEncoderFactory.getDefaultEncoder());
/*     */   }
/*     */ 
/*     */   public static String getEncoding(Message message, MessageContext msgContext, XMLEncoder defaultEncoder)
/*     */   {
/* 947 */     String encoding = null;
/*     */     try {
/* 949 */       if (message != null)
/* 950 */         encoding = (String)message.getProperty("javax.xml.soap.character-set-encoding");
/*     */     }
/*     */     catch (SOAPException e) {
/*     */     }
/* 954 */     if (msgContext == null) {
/* 955 */       msgContext = MessageContext.getCurrentContext();
/*     */     }
/* 957 */     if ((msgContext != null) && (encoding == null)) {
/* 958 */       encoding = (String)msgContext.getProperty("javax.xml.soap.character-set-encoding");
/*     */     }
/* 960 */     if ((msgContext != null) && (encoding == null) && (msgContext.getAxisEngine() != null)) {
/* 961 */       encoding = (String)msgContext.getAxisEngine().getOption("axis.xmlEncoding");
/*     */     }
/* 963 */     if ((encoding == null) && (defaultEncoder != null)) {
/* 964 */       encoding = defaultEncoder.getEncoding();
/*     */     }
/* 966 */     return encoding;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/* 110 */     initSAXFactory(null, true, false);
/*     */ 
/* 112 */     String value = AxisProperties.getProperty("axis.xml.reuseParsers", "true");
/*     */ 
/* 114 */     if ((value.equalsIgnoreCase("true")) || (value.equals("1")) || (value.equalsIgnoreCase("yes")))
/*     */     {
/* 117 */       enableParserReuse = true;
/*     */     }
/* 119 */     else enableParserReuse = false;
/*     */   }
/*     */ 
/*     */   public static class ParserErrorHandler
/*     */     implements ErrorHandler
/*     */   {
/* 692 */     protected static Log log = LogFactory.getLog(ParserErrorHandler.class.getName());
/*     */ 
/*     */     private String getParseExceptionInfo(SAXParseException spe)
/*     */     {
/* 698 */       String systemId = spe.getSystemId();
/* 699 */       if (systemId == null) {
/* 700 */         systemId = "null";
/*     */       }
/* 702 */       String info = "URI=" + systemId + " Line=" + spe.getLineNumber() + ": " + spe.getMessage();
/*     */ 
/* 705 */       return info;
/*     */     }
/*     */ 
/*     */     public void warning(SAXParseException spe)
/*     */       throws SAXException
/*     */     {
/* 712 */       if (log.isDebugEnabled())
/* 713 */         log.debug(Messages.getMessage("warning00", getParseExceptionInfo(spe)));
/*     */     }
/*     */ 
/*     */     public void error(SAXParseException spe) throws SAXException {
/* 717 */       String message = "Error: " + getParseExceptionInfo(spe);
/* 718 */       throw new SAXException(message);
/*     */     }
/*     */ 
/*     */     public void fatalError(SAXParseException spe) throws SAXException {
/* 722 */       String message = "Fatal Error: " + getParseExceptionInfo(spe);
/* 723 */       throw new SAXException(message);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class ThreadLocalDocumentBuilder extends ThreadLocal
/*     */   {
/*     */     private ThreadLocalDocumentBuilder()
/*     */     {
/*     */     }
/*     */ 
/*     */     protected Object initialValue()
/*     */     {
/*     */       try
/*     */       {
/*  98 */         return XMLUtils.access$000().newDocumentBuilder();
/*     */       } catch (ParserConfigurationException e) {
/* 100 */         XMLUtils.log.error(Messages.getMessage("parserConfigurationException00"), e);
/*     */       }
/*     */ 
/* 103 */       return null;
/*     */     }
/*     */ 
/*     */     ThreadLocalDocumentBuilder(XMLUtils.1 x0)
/*     */     {
/*  95 */       this();
/*     */     }
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.utils.XMLUtils
 * JD-Core Version:    0.6.0
 */