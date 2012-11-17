/*     */ package org.apache.axis.encoding;
/*     */ 
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.Set;
/*     */ import org.apache.axis.utils.Messages;
/*     */ 
/*     */ public class TypeMappingRegistryImpl
/*     */   implements TypeMappingRegistry
/*     */ {
/*     */   private HashMap mapTM;
/*     */   private TypeMappingDelegate defaultDelTM;
/* 132 */   private boolean isDelegated = false;
/*     */ 
/*     */   public TypeMappingRegistryImpl(TypeMappingImpl tm)
/*     */   {
/* 139 */     this.mapTM = new HashMap();
/* 140 */     this.defaultDelTM = new TypeMappingDelegate(tm);
/*     */   }
/*     */ 
/*     */   public TypeMappingRegistryImpl()
/*     */   {
/* 149 */     this(true);
/*     */   }
/*     */ 
/*     */   public TypeMappingRegistryImpl(boolean registerDefaults) {
/* 153 */     this.mapTM = new HashMap();
/* 154 */     if (registerDefaults) {
/* 155 */       this.defaultDelTM = DefaultTypeMappingImpl.getSingletonDelegate();
/* 156 */       TypeMappingDelegate del = new TypeMappingDelegate(new DefaultSOAPEncodingTypeMappingImpl());
/* 157 */       register("http://schemas.xmlsoap.org/soap/encoding/", del);
/*     */     } else {
/* 159 */       this.defaultDelTM = new TypeMappingDelegate(TypeMappingDelegate.placeholder);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void delegate(TypeMappingRegistry secondaryTMR)
/*     */   {
/* 171 */     if ((this.isDelegated) || (secondaryTMR == null) || (secondaryTMR == this)) {
/* 172 */       return;
/*     */     }
/*     */ 
/* 175 */     this.isDelegated = true;
/* 176 */     String[] keys = secondaryTMR.getRegisteredEncodingStyleURIs();
/* 177 */     TypeMappingDelegate otherDefault = ((TypeMappingRegistryImpl)secondaryTMR).defaultDelTM;
/*     */ 
/* 179 */     if (keys != null) {
/* 180 */       for (int i = 0; i < keys.length; i++) {
/*     */         try {
/* 182 */           String nsURI = keys[i];
/* 183 */           TypeMappingDelegate tm = (TypeMappingDelegate)this.mapTM.get(nsURI);
/* 184 */           if (tm == null) {
/* 185 */             tm = (TypeMappingDelegate)createTypeMapping();
/* 186 */             tm.setSupportedEncodings(new String[] { nsURI });
/* 187 */             register(nsURI, tm);
/*     */           }
/*     */ 
/* 190 */           if (tm != null)
/*     */           {
/* 192 */             TypeMappingDelegate del = (TypeMappingDelegate)((TypeMappingRegistryImpl)secondaryTMR).mapTM.get(nsURI);
/*     */ 
/* 195 */             while (del.next != null) {
/* 196 */               TypeMappingDelegate nu = new TypeMappingDelegate(del.delegate);
/* 197 */               tm.setNext(nu);
/*     */ 
/* 199 */               if (del.next == otherDefault) {
/* 200 */                 nu.setNext(this.defaultDelTM);
/* 201 */                 break;
/*     */               }
/* 203 */               del = del.next;
/* 204 */               tm = nu;
/*     */             }
/*     */           }
/*     */         }
/*     */         catch (Exception e)
/*     */         {
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 214 */     if (this.defaultDelTM.delegate != TypeMappingDelegate.placeholder)
/* 215 */       this.defaultDelTM.setNext(otherDefault);
/*     */     else
/* 217 */       this.defaultDelTM.delegate = otherDefault.delegate;
/*     */   }
/*     */ 
/*     */   public javax.xml.rpc.encoding.TypeMapping register(String namespaceURI, javax.xml.rpc.encoding.TypeMapping mapping)
/*     */   {
/* 240 */     if ((mapping == null) || (!(mapping instanceof TypeMappingDelegate)))
/*     */     {
/* 242 */       throw new IllegalArgumentException(Messages.getMessage("badTypeMapping"));
/*     */     }
/*     */ 
/* 245 */     if (namespaceURI == null) {
/* 246 */       throw new IllegalArgumentException(Messages.getMessage("nullNamespaceURI"));
/*     */     }
/*     */ 
/* 250 */     TypeMappingDelegate del = (TypeMappingDelegate)mapping;
/* 251 */     TypeMappingDelegate old = (TypeMappingDelegate)this.mapTM.get(namespaceURI);
/* 252 */     if (old == null)
/* 253 */       del.setNext(this.defaultDelTM);
/*     */     else {
/* 255 */       del.setNext(old);
/*     */     }
/* 257 */     this.mapTM.put(namespaceURI, del);
/* 258 */     return old;
/*     */   }
/*     */ 
/*     */   public void registerDefault(javax.xml.rpc.encoding.TypeMapping mapping)
/*     */   {
/* 271 */     if ((mapping == null) || (!(mapping instanceof TypeMappingDelegate)))
/*     */     {
/* 273 */       throw new IllegalArgumentException(Messages.getMessage("badTypeMapping"));
/*     */     }
/*     */ 
/* 281 */     if (this.defaultDelTM.getNext() != null) {
/* 282 */       throw new IllegalArgumentException(Messages.getMessage("defaultTypeMappingSet"));
/*     */     }
/*     */ 
/* 286 */     this.defaultDelTM = ((TypeMappingDelegate)mapping);
/*     */   }
/*     */ 
/*     */   public void doRegisterFromVersion(String version)
/*     */   {
/* 296 */     if ((version == null) || (version.equals("1.0")) || (version.equals("1.2"))) {
/* 297 */       TypeMappingImpl.dotnet_soapenc_bugfix = false;
/*     */     } else {
/* 299 */       if (version.equals("1.1")) {
/* 300 */         TypeMappingImpl.dotnet_soapenc_bugfix = true;
/*     */ 
/* 302 */         return;
/* 303 */       }if (version.equals("1.3"))
/*     */       {
/* 305 */         this.defaultDelTM = new TypeMappingDelegate(DefaultJAXRPC11TypeMappingImpl.getSingleton());
/*     */       }
/*     */       else {
/* 308 */         throw new RuntimeException(Messages.getMessage("j2wBadTypeMapping00"));
/*     */       }
/*     */     }
/* 311 */     registerSOAPENCDefault(new TypeMappingDelegate(DefaultSOAPEncodingTypeMappingImpl.getSingleton()));
/*     */   }
/*     */ 
/*     */   private void registerSOAPENCDefault(TypeMappingDelegate mapping)
/*     */   {
/* 324 */     if (!this.mapTM.containsKey("http://schemas.xmlsoap.org/soap/encoding/")) {
/* 325 */       this.mapTM.put("http://schemas.xmlsoap.org/soap/encoding/", mapping);
/*     */     }
/*     */     else
/*     */     {
/* 331 */       TypeMappingDelegate del = (TypeMappingDelegate)this.mapTM.get("http://schemas.xmlsoap.org/soap/encoding/");
/*     */ 
/* 333 */       while ((del.getNext() != null) && (!(del.delegate instanceof DefaultTypeMappingImpl))) {
/* 334 */         del = del.getNext();
/*     */       }
/* 336 */       del.setNext(this.defaultDelTM);
/*     */     }
/*     */ 
/* 339 */     if (!this.mapTM.containsKey("http://www.w3.org/2003/05/soap-encoding")) {
/* 340 */       this.mapTM.put("http://www.w3.org/2003/05/soap-encoding", mapping);
/*     */     }
/*     */     else
/*     */     {
/* 346 */       TypeMappingDelegate del = (TypeMappingDelegate)this.mapTM.get("http://www.w3.org/2003/05/soap-encoding");
/*     */ 
/* 348 */       while ((del.getNext() != null) && (!(del.delegate instanceof DefaultTypeMappingImpl))) {
/* 349 */         del = del.getNext();
/*     */       }
/* 351 */       del.setNext(this.defaultDelTM);
/*     */     }
/*     */ 
/* 357 */     mapping.setNext(this.defaultDelTM);
/*     */   }
/*     */ 
/*     */   public javax.xml.rpc.encoding.TypeMapping getTypeMapping(String namespaceURI)
/*     */   {
/* 371 */     TypeMapping del = (TypeMappingDelegate)this.mapTM.get(namespaceURI);
/* 372 */     if (del == null) {
/* 373 */       del = (TypeMapping)getDefaultTypeMapping();
/*     */     }
/* 375 */     return del;
/*     */   }
/*     */ 
/*     */   public TypeMapping getOrMakeTypeMapping(String encodingStyle)
/*     */   {
/* 387 */     TypeMappingDelegate del = (TypeMappingDelegate)this.mapTM.get(encodingStyle);
/* 388 */     if ((del == null) || ((del.delegate instanceof DefaultTypeMappingImpl))) {
/* 389 */       del = (TypeMappingDelegate)createTypeMapping();
/* 390 */       del.setSupportedEncodings(new String[] { encodingStyle });
/* 391 */       register(encodingStyle, del);
/*     */     }
/* 393 */     return del;
/*     */   }
/*     */ 
/*     */   public javax.xml.rpc.encoding.TypeMapping unregisterTypeMapping(String namespaceURI)
/*     */   {
/* 404 */     return (TypeMappingDelegate)this.mapTM.remove(namespaceURI);
/*     */   }
/*     */ 
/*     */   public boolean removeTypeMapping(javax.xml.rpc.encoding.TypeMapping mapping)
/*     */   {
/* 415 */     String[] ns = getRegisteredEncodingStyleURIs();
/* 416 */     boolean rc = false;
/* 417 */     for (int i = 0; i < ns.length; i++) {
/* 418 */       if (getTypeMapping(ns[i]) == mapping) {
/* 419 */         rc = true;
/* 420 */         unregisterTypeMapping(ns[i]);
/*     */       }
/*     */     }
/* 423 */     return rc;
/*     */   }
/*     */ 
/*     */   public javax.xml.rpc.encoding.TypeMapping createTypeMapping()
/*     */   {
/* 433 */     TypeMappingImpl impl = new TypeMappingImpl();
/* 434 */     TypeMappingDelegate del = new TypeMappingDelegate(impl);
/* 435 */     del.setNext(this.defaultDelTM);
/* 436 */     return del;
/*     */   }
/*     */ 
/*     */   public String[] getRegisteredEncodingStyleURIs()
/*     */   {
/* 446 */     Set s = this.mapTM.keySet();
/* 447 */     if (s != null) {
/* 448 */       String[] rc = new String[s.size()];
/* 449 */       int i = 0;
/* 450 */       Iterator it = s.iterator();
/* 451 */       while (it.hasNext()) {
/* 452 */         rc[(i++)] = ((String)it.next());
/*     */       }
/* 454 */       return rc;
/*     */     }
/* 456 */     return null;
/*     */   }
/*     */ 
/*     */   public void clear()
/*     */   {
/* 464 */     this.mapTM.clear();
/*     */   }
/*     */ 
/*     */   public javax.xml.rpc.encoding.TypeMapping getDefaultTypeMapping()
/*     */   {
/* 472 */     return this.defaultDelTM;
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.encoding.TypeMappingRegistryImpl
 * JD-Core Version:    0.6.0
 */