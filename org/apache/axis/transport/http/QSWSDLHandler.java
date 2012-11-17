/*     */ package org.apache.axis.transport.http;
/*     */ 
/*     */ import java.io.PrintWriter;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.Set;
/*     */ import javax.servlet.http.HttpServletResponse;
/*     */ import javax.xml.namespace.QName;
/*     */ import org.apache.axis.AxisEngine;
/*     */ import org.apache.axis.AxisFault;
/*     */ import org.apache.axis.ConfigurationException;
/*     */ import org.apache.axis.Constants;
/*     */ import org.apache.axis.EngineConfiguration;
/*     */ import org.apache.axis.MessageContext;
/*     */ import org.apache.axis.description.ServiceDesc;
/*     */ import org.apache.axis.handlers.soap.SOAPService;
/*     */ import org.apache.axis.server.AxisServer;
/*     */ import org.apache.axis.utils.Messages;
/*     */ import org.apache.axis.utils.XMLUtils;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.w3c.dom.Document;
/*     */ import org.w3c.dom.Element;
/*     */ import org.w3c.dom.NamedNodeMap;
/*     */ import org.w3c.dom.Node;
/*     */ import org.w3c.dom.NodeList;
/*     */ 
/*     */ public class QSWSDLHandler extends AbstractQueryStringHandler
/*     */ {
/*     */   public void invoke(MessageContext msgContext)
/*     */     throws AxisFault
/*     */   {
/*  60 */     configureFromContext(msgContext);
/*  61 */     AxisServer engine = (AxisServer)msgContext.getProperty("transport.http.plugin.engine");
/*     */ 
/*  63 */     PrintWriter writer = (PrintWriter)msgContext.getProperty("transport.http.plugin.writer");
/*     */ 
/*  65 */     HttpServletResponse response = (HttpServletResponse)msgContext.getProperty(HTTPConstants.MC_HTTP_SERVLETRESPONSE);
/*     */     try
/*     */     {
/*  68 */       engine.generateWSDL(msgContext);
/*  69 */       Document wsdlDoc = (Document)msgContext.getProperty("WSDL");
/*  70 */       if (wsdlDoc != null) {
/*     */         try {
/*  72 */           updateSoapAddressLocationURLs(wsdlDoc, msgContext);
/*     */         } catch (RuntimeException re) {
/*  74 */           this.log.warn("Failed to update soap:address location URL(s) in WSDL.", re);
/*     */         }
/*     */ 
/*  78 */         response.setContentType("text/xml; charset=" + XMLUtils.getEncoding().toLowerCase());
/*     */ 
/*  81 */         reportWSDL(wsdlDoc, writer);
/*     */       } else {
/*  83 */         if (this.log.isDebugEnabled()) {
/*  84 */           this.log.debug("processWsdlRequest: failed to create WSDL");
/*     */         }
/*  86 */         reportNoWSDL(response, writer, "noWSDL02", null);
/*     */       }
/*     */     }
/*     */     catch (AxisFault axisFault) {
/*  90 */       if (axisFault.getFaultCode().equals(Constants.QNAME_NO_SERVICE_FAULT_CODE))
/*     */       {
/*  93 */         processAxisFault(axisFault);
/*     */ 
/*  96 */         response.setStatus(404);
/*  97 */         reportNoWSDL(response, writer, "noWSDL01", axisFault);
/*     */       }
/*     */       else {
/* 100 */         throw axisFault;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void reportWSDL(Document doc, PrintWriter writer)
/*     */   {
/* 112 */     XMLUtils.PrettyDocumentToWriter(doc, writer);
/*     */   }
/*     */ 
/*     */   public void reportNoWSDL(HttpServletResponse res, PrintWriter writer, String moreDetailCode, AxisFault axisFault)
/*     */   {
/* 125 */     res.setStatus(404);
/* 126 */     res.setContentType("text/html");
/* 127 */     writer.println("<h2>" + Messages.getMessage("error00") + "</h2>");
/* 128 */     writer.println("<p>" + Messages.getMessage("noWSDL00") + "</p>");
/* 129 */     if (moreDetailCode != null) {
/* 130 */       writer.println("<p>" + Messages.getMessage(moreDetailCode) + "</p>");
/*     */     }
/*     */ 
/* 133 */     if ((axisFault != null) && (isDevelopment()))
/*     */     {
/* 135 */       writeFault(writer, axisFault);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void updateSoapAddressLocationURLs(Document wsdlDoc, MessageContext msgContext)
/*     */     throws AxisFault
/*     */   {
/*     */     try
/*     */     {
/* 153 */       deployedServiceNames = getDeployedServiceNames(msgContext);
/*     */     }
/*     */     catch (ConfigurationException ce)
/*     */     {
/*     */       Set deployedServiceNames;
/* 156 */       throw new AxisFault("Failed to determine deployed service names.", ce);
/*     */     }
/*     */     Set deployedServiceNames;
/* 158 */     NodeList wsdlPorts = wsdlDoc.getDocumentElement().getElementsByTagNameNS("http://schemas.xmlsoap.org/wsdl/", "port");
/* 159 */     if (wsdlPorts != null) {
/* 160 */       String endpointURL = getEndpointURL(msgContext);
/* 161 */       String baseEndpointURL = endpointURL.substring(0, endpointURL.lastIndexOf("/") + 1);
/* 162 */       for (int i = 0; i < wsdlPorts.getLength(); i++) {
/* 163 */         Element portElem = (Element)wsdlPorts.item(i);
/* 164 */         Node portNameAttrib = portElem.getAttributes().getNamedItem("name");
/* 165 */         if (portNameAttrib == null) {
/*     */           continue;
/*     */         }
/* 168 */         String portName = portNameAttrib.getNodeValue();
/* 169 */         NodeList soapAddresses = portElem.getElementsByTagNameNS("http://schemas.xmlsoap.org/wsdl/soap/", "address");
/* 170 */         if ((soapAddresses == null) || (soapAddresses.getLength() == 0)) {
/* 171 */           soapAddresses = portElem.getElementsByTagNameNS("http://schemas.xmlsoap.org/wsdl/soap12/", "address");
/*     */         }
/* 173 */         if (soapAddresses != null)
/* 174 */           for (int j = 0; j < soapAddresses.getLength(); j++) {
/* 175 */             Element addressElem = (Element)soapAddresses.item(j);
/* 176 */             Node addressLocationAttrib = addressElem.getAttributes().getNamedItem("location");
/* 177 */             if (addressLocationAttrib == null)
/*     */             {
/*     */               continue;
/*     */             }
/* 181 */             String addressLocation = addressLocationAttrib.getNodeValue();
/* 182 */             String addressServiceName = addressLocation.substring(addressLocation.lastIndexOf("/") + 1);
/* 183 */             String newServiceName = getNewServiceName(deployedServiceNames, addressServiceName, portName);
/* 184 */             if (newServiceName != null) {
/* 185 */               String newAddressLocation = baseEndpointURL + newServiceName;
/* 186 */               addressLocationAttrib.setNodeValue(newAddressLocation);
/* 187 */               this.log.debug("Setting soap:address location values in WSDL for port " + portName + " to: " + newAddressLocation);
/*     */             }
/*     */             else
/*     */             {
/* 194 */               this.log.debug("For WSDL port: " + portName + ", unable to match port name or the last component of " + "the SOAP address url with a " + "service name deployed in server-config.wsdd.  Leaving SOAP address: " + addressLocation + " unmodified.");
/*     */             }
/*     */           }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private String getNewServiceName(Set deployedServiceNames, String currentServiceEndpointName, String portName)
/*     */   {
/* 206 */     String endpointName = null;
/* 207 */     if (deployedServiceNames.contains(currentServiceEndpointName)) {
/* 208 */       endpointName = currentServiceEndpointName;
/*     */     }
/* 210 */     else if (deployedServiceNames.contains(portName)) {
/* 211 */       endpointName = portName;
/*     */     }
/* 213 */     return endpointName;
/*     */   }
/*     */ 
/*     */   private Set getDeployedServiceNames(MessageContext msgContext) throws ConfigurationException {
/* 217 */     Set serviceNames = new HashSet();
/* 218 */     Iterator deployedServicesIter = msgContext.getAxisEngine().getConfig().getDeployedServices();
/* 219 */     while (deployedServicesIter.hasNext()) {
/* 220 */       ServiceDesc serviceDesc = (ServiceDesc)deployedServicesIter.next();
/* 221 */       serviceNames.add(serviceDesc.getName());
/*     */     }
/* 223 */     return serviceNames;
/*     */   }
/*     */ 
/*     */   protected String getEndpointURL(MessageContext msgContext)
/*     */     throws AxisFault
/*     */   {
/* 236 */     String locationUrl = msgContext.getStrProp("axis.wsdlgen.serv.loc.url");
/*     */ 
/* 238 */     if (locationUrl == null)
/*     */     {
/* 240 */       locationUrl = msgContext.getService().getInitializedServiceDesc(msgContext).getEndpointURL();
/*     */     }
/*     */ 
/* 245 */     if (locationUrl == null)
/*     */     {
/* 247 */       locationUrl = msgContext.getStrProp("transport.url");
/*     */     }
/* 249 */     return locationUrl;
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.transport.http.QSWSDLHandler
 * JD-Core Version:    0.6.0
 */