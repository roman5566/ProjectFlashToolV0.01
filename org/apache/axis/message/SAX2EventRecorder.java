/*     */ package org.apache.axis.message;
/*     */ 
/*     */ import org.apache.axis.encoding.DeserializationContext;
/*     */ import org.xml.sax.Attributes;
/*     */ import org.xml.sax.ContentHandler;
/*     */ import org.xml.sax.Locator;
/*     */ import org.xml.sax.SAXException;
/*     */ import org.xml.sax.ext.LexicalHandler;
/*     */ 
/*     */ public class SAX2EventRecorder
/*     */ {
/*  29 */   private static final Integer Z = new Integer(0);
/*     */ 
/*  31 */   private static final Integer STATE_SET_DOCUMENT_LOCATOR = new Integer(0);
/*  32 */   private static final Integer STATE_START_DOCUMENT = new Integer(1);
/*  33 */   private static final Integer STATE_END_DOCUMENT = new Integer(2);
/*  34 */   private static final Integer STATE_START_PREFIX_MAPPING = new Integer(3);
/*  35 */   private static final Integer STATE_END_PREFIX_MAPPING = new Integer(4);
/*  36 */   private static final Integer STATE_START_ELEMENT = new Integer(5);
/*  37 */   private static final Integer STATE_END_ELEMENT = new Integer(6);
/*  38 */   private static final Integer STATE_CHARACTERS = new Integer(7);
/*  39 */   private static final Integer STATE_IGNORABLE_WHITESPACE = new Integer(8);
/*  40 */   private static final Integer STATE_PROCESSING_INSTRUCTION = new Integer(9);
/*  41 */   private static final Integer STATE_SKIPPED_ENTITY = new Integer(10);
/*     */ 
/*  45 */   private static final Integer STATE_NEWELEMENT = new Integer(11);
/*     */ 
/*  48 */   private static final Integer STATE_START_DTD = new Integer(12);
/*  49 */   private static final Integer STATE_END_DTD = new Integer(13);
/*  50 */   private static final Integer STATE_START_ENTITY = new Integer(14);
/*  51 */   private static final Integer STATE_END_ENTITY = new Integer(15);
/*  52 */   private static final Integer STATE_START_CDATA = new Integer(16);
/*  53 */   private static final Integer STATE_END_CDATA = new Integer(17);
/*  54 */   private static final Integer STATE_COMMENT = new Integer(18);
/*     */   Locator locator;
/*     */   objArrayVector events;
/*     */ 
/*     */   public SAX2EventRecorder()
/*     */   {
/*  57 */     this.events = new objArrayVector();
/*     */   }
/*     */   public void clear() {
/*  60 */     this.locator = null;
/*  61 */     this.events = new objArrayVector();
/*     */   }
/*     */ 
/*     */   public int getLength() {
/*  65 */     return this.events.getLength();
/*     */   }
/*     */ 
/*     */   public int setDocumentLocator(Locator p1) {
/*  69 */     this.locator = p1;
/*  70 */     return this.events.add(STATE_SET_DOCUMENT_LOCATOR, Z, Z, Z, Z);
/*     */   }
/*     */   public int startDocument() {
/*  73 */     return this.events.add(STATE_START_DOCUMENT, Z, Z, Z, Z);
/*     */   }
/*     */   public int endDocument() {
/*  76 */     return this.events.add(STATE_END_DOCUMENT, Z, Z, Z, Z);
/*     */   }
/*     */   public int startPrefixMapping(String p1, String p2) {
/*  79 */     return this.events.add(STATE_START_PREFIX_MAPPING, p1, p2, Z, Z);
/*     */   }
/*     */   public int endPrefixMapping(String p1) {
/*  82 */     return this.events.add(STATE_END_PREFIX_MAPPING, p1, Z, Z, Z);
/*     */   }
/*     */   public int startElement(String p1, String p2, String p3, Attributes p4) {
/*  85 */     return this.events.add(STATE_START_ELEMENT, p1, p2, p3, p4);
/*     */   }
/*     */   public int endElement(String p1, String p2, String p3) {
/*  88 */     return this.events.add(STATE_END_ELEMENT, p1, p2, p3, Z);
/*     */   }
/*     */   public int characters(char[] p1, int p2, int p3) {
/*  91 */     return this.events.add(STATE_CHARACTERS, clone(p1, p2, p3), Z, Z, Z);
/*     */   }
/*     */ 
/*     */   public int ignorableWhitespace(char[] p1, int p2, int p3)
/*     */   {
/*  96 */     return this.events.add(STATE_IGNORABLE_WHITESPACE, clone(p1, p2, p3), Z, Z, Z);
/*     */   }
/*     */ 
/*     */   public int processingInstruction(String p1, String p2)
/*     */   {
/* 101 */     return this.events.add(STATE_PROCESSING_INSTRUCTION, p1, p2, Z, Z);
/*     */   }
/*     */   public int skippedEntity(String p1) {
/* 104 */     return this.events.add(STATE_SKIPPED_ENTITY, p1, Z, Z, Z);
/*     */   }
/*     */ 
/*     */   public void startDTD(String name, String publicId, String systemId)
/*     */   {
/* 110 */     this.events.add(STATE_START_DTD, name, publicId, systemId, Z);
/*     */   }
/*     */   public void endDTD() {
/* 113 */     this.events.add(STATE_END_DTD, Z, Z, Z, Z);
/*     */   }
/*     */   public void startEntity(String name) {
/* 116 */     this.events.add(STATE_START_ENTITY, name, Z, Z, Z);
/*     */   }
/*     */   public void endEntity(String name) {
/* 119 */     this.events.add(STATE_END_ENTITY, name, Z, Z, Z);
/*     */   }
/*     */   public void startCDATA() {
/* 122 */     this.events.add(STATE_START_CDATA, Z, Z, Z, Z);
/*     */   }
/*     */   public void endCDATA() {
/* 125 */     this.events.add(STATE_END_CDATA, Z, Z, Z, Z);
/*     */   }
/*     */ 
/*     */   public void comment(char[] ch, int start, int length)
/*     */   {
/* 130 */     this.events.add(STATE_COMMENT, clone(ch, start, length), Z, Z, Z);
/*     */   }
/*     */ 
/*     */   public int newElement(MessageElement elem)
/*     */   {
/* 136 */     return this.events.add(STATE_NEWELEMENT, elem, Z, Z, Z);
/*     */   }
/*     */ 
/*     */   public void replay(ContentHandler handler) throws SAXException {
/* 140 */     if (this.events.getLength() > 0)
/* 141 */       replay(0, this.events.getLength() - 1, handler);
/*     */   }
/*     */ 
/*     */   public void replay(int start, int stop, ContentHandler handler)
/*     */     throws SAXException
/*     */   {
/* 147 */     if ((start == 0) && (stop == -1)) {
/* 148 */       replay(handler);
/* 149 */       return;
/*     */     }
/*     */ 
/* 152 */     if ((stop + 1 > this.events.getLength()) || (stop < start))
/*     */     {
/* 154 */       return;
/*     */     }
/*     */ 
/* 157 */     LexicalHandler lexicalHandler = null;
/* 158 */     if ((handler instanceof LexicalHandler)) {
/* 159 */       lexicalHandler = (LexicalHandler)handler;
/*     */     }
/*     */ 
/* 162 */     for (int n = start; n <= stop; n++) {
/* 163 */       Object event = this.events.get(n, 0);
/* 164 */       if (event == STATE_START_ELEMENT) {
/* 165 */         handler.startElement((String)this.events.get(n, 1), (String)this.events.get(n, 2), (String)this.events.get(n, 3), (Attributes)this.events.get(n, 4));
/*     */       }
/* 170 */       else if (event == STATE_END_ELEMENT) {
/* 171 */         handler.endElement((String)this.events.get(n, 1), (String)this.events.get(n, 2), (String)this.events.get(n, 3));
/*     */       }
/* 175 */       else if (event == STATE_CHARACTERS) {
/* 176 */         char[] data = (char[])this.events.get(n, 1);
/* 177 */         handler.characters(data, 0, data.length);
/*     */       }
/* 179 */       else if (event == STATE_IGNORABLE_WHITESPACE) {
/* 180 */         char[] data = (char[])this.events.get(n, 1);
/* 181 */         handler.ignorableWhitespace(data, 0, data.length);
/*     */       }
/* 183 */       else if (event == STATE_PROCESSING_INSTRUCTION) {
/* 184 */         handler.processingInstruction((String)this.events.get(n, 1), (String)this.events.get(n, 2));
/*     */       }
/* 187 */       else if (event == STATE_SKIPPED_ENTITY) {
/* 188 */         handler.skippedEntity((String)this.events.get(n, 1));
/*     */       }
/* 190 */       else if (event == STATE_SET_DOCUMENT_LOCATOR) {
/* 191 */         handler.setDocumentLocator(this.locator);
/*     */       }
/* 193 */       else if (event == STATE_START_DOCUMENT) {
/* 194 */         handler.startDocument();
/*     */       }
/* 196 */       else if (event == STATE_END_DOCUMENT) {
/* 197 */         handler.endDocument();
/*     */       }
/* 199 */       else if (event == STATE_START_PREFIX_MAPPING) {
/* 200 */         handler.startPrefixMapping((String)this.events.get(n, 1), (String)this.events.get(n, 2));
/*     */       }
/* 203 */       else if (event == STATE_END_PREFIX_MAPPING) {
/* 204 */         handler.endPrefixMapping((String)this.events.get(n, 1));
/*     */       }
/* 206 */       else if ((event == STATE_START_DTD) && (lexicalHandler != null)) {
/* 207 */         lexicalHandler.startDTD((String)this.events.get(n, 1), (String)this.events.get(n, 2), (String)this.events.get(n, 3));
/*     */       }
/* 210 */       else if ((event == STATE_END_DTD) && (lexicalHandler != null)) {
/* 211 */         lexicalHandler.endDTD();
/*     */       }
/* 213 */       else if ((event == STATE_START_ENTITY) && (lexicalHandler != null)) {
/* 214 */         lexicalHandler.startEntity((String)this.events.get(n, 1));
/*     */       }
/* 216 */       else if ((event == STATE_END_ENTITY) && (lexicalHandler != null)) {
/* 217 */         lexicalHandler.endEntity((String)this.events.get(n, 1));
/*     */       }
/* 219 */       else if ((event == STATE_START_CDATA) && (lexicalHandler != null)) {
/* 220 */         lexicalHandler.startCDATA();
/*     */       }
/* 222 */       else if ((event == STATE_END_CDATA) && (lexicalHandler != null)) {
/* 223 */         lexicalHandler.endCDATA();
/*     */       }
/* 225 */       else if ((event == STATE_COMMENT) && (lexicalHandler != null)) {
/* 226 */         char[] data = (char[])this.events.get(n, 1);
/* 227 */         lexicalHandler.comment(data, 0, data.length);
/*     */       } else {
/* 229 */         if ((event != STATE_NEWELEMENT) || 
/* 230 */           (!(handler instanceof DeserializationContext))) continue;
/* 231 */         DeserializationContext context = (DeserializationContext)handler;
/*     */ 
/* 233 */         context.setCurElement((MessageElement)this.events.get(n, 1));
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private static char[] clone(char[] in, int off, int len)
/*     */   {
/* 241 */     char[] out = new char[len];
/* 242 */     System.arraycopy(in, off, out, 0, len);
/* 243 */     return out;
/*     */   }
/*     */ 
/*     */   class objArrayVector
/*     */   {
/* 248 */     private int RECORD_SIZE = 5;
/* 249 */     private int currentSize = 0;
/* 250 */     private Object[] objarray = new Object[50 * this.RECORD_SIZE];
/*     */ 
/*     */     objArrayVector() {  }
/*     */ 
/* 253 */     public int add(Object p1, Object p2, Object p3, Object p4, Object p5) { if (this.currentSize == this.objarray.length) {
/* 254 */         Object[] newarray = new Object[this.currentSize * 2];
/* 255 */         System.arraycopy(this.objarray, 0, newarray, 0, this.currentSize);
/* 256 */         this.objarray = newarray;
/*     */       }
/* 258 */       int pos = this.currentSize / this.RECORD_SIZE;
/* 259 */       this.objarray[(this.currentSize++)] = p1;
/* 260 */       this.objarray[(this.currentSize++)] = p2;
/* 261 */       this.objarray[(this.currentSize++)] = p3;
/* 262 */       this.objarray[(this.currentSize++)] = p4;
/* 263 */       this.objarray[(this.currentSize++)] = p5;
/* 264 */       return pos; }
/*     */ 
/*     */     public Object get(int pos, int fld)
/*     */     {
/* 268 */       return this.objarray[(pos * this.RECORD_SIZE + fld)];
/*     */     }
/*     */ 
/*     */     public int getLength() {
/* 272 */       return this.currentSize / this.RECORD_SIZE;
/*     */     }
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.message.SAX2EventRecorder
 * JD-Core Version:    0.6.0
 */