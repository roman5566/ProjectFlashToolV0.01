/*     */ package org.apache.axis.handlers;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.FileReader;
/*     */ import java.io.FileWriter;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import org.apache.axis.AxisFault;
/*     */ import org.apache.axis.MessageContext;
/*     */ import org.apache.axis.components.compiler.Compiler;
/*     */ import org.apache.axis.components.compiler.CompilerError;
/*     */ import org.apache.axis.components.compiler.CompilerFactory;
/*     */ import org.apache.axis.components.logger.LogFactory;
/*     */ import org.apache.axis.constants.Scope;
/*     */ import org.apache.axis.handlers.soap.SOAPService;
/*     */ import org.apache.axis.providers.java.RPCProvider;
/*     */ import org.apache.axis.utils.ClassUtils;
/*     */ import org.apache.axis.utils.ClasspathUtils;
/*     */ import org.apache.axis.utils.JWSClassLoader;
/*     */ import org.apache.axis.utils.Messages;
/*     */ import org.apache.axis.utils.XMLUtils;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.w3c.dom.Document;
/*     */ import org.w3c.dom.Element;
/*     */ 
/*     */ public class JWSHandler extends BasicHandler
/*     */ {
/*  54 */   protected static Log log = LogFactory.getLog(JWSHandler.class.getName());
/*     */ 
/*  57 */   public final String OPTION_JWS_FILE_EXTENSION = "extension";
/*  58 */   public final String DEFAULT_JWS_FILE_EXTENSION = ".jws";
/*     */ 
/*  60 */   protected static HashMap soapServices = new HashMap();
/*     */ 
/*     */   public void invoke(MessageContext msgContext)
/*     */     throws AxisFault
/*     */   {
/*  67 */     if (log.isDebugEnabled()) {
/*  68 */       log.debug("Enter: JWSHandler::invoke");
/*     */     }
/*     */     try
/*     */     {
/*  72 */       setupService(msgContext);
/*     */     } catch (Exception e) {
/*  74 */       log.error(Messages.getMessage("exception00"), e);
/*  75 */       throw AxisFault.makeFault(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void setupService(MessageContext msgContext)
/*     */     throws Exception
/*     */   {
/*  88 */     String realpath = msgContext.getStrProp("realpath");
/*  89 */     String extension = (String)getOption("extension");
/*  90 */     if (extension == null) extension = ".jws";
/*     */ 
/*  92 */     if ((realpath != null) && (realpath.endsWith(extension)))
/*     */     {
/*  96 */       String jwsFile = realpath;
/*  97 */       String rel = msgContext.getStrProp("path");
/*     */ 
/* 101 */       File f2 = new File(jwsFile);
/* 102 */       if (!f2.exists()) {
/* 103 */         throw new FileNotFoundException(rel);
/*     */       }
/*     */ 
/* 106 */       if (rel.charAt(0) == '/') {
/* 107 */         rel = rel.substring(1);
/*     */       }
/*     */ 
/* 110 */       int lastSlash = rel.lastIndexOf('/');
/* 111 */       String dir = null;
/*     */ 
/* 113 */       if (lastSlash > 0) {
/* 114 */         dir = rel.substring(0, lastSlash);
/*     */       }
/*     */ 
/* 117 */       String file = rel.substring(lastSlash + 1);
/*     */ 
/* 119 */       String outdir = msgContext.getStrProp("jws.classDir");
/* 120 */       if (outdir == null) outdir = ".";
/*     */ 
/* 128 */       if (dir != null) {
/* 129 */         outdir = outdir + File.separator + dir;
/*     */       }
/*     */ 
/* 135 */       File outDirectory = new File(outdir);
/* 136 */       if (!outDirectory.exists()) {
/* 137 */         outDirectory.mkdirs();
/*     */       }
/*     */ 
/* 140 */       if (log.isDebugEnabled()) {
/* 141 */         log.debug("jwsFile: " + jwsFile);
/*     */       }
/* 143 */       String jFile = outdir + File.separator + file.substring(0, file.length() - extension.length() + 1) + "java";
/*     */ 
/* 145 */       String cFile = outdir + File.separator + file.substring(0, file.length() - extension.length() + 1) + "class";
/*     */ 
/* 148 */       if (log.isDebugEnabled()) {
/* 149 */         log.debug("jFile: " + jFile);
/* 150 */         log.debug("cFile: " + cFile);
/* 151 */         log.debug("outdir: " + outdir);
/*     */       }
/*     */ 
/* 154 */       File f1 = new File(cFile);
/*     */ 
/* 158 */       String clsName = null;
/*     */ 
/* 160 */       if (clsName == null) clsName = f2.getName();
/* 161 */       if ((clsName != null) && (clsName.charAt(0) == '/')) {
/* 162 */         clsName = clsName.substring(1);
/*     */       }
/* 164 */       clsName = clsName.substring(0, clsName.length() - extension.length());
/* 165 */       clsName = clsName.replace('/', '.');
/*     */ 
/* 167 */       if (log.isDebugEnabled()) {
/* 168 */         log.debug("ClsName: " + clsName);
/*     */       }
/*     */ 
/* 172 */       if ((!f1.exists()) || (f2.lastModified() > f1.lastModified()))
/*     */       {
/* 177 */         log.debug(Messages.getMessage("compiling00", jwsFile));
/* 178 */         log.debug(Messages.getMessage("copy00", jwsFile, jFile));
/* 179 */         FileReader fr = new FileReader(jwsFile);
/* 180 */         FileWriter fw = new FileWriter(jFile);
/* 181 */         char[] buf = new char[4096];
/*     */         int rc;
/* 183 */         while ((rc = fr.read(buf, 0, 4095)) >= 0)
/* 184 */           fw.write(buf, 0, rc);
/* 185 */         fw.close();
/* 186 */         fr.close();
/*     */ 
/* 190 */         log.debug("javac " + jFile);
/*     */ 
/* 193 */         Compiler compiler = CompilerFactory.getCompiler();
/*     */ 
/* 195 */         compiler.setClasspath(ClasspathUtils.getDefaultClasspath(msgContext));
/* 196 */         compiler.setDestination(outdir);
/* 197 */         compiler.addFile(jFile);
/*     */ 
/* 199 */         boolean result = compiler.compile();
/*     */ 
/* 203 */         new File(jFile).delete();
/*     */ 
/* 205 */         if (!result)
/*     */         {
/* 210 */           new File(cFile).delete();
/*     */ 
/* 212 */           Document doc = XMLUtils.newDocument();
/*     */ 
/* 214 */           Element root = doc.createElementNS("", "Errors");
/* 215 */           StringBuffer message = new StringBuffer("Error compiling ");
/* 216 */           message.append(jFile);
/* 217 */           message.append(":\n");
/*     */ 
/* 219 */           List errors = compiler.getErrors();
/* 220 */           int count = errors.size();
/* 221 */           for (int i = 0; i < count; i++) {
/* 222 */             CompilerError error = (CompilerError)errors.get(i);
/* 223 */             if (i > 0) message.append("\n");
/* 224 */             message.append("Line ");
/* 225 */             message.append(error.getStartLine());
/* 226 */             message.append(", column ");
/* 227 */             message.append(error.getStartColumn());
/* 228 */             message.append(": ");
/* 229 */             message.append(error.getMessage());
/*     */           }
/* 231 */           root.appendChild(doc.createTextNode(message.toString()));
/* 232 */           throw new AxisFault("Server.compileError", Messages.getMessage("badCompile00", jFile), null, new Element[] { root });
/*     */         }
/*     */ 
/* 236 */         ClassUtils.removeClassLoader(clsName);
/*     */ 
/* 238 */         soapServices.remove(clsName);
/*     */       }
/*     */ 
/* 241 */       ClassLoader cl = ClassUtils.getClassLoader(clsName);
/* 242 */       if (cl == null) {
/* 243 */         cl = new JWSClassLoader(clsName, msgContext.getClassLoader(), cFile);
/*     */       }
/*     */ 
/* 248 */       msgContext.setClassLoader(cl);
/*     */ 
/* 256 */       SOAPService rpc = (SOAPService)soapServices.get(clsName);
/* 257 */       if (rpc == null) {
/* 258 */         rpc = new SOAPService(new RPCProvider());
/* 259 */         rpc.setName(clsName);
/* 260 */         rpc.setOption("className", clsName);
/* 261 */         rpc.setEngine(msgContext.getAxisEngine());
/*     */ 
/* 264 */         String allowed = (String)getOption("allowedMethods");
/* 265 */         if (allowed == null) allowed = "*";
/* 266 */         rpc.setOption("allowedMethods", allowed);
/*     */ 
/* 269 */         String scope = (String)getOption("scope");
/* 270 */         if (scope == null) scope = Scope.DEFAULT.getName();
/* 271 */         rpc.setOption("scope", scope);
/*     */ 
/* 273 */         rpc.getInitializedServiceDesc(msgContext);
/*     */ 
/* 275 */         soapServices.put(clsName, rpc);
/*     */       }
/*     */ 
/* 279 */       rpc.setEngine(msgContext.getAxisEngine());
/*     */ 
/* 281 */       rpc.init();
/*     */ 
/* 284 */       msgContext.setService(rpc);
/*     */     }
/*     */ 
/* 287 */     if (log.isDebugEnabled())
/* 288 */       log.debug("Exit: JWSHandler::invoke");
/*     */   }
/*     */ 
/*     */   public void generateWSDL(MessageContext msgContext) throws AxisFault
/*     */   {
/*     */     try {
/* 294 */       setupService(msgContext);
/*     */     } catch (Exception e) {
/* 296 */       log.error(Messages.getMessage("exception00"), e);
/* 297 */       throw AxisFault.makeFault(e);
/*     */     }
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.handlers.JWSHandler
 * JD-Core Version:    0.6.0
 */