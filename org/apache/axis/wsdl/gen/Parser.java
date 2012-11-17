/*     */ package org.apache.axis.wsdl.gen;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Vector;
/*     */ import javax.wsdl.Binding;
/*     */ import javax.wsdl.Definition;
/*     */ import javax.wsdl.PortType;
/*     */ import javax.wsdl.WSDLException;
/*     */ import javax.xml.parsers.ParserConfigurationException;
/*     */ import org.apache.axis.utils.Messages;
/*     */ import org.apache.axis.wsdl.symbolTable.BindingEntry;
/*     */ import org.apache.axis.wsdl.symbolTable.CollectionElement;
/*     */ import org.apache.axis.wsdl.symbolTable.MessageEntry;
/*     */ import org.apache.axis.wsdl.symbolTable.PortTypeEntry;
/*     */ import org.apache.axis.wsdl.symbolTable.ServiceEntry;
/*     */ import org.apache.axis.wsdl.symbolTable.SymTabEntry;
/*     */ import org.apache.axis.wsdl.symbolTable.SymbolTable;
/*     */ import org.apache.axis.wsdl.symbolTable.Type;
/*     */ import org.apache.axis.wsdl.symbolTable.TypeEntry;
/*     */ import org.apache.axis.wsdl.toJava.Utils;
/*     */ import org.w3c.dom.Document;
/*     */ import org.xml.sax.SAXException;
/*     */ 
/*     */ public class Parser
/*     */ {
/*     */   protected boolean debug;
/*     */   protected boolean quiet;
/*     */   protected boolean imports;
/*     */   protected boolean verbose;
/*     */   protected boolean nowrap;
/*     */   protected String username;
/*     */   protected String password;
/*     */   protected boolean wrapArrays;
/*     */   private long timeoutms;
/*     */   private GeneratorFactory genFactory;
/*     */   private SymbolTable symbolTable;
/*     */ 
/*     */   public Parser()
/*     */   {
/*  48 */     this.debug = false;
/*     */ 
/*  51 */     this.quiet = false;
/*     */ 
/*  54 */     this.imports = true;
/*     */ 
/*  57 */     this.verbose = false;
/*     */ 
/*  60 */     this.nowrap = false;
/*     */ 
/*  65 */     this.username = null;
/*     */ 
/*  68 */     this.password = null;
/*     */ 
/*  71 */     this.wrapArrays = false;
/*     */ 
/*  76 */     this.timeoutms = 45000L;
/*     */ 
/*  79 */     this.genFactory = null;
/*     */ 
/*  82 */     this.symbolTable = null;
/*     */   }
/*     */ 
/*     */   public boolean isDebug()
/*     */   {
/*  90 */     return this.debug;
/*     */   }
/*     */ 
/*     */   public void setDebug(boolean debug)
/*     */   {
/*  99 */     this.debug = debug;
/*     */   }
/*     */ 
/*     */   public boolean isQuiet()
/*     */   {
/* 108 */     return this.quiet;
/*     */   }
/*     */ 
/*     */   public void setQuiet(boolean quiet)
/*     */   {
/* 117 */     this.quiet = quiet;
/*     */   }
/*     */ 
/*     */   public boolean isImports()
/*     */   {
/* 126 */     return this.imports;
/*     */   }
/*     */ 
/*     */   public void setImports(boolean imports)
/*     */   {
/* 135 */     this.imports = imports;
/*     */   }
/*     */ 
/*     */   public boolean isVerbose()
/*     */   {
/* 144 */     return this.verbose;
/*     */   }
/*     */ 
/*     */   public void setVerbose(boolean verbose)
/*     */   {
/* 153 */     this.verbose = verbose;
/*     */   }
/*     */ 
/*     */   public boolean isNowrap()
/*     */   {
/* 162 */     return this.nowrap;
/*     */   }
/*     */ 
/*     */   public void setNowrap(boolean nowrap)
/*     */   {
/* 171 */     this.nowrap = nowrap;
/*     */   }
/*     */ 
/*     */   public long getTimeout()
/*     */   {
/* 180 */     return this.timeoutms;
/*     */   }
/*     */ 
/*     */   public void setTimeout(long timeout)
/*     */   {
/* 189 */     this.timeoutms = timeout;
/*     */   }
/*     */ 
/*     */   public String getUsername()
/*     */   {
/* 198 */     return this.username;
/*     */   }
/*     */ 
/*     */   public void setUsername(String username)
/*     */   {
/* 207 */     this.username = username;
/*     */   }
/*     */ 
/*     */   public String getPassword()
/*     */   {
/* 216 */     return this.password;
/*     */   }
/*     */ 
/*     */   public void setPassword(String password)
/*     */   {
/* 225 */     this.password = password;
/*     */   }
/*     */ 
/*     */   public GeneratorFactory getFactory()
/*     */   {
/* 234 */     return this.genFactory;
/*     */   }
/*     */ 
/*     */   public void setFactory(GeneratorFactory factory)
/*     */   {
/* 243 */     this.genFactory = factory;
/*     */   }
/*     */ 
/*     */   public SymbolTable getSymbolTable()
/*     */   {
/* 253 */     return this.symbolTable;
/*     */   }
/*     */ 
/*     */   public Definition getCurrentDefinition()
/*     */   {
/* 264 */     return this.symbolTable == null ? null : this.symbolTable.getDefinition();
/*     */   }
/*     */ 
/*     */   public String getWSDLURI()
/*     */   {
/* 277 */     return this.symbolTable == null ? null : this.symbolTable.getWSDLURI();
/*     */   }
/*     */ 
/*     */   public void run(String wsdlURI)
/*     */     throws Exception
/*     */   {
/* 293 */     if (getFactory() == null) {
/* 294 */       setFactory(new NoopFactory());
/*     */     }
/*     */ 
/* 297 */     this.symbolTable = new SymbolTable(this.genFactory.getBaseTypeMapping(), this.imports, this.verbose, this.nowrap);
/*     */ 
/* 299 */     this.symbolTable.setQuiet(this.quiet);
/* 300 */     this.symbolTable.setWrapArrays(this.wrapArrays);
/*     */ 
/* 303 */     WSDLRunnable runnable = new WSDLRunnable(this.symbolTable, wsdlURI);
/* 304 */     Thread wsdlThread = new Thread(runnable);
/*     */ 
/* 306 */     wsdlThread.start();
/*     */     try
/*     */     {
/* 309 */       if (this.timeoutms > 0L)
/* 310 */         wsdlThread.join(this.timeoutms);
/*     */       else
/* 312 */         wsdlThread.join();
/*     */     }
/*     */     catch (InterruptedException e)
/*     */     {
/*     */     }
/* 317 */     if (wsdlThread.isAlive()) {
/* 318 */       wsdlThread.interrupt();
/*     */ 
/* 320 */       throw new IOException(Messages.getMessage("timedOut"));
/*     */     }
/*     */ 
/* 323 */     if (runnable.getFailure() != null)
/* 324 */       throw runnable.getFailure();
/*     */   }
/*     */ 
/*     */   public void run(String context, Document doc)
/*     */     throws IOException, SAXException, WSDLException, ParserConfigurationException
/*     */   {
/* 392 */     if (getFactory() == null) {
/* 393 */       setFactory(new NoopFactory());
/*     */     }
/*     */ 
/* 396 */     this.symbolTable = new SymbolTable(this.genFactory.getBaseTypeMapping(), this.imports, this.verbose, this.nowrap);
/*     */ 
/* 399 */     this.symbolTable.populate(context, doc);
/* 400 */     generate(this.symbolTable);
/*     */   }
/*     */ 
/*     */   protected void sanityCheck(SymbolTable symbolTable)
/*     */   {
/*     */   }
/*     */ 
/*     */   private void generate(SymbolTable symbolTable)
/*     */     throws IOException
/*     */   {
/* 421 */     sanityCheck(symbolTable);
/*     */ 
/* 423 */     Definition def = symbolTable.getDefinition();
/*     */ 
/* 425 */     this.genFactory.generatorPass(def, symbolTable);
/*     */ 
/* 427 */     if (isDebug()) {
/* 428 */       symbolTable.dump(System.out);
/*     */     }
/*     */ 
/* 432 */     generateTypes(symbolTable);
/*     */ 
/* 434 */     Iterator it = symbolTable.getHashMap().values().iterator();
/*     */ 
/* 436 */     while (it.hasNext()) {
/* 437 */       Vector v = (Vector)it.next();
/*     */ 
/* 439 */       for (int i = 0; i < v.size(); i++) {
/* 440 */         SymTabEntry entry = (SymTabEntry)v.elementAt(i);
/* 441 */         Generator gen = null;
/*     */ 
/* 443 */         if ((entry instanceof MessageEntry)) {
/* 444 */           gen = this.genFactory.getGenerator(((MessageEntry)entry).getMessage(), symbolTable);
/*     */         }
/* 446 */         else if ((entry instanceof PortTypeEntry)) {
/* 447 */           PortTypeEntry pEntry = (PortTypeEntry)entry;
/*     */ 
/* 452 */           if (pEntry.getPortType().isUndefined())
/*     */           {
/*     */             continue;
/*     */           }
/* 456 */           gen = this.genFactory.getGenerator(pEntry.getPortType(), symbolTable);
/*     */         }
/* 458 */         else if ((entry instanceof BindingEntry)) {
/* 459 */           BindingEntry bEntry = (BindingEntry)entry;
/* 460 */           Binding binding = bEntry.getBinding();
/*     */ 
/* 465 */           if ((binding.isUndefined()) || (!bEntry.isReferenced()))
/*     */           {
/*     */             continue;
/*     */           }
/* 469 */           gen = this.genFactory.getGenerator(binding, symbolTable);
/* 470 */         } else if ((entry instanceof ServiceEntry)) {
/* 471 */           gen = this.genFactory.getGenerator(((ServiceEntry)entry).getService(), symbolTable);
/*     */         }
/*     */ 
/* 475 */         if (gen != null) {
/* 476 */           gen.generate();
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 483 */     Generator gen = this.genFactory.getGenerator(def, symbolTable);
/*     */ 
/* 485 */     gen.generate();
/*     */   }
/*     */ 
/*     */   private void generateTypes(SymbolTable symbolTable)
/*     */     throws IOException
/*     */   {
/* 497 */     Map elements = symbolTable.getElementIndex();
/* 498 */     Collection elementCollection = elements.values();
/* 499 */     for (Iterator i = elementCollection.iterator(); i.hasNext(); ) {
/* 500 */       TypeEntry type = (TypeEntry)i.next();
/*     */ 
/* 510 */       boolean isType = ((type instanceof Type)) || ((type instanceof CollectionElement));
/*     */ 
/* 513 */       if ((type.getNode() != null) && (!Utils.isXsNode(type.getNode(), "attributeGroup")) && (!Utils.isXsNode(type.getNode(), "group")) && (type.isReferenced()) && (isType) && (type.getBaseType() == null))
/*     */       {
/* 518 */         Generator gen = this.genFactory.getGenerator(type, symbolTable);
/*     */ 
/* 520 */         gen.generate();
/*     */       }
/*     */     }
/*     */ 
/* 524 */     Map types = symbolTable.getTypeIndex();
/* 525 */     Collection typeCollection = types.values();
/* 526 */     for (Iterator i = typeCollection.iterator(); i.hasNext(); ) {
/* 527 */       TypeEntry type = (TypeEntry)i.next();
/*     */ 
/* 537 */       boolean isType = ((type instanceof Type)) || ((type instanceof CollectionElement));
/*     */ 
/* 540 */       if ((type.getNode() != null) && (!Utils.isXsNode(type.getNode(), "attributeGroup")) && (!Utils.isXsNode(type.getNode(), "group")) && (type.isReferenced()) && (isType) && (type.getBaseType() == null))
/*     */       {
/* 545 */         Generator gen = this.genFactory.getGenerator(type, symbolTable);
/*     */ 
/* 547 */         gen.generate();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private class WSDLRunnable
/*     */     implements Runnable
/*     */   {
/*     */     private SymbolTable symbolTable;
/*     */     private String wsdlURI;
/* 342 */     private Exception failure = null;
/*     */ 
/*     */     public WSDLRunnable(SymbolTable symbolTable, String wsdlURI)
/*     */     {
/* 351 */       this.symbolTable = symbolTable;
/* 352 */       this.wsdlURI = wsdlURI;
/*     */     }
/*     */ 
/*     */     public void run()
/*     */     {
/*     */       try
/*     */       {
/* 361 */         this.symbolTable.populate(this.wsdlURI, Parser.this.username, Parser.this.password);
/* 362 */         Parser.this.generate(this.symbolTable);
/*     */       } catch (Exception e) {
/* 364 */         this.failure = e;
/*     */       }
/*     */     }
/*     */ 
/*     */     public Exception getFailure()
/*     */     {
/* 374 */       return this.failure;
/*     */     }
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.wsdl.gen.Parser
 * JD-Core Version:    0.6.0
 */