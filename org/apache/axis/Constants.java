/*     */ package org.apache.axis;
/*     */ 
/*     */ import javax.xml.namespace.QName;
/*     */ import org.apache.axis.schema.SchemaVersion1999;
/*     */ import org.apache.axis.schema.SchemaVersion2000;
/*     */ import org.apache.axis.schema.SchemaVersion2001;
/*     */ import org.apache.axis.soap.SOAPConstants;
/*     */ import org.xml.sax.Attributes;
/*     */ 
/*     */ public class Constants
/*     */ {
/*     */   public static final String NS_PREFIX_SOAP_ENV = "soapenv";
/*     */   public static final String NS_PREFIX_SOAP_ENC = "soapenc";
/*     */   public static final String NS_PREFIX_SCHEMA_XSI = "xsi";
/*     */   public static final String NS_PREFIX_SCHEMA_XSD = "xsd";
/*     */   public static final String NS_PREFIX_WSDL = "wsdl";
/*     */   public static final String NS_PREFIX_WSDL_SOAP = "wsdlsoap";
/*     */   public static final String NS_PREFIX_XMLSOAP = "apachesoap";
/*     */   public static final String NS_PREFIX_XML = "xml";
/*     */   public static final String NS_PREFIX_XOP = "xop";
/*     */   public static final String NS_URI_AXIS = "http://xml.apache.org/axis/";
/*     */   public static final String NS_URI_XMLSOAP = "http://xml.apache.org/xml-soap";
/*     */   public static final String NS_URI_JAVA = "http://xml.apache.org/axis/java";
/*  59 */   public static final SOAPConstants DEFAULT_SOAP_VERSION = SOAPConstants.SOAP11_CONSTANTS;
/*     */   public static final String URI_SOAP11_ENV = "http://schemas.xmlsoap.org/soap/envelope/";
/*     */   public static final String URI_SOAP12_ENV = "http://www.w3.org/2003/05/soap-envelope";
/*  69 */   public static final String URI_DEFAULT_SOAP_ENV = DEFAULT_SOAP_VERSION.getEnvelopeURI();
/*     */ 
/*  75 */   public static final String[] URIS_SOAP_ENV = { "http://schemas.xmlsoap.org/soap/envelope/", "http://www.w3.org/2003/05/soap-envelope" };
/*     */   public static final String ENTERPRISE_LOG_CATEGORY = "org.apache.axis.enterprise";
/*     */   public static final String TIME_LOG_CATEGORY = "org.apache.axis.TIME";
/*     */   public static final String EXCEPTION_LOG_CATEGORY = "org.apache.axis.EXCEPTIONS";
/*     */   public static final String ANYCONTENT = "_any";
/*     */   public static final int HTTP_TXR_BUFFER_SIZE = 8192;
/*     */   public static final String WSIBP11_COMPAT_PROPERTY = "axis.ws-i.bp11.compatibility";
/*     */   public static final String URI_LITERAL_ENC = "";
/*     */   public static final String URI_SOAP11_ENC = "http://schemas.xmlsoap.org/soap/encoding/";
/*     */   public static final String URI_SOAP12_ENC = "http://www.w3.org/2003/05/soap-encoding";
/*     */   public static final String URI_SOAP12_NOENC = "http://www.w3.org/2003/05/soap-envelope/encoding/none";
/* 134 */   public static final String URI_DEFAULT_SOAP_ENC = DEFAULT_SOAP_VERSION.getEncodingURI();
/*     */ 
/* 137 */   public static final String[] URIS_SOAP_ENC = { "http://www.w3.org/2003/05/soap-encoding", "http://schemas.xmlsoap.org/soap/encoding/" };
/*     */   public static final String URI_SOAP11_NEXT_ACTOR = "http://schemas.xmlsoap.org/soap/actor/next";
/*     */   public static final String URI_SOAP12_NEXT_ROLE = "http://www.w3.org/2003/05/soap-envelope/role/next";
/*     */ 
/*     */   /** @deprecated */
/*     */   public static final String URI_SOAP12_NEXT_ACTOR = "http://www.w3.org/2003/05/soap-envelope/role/next";
/*     */   public static final String URI_SOAP12_RPC = "http://www.w3.org/2003/05/soap-rpc";
/*     */   public static final String URI_SOAP12_NONE_ROLE = "http://www.w3.org/2003/05/soap-envelope/role/none";
/*     */   public static final String URI_SOAP12_ULTIMATE_ROLE = "http://www.w3.org/2003/05/soap-envelope/role/ultimateReceiver";
/*     */   public static final String URI_SOAP11_HTTP = "http://schemas.xmlsoap.org/soap/http";
/*     */   public static final String URI_SOAP12_HTTP = "http://www.w3.org/2003/05/http";
/*     */   public static final String NS_URI_XMLNS = "http://www.w3.org/2000/xmlns/";
/*     */   public static final String NS_URI_XML = "http://www.w3.org/XML/1998/namespace";
/*     */   public static final String URI_1999_SCHEMA_XSD = "http://www.w3.org/1999/XMLSchema";
/*     */   public static final String URI_2000_SCHEMA_XSD = "http://www.w3.org/2000/10/XMLSchema";
/*     */   public static final String URI_2001_SCHEMA_XSD = "http://www.w3.org/2001/XMLSchema";
/*     */   public static final String URI_DEFAULT_SCHEMA_XSD = "http://www.w3.org/2001/XMLSchema";
/* 298 */   public static final String[] URIS_SCHEMA_XSD = { "http://www.w3.org/1999/XMLSchema", "http://www.w3.org/2000/10/XMLSchema", "http://www.w3.org/2001/XMLSchema" };
/*     */ 
/* 303 */   public static final QName[] QNAMES_NIL = { SchemaVersion2001.QNAME_NIL, SchemaVersion2000.QNAME_NIL, SchemaVersion1999.QNAME_NIL };
/*     */   public static final String URI_1999_SCHEMA_XSI = "http://www.w3.org/1999/XMLSchema-instance";
/*     */   public static final String URI_2000_SCHEMA_XSI = "http://www.w3.org/2000/10/XMLSchema-instance";
/*     */   public static final String URI_2001_SCHEMA_XSI = "http://www.w3.org/2001/XMLSchema-instance";
/*     */   public static final String URI_DEFAULT_SCHEMA_XSI = "http://www.w3.org/2001/XMLSchema-instance";
/* 335 */   public static final String[] URIS_SCHEMA_XSI = { "http://www.w3.org/1999/XMLSchema-instance", "http://www.w3.org/2000/10/XMLSchema-instance", "http://www.w3.org/2001/XMLSchema-instance" };
/*     */   public static final String NS_URI_WSDL11 = "http://schemas.xmlsoap.org/wsdl/";
/* 362 */   public static final String[] NS_URIS_WSDL = { "http://schemas.xmlsoap.org/wsdl/" };
/*     */   public static final String URI_DIME_WSDL = "http://schemas.xmlsoap.org/ws/2002/04/dime/wsdl/";
/*     */   public static final String URI_DIME_CONTENT = "http://schemas.xmlsoap.org/ws/2002/04/content-type/";
/*     */   public static final String URI_DIME_REFERENCE = "http://schemas.xmlsoap.org/ws/2002/04/reference/";
/*     */   public static final String URI_DIME_CLOSED_LAYOUT = "http://schemas.xmlsoap.org/ws/2002/04/dime/closed-layout";
/*     */   public static final String URI_DIME_OPEN_LAYOUT = "http://schemas.xmlsoap.org/ws/2002/04/dime/open-layout";
/*     */   public static final String URI_XOP_INCLUDE = "http://www.w3.org/2004/08/xop/include";
/*     */   public static final String ELEM_XOP_INCLUDE = "Include";
/*     */   public static final String URI_WSDL11_SOAP = "http://schemas.xmlsoap.org/wsdl/soap/";
/*     */   public static final String URI_WSDL12_SOAP = "http://schemas.xmlsoap.org/wsdl/soap12/";
/* 414 */   public static final String[] NS_URIS_WSDL_SOAP = { "http://schemas.xmlsoap.org/wsdl/soap/", "http://schemas.xmlsoap.org/wsdl/soap12/" };
/*     */   public static final String AXIS_SAX = "Axis SAX Mechanism";
/*     */   public static final String ELEM_ENVELOPE = "Envelope";
/*     */   public static final String ELEM_HEADER = "Header";
/*     */   public static final String ELEM_BODY = "Body";
/*     */   public static final String ELEM_FAULT = "Fault";
/*     */   public static final String ELEM_NOTUNDERSTOOD = "NotUnderstood";
/*     */   public static final String ELEM_UPGRADE = "Upgrade";
/*     */   public static final String ELEM_SUPPORTEDENVELOPE = "SupportedEnvelope";
/*     */   public static final String ELEM_FAULT_CODE = "faultcode";
/*     */   public static final String ELEM_FAULT_STRING = "faultstring";
/*     */   public static final String ELEM_FAULT_DETAIL = "detail";
/*     */   public static final String ELEM_FAULT_ACTOR = "faultactor";
/*     */   public static final String ELEM_FAULT_CODE_SOAP12 = "Code";
/*     */   public static final String ELEM_FAULT_VALUE_SOAP12 = "Value";
/*     */   public static final String ELEM_FAULT_SUBCODE_SOAP12 = "Subcode";
/*     */   public static final String ELEM_FAULT_REASON_SOAP12 = "Reason";
/*     */   public static final String ELEM_FAULT_NODE_SOAP12 = "Node";
/*     */   public static final String ELEM_FAULT_ROLE_SOAP12 = "Role";
/*     */   public static final String ELEM_FAULT_DETAIL_SOAP12 = "Detail";
/*     */   public static final String ELEM_TEXT_SOAP12 = "Text";
/*     */   public static final String ATTR_MUST_UNDERSTAND = "mustUnderstand";
/*     */   public static final String ATTR_ENCODING_STYLE = "encodingStyle";
/*     */   public static final String ATTR_ACTOR = "actor";
/*     */   public static final String ATTR_ROLE = "role";
/*     */   public static final String ATTR_RELAY = "relay";
/*     */   public static final String ATTR_ROOT = "root";
/*     */   public static final String ATTR_ID = "id";
/*     */   public static final String ATTR_HREF = "href";
/*     */   public static final String ATTR_REF = "ref";
/*     */   public static final String ATTR_QNAME = "qname";
/*     */   public static final String ATTR_ARRAY_TYPE = "arrayType";
/*     */   public static final String ATTR_ITEM_TYPE = "itemType";
/*     */   public static final String ATTR_ARRAY_SIZE = "arraySize";
/*     */   public static final String ATTR_OFFSET = "offset";
/*     */   public static final String ATTR_POSITION = "position";
/*     */   public static final String ATTR_TYPE = "type";
/*     */   public static final String ATTR_HANDLERINFOCHAIN = "handlerInfoChain";
/*     */   public static final String FAULT_CLIENT = "Client";
/*     */   public static final String FAULT_SERVER_GENERAL = "Server.generalException";
/*     */   public static final String FAULT_SERVER_USER = "Server.userException";
/* 489 */   public static final QName FAULT_VERSIONMISMATCH = new QName("http://schemas.xmlsoap.org/soap/envelope/", "VersionMismatch");
/*     */ 
/* 492 */   public static final QName FAULT_MUSTUNDERSTAND = new QName("http://schemas.xmlsoap.org/soap/envelope/", "MustUnderstand");
/*     */ 
/* 496 */   public static final QName FAULT_SOAP12_MUSTUNDERSTAND = new QName("http://www.w3.org/2003/05/soap-envelope", "MustUnderstand");
/*     */ 
/* 499 */   public static final QName FAULT_SOAP12_VERSIONMISMATCH = new QName("http://www.w3.org/2003/05/soap-envelope", "VersionMismatch");
/*     */ 
/* 502 */   public static final QName FAULT_SOAP12_DATAENCODINGUNKNOWN = new QName("http://www.w3.org/2003/05/soap-envelope", "DataEncodingUnknown");
/*     */ 
/* 505 */   public static final QName FAULT_SOAP12_SENDER = new QName("http://www.w3.org/2003/05/soap-envelope", "Sender");
/*     */ 
/* 508 */   public static final QName FAULT_SOAP12_RECEIVER = new QName("http://www.w3.org/2003/05/soap-envelope", "Receiver");
/*     */ 
/* 512 */   public static final QName FAULT_SUBCODE_BADARGS = new QName("http://www.w3.org/2003/05/soap-rpc", "BadArguments");
/*     */ 
/* 514 */   public static final QName FAULT_SUBCODE_PROC_NOT_PRESENT = new QName("http://www.w3.org/2003/05/soap-rpc", "ProcedureNotPresent");
/*     */ 
/* 519 */   public static final QName QNAME_FAULTCODE = new QName("", "faultcode");
/*     */ 
/* 521 */   public static final QName QNAME_FAULTSTRING = new QName("", "faultstring");
/*     */ 
/* 523 */   public static final QName QNAME_FAULTACTOR = new QName("", "faultactor");
/*     */ 
/* 525 */   public static final QName QNAME_FAULTDETAILS = new QName("", "detail");
/*     */ 
/* 528 */   public static final QName QNAME_FAULTCODE_SOAP12 = new QName("http://www.w3.org/2003/05/soap-envelope", "Code");
/*     */ 
/* 530 */   public static final QName QNAME_FAULTVALUE_SOAP12 = new QName("http://www.w3.org/2003/05/soap-envelope", "Value");
/*     */ 
/* 532 */   public static final QName QNAME_FAULTSUBCODE_SOAP12 = new QName("http://www.w3.org/2003/05/soap-envelope", "Subcode");
/*     */ 
/* 534 */   public static final QName QNAME_FAULTREASON_SOAP12 = new QName("http://www.w3.org/2003/05/soap-envelope", "Reason");
/*     */ 
/* 536 */   public static final QName QNAME_TEXT_SOAP12 = new QName("http://www.w3.org/2003/05/soap-envelope", "Text");
/*     */ 
/* 539 */   public static final QName QNAME_FAULTNODE_SOAP12 = new QName("http://www.w3.org/2003/05/soap-envelope", "Node");
/*     */ 
/* 541 */   public static final QName QNAME_FAULTROLE_SOAP12 = new QName("http://www.w3.org/2003/05/soap-envelope", "Role");
/*     */ 
/* 543 */   public static final QName QNAME_FAULTDETAIL_SOAP12 = new QName("http://www.w3.org/2003/05/soap-envelope", "Detail");
/*     */ 
/* 545 */   public static final QName QNAME_NOTUNDERSTOOD = new QName("http://www.w3.org/2003/05/soap-envelope", "NotUnderstood");
/*     */ 
/* 549 */   public static final QName XSD_STRING = new QName("http://www.w3.org/2001/XMLSchema", "string");
/* 550 */   public static final QName XSD_BOOLEAN = new QName("http://www.w3.org/2001/XMLSchema", "boolean");
/* 551 */   public static final QName XSD_DOUBLE = new QName("http://www.w3.org/2001/XMLSchema", "double");
/* 552 */   public static final QName XSD_FLOAT = new QName("http://www.w3.org/2001/XMLSchema", "float");
/* 553 */   public static final QName XSD_INT = new QName("http://www.w3.org/2001/XMLSchema", "int");
/* 554 */   public static final QName XSD_INTEGER = new QName("http://www.w3.org/2001/XMLSchema", "integer");
/* 555 */   public static final QName XSD_LONG = new QName("http://www.w3.org/2001/XMLSchema", "long");
/* 556 */   public static final QName XSD_SHORT = new QName("http://www.w3.org/2001/XMLSchema", "short");
/* 557 */   public static final QName XSD_BYTE = new QName("http://www.w3.org/2001/XMLSchema", "byte");
/* 558 */   public static final QName XSD_DECIMAL = new QName("http://www.w3.org/2001/XMLSchema", "decimal");
/* 559 */   public static final QName XSD_BASE64 = new QName("http://www.w3.org/2001/XMLSchema", "base64Binary");
/* 560 */   public static final QName XSD_HEXBIN = new QName("http://www.w3.org/2001/XMLSchema", "hexBinary");
/* 561 */   public static final QName XSD_ANYSIMPLETYPE = new QName("http://www.w3.org/2001/XMLSchema", "anySimpleType");
/* 562 */   public static final QName XSD_ANYTYPE = new QName("http://www.w3.org/2001/XMLSchema", "anyType");
/* 563 */   public static final QName XSD_ANY = new QName("http://www.w3.org/2001/XMLSchema", "any");
/* 564 */   public static final QName XSD_QNAME = new QName("http://www.w3.org/2001/XMLSchema", "QName");
/* 565 */   public static final QName XSD_DATETIME = new QName("http://www.w3.org/2001/XMLSchema", "dateTime");
/* 566 */   public static final QName XSD_DATE = new QName("http://www.w3.org/2001/XMLSchema", "date");
/* 567 */   public static final QName XSD_TIME = new QName("http://www.w3.org/2001/XMLSchema", "time");
/* 568 */   public static final QName XSD_TIMEINSTANT1999 = new QName("http://www.w3.org/1999/XMLSchema", "timeInstant");
/* 569 */   public static final QName XSD_TIMEINSTANT2000 = new QName("http://www.w3.org/2000/10/XMLSchema", "timeInstant");
/*     */ 
/* 571 */   public static final QName XSD_NORMALIZEDSTRING = new QName("http://www.w3.org/2001/XMLSchema", "normalizedString");
/* 572 */   public static final QName XSD_TOKEN = new QName("http://www.w3.org/2001/XMLSchema", "token");
/*     */ 
/* 574 */   public static final QName XSD_UNSIGNEDLONG = new QName("http://www.w3.org/2001/XMLSchema", "unsignedLong");
/* 575 */   public static final QName XSD_UNSIGNEDINT = new QName("http://www.w3.org/2001/XMLSchema", "unsignedInt");
/* 576 */   public static final QName XSD_UNSIGNEDSHORT = new QName("http://www.w3.org/2001/XMLSchema", "unsignedShort");
/* 577 */   public static final QName XSD_UNSIGNEDBYTE = new QName("http://www.w3.org/2001/XMLSchema", "unsignedByte");
/* 578 */   public static final QName XSD_POSITIVEINTEGER = new QName("http://www.w3.org/2001/XMLSchema", "positiveInteger");
/* 579 */   public static final QName XSD_NEGATIVEINTEGER = new QName("http://www.w3.org/2001/XMLSchema", "negativeInteger");
/* 580 */   public static final QName XSD_NONNEGATIVEINTEGER = new QName("http://www.w3.org/2001/XMLSchema", "nonNegativeInteger");
/* 581 */   public static final QName XSD_NONPOSITIVEINTEGER = new QName("http://www.w3.org/2001/XMLSchema", "nonPositiveInteger");
/*     */ 
/* 583 */   public static final QName XSD_YEARMONTH = new QName("http://www.w3.org/2001/XMLSchema", "gYearMonth");
/* 584 */   public static final QName XSD_MONTHDAY = new QName("http://www.w3.org/2001/XMLSchema", "gMonthDay");
/* 585 */   public static final QName XSD_YEAR = new QName("http://www.w3.org/2001/XMLSchema", "gYear");
/* 586 */   public static final QName XSD_MONTH = new QName("http://www.w3.org/2001/XMLSchema", "gMonth");
/* 587 */   public static final QName XSD_DAY = new QName("http://www.w3.org/2001/XMLSchema", "gDay");
/* 588 */   public static final QName XSD_DURATION = new QName("http://www.w3.org/2001/XMLSchema", "duration");
/*     */ 
/* 590 */   public static final QName XSD_NAME = new QName("http://www.w3.org/2001/XMLSchema", "Name");
/* 591 */   public static final QName XSD_NCNAME = new QName("http://www.w3.org/2001/XMLSchema", "NCName");
/* 592 */   public static final QName XSD_NMTOKEN = new QName("http://www.w3.org/2001/XMLSchema", "NMTOKEN");
/* 593 */   public static final QName XSD_NMTOKENS = new QName("http://www.w3.org/2001/XMLSchema", "NMTOKENS");
/* 594 */   public static final QName XSD_NOTATION = new QName("http://www.w3.org/2001/XMLSchema", "NOTATION");
/* 595 */   public static final QName XSD_ENTITY = new QName("http://www.w3.org/2001/XMLSchema", "ENTITY");
/* 596 */   public static final QName XSD_ENTITIES = new QName("http://www.w3.org/2001/XMLSchema", "ENTITIES");
/* 597 */   public static final QName XSD_IDREF = new QName("http://www.w3.org/2001/XMLSchema", "IDREF");
/* 598 */   public static final QName XSD_IDREFS = new QName("http://www.w3.org/2001/XMLSchema", "IDREFS");
/* 599 */   public static final QName XSD_ANYURI = new QName("http://www.w3.org/2001/XMLSchema", "anyURI");
/* 600 */   public static final QName XSD_LANGUAGE = new QName("http://www.w3.org/2001/XMLSchema", "language");
/* 601 */   public static final QName XSD_ID = new QName("http://www.w3.org/2001/XMLSchema", "ID");
/* 602 */   public static final QName XSD_SCHEMA = new QName("http://www.w3.org/2001/XMLSchema", "schema");
/*     */ 
/* 604 */   public static final QName XML_LANG = new QName("http://www.w3.org/XML/1998/namespace", "lang");
/*     */ 
/* 606 */   public static final QName SOAP_BASE64 = new QName(URI_DEFAULT_SOAP_ENC, "base64");
/* 607 */   public static final QName SOAP_BASE64BINARY = new QName(URI_DEFAULT_SOAP_ENC, "base64Binary");
/* 608 */   public static final QName SOAP_STRING = new QName(URI_DEFAULT_SOAP_ENC, "string");
/* 609 */   public static final QName SOAP_BOOLEAN = new QName(URI_DEFAULT_SOAP_ENC, "boolean");
/* 610 */   public static final QName SOAP_DOUBLE = new QName(URI_DEFAULT_SOAP_ENC, "double");
/* 611 */   public static final QName SOAP_FLOAT = new QName(URI_DEFAULT_SOAP_ENC, "float");
/* 612 */   public static final QName SOAP_INT = new QName(URI_DEFAULT_SOAP_ENC, "int");
/* 613 */   public static final QName SOAP_LONG = new QName(URI_DEFAULT_SOAP_ENC, "long");
/* 614 */   public static final QName SOAP_SHORT = new QName(URI_DEFAULT_SOAP_ENC, "short");
/* 615 */   public static final QName SOAP_BYTE = new QName(URI_DEFAULT_SOAP_ENC, "byte");
/* 616 */   public static final QName SOAP_INTEGER = new QName(URI_DEFAULT_SOAP_ENC, "integer");
/* 617 */   public static final QName SOAP_DECIMAL = new QName(URI_DEFAULT_SOAP_ENC, "decimal");
/* 618 */   public static final QName SOAP_ARRAY = new QName(URI_DEFAULT_SOAP_ENC, "Array");
/* 619 */   public static final QName SOAP_COMMON_ATTRS11 = new QName("http://schemas.xmlsoap.org/soap/encoding/", "commonAttributes");
/* 620 */   public static final QName SOAP_COMMON_ATTRS12 = new QName("http://www.w3.org/2003/05/soap-encoding", "commonAttributes");
/* 621 */   public static final QName SOAP_ARRAY_ATTRS11 = new QName("http://schemas.xmlsoap.org/soap/encoding/", "arrayAttributes");
/* 622 */   public static final QName SOAP_ARRAY_ATTRS12 = new QName("http://www.w3.org/2003/05/soap-encoding", "arrayAttributes");
/* 623 */   public static final QName SOAP_ARRAY12 = new QName("http://www.w3.org/2003/05/soap-encoding", "Array");
/*     */ 
/* 625 */   public static final QName SOAP_MAP = new QName("http://xml.apache.org/xml-soap", "Map");
/* 626 */   public static final QName SOAP_ELEMENT = new QName("http://xml.apache.org/xml-soap", "Element");
/* 627 */   public static final QName SOAP_DOCUMENT = new QName("http://xml.apache.org/xml-soap", "Document");
/* 628 */   public static final QName SOAP_VECTOR = new QName("http://xml.apache.org/xml-soap", "Vector");
/* 629 */   public static final QName MIME_IMAGE = new QName("http://xml.apache.org/xml-soap", "Image");
/* 630 */   public static final QName MIME_PLAINTEXT = new QName("http://xml.apache.org/xml-soap", "PlainText");
/* 631 */   public static final QName MIME_MULTIPART = new QName("http://xml.apache.org/xml-soap", "Multipart");
/* 632 */   public static final QName MIME_SOURCE = new QName("http://xml.apache.org/xml-soap", "Source");
/* 633 */   public static final QName MIME_OCTETSTREAM = new QName("http://xml.apache.org/xml-soap", "octet-stream");
/* 634 */   public static final QName MIME_DATA_HANDLER = new QName("http://xml.apache.org/xml-soap", "DataHandler");
/*     */ 
/* 637 */   public static final QName QNAME_LITERAL_ITEM = new QName("", "item");
/* 638 */   public static final QName QNAME_RPC_RESULT = new QName("http://www.w3.org/2003/05/soap-rpc", "result");
/*     */ 
/* 643 */   public static final QName QNAME_FAULTDETAIL_STACKTRACE = new QName("http://xml.apache.org/axis/", "stackTrace");
/*     */ 
/* 649 */   public static final QName QNAME_FAULTDETAIL_EXCEPTIONNAME = new QName("http://xml.apache.org/axis/", "exceptionName");
/*     */ 
/* 655 */   public static final QName QNAME_FAULTDETAIL_RUNTIMEEXCEPTION = new QName("http://xml.apache.org/axis/", "isRuntimeException");
/*     */ 
/* 660 */   public static final QName QNAME_FAULTDETAIL_HTTPERRORCODE = new QName("http://xml.apache.org/axis/", "HttpErrorCode");
/*     */ 
/* 665 */   public static final QName QNAME_FAULTDETAIL_NESTEDFAULT = new QName("http://xml.apache.org/axis/", "nestedFault");
/*     */ 
/* 670 */   public static final QName QNAME_FAULTDETAIL_HOSTNAME = new QName("http://xml.apache.org/axis/", "hostname");
/*     */ 
/* 676 */   public static final QName QNAME_NO_SERVICE_FAULT_CODE = new QName("http://xml.apache.org/axis/", "Server.NoService");
/*     */   public static final String MC_JWS_CLASSDIR = "jws.classDir";
/*     */   public static final String MC_HOME_DIR = "home.dir";
/*     */   public static final String MC_RELATIVE_PATH = "path";
/*     */   public static final String MC_REALPATH = "realpath";
/*     */   public static final String MC_CONFIGPATH = "configPath";
/*     */   public static final String MC_REMOTE_ADDR = "remoteaddr";
/*     */   public static final String MC_SERVLET_ENDPOINT_CONTEXT = "servletEndpointContext";
/*     */   public static final String MC_NO_OPERATION_OK = "NoOperationOK";
/*     */   public static final String MC_SINGLE_SOAP_VERSION = "SingleSOAPVersion";
/*     */   public static final String JWS_DEFAULT_FILE_EXTENSION = ".jws";
/*     */   public static final int DEFAULT_MESSAGE_TIMEOUT = 600000;
/*     */   public static final String MIME_CT_APPLICATION_OCTETSTREAM = "application/octet-stream";
/*     */   public static final String MIME_CT_TEXT_PLAIN = "text/plain";
/*     */   public static final String MIME_CT_IMAGE_JPEG = "image/jpeg";
/*     */   public static final String MIME_CT_IMAGE_GIF = "image/gif";
/*     */   public static final String MIME_CT_TEXT_XML = "text/xml";
/*     */   public static final String MIME_CT_APPLICATION_XML = "application/xml";
/*     */   public static final String MIME_CT_MULTIPART_PREFIX = "multipart/";
/*     */ 
/*     */   public static boolean isSOAP_ENV(String s)
/*     */   {
/* 114 */     for (int i = 0; i < URIS_SOAP_ENV.length; i++) {
/* 115 */       if (URIS_SOAP_ENV[i].equals(s)) {
/* 116 */         return true;
/*     */       }
/*     */     }
/* 119 */     return false;
/*     */   }
/*     */ 
/*     */   public static boolean isSOAP_ENC(String s)
/*     */   {
/* 150 */     for (int i = 0; i < URIS_SOAP_ENC.length; i++) {
/* 151 */       if (URIS_SOAP_ENC[i].equals(s)) {
/* 152 */         return true;
/*     */       }
/*     */     }
/* 155 */     return false;
/*     */   }
/*     */ 
/*     */   public static String getValue(Attributes attributes, String[] search, String localPart)
/*     */   {
/* 170 */     if ((attributes == null) || (search == null) || (localPart == null)) {
/* 171 */       return null;
/*     */     }
/*     */ 
/* 174 */     int len = attributes.getLength();
/*     */ 
/* 176 */     if (len == 0) {
/* 177 */       return null;
/*     */     }
/*     */ 
/* 180 */     for (int i = 0; i < len; i++) {
/* 181 */       if (attributes.getLocalName(i).equals(localPart)) {
/* 182 */         String uri = attributes.getURI(i);
/* 183 */         for (int j = 0; j < search.length; j++) {
/* 184 */           if (search[j].equals(uri)) return attributes.getValue(i);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 189 */     return null;
/*     */   }
/*     */ 
/*     */   public static String getValue(Attributes attributes, QName[] search)
/*     */   {
/* 202 */     if ((attributes == null) || (search == null)) {
/* 203 */       return null;
/*     */     }
/* 205 */     if (attributes.getLength() == 0) return null;
/*     */ 
/* 207 */     String value = null;
/* 208 */     for (int i = 0; (value == null) && (i < search.length); i++) {
/* 209 */       value = attributes.getValue(search[i].getNamespaceURI(), search[i].getLocalPart());
/*     */     }
/*     */ 
/* 213 */     return value;
/*     */   }
/*     */ 
/*     */   public static boolean equals(QName first, QName second)
/*     */   {
/* 225 */     if (first == second) {
/* 226 */       return true;
/*     */     }
/* 228 */     if ((first == null) || (second == null)) {
/* 229 */       return false;
/*     */     }
/* 231 */     if (first.equals(second)) {
/* 232 */       return true;
/*     */     }
/* 234 */     if (!first.getLocalPart().equals(second.getLocalPart())) {
/* 235 */       return false;
/*     */     }
/*     */ 
/* 238 */     String namespaceURI = first.getNamespaceURI();
/* 239 */     String[] search = null;
/* 240 */     if (namespaceURI.equals(URI_DEFAULT_SOAP_ENC))
/* 241 */       search = URIS_SOAP_ENC;
/* 242 */     else if (namespaceURI.equals(URI_DEFAULT_SOAP_ENV))
/* 243 */       search = URIS_SOAP_ENV;
/* 244 */     else if (namespaceURI.equals("http://www.w3.org/2001/XMLSchema"))
/* 245 */       search = URIS_SCHEMA_XSD;
/* 246 */     else if (namespaceURI.equals("http://www.w3.org/2001/XMLSchema-instance"))
/* 247 */       search = URIS_SCHEMA_XSI;
/*     */     else {
/* 249 */       search = new String[] { namespaceURI };
/*     */     }
/* 251 */     for (int i = 0; i < search.length; i++) {
/* 252 */       if (search[i].equals(second.getNamespaceURI())) {
/* 253 */         return true;
/*     */       }
/*     */     }
/* 256 */     return false;
/*     */   }
/*     */ 
/*     */   public static boolean isSchemaXSD(String s)
/*     */   {
/* 316 */     for (int i = 0; i < URIS_SCHEMA_XSD.length; i++) {
/* 317 */       if (URIS_SCHEMA_XSD[i].equals(s)) {
/* 318 */         return true;
/*     */       }
/*     */     }
/* 321 */     return false;
/*     */   }
/*     */ 
/*     */   public static boolean isSchemaXSI(String s)
/*     */   {
/* 348 */     for (int i = 0; i < URIS_SCHEMA_XSI.length; i++) {
/* 349 */       if (URIS_SCHEMA_XSI[i].equals(s)) {
/* 350 */         return true;
/*     */       }
/*     */     }
/* 353 */     return false;
/*     */   }
/*     */ 
/*     */   public static boolean isWSDL(String s)
/*     */   {
/* 373 */     for (int i = 0; i < NS_URIS_WSDL.length; i++) {
/* 374 */       if (NS_URIS_WSDL[i].equals(s)) {
/* 375 */         return true;
/*     */       }
/*     */     }
/* 378 */     return false;
/*     */   }
/*     */ 
/*     */   public static boolean isWSDLSOAP(String s)
/*     */   {
/* 427 */     for (int i = 0; i < NS_URIS_WSDL_SOAP.length; i++) {
/* 428 */       if (NS_URIS_WSDL_SOAP[i].equals(s)) {
/* 429 */         return true;
/*     */       }
/*     */     }
/* 432 */     return false;
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.Constants
 * JD-Core Version:    0.6.0
 */