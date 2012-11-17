/*     */ package org.apache.axis.wsdl.gen;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.net.Authenticator;
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.URL;
/*     */ import java.util.List;
/*     */ import org.apache.axis.utils.CLArgsParser;
/*     */ import org.apache.axis.utils.CLOption;
/*     */ import org.apache.axis.utils.CLOptionDescriptor;
/*     */ import org.apache.axis.utils.CLUtil;
/*     */ import org.apache.axis.utils.DefaultAuthenticator;
/*     */ import org.apache.axis.utils.Messages;
/*     */ 
/*     */ public class WSDL2
/*     */ {
/*     */   protected static final int DEBUG_OPT = 68;
/*     */   protected static final int HELP_OPT = 104;
/*     */   protected static final int NETWORK_TIMEOUT_OPT = 79;
/*     */   protected static final int NOIMPORTS_OPT = 110;
/*     */   protected static final int VERBOSE_OPT = 118;
/*     */   protected static final int NOWRAP_OPT = 87;
/*     */   protected static final int QUIET_OPT = 113;
/*  59 */   protected CLOptionDescriptor[] options = { new CLOptionDescriptor("help", 8, 104, Messages.getMessage("optionHelp00")), new CLOptionDescriptor("verbose", 8, 118, Messages.getMessage("optionVerbose00")), new CLOptionDescriptor("noImports", 8, 110, Messages.getMessage("optionImport00")), new CLOptionDescriptor("timeout", 2, 79, Messages.getMessage("optionTimeout00")), new CLOptionDescriptor("Debug", 8, 68, Messages.getMessage("optionDebug00")), new CLOptionDescriptor("noWrapped", 8, 87, Messages.getMessage("optionNoWrap00")), new CLOptionDescriptor("quiet", 8, 113, Messages.getMessage("optionQuiet")) };
/*     */ 
/*  86 */   protected String wsdlURI = null;
/*     */   protected Parser parser;
/*     */ 
/*     */   protected WSDL2()
/*     */   {
/*  96 */     this.parser = createParser();
/*     */   }
/*     */ 
/*     */   protected Parser createParser()
/*     */   {
/* 106 */     return new Parser();
/*     */   }
/*     */ 
/*     */   protected Parser getParser()
/*     */   {
/* 116 */     return this.parser;
/*     */   }
/*     */ 
/*     */   protected void addOptions(CLOptionDescriptor[] newOptions)
/*     */   {
/* 127 */     if ((newOptions != null) && (newOptions.length > 0)) {
/* 128 */       CLOptionDescriptor[] allOptions = new CLOptionDescriptor[this.options.length + newOptions.length];
/*     */ 
/* 131 */       System.arraycopy(this.options, 0, allOptions, 0, this.options.length);
/* 132 */       System.arraycopy(newOptions, 0, allOptions, this.options.length, newOptions.length);
/*     */ 
/* 135 */       this.options = allOptions;
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void removeOption(String name)
/*     */   {
/* 147 */     int foundOptionIndex = -1;
/*     */ 
/* 149 */     for (int i = 0; i < this.options.length; i++) {
/* 150 */       if (this.options[i].getName().equals(name)) {
/* 151 */         foundOptionIndex = i;
/*     */ 
/* 153 */         break;
/*     */       }
/*     */     }
/*     */ 
/* 157 */     if (foundOptionIndex != -1) {
/* 158 */       CLOptionDescriptor[] newOptions = new CLOptionDescriptor[this.options.length - 1];
/*     */ 
/* 161 */       System.arraycopy(this.options, 0, newOptions, 0, foundOptionIndex);
/*     */ 
/* 163 */       if (foundOptionIndex < newOptions.length) {
/* 164 */         System.arraycopy(this.options, foundOptionIndex + 1, newOptions, foundOptionIndex, newOptions.length - foundOptionIndex);
/*     */       }
/*     */ 
/* 169 */       this.options = newOptions;
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void parseOption(CLOption option)
/*     */   {
/* 180 */     switch (option.getId())
/*     */     {
/*     */     case 0:
/* 183 */       if (this.wsdlURI != null) {
/* 184 */         System.out.println(Messages.getMessage("w2jDuplicateWSDLURI00", this.wsdlURI, option.getArgument()));
/*     */ 
/* 188 */         printUsage();
/*     */       }
/*     */ 
/* 191 */       this.wsdlURI = option.getArgument();
/* 192 */       break;
/*     */     case 104:
/* 195 */       printUsage();
/* 196 */       break;
/*     */     case 110:
/* 199 */       this.parser.setImports(false);
/* 200 */       break;
/*     */     case 79:
/* 203 */       String timeoutValue = option.getArgument();
/* 204 */       long timeout = Long.parseLong(timeoutValue);
/*     */ 
/* 207 */       if (timeout > 0L) {
/* 208 */         timeout *= 1000L;
/*     */       }
/*     */ 
/* 211 */       this.parser.setTimeout(timeout);
/* 212 */       break;
/*     */     case 118:
/* 215 */       this.parser.setVerbose(true);
/* 216 */       break;
/*     */     case 68:
/* 219 */       this.parser.setDebug(true);
/* 220 */       break;
/*     */     case 113:
/* 223 */       this.parser.setQuiet(true);
/* 224 */       break;
/*     */     case 87:
/* 227 */       this.parser.setNowrap(true);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void validateOptions()
/*     */   {
/* 239 */     if (this.wsdlURI == null) {
/* 240 */       System.out.println(Messages.getMessage("w2jMissingWSDLURI00"));
/* 241 */       printUsage();
/*     */     }
/*     */ 
/* 244 */     if (this.parser.isQuiet()) {
/* 245 */       if (this.parser.isVerbose()) {
/* 246 */         System.out.println(Messages.getMessage("exclusiveQuietVerbose"));
/* 247 */         printUsage();
/*     */       }
/* 249 */       if (this.parser.isDebug()) {
/* 250 */         System.out.println(Messages.getMessage("exclusiveQuietDebug"));
/* 251 */         printUsage();
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 256 */     checkForAuthInfo(this.wsdlURI);
/* 257 */     Authenticator.setDefault(new DefaultAuthenticator(this.parser.getUsername(), this.parser.getPassword()));
/*     */   }
/*     */ 
/*     */   private void checkForAuthInfo(String uri)
/*     */   {
/* 269 */     URL url = null;
/*     */     try
/*     */     {
/* 272 */       url = new URL(uri);
/*     */     }
/*     */     catch (MalformedURLException e)
/*     */     {
/* 276 */       return;
/*     */     }
/*     */ 
/* 279 */     String userInfo = url.getUserInfo();
/*     */ 
/* 281 */     if (userInfo != null) {
/* 282 */       int i = userInfo.indexOf(':');
/*     */ 
/* 284 */       if (i >= 0) {
/* 285 */         this.parser.setUsername(userInfo.substring(0, i));
/* 286 */         this.parser.setPassword(userInfo.substring(i + 1));
/*     */       } else {
/* 288 */         this.parser.setUsername(userInfo);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void printUsage()
/*     */   {
/* 299 */     String lSep = System.getProperty("line.separator");
/* 300 */     StringBuffer msg = new StringBuffer();
/*     */ 
/* 302 */     msg.append(Messages.getMessage("usage00", "java " + getClass().getName() + " [options] WSDL-URI")).append(lSep);
/*     */ 
/* 305 */     msg.append(Messages.getMessage("options00")).append(lSep);
/* 306 */     msg.append(CLUtil.describeOptions(this.options).toString());
/* 307 */     System.out.println(msg.toString());
/* 308 */     System.exit(1);
/*     */   }
/*     */ 
/*     */   protected void run(String[] args)
/*     */   {
/* 320 */     CLArgsParser argsParser = new CLArgsParser(args, this.options);
/*     */ 
/* 323 */     if (null != argsParser.getErrorString()) {
/* 324 */       System.err.println(Messages.getMessage("error01", argsParser.getErrorString()));
/*     */ 
/* 326 */       printUsage();
/*     */     }
/*     */ 
/* 330 */     List clOptions = argsParser.getArguments();
/* 331 */     int size = clOptions.size();
/*     */     try
/*     */     {
/* 336 */       for (int i = 0; i < size; i++) {
/* 337 */         parseOption((CLOption)clOptions.get(i));
/*     */       }
/*     */ 
/* 342 */       validateOptions();
/* 343 */       this.parser.run(this.wsdlURI);
/*     */ 
/* 346 */       System.exit(0);
/*     */     } catch (Throwable t) {
/* 348 */       t.printStackTrace();
/* 349 */       System.exit(1);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void main(String[] args)
/*     */   {
/* 361 */     WSDL2 wsdl2 = new WSDL2();
/*     */ 
/* 363 */     wsdl2.run(args);
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.wsdl.gen.WSDL2
 * JD-Core Version:    0.6.0
 */