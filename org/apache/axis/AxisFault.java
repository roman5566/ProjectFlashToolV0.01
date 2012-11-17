/*     */ package org.apache.axis;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.io.StringWriter;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.rmi.RemoteException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.Vector;
/*     */ import javax.xml.namespace.QName;
/*     */ import javax.xml.parsers.ParserConfigurationException;
/*     */ import javax.xml.rpc.soap.SOAPFaultException;
/*     */ import javax.xml.soap.Detail;
/*     */ import org.apache.axis.components.logger.LogFactory;
/*     */ import org.apache.axis.encoding.SerializationContext;
/*     */ import org.apache.axis.encoding.TypeMapping;
/*     */ import org.apache.axis.message.SOAPEnvelope;
/*     */ import org.apache.axis.message.SOAPFault;
/*     */ import org.apache.axis.message.SOAPHeaderElement;
/*     */ import org.apache.axis.soap.SOAPConstants;
/*     */ import org.apache.axis.utils.JavaUtils;
/*     */ import org.apache.axis.utils.NetworkUtils;
/*     */ import org.apache.axis.utils.XMLUtils;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.w3c.dom.Document;
/*     */ import org.w3c.dom.Element;
/*     */ import org.w3c.dom.Text;
/*     */ 
/*     */ public class AxisFault extends RemoteException
/*     */ {
/*  64 */   protected static Log log = LogFactory.getLog(AxisFault.class.getName());
/*     */   protected QName faultCode;
/*     */   protected Vector faultSubCode;
/*  70 */   protected String faultString = "";
/*     */   protected String faultActor;
/*     */   protected Vector faultDetails;
/*     */   protected String faultNode;
/*  76 */   protected ArrayList faultHeaders = null;
/*     */ 
/*     */   public static AxisFault makeFault(Exception e)
/*     */   {
/*  90 */     if ((e instanceof InvocationTargetException)) {
/*  91 */       Throwable t = ((InvocationTargetException)e).getTargetException();
/*  92 */       if ((t instanceof Exception)) {
/*  93 */         e = (Exception)t;
/*     */       }
/*     */     }
/*     */ 
/*  97 */     if ((e instanceof AxisFault)) {
/*  98 */       return (AxisFault)e;
/*     */     }
/*     */ 
/* 101 */     return new AxisFault(e);
/*     */   }
/*     */ 
/*     */   public AxisFault(String code, String faultString, String actor, Element[] details)
/*     */   {
/* 115 */     this(new QName("http://xml.apache.org/axis/", code), faultString, actor, details);
/*     */   }
/*     */ 
/*     */   public AxisFault(QName code, String faultString, String actor, Element[] details)
/*     */   {
/* 130 */     super(faultString);
/* 131 */     setFaultCode(code);
/* 132 */     setFaultString(faultString);
/* 133 */     setFaultActor(actor);
/* 134 */     setFaultDetail(details);
/* 135 */     if (details == null)
/* 136 */       initFromException(this);
/*     */   }
/*     */ 
/*     */   public AxisFault(QName code, QName[] subcodes, String faultString, String actor, String node, Element[] details)
/*     */   {
/* 154 */     super(faultString);
/* 155 */     setFaultCode(code);
/* 156 */     if (subcodes != null) {
/* 157 */       for (int i = 0; i < subcodes.length; i++) {
/* 158 */         addFaultSubCode(subcodes[i]);
/*     */       }
/*     */     }
/* 161 */     setFaultString(faultString);
/* 162 */     setFaultActor(actor);
/* 163 */     setFaultNode(node);
/* 164 */     setFaultDetail(details);
/* 165 */     if (details == null)
/* 166 */       initFromException(this);
/*     */   }
/*     */ 
/*     */   protected AxisFault(Exception target)
/*     */   {
/* 178 */     super("", target);
/*     */ 
/* 180 */     setFaultCodeAsString("Server.userException");
/* 181 */     initFromException(target);
/*     */ 
/* 185 */     if ((target instanceof SOAPFaultException))
/*     */     {
/* 187 */       removeHostname();
/* 188 */       initFromSOAPFaultException((SOAPFaultException)target);
/*     */ 
/* 190 */       addHostnameIfNeeded();
/*     */     }
/*     */   }
/*     */ 
/*     */   public AxisFault(String message)
/*     */   {
/* 202 */     super(message);
/* 203 */     setFaultCodeAsString("Server.generalException");
/* 204 */     setFaultString(message);
/* 205 */     initFromException(this);
/*     */   }
/*     */ 
/*     */   public AxisFault()
/*     */   {
/* 214 */     setFaultCodeAsString("Server.generalException");
/* 215 */     initFromException(this);
/*     */   }
/*     */ 
/*     */   public AxisFault(String message, Throwable t)
/*     */   {
/* 227 */     super(message, t);
/* 228 */     setFaultCodeAsString("Server.generalException");
/* 229 */     setFaultString(getMessage());
/* 230 */     addHostnameIfNeeded();
/*     */   }
/*     */ 
/*     */   private void initFromException(Exception target)
/*     */   {
/* 243 */     Element oldStackTrace = lookupFaultDetail(Constants.QNAME_FAULTDETAIL_STACKTRACE);
/* 244 */     if (oldStackTrace != null)
/*     */     {
/* 246 */       return;
/*     */     }
/*     */ 
/* 250 */     setFaultString(target.toString());
/*     */ 
/* 261 */     if (((target instanceof AxisFault)) && (target.getClass() != AxisFault.class))
/*     */     {
/* 263 */       addFaultDetail(Constants.QNAME_FAULTDETAIL_EXCEPTIONNAME, target.getClass().getName());
/*     */     }
/*     */ 
/* 268 */     if (target == this)
/*     */     {
/* 272 */       addFaultDetail(Constants.QNAME_FAULTDETAIL_STACKTRACE, getPlainStackTrace());
/*     */     }
/*     */     else {
/* 275 */       addFaultDetail(Constants.QNAME_FAULTDETAIL_STACKTRACE, JavaUtils.stackToString(target));
/*     */     }
/*     */ 
/* 280 */     addHostnameIfNeeded();
/*     */   }
/*     */ 
/*     */   private void initFromSOAPFaultException(SOAPFaultException fault)
/*     */   {
/* 290 */     if (fault.getFaultCode() != null) {
/* 291 */       setFaultCode(fault.getFaultCode());
/*     */     }
/*     */ 
/* 295 */     if (fault.getFaultString() != null) {
/* 296 */       setFaultString(fault.getFaultString());
/*     */     }
/*     */ 
/* 300 */     if (fault.getFaultActor() != null) {
/* 301 */       setFaultActor(fault.getFaultActor());
/*     */     }
/*     */ 
/* 304 */     if (null == fault.getDetail()) {
/* 305 */       return;
/*     */     }
/*     */ 
/* 309 */     Vector details = new Vector();
/* 310 */     Iterator detailIter = fault.getDetail().getChildElements();
/* 311 */     while (detailIter.hasNext()) {
/* 312 */       details.add(detailIter.next());
/*     */     }
/*     */ 
/* 316 */     setFaultDetail(XMLUtils.asElementArray(details));
/*     */   }
/*     */ 
/*     */   private void initFaultDetails()
/*     */   {
/* 324 */     if (this.faultDetails == null)
/* 325 */       this.faultDetails = new Vector();
/*     */   }
/*     */ 
/*     */   public void clearFaultDetails()
/*     */   {
/* 333 */     this.faultDetails = null;
/*     */   }
/*     */ 
/*     */   public void dump()
/*     */   {
/* 340 */     log.debug(dumpToString());
/*     */   }
/*     */ 
/*     */   public String dumpToString()
/*     */   {
/* 352 */     StringBuffer buf = new StringBuffer("AxisFault");
/* 353 */     buf.append(JavaUtils.LS);
/* 354 */     buf.append(" faultCode: ");
/* 355 */     buf.append(XMLUtils.xmlEncodeString(this.faultCode.toString()));
/* 356 */     buf.append(JavaUtils.LS);
/* 357 */     buf.append(" faultSubcode: ");
/* 358 */     if (this.faultSubCode != null) {
/* 359 */       for (int i = 0; i < this.faultSubCode.size(); i++) {
/* 360 */         buf.append(JavaUtils.LS);
/* 361 */         buf.append(this.faultSubCode.elementAt(i).toString());
/*     */       }
/*     */     }
/* 364 */     buf.append(JavaUtils.LS);
/* 365 */     buf.append(" faultString: ");
/*     */     try {
/* 367 */       buf.append(XMLUtils.xmlEncodeString(this.faultString));
/*     */     } catch (RuntimeException re) {
/* 369 */       buf.append(re.getMessage());
/*     */     }
/* 371 */     buf.append(JavaUtils.LS);
/* 372 */     buf.append(" faultActor: ");
/* 373 */     buf.append(XMLUtils.xmlEncodeString(this.faultActor));
/* 374 */     buf.append(JavaUtils.LS);
/* 375 */     buf.append(" faultNode: ");
/* 376 */     buf.append(XMLUtils.xmlEncodeString(this.faultNode));
/* 377 */     buf.append(JavaUtils.LS);
/* 378 */     buf.append(" faultDetail: ");
/* 379 */     if (this.faultDetails != null) {
/* 380 */       for (int i = 0; i < this.faultDetails.size(); i++) {
/* 381 */         Element e = (Element)this.faultDetails.get(i);
/* 382 */         buf.append(JavaUtils.LS);
/* 383 */         buf.append("\t{");
/* 384 */         buf.append(null == e.getNamespaceURI() ? "" : e.getNamespaceURI());
/* 385 */         buf.append("}");
/* 386 */         buf.append(null == e.getLocalName() ? "" : e.getLocalName());
/* 387 */         buf.append(":");
/* 388 */         buf.append(XMLUtils.getInnerXMLString(e));
/*     */       }
/*     */     }
/* 391 */     buf.append(JavaUtils.LS);
/* 392 */     return buf.toString();
/*     */   }
/*     */ 
/*     */   public void setFaultCode(QName code)
/*     */   {
/* 401 */     this.faultCode = code;
/*     */   }
/*     */ 
/*     */   /** @deprecated */
/*     */   public void setFaultCode(String code)
/*     */   {
/* 413 */     setFaultCodeAsString(code);
/*     */   }
/*     */ 
/*     */   public void setFaultCodeAsString(String code)
/*     */   {
/* 422 */     SOAPConstants soapConstants = MessageContext.getCurrentContext() == null ? SOAPConstants.SOAP11_CONSTANTS : MessageContext.getCurrentContext().getSOAPConstants();
/*     */ 
/* 426 */     this.faultCode = new QName(soapConstants.getEnvelopeURI(), code);
/*     */   }
/*     */ 
/*     */   public QName getFaultCode()
/*     */   {
/* 435 */     return this.faultCode;
/*     */   }
/*     */ 
/*     */   public void addFaultSubCodeAsString(String code)
/*     */   {
/* 447 */     initFaultSubCodes();
/* 448 */     this.faultSubCode.add(new QName("http://xml.apache.org/axis/", code));
/*     */   }
/*     */ 
/*     */   protected void initFaultSubCodes()
/*     */   {
/* 456 */     if (this.faultSubCode == null)
/* 457 */       this.faultSubCode = new Vector();
/*     */   }
/*     */ 
/*     */   public void addFaultSubCode(QName code)
/*     */   {
/* 469 */     initFaultSubCodes();
/* 470 */     this.faultSubCode.add(code);
/*     */   }
/*     */ 
/*     */   public void clearFaultSubCodes()
/*     */   {
/* 480 */     this.faultSubCode = null;
/*     */   }
/*     */ 
/*     */   public QName[] getFaultSubCodes()
/*     */   {
/* 489 */     if (this.faultSubCode == null) {
/* 490 */       return null;
/*     */     }
/* 492 */     QName[] q = new QName[this.faultSubCode.size()];
/* 493 */     return (QName[])this.faultSubCode.toArray(q);
/*     */   }
/*     */ 
/*     */   public void setFaultString(String str)
/*     */   {
/* 502 */     if (str != null)
/* 503 */       this.faultString = str;
/*     */     else
/* 505 */       this.faultString = "";
/*     */   }
/*     */ 
/*     */   public String getFaultString()
/*     */   {
/* 516 */     return this.faultString;
/*     */   }
/*     */ 
/*     */   public void setFaultReason(String str)
/*     */   {
/* 526 */     setFaultString(str);
/*     */   }
/*     */ 
/*     */   public String getFaultReason()
/*     */   {
/* 535 */     return getFaultString();
/*     */   }
/*     */ 
/*     */   public void setFaultActor(String actor)
/*     */   {
/* 544 */     this.faultActor = actor;
/*     */   }
/*     */ 
/*     */   public String getFaultActor()
/*     */   {
/* 552 */     return this.faultActor;
/*     */   }
/*     */ 
/*     */   public String getFaultRole()
/*     */   {
/* 561 */     return getFaultActor();
/*     */   }
/*     */ 
/*     */   public void setFaultRole(String role)
/*     */   {
/* 571 */     setFaultActor(role);
/*     */   }
/*     */ 
/*     */   public String getFaultNode()
/*     */   {
/* 582 */     return this.faultNode;
/*     */   }
/*     */ 
/*     */   public void setFaultNode(String node)
/*     */   {
/* 594 */     this.faultNode = node;
/*     */   }
/*     */ 
/*     */   public void setFaultDetail(Element[] details)
/*     */   {
/* 603 */     if (details == null) {
/* 604 */       this.faultDetails = null;
/* 605 */       return;
/*     */     }
/* 607 */     this.faultDetails = new Vector(details.length);
/* 608 */     for (int loop = 0; loop < details.length; loop++)
/* 609 */       this.faultDetails.add(details[loop]);
/*     */   }
/*     */ 
/*     */   public void setFaultDetailString(String details)
/*     */   {
/* 618 */     clearFaultDetails();
/* 619 */     addFaultDetailString(details);
/*     */   }
/*     */ 
/*     */   public void addFaultDetailString(String detail)
/*     */   {
/* 627 */     initFaultDetails();
/*     */     try {
/* 629 */       Document doc = XMLUtils.newDocument();
/* 630 */       Element element = doc.createElement("string");
/* 631 */       Text text = doc.createTextNode(detail);
/* 632 */       element.appendChild(text);
/* 633 */       this.faultDetails.add(element);
/*     */     }
/*     */     catch (ParserConfigurationException e) {
/* 636 */       throw new InternalException(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void addFaultDetail(Element detail)
/*     */   {
/* 647 */     initFaultDetails();
/* 648 */     this.faultDetails.add(detail);
/*     */   }
/*     */ 
/*     */   public void addFaultDetail(QName qname, String body)
/*     */   {
/* 658 */     Element detail = XMLUtils.StringToElement(qname.getNamespaceURI(), qname.getLocalPart(), body);
/*     */ 
/* 662 */     addFaultDetail(detail);
/*     */   }
/*     */ 
/*     */   public Element[] getFaultDetails()
/*     */   {
/* 672 */     if (this.faultDetails == null) {
/* 673 */       return null;
/*     */     }
/* 675 */     Element[] result = new Element[this.faultDetails.size()];
/* 676 */     for (int i = 0; i < result.length; i++) {
/* 677 */       result[i] = ((Element)this.faultDetails.elementAt(i));
/*     */     }
/* 679 */     return result;
/*     */   }
/*     */ 
/*     */   public Element lookupFaultDetail(QName qname)
/*     */   {
/* 689 */     if (this.faultDetails != null)
/*     */     {
/* 692 */       String searchNamespace = qname.getNamespaceURI();
/* 693 */       String searchLocalpart = qname.getLocalPart();
/*     */ 
/* 695 */       Iterator it = this.faultDetails.iterator();
/* 696 */       while (it.hasNext()) {
/* 697 */         Element e = (Element)it.next();
/* 698 */         String localpart = e.getLocalName();
/* 699 */         if (localpart == null) {
/* 700 */           localpart = e.getNodeName();
/*     */         }
/* 702 */         String namespace = e.getNamespaceURI();
/* 703 */         if (namespace == null) {
/* 704 */           namespace = "";
/*     */         }
/*     */ 
/* 708 */         if ((searchNamespace.equals(namespace)) && (searchLocalpart.equals(localpart)))
/*     */         {
/* 710 */           return e;
/*     */         }
/*     */       }
/*     */     }
/* 714 */     return null;
/*     */   }
/*     */ 
/*     */   public boolean removeFaultDetail(QName qname)
/*     */   {
/* 725 */     Element elt = lookupFaultDetail(qname);
/* 726 */     if (elt == null) {
/* 727 */       return false;
/*     */     }
/* 729 */     return this.faultDetails.remove(elt);
/*     */   }
/*     */ 
/*     */   public void output(SerializationContext context)
/*     */     throws Exception
/*     */   {
/* 741 */     SOAPConstants soapConstants = Constants.DEFAULT_SOAP_VERSION;
/* 742 */     if (context.getMessageContext() != null) {
/* 743 */       soapConstants = context.getMessageContext().getSOAPConstants();
/*     */     }
/*     */ 
/* 746 */     SOAPEnvelope envelope = new SOAPEnvelope(soapConstants);
/*     */ 
/* 748 */     SOAPFault fault = new SOAPFault(this);
/* 749 */     envelope.addBodyElement(fault);
/*     */     Iterator i;
/* 752 */     if (this.faultHeaders != null) {
/* 753 */       for (i = this.faultHeaders.iterator(); i.hasNext(); ) {
/* 754 */         SOAPHeaderElement header = (SOAPHeaderElement)i.next();
/* 755 */         envelope.addHeader(header);
/*     */       }
/*     */     }
/*     */ 
/* 759 */     envelope.output(context);
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 768 */     return this.faultString;
/*     */   }
/*     */ 
/*     */   private String getPlainStackTrace()
/*     */   {
/* 775 */     StringWriter sw = new StringWriter(512);
/* 776 */     PrintWriter pw = new PrintWriter(sw);
/* 777 */     super.printStackTrace(pw);
/* 778 */     pw.close();
/* 779 */     return sw.toString();
/*     */   }
/*     */ 
/*     */   public void printStackTrace(PrintStream ps)
/*     */   {
/* 789 */     ps.println(dumpToString());
/* 790 */     super.printStackTrace(ps);
/*     */   }
/*     */ 
/*     */   public void printStackTrace(PrintWriter pw)
/*     */   {
/* 800 */     pw.println(dumpToString());
/* 801 */     super.printStackTrace(pw);
/*     */   }
/*     */ 
/*     */   public void addHeader(SOAPHeaderElement header)
/*     */   {
/* 811 */     if (this.faultHeaders == null) {
/* 812 */       this.faultHeaders = new ArrayList();
/*     */     }
/* 814 */     this.faultHeaders.add(header);
/*     */   }
/*     */ 
/*     */   public ArrayList getHeaders()
/*     */   {
/* 823 */     return this.faultHeaders;
/*     */   }
/*     */ 
/*     */   public void clearHeaders()
/*     */   {
/* 830 */     this.faultHeaders = null;
/*     */   }
/*     */ 
/*     */   public void writeDetails(QName qname, SerializationContext context)
/*     */     throws IOException
/*     */   {
/* 848 */     Object detailObject = this.detail;
/* 849 */     if (detailObject == null) {
/* 850 */       return;
/*     */     }
/*     */ 
/* 853 */     boolean haveSerializer = false;
/*     */     try {
/* 855 */       if (context.getTypeMapping().getSerializer(detailObject.getClass()) != null) {
/* 856 */         haveSerializer = true;
/*     */       }
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/*     */     }
/* 862 */     if (haveSerializer) {
/* 863 */       boolean oldMR = context.getDoMultiRefs();
/* 864 */       context.setDoMultiRefs(false);
/* 865 */       context.serialize(qname, null, detailObject);
/* 866 */       context.setDoMultiRefs(oldMR);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void addHostnameIfNeeded()
/*     */   {
/* 877 */     if (lookupFaultDetail(Constants.QNAME_FAULTDETAIL_HOSTNAME) != null)
/*     */     {
/* 879 */       return;
/*     */     }
/* 881 */     addHostname(NetworkUtils.getLocalHostname());
/*     */   }
/*     */ 
/*     */   public void addHostname(String hostname)
/*     */   {
/* 891 */     removeHostname();
/* 892 */     addFaultDetail(Constants.QNAME_FAULTDETAIL_HOSTNAME, hostname);
/*     */   }
/*     */ 
/*     */   public void removeHostname()
/*     */   {
/* 901 */     removeFaultDetail(Constants.QNAME_FAULTDETAIL_HOSTNAME);
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.AxisFault
 * JD-Core Version:    0.6.0
 */