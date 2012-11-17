/*     */ package org.apache.axis.wsdl;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import org.apache.axis.constants.Scope;
/*     */ import org.apache.axis.utils.CLOption;
/*     */ import org.apache.axis.utils.CLOptionDescriptor;
/*     */ import org.apache.axis.utils.ClassUtils;
/*     */ import org.apache.axis.utils.JavaUtils;
/*     */ import org.apache.axis.utils.Messages;
/*     */ import org.apache.axis.wsdl.gen.Parser;
/*     */ import org.apache.axis.wsdl.gen.WSDL2;
/*     */ import org.apache.axis.wsdl.toJava.Emitter;
/*     */ import org.apache.axis.wsdl.toJava.NamespaceSelector;
/*     */ 
/*     */ public class WSDL2Java extends WSDL2
/*     */ {
/*     */   protected static final int SERVER_OPT = 115;
/*     */   protected static final int SKELETON_DEPLOY_OPT = 83;
/*     */   protected static final int NAMESPACE_OPT = 78;
/*     */   protected static final int NAMESPACE_FILE_OPT = 102;
/*     */   protected static final int OUTPUT_OPT = 111;
/*     */   protected static final int SCOPE_OPT = 100;
/*     */   protected static final int TEST_OPT = 116;
/*     */   protected static final int BUILDFILE_OPT = 66;
/*     */   protected static final int PACKAGE_OPT = 112;
/*     */   protected static final int ALL_OPT = 97;
/*     */   protected static final int TYPEMAPPING_OPT = 84;
/*     */   protected static final int FACTORY_CLASS_OPT = 70;
/*     */   protected static final int HELPER_CLASS_OPT = 72;
/*     */   protected static final int USERNAME_OPT = 85;
/*     */   protected static final int PASSWORD_OPT = 80;
/*     */   protected static final int CLASSPATH_OPT = 88;
/*  82 */   protected boolean bPackageOpt = false;
/*     */   protected static final int NS_INCLUDE_OPT = 105;
/*     */   protected static final int NS_EXCLUDE_OPT = 120;
/*     */   protected static final int IMPL_CLASS_OPT = 99;
/*     */   protected static final int ALLOW_INVALID_URL_OPT = 117;
/*     */   protected static final int WRAP_ARRAYS_OPT = 119;
/*     */   private Emitter emitter;
/* 112 */   protected static final CLOptionDescriptor[] options = { new CLOptionDescriptor("server-side", 8, 115, Messages.getMessage("optionSkel00")), new CLOptionDescriptor("skeletonDeploy", 2, 83, Messages.getMessage("optionSkeletonDeploy00")), new CLOptionDescriptor("NStoPkg", 48, 78, Messages.getMessage("optionNStoPkg00")), new CLOptionDescriptor("fileNStoPkg", 2, 102, Messages.getMessage("optionFileNStoPkg00")), new CLOptionDescriptor("package", 2, 112, Messages.getMessage("optionPackage00")), new CLOptionDescriptor("output", 2, 111, Messages.getMessage("optionOutput00")), new CLOptionDescriptor("deployScope", 2, 100, Messages.getMessage("optionScope00")), new CLOptionDescriptor("testCase", 8, 116, Messages.getMessage("optionTest00")), new CLOptionDescriptor("all", 8, 97, Messages.getMessage("optionAll00")), new CLOptionDescriptor("typeMappingVersion", 2, 84, Messages.getMessage("optionTypeMapping00")), new CLOptionDescriptor("factory", 2, 70, Messages.getMessage("optionFactory00")), new CLOptionDescriptor("helperGen", 8, 72, Messages.getMessage("optionHelper00")), new CLOptionDescriptor("buildFile", 8, 66, Messages.getMessage("optionBuildFile00")), new CLOptionDescriptor("user", 2, 85, Messages.getMessage("optionUsername")), new CLOptionDescriptor("password", 2, 80, Messages.getMessage("optionPassword")), new CLOptionDescriptor("classpath", 4, 88, Messages.getMessage("optionClasspath")), new CLOptionDescriptor("nsInclude", 34, 105, Messages.getMessage("optionNSInclude")), new CLOptionDescriptor("nsExclude", 34, 120, Messages.getMessage("optionNSExclude")), new CLOptionDescriptor("implementationClassName", 2, 99, Messages.getMessage("implementationClassName")), new CLOptionDescriptor("allowInvalidURL", 8, 117, Messages.getMessage("optionAllowInvalidURL")), new CLOptionDescriptor("wrapArrays", 4, 119, Messages.getMessage("optionWrapArrays")) };
/*     */ 
/*     */   protected WSDL2Java()
/*     */   {
/* 198 */     this.emitter = ((Emitter)this.parser);
/*     */ 
/* 200 */     addOptions(options);
/*     */   }
/*     */ 
/*     */   protected Parser createParser()
/*     */   {
/* 209 */     return new Emitter();
/*     */   }
/*     */ 
/*     */   protected void parseOption(CLOption option)
/*     */   {
/* 219 */     switch (option.getId())
/*     */     {
/*     */     case 70:
/* 222 */       this.emitter.setFactory(option.getArgument());
/* 223 */       break;
/*     */     case 72:
/* 226 */       this.emitter.setHelperWanted(true);
/* 227 */       break;
/*     */     case 83:
/* 230 */       this.emitter.setSkeletonWanted(JavaUtils.isTrueExplicitly(option.getArgument(0)));
/*     */     case 115:
/* 235 */       this.emitter.setServerSide(true);
/* 236 */       break;
/*     */     case 78:
/* 239 */       String namespace = option.getArgument(0);
/* 240 */       String packageName = option.getArgument(1);
/*     */ 
/* 242 */       this.emitter.getNamespaceMap().put(namespace, packageName);
/* 243 */       break;
/*     */     case 102:
/* 246 */       this.emitter.setNStoPkg(option.getArgument());
/* 247 */       break;
/*     */     case 112:
/* 250 */       this.bPackageOpt = true;
/*     */ 
/* 252 */       this.emitter.setPackageName(option.getArgument());
/* 253 */       break;
/*     */     case 111:
/* 256 */       this.emitter.setOutputDir(option.getArgument());
/* 257 */       break;
/*     */     case 100:
/* 260 */       String arg = option.getArgument();
/*     */ 
/* 264 */       Scope scope = Scope.getScope(arg, null);
/*     */ 
/* 266 */       if (scope != null)
/* 267 */         this.emitter.setScope(scope);
/*     */       else {
/* 269 */         System.err.println(Messages.getMessage("badScope00", arg));
/*     */       }
/* 271 */       break;
/*     */     case 116:
/* 274 */       this.emitter.setTestCaseWanted(true);
/* 275 */       break;
/*     */     case 66:
/* 277 */       this.emitter.setBuildFileWanted(true);
/* 278 */       break;
/*     */     case 97:
/* 280 */       this.emitter.setAllWanted(true);
/* 281 */       break;
/*     */     case 84:
/* 284 */       String tmValue = option.getArgument();
/*     */ 
/* 286 */       if (tmValue.equals("1.0"))
/* 287 */         this.emitter.setTypeMappingVersion("1.0");
/* 288 */       else if (tmValue.equals("1.1"))
/* 289 */         this.emitter.setTypeMappingVersion("1.1");
/* 290 */       else if (tmValue.equals("1.2"))
/* 291 */         this.emitter.setTypeMappingVersion("1.2");
/* 292 */       else if (tmValue.equals("1.3"))
/* 293 */         this.emitter.setTypeMappingVersion("1.3");
/*     */       else {
/* 295 */         System.out.println(Messages.getMessage("badTypeMappingOption00"));
/*     */       }
/*     */ 
/* 298 */       break;
/*     */     case 85:
/* 301 */       this.emitter.setUsername(option.getArgument());
/* 302 */       break;
/*     */     case 80:
/* 305 */       this.emitter.setPassword(option.getArgument());
/* 306 */       break;
/*     */     case 88:
/* 309 */       ClassUtils.setDefaultClassLoader(ClassUtils.createClassLoader(option.getArgument(), getClass().getClassLoader()));
/*     */ 
/* 312 */       break;
/*     */     case 105:
/* 315 */       NamespaceSelector include = new NamespaceSelector();
/* 316 */       include.setNamespace(option.getArgument());
/* 317 */       this.emitter.getNamespaceIncludes().add(include);
/* 318 */       break;
/*     */     case 120:
/* 320 */       NamespaceSelector exclude = new NamespaceSelector();
/* 321 */       exclude.setNamespace(option.getArgument());
/* 322 */       this.emitter.getNamespaceExcludes().add(exclude);
/* 323 */       break;
/*     */     case 99:
/* 326 */       this.emitter.setImplementationClassName(option.getArgument());
/* 327 */       break;
/*     */     case 117:
/* 330 */       this.emitter.setAllowInvalidURL(true);
/* 331 */       break;
/*     */     case 119:
/* 334 */       this.emitter.setWrapArrays(true);
/* 335 */       break;
/*     */     case 67:
/*     */     case 68:
/*     */     case 69:
/*     */     case 71:
/*     */     case 73:
/*     */     case 74:
/*     */     case 75:
/*     */     case 76:
/*     */     case 77:
/*     */     case 79:
/*     */     case 81:
/*     */     case 82:
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
/*     */     case 98:
/*     */     case 101:
/*     */     case 103:
/*     */     case 104:
/*     */     case 106:
/*     */     case 107:
/*     */     case 108:
/*     */     case 109:
/*     */     case 110:
/*     */     case 113:
/*     */     case 114:
/*     */     case 118:
/*     */     default:
/* 338 */       super.parseOption(option);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void validateOptions()
/*     */   {
/* 349 */     super.validateOptions();
/*     */ 
/* 352 */     if ((this.emitter.isSkeletonWanted()) && (!this.emitter.isServerSide())) {
/* 353 */       System.out.println(Messages.getMessage("badSkeleton00"));
/* 354 */       printUsage();
/*     */     }
/*     */ 
/* 357 */     if ((!this.emitter.getNamespaceMap().isEmpty()) && (this.bPackageOpt)) {
/* 358 */       System.out.println(Messages.getMessage("badpackage00"));
/* 359 */       printUsage();
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void main(String[] args)
/*     */   {
/* 371 */     WSDL2Java wsdl2java = new WSDL2Java();
/*     */ 
/* 373 */     wsdl2java.run(args);
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.wsdl.WSDL2Java
 * JD-Core Version:    0.6.0
 */