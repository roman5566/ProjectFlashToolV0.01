/*     */ package org.apache.axis.wsdl;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.PrintStream;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.apache.axis.encoding.TypeMappingRegistryImpl;
/*     */ import org.apache.axis.utils.CLArgsParser;
/*     */ import org.apache.axis.utils.CLOption;
/*     */ import org.apache.axis.utils.CLOptionDescriptor;
/*     */ import org.apache.axis.utils.CLUtil;
/*     */ import org.apache.axis.utils.ClassUtils;
/*     */ import org.apache.axis.utils.Messages;
/*     */ 
/*     */ public class Java2WSDL
/*     */ {
/*     */   protected static final int INHERITED_CLASS_OPT = 97;
/*     */   protected static final int SOAPACTION_OPT = 65;
/*     */   protected static final int BINDING_NAME_OPT = 98;
/*     */   protected static final int STOP_CLASSES_OPT = 99;
/*     */   protected static final int IMPORT_SCHEMA_OPT = 67;
/*     */   protected static final int EXTRA_CLASSES_OPT = 101;
/*     */   protected static final int HELP_OPT = 104;
/*     */   protected static final int IMPL_CLASS_OPT = 105;
/*     */   protected static final int INPUT_OPT = 73;
/*     */   protected static final int LOCATION_OPT = 108;
/*     */   protected static final int LOCATION_IMPORT_OPT = 76;
/*     */   protected static final int METHODS_ALLOWED_OPT = 109;
/*     */   protected static final int NAMESPACE_OPT = 110;
/*     */   protected static final int NAMESPACE_IMPL_OPT = 78;
/*     */   protected static final int OUTPUT_OPT = 111;
/*     */   protected static final int OUTPUT_IMPL_OPT = 79;
/*     */   protected static final int PACKAGE_OPT = 112;
/*     */   protected static final int PORTTYPE_NAME_OPT = 80;
/*     */   protected static final int SERVICE_PORT_NAME_OPT = 115;
/*     */   protected static final int SERVICE_ELEMENT_NAME_OPT = 83;
/*     */   protected static final int TYPEMAPPING_OPT = 84;
/*     */   protected static final int USE_OPT = 117;
/*     */   protected static final int OUTPUT_WSDL_MODE_OPT = 119;
/*     */   protected static final int METHODS_NOTALLOWED_OPT = 120;
/*     */   protected static final int CLASSPATH_OPT = 88;
/*     */   protected static final int STYLE_OPT = 121;
/*     */   protected static final int DEPLOY_OPT = 100;
/* 133 */   protected CLOptionDescriptor[] options = { new CLOptionDescriptor("help", 8, 104, Messages.getMessage("j2wopthelp00")), new CLOptionDescriptor("input", 2, 73, Messages.getMessage("j2woptinput00")), new CLOptionDescriptor("output", 2, 111, Messages.getMessage("j2woptoutput00")), new CLOptionDescriptor("location", 2, 108, Messages.getMessage("j2woptlocation00")), new CLOptionDescriptor("portTypeName", 2, 80, Messages.getMessage("j2woptportTypeName00")), new CLOptionDescriptor("bindingName", 2, 98, Messages.getMessage("j2woptbindingName00")), new CLOptionDescriptor("serviceElementName", 2, 83, Messages.getMessage("j2woptserviceElementName00")), new CLOptionDescriptor("servicePortName", 2, 115, Messages.getMessage("j2woptservicePortName00")), new CLOptionDescriptor("namespace", 2, 110, Messages.getMessage("j2woptnamespace00")), new CLOptionDescriptor("PkgtoNS", 48, 112, Messages.getMessage("j2woptPkgtoNS00")), new CLOptionDescriptor("methods", 34, 109, Messages.getMessage("j2woptmethods00")), new CLOptionDescriptor("all", 8, 97, Messages.getMessage("j2woptall00")), new CLOptionDescriptor("outputWsdlMode", 2, 119, Messages.getMessage("j2woptoutputWsdlMode00")), new CLOptionDescriptor("locationImport", 2, 76, Messages.getMessage("j2woptlocationImport00")), new CLOptionDescriptor("namespaceImpl", 2, 78, Messages.getMessage("j2woptnamespaceImpl00")), new CLOptionDescriptor("outputImpl", 2, 79, Messages.getMessage("j2woptoutputImpl00")), new CLOptionDescriptor("implClass", 2, 105, Messages.getMessage("j2woptimplClass00")), new CLOptionDescriptor("exclude", 34, 120, Messages.getMessage("j2woptexclude00")), new CLOptionDescriptor("stopClasses", 34, 99, Messages.getMessage("j2woptstopClass00")), new CLOptionDescriptor("typeMappingVersion", 2, 84, Messages.getMessage("j2wopttypeMapping00")), new CLOptionDescriptor("soapAction", 2, 65, Messages.getMessage("j2woptsoapAction00")), new CLOptionDescriptor("style", 2, 121, Messages.getMessage("j2woptStyle00")), new CLOptionDescriptor("use", 2, 117, Messages.getMessage("j2woptUse00")), new CLOptionDescriptor("extraClasses", 34, 101, Messages.getMessage("j2woptExtraClasses00")), new CLOptionDescriptor("importSchema", 4, 67, Messages.getMessage("j2woptImportSchema00")), new CLOptionDescriptor("classpath", 4, 88, Messages.getMessage("optionClasspath")), new CLOptionDescriptor("deploy", 8, 100, Messages.getMessage("j2woptDeploy00")) };
/*     */   protected org.apache.axis.wsdl.fromJava.Emitter emitter;
/* 243 */   protected String className = null;
/*     */ 
/* 246 */   protected String wsdlFilename = null;
/*     */ 
/* 249 */   protected String wsdlImplFilename = null;
/*     */ 
/* 252 */   protected HashMap namespaceMap = new HashMap();
/*     */ 
/* 255 */   protected int mode = 0;
/*     */ 
/* 258 */   boolean locationSet = false;
/*     */ 
/* 261 */   protected String typeMappingVersion = "1.2";
/*     */ 
/* 264 */   protected boolean isDeploy = false;
/*     */ 
/*     */   protected Java2WSDL()
/*     */   {
/* 270 */     this.emitter = createEmitter();
/*     */   }
/*     */ 
/*     */   protected org.apache.axis.wsdl.fromJava.Emitter createEmitter()
/*     */   {
/* 279 */     return new org.apache.axis.wsdl.fromJava.Emitter();
/*     */   }
/*     */ 
/*     */   protected void addOptions(CLOptionDescriptor[] newOptions)
/*     */   {
/* 291 */     if ((newOptions != null) && (newOptions.length > 0)) {
/* 292 */       CLOptionDescriptor[] allOptions = new CLOptionDescriptor[this.options.length + newOptions.length];
/*     */ 
/* 295 */       System.arraycopy(this.options, 0, allOptions, 0, this.options.length);
/* 296 */       System.arraycopy(newOptions, 0, allOptions, this.options.length, newOptions.length);
/*     */ 
/* 299 */       this.options = allOptions;
/*     */     }
/*     */   }
/*     */ 
/*     */   protected boolean parseOption(CLOption option)
/*     */   {
/* 312 */     boolean status = true;
/*     */ 
/* 314 */     switch (option.getId())
/*     */     {
/*     */     case 0:
/* 317 */       if (this.className != null) {
/* 318 */         System.out.println(Messages.getMessage("j2wDuplicateClass00", this.className, option.getArgument()));
/*     */ 
/* 322 */         printUsage();
/*     */ 
/* 324 */         status = false;
/*     */       }
/*     */ 
/* 327 */       this.className = option.getArgument();
/* 328 */       break;
/*     */     case 109:
/* 331 */       this.emitter.setAllowedMethods(option.getArgument());
/* 332 */       break;
/*     */     case 97:
/* 335 */       this.emitter.setUseInheritedMethods(true);
/* 336 */       break;
/*     */     case 105:
/* 339 */       this.emitter.setImplCls(option.getArgument());
/* 340 */       break;
/*     */     case 104:
/* 343 */       printUsage();
/*     */ 
/* 345 */       status = false;
/* 346 */       break;
/*     */     case 119:
/* 349 */       String modeArg = option.getArgument();
/*     */ 
/* 351 */       if ("All".equalsIgnoreCase(modeArg)) {
/* 352 */         this.mode = 0;
/* 353 */       } else if ("Interface".equalsIgnoreCase(modeArg)) {
/* 354 */         this.mode = 1;
/* 355 */       } else if ("Implementation".equalsIgnoreCase(modeArg)) {
/* 356 */         this.mode = 2;
/*     */       } else {
/* 358 */         this.mode = 0;
/*     */ 
/* 360 */         System.err.println(Messages.getMessage("j2wmodeerror", modeArg));
/*     */       }
/*     */ 
/* 363 */       break;
/*     */     case 111:
/* 366 */       this.wsdlFilename = option.getArgument();
/* 367 */       break;
/*     */     case 73:
/* 370 */       this.emitter.setInputWSDL(option.getArgument());
/* 371 */       break;
/*     */     case 79:
/* 374 */       this.wsdlImplFilename = option.getArgument();
/* 375 */       break;
/*     */     case 112:
/* 378 */       String packageName = option.getArgument(0);
/* 379 */       String namespace = option.getArgument(1);
/*     */ 
/* 381 */       this.namespaceMap.put(packageName, namespace);
/* 382 */       break;
/*     */     case 110:
/* 385 */       this.emitter.setIntfNamespace(option.getArgument());
/* 386 */       break;
/*     */     case 78:
/* 389 */       this.emitter.setImplNamespace(option.getArgument());
/* 390 */       break;
/*     */     case 83:
/* 393 */       this.emitter.setServiceElementName(option.getArgument());
/* 394 */       break;
/*     */     case 115:
/* 397 */       this.emitter.setServicePortName(option.getArgument());
/* 398 */       break;
/*     */     case 108:
/* 401 */       this.emitter.setLocationUrl(option.getArgument());
/*     */ 
/* 403 */       this.locationSet = true;
/* 404 */       break;
/*     */     case 76:
/* 407 */       this.emitter.setImportUrl(option.getArgument());
/* 408 */       break;
/*     */     case 120:
/* 411 */       this.emitter.setDisallowedMethods(option.getArgument());
/* 412 */       break;
/*     */     case 80:
/* 415 */       this.emitter.setPortTypeName(option.getArgument());
/* 416 */       break;
/*     */     case 98:
/* 419 */       this.emitter.setBindingName(option.getArgument());
/* 420 */       break;
/*     */     case 99:
/* 423 */       this.emitter.setStopClasses(option.getArgument());
/* 424 */       break;
/*     */     case 84:
/* 427 */       String value = option.getArgument();
/* 428 */       this.typeMappingVersion = value;
/* 429 */       break;
/*     */     case 65:
/* 432 */       String value = option.getArgument();
/*     */ 
/* 434 */       if (value.equalsIgnoreCase("DEFAULT")) {
/* 435 */         this.emitter.setSoapAction("DEFAULT");
/* 436 */       } else if (value.equalsIgnoreCase("OPERATION")) {
/* 437 */         this.emitter.setSoapAction("OPERATION");
/* 438 */       } else if (value.equalsIgnoreCase("NONE")) {
/* 439 */         this.emitter.setSoapAction("NONE");
/*     */       } else {
/* 441 */         System.out.println(Messages.getMessage("j2wBadSoapAction00"));
/*     */ 
/* 444 */         status = false;
/*     */       }
/* 446 */       break;
/*     */     case 121:
/* 449 */       String value = option.getArgument();
/*     */ 
/* 451 */       if ((value.equalsIgnoreCase("DOCUMENT")) || (value.equalsIgnoreCase("RPC")) || (value.equalsIgnoreCase("WRAPPED")))
/*     */       {
/* 454 */         this.emitter.setStyle(value);
/*     */       } else {
/* 456 */         System.out.println(Messages.getMessage("j2woptBadStyle00"));
/*     */ 
/* 458 */         status = false;
/*     */       }
/* 460 */       break;
/*     */     case 117:
/* 463 */       String value = option.getArgument();
/*     */ 
/* 465 */       if ((value.equalsIgnoreCase("LITERAL")) || (value.equalsIgnoreCase("ENCODED")))
/*     */       {
/* 467 */         this.emitter.setUse(value);
/*     */       } else {
/* 469 */         System.out.println(Messages.getMessage("j2woptBadUse00"));
/*     */ 
/* 471 */         status = false;
/*     */       }
/* 473 */       break;
/*     */     case 101:
/*     */       try
/*     */       {
/* 477 */         this.emitter.setExtraClasses(option.getArgument());
/*     */       } catch (ClassNotFoundException e) {
/* 479 */         System.out.println(Messages.getMessage("j2woptBadClass00", e.toString()));
/*     */ 
/* 482 */         status = false;
/*     */       }
/*     */ 
/*     */     case 67:
/* 487 */       this.emitter.setInputSchema(option.getArgument());
/* 488 */       break;
/*     */     case 88:
/* 491 */       ClassUtils.setDefaultClassLoader(ClassUtils.createClassLoader(option.getArgument(), getClass().getClassLoader()));
/*     */ 
/* 494 */       break;
/*     */     case 100:
/* 497 */       this.isDeploy = true;
/* 498 */       break;
/*     */     case 1:
/*     */     case 2:
/*     */     case 3:
/*     */     case 4:
/*     */     case 5:
/*     */     case 6:
/*     */     case 7:
/*     */     case 8:
/*     */     case 9:
/*     */     case 10:
/*     */     case 11:
/*     */     case 12:
/*     */     case 13:
/*     */     case 14:
/*     */     case 15:
/*     */     case 16:
/*     */     case 17:
/*     */     case 18:
/*     */     case 19:
/*     */     case 20:
/*     */     case 21:
/*     */     case 22:
/*     */     case 23:
/*     */     case 24:
/*     */     case 25:
/*     */     case 26:
/*     */     case 27:
/*     */     case 28:
/*     */     case 29:
/*     */     case 30:
/*     */     case 31:
/*     */     case 32:
/*     */     case 33:
/*     */     case 34:
/*     */     case 35:
/*     */     case 36:
/*     */     case 37:
/*     */     case 38:
/*     */     case 39:
/*     */     case 40:
/*     */     case 41:
/*     */     case 42:
/*     */     case 43:
/*     */     case 44:
/*     */     case 45:
/*     */     case 46:
/*     */     case 47:
/*     */     case 48:
/*     */     case 49:
/*     */     case 50:
/*     */     case 51:
/*     */     case 52:
/*     */     case 53:
/*     */     case 54:
/*     */     case 55:
/*     */     case 56:
/*     */     case 57:
/*     */     case 58:
/*     */     case 59:
/*     */     case 60:
/*     */     case 61:
/*     */     case 62:
/*     */     case 63:
/*     */     case 64:
/*     */     case 66:
/*     */     case 68:
/*     */     case 69:
/*     */     case 70:
/*     */     case 71:
/*     */     case 72:
/*     */     case 74:
/*     */     case 75:
/*     */     case 77:
/*     */     case 81:
/*     */     case 82:
/*     */     case 85:
/*     */     case 86:
/*     */     case 87:
/*     */     case 89:
/*     */     case 90:
/*     */     case 91:
/*     */     case 92:
/*     */     case 93:
/*     */     case 94:
/*     */     case 95:
/*     */     case 96:
/*     */     case 102:
/*     */     case 103:
/*     */     case 106:
/*     */     case 107:
/*     */     case 113:
/*     */     case 114:
/*     */     case 116:
/* 504 */     case 118: } return status;
/*     */   }
/*     */ 
/*     */   protected boolean validateOptions()
/*     */   {
/* 517 */     if (this.className == null) {
/* 518 */       System.out.println(Messages.getMessage("j2wMissingClass00"));
/* 519 */       printUsage();
/*     */ 
/* 521 */       return false;
/*     */     }
/*     */ 
/* 524 */     if ((!this.locationSet) && ((this.mode == 0) || (this.mode == 2)))
/*     */     {
/* 527 */       System.out.println(Messages.getMessage("j2wMissingLocation00"));
/* 528 */       printUsage();
/*     */ 
/* 530 */       return false;
/*     */     }
/* 532 */     return true;
/*     */   }
/*     */ 
/*     */   protected int run(String[] args)
/*     */   {
/* 545 */     CLArgsParser argsParser = new CLArgsParser(args, this.options);
/*     */ 
/* 548 */     if (null != argsParser.getErrorString()) {
/* 549 */       System.err.println(Messages.getMessage("j2werror00", argsParser.getErrorString()));
/*     */ 
/* 551 */       printUsage();
/*     */ 
/* 553 */       return 1;
/*     */     }
/*     */ 
/* 557 */     List clOptions = argsParser.getArguments();
/* 558 */     int size = clOptions.size();
/*     */     try
/*     */     {
/* 563 */       for (int i = 0; i < size; i++) {
/* 564 */         if (!parseOption((CLOption)clOptions.get(i))) {
/* 565 */           return 1;
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 570 */       if (!validateOptions()) {
/* 571 */         return 1;
/*     */       }
/*     */ 
/* 575 */       if (!this.namespaceMap.isEmpty()) {
/* 576 */         this.emitter.setNamespaceMap(this.namespaceMap);
/*     */       }
/*     */ 
/* 579 */       TypeMappingRegistryImpl tmr = new TypeMappingRegistryImpl();
/* 580 */       tmr.doRegisterFromVersion(this.typeMappingVersion);
/* 581 */       this.emitter.setTypeMappingRegistry(tmr);
/*     */ 
/* 584 */       this.emitter.setCls(this.className);
/*     */ 
/* 587 */       if (this.wsdlImplFilename == null)
/* 588 */         this.emitter.emit(this.wsdlFilename, this.mode);
/*     */       else {
/* 590 */         this.emitter.emit(this.wsdlFilename, this.wsdlImplFilename);
/*     */       }
/*     */ 
/* 593 */       if (this.isDeploy) {
/* 594 */         generateServerSide(this.emitter, this.wsdlImplFilename != null ? this.wsdlImplFilename : this.wsdlFilename);
/*     */       }
/*     */ 
/* 597 */       return 0;
/*     */     } catch (Throwable t) {
/* 599 */       t.printStackTrace();
/*     */     }
/* 601 */     return 1;
/*     */   }
/*     */ 
/*     */   protected void generateServerSide(org.apache.axis.wsdl.fromJava.Emitter j2w, String wsdlFileName)
/*     */     throws Exception
/*     */   {
/* 613 */     org.apache.axis.wsdl.toJava.Emitter w2j = new org.apache.axis.wsdl.toJava.Emitter();
/* 614 */     File wsdlFile = new File(wsdlFileName);
/* 615 */     w2j.setServiceDesc(j2w.getServiceDesc());
/* 616 */     w2j.setQName2ClassMap(j2w.getQName2ClassMap());
/* 617 */     w2j.setOutputDir(wsdlFile.getParent());
/* 618 */     w2j.setServerSide(true);
/* 619 */     w2j.setHelperWanted(true);
/*     */ 
/* 622 */     String ns = j2w.getIntfNamespace();
/* 623 */     String pkg = j2w.getCls().getPackage().getName();
/* 624 */     w2j.getNamespaceMap().put(ns, pkg);
/*     */ 
/* 626 */     Map nsmap = j2w.getNamespaceMap();
/*     */     Iterator i;
/* 627 */     if (nsmap != null) {
/* 628 */       for (i = nsmap.keySet().iterator(); i.hasNext(); ) {
/* 629 */         pkg = (String)i.next();
/* 630 */         ns = (String)nsmap.get(pkg);
/* 631 */         w2j.getNamespaceMap().put(ns, pkg);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 636 */     w2j.setDeploy(true);
/*     */ 
/* 638 */     if (j2w.getImplCls() != null) {
/* 639 */       w2j.setImplementationClassName(j2w.getImplCls().getName());
/*     */     }
/* 641 */     else if (!j2w.getCls().isInterface())
/* 642 */       w2j.setImplementationClassName(j2w.getCls().getName());
/*     */     else {
/* 644 */       throw new Exception("implementation class is not specified.");
/*     */     }
/*     */ 
/* 648 */     w2j.run(wsdlFileName);
/*     */   }
/*     */ 
/*     */   protected void printUsage()
/*     */   {
/* 656 */     String lSep = System.getProperty("line.separator");
/* 657 */     StringBuffer msg = new StringBuffer();
/*     */ 
/* 659 */     msg.append("Java2WSDL " + Messages.getMessage("j2wemitter00")).append(lSep);
/*     */ 
/* 661 */     msg.append(Messages.getMessage("j2wusage00", "java " + getClass().getName() + " [options] class-of-portType")).append(lSep);
/*     */ 
/* 666 */     msg.append(Messages.getMessage("j2woptions00")).append(lSep);
/* 667 */     msg.append(CLUtil.describeOptions(this.options).toString());
/* 668 */     msg.append(Messages.getMessage("j2wdetails00")).append(lSep);
/* 669 */     System.out.println(msg.toString());
/*     */   }
/*     */ 
/*     */   public static void main(String[] args)
/*     */   {
/* 680 */     Java2WSDL java2wsdl = new Java2WSDL();
/*     */ 
/* 682 */     System.exit(java2wsdl.run(args));
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.wsdl.Java2WSDL
 * JD-Core Version:    0.6.0
 */