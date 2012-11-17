/*     */ package org.apache.axis.utils;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import org.apache.axis.components.logger.LogFactory;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public class NSStack
/*     */ {
/*  47 */   protected static Log log = LogFactory.getLog(NSStack.class.getName());
/*     */   private Mapping[] stack;
/*  51 */   private int top = 0;
/*  52 */   private int iterator = 0;
/*  53 */   private int currentDefaultNS = -1;
/*  54 */   private boolean optimizePrefixes = true;
/*     */ 
/*  59 */   private final boolean traceEnabled = log.isTraceEnabled();
/*     */ 
/*     */   public NSStack(boolean optimizePrefixes) {
/*  62 */     this.optimizePrefixes = optimizePrefixes;
/*  63 */     this.stack = new Mapping[32];
/*  64 */     this.stack[0] = null;
/*     */   }
/*     */ 
/*     */   public NSStack() {
/*  68 */     this.stack = new Mapping[32];
/*  69 */     this.stack[0] = null;
/*     */   }
/*     */ 
/*     */   public void push()
/*     */   {
/*  76 */     this.top += 1;
/*     */ 
/*  78 */     if (this.top >= this.stack.length) {
/*  79 */       Mapping[] newstack = new Mapping[this.stack.length * 2];
/*  80 */       System.arraycopy(this.stack, 0, newstack, 0, this.stack.length);
/*  81 */       this.stack = newstack;
/*     */     }
/*     */ 
/*  84 */     if (this.traceEnabled) {
/*  85 */       log.trace("NSPush (" + this.stack.length + ")");
/*     */     }
/*  87 */     this.stack[this.top] = null;
/*     */   }
/*     */ 
/*     */   public void pop()
/*     */   {
/*  94 */     clearFrame();
/*     */ 
/*  96 */     this.top -= 1;
/*     */ 
/* 100 */     if (this.top < this.currentDefaultNS)
/*     */     {
/* 102 */       this.currentDefaultNS = this.top;
/* 103 */       while ((this.currentDefaultNS > 0) && (
/* 104 */         (this.stack[this.currentDefaultNS] == null) || (this.stack[this.currentDefaultNS].getPrefix().length() != 0)))
/*     */       {
/* 107 */         this.currentDefaultNS -= 1;
/*     */       }
/*     */     }
/*     */ 
/* 111 */     if (this.top == 0) {
/* 112 */       if (this.traceEnabled) {
/* 113 */         log.trace("NSPop (" + Messages.getMessage("empty00") + ")");
/*     */       }
/* 115 */       return;
/*     */     }
/*     */ 
/* 118 */     if (this.traceEnabled)
/* 119 */       log.trace("NSPop (" + this.stack.length + ")");
/*     */   }
/*     */ 
/*     */   public ArrayList cloneFrame()
/*     */   {
/* 127 */     if (this.stack[this.top] == null) return null;
/*     */ 
/* 129 */     ArrayList clone = new ArrayList();
/*     */ 
/* 131 */     for (Mapping map = topOfFrame(); map != null; map = next()) {
/* 132 */       clone.add(map);
/*     */     }
/*     */ 
/* 135 */     return clone;
/*     */   }
/*     */ 
/*     */   private void clearFrame()
/*     */   {
/* 142 */     while (this.stack[this.top] != null) this.top -= 1;
/*     */   }
/*     */ 
/*     */   public Mapping topOfFrame()
/*     */   {
/* 152 */     this.iterator = this.top;
/* 153 */     while (this.stack[this.iterator] != null) this.iterator -= 1;
/* 154 */     this.iterator += 1;
/* 155 */     return next();
/*     */   }
/*     */ 
/*     */   public Mapping next()
/*     */   {
/* 162 */     if (this.iterator > this.top) {
/* 163 */       return null;
/*     */     }
/* 165 */     return this.stack[(this.iterator++)];
/*     */   }
/*     */ 
/*     */   public void add(String namespaceURI, String prefix)
/*     */   {
/* 175 */     int idx = this.top;
/* 176 */     prefix = prefix.intern();
/*     */     try
/*     */     {
/* 179 */       for (int cursor = this.top; this.stack[cursor] != null; cursor--) {
/* 180 */         if (this.stack[cursor].getPrefix() == prefix) {
/* 181 */           this.stack[cursor].setNamespaceURI(namespaceURI);
/* 182 */           idx = cursor;
/* 183 */           jsr 51;
/*     */         }
/*     */       }
/*     */ 
/* 187 */       push();
/* 188 */       this.stack[this.top] = new Mapping(namespaceURI, prefix);
/* 189 */       idx = this.top;
/*     */     }
/*     */     finally
/*     */     {
/* 193 */       if (prefix.length() == 0)
/* 194 */         this.currentDefaultNS = idx;
/*     */     }
/*     */   }
/*     */ 
/*     */   public String getPrefix(String namespaceURI, boolean noDefault)
/*     */   {
/* 215 */     if ((namespaceURI == null) || (namespaceURI.length() == 0)) {
/* 216 */       return null;
/*     */     }
/* 218 */     if (this.optimizePrefixes)
/*     */     {
/* 221 */       if ((!noDefault) && (this.currentDefaultNS > 0) && (this.stack[this.currentDefaultNS] != null) && (namespaceURI == this.stack[this.currentDefaultNS].getNamespaceURI()))
/*     */       {
/* 223 */         return "";
/*     */       }
/*     */     }
/* 225 */     namespaceURI = namespaceURI.intern();
/*     */ 
/* 227 */     for (int cursor = this.top; cursor > 0; cursor--) {
/* 228 */       Mapping map = this.stack[cursor];
/* 229 */       if (map == null) {
/*     */         continue;
/*     */       }
/* 232 */       if (map.getNamespaceURI() == namespaceURI) {
/* 233 */         String possiblePrefix = map.getPrefix();
/* 234 */         if ((noDefault) && (possiblePrefix.length() == 0))
/*     */         {
/*     */           continue;
/*     */         }
/*     */ 
/* 239 */         for (int cursor2 = this.top; ; cursor2--) {
/* 240 */           if (cursor2 == cursor)
/* 241 */             return possiblePrefix;
/* 242 */           map = this.stack[cursor2];
/* 243 */           if ((map != null) && 
/* 245 */             (possiblePrefix == map.getPrefix())) {
/*     */             break;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 251 */     return null;
/*     */   }
/*     */ 
/*     */   public String getPrefix(String namespaceURI)
/*     */   {
/* 259 */     return getPrefix(namespaceURI, false);
/*     */   }
/*     */ 
/*     */   public String getNamespaceURI(String prefix)
/*     */   {
/* 266 */     if (prefix == null) {
/* 267 */       prefix = "";
/*     */     }
/* 269 */     prefix = prefix.intern();
/*     */ 
/* 271 */     for (int cursor = this.top; cursor > 0; cursor--) {
/* 272 */       Mapping map = this.stack[cursor];
/* 273 */       if (map == null)
/*     */         continue;
/* 275 */       if (map.getPrefix() == prefix) {
/* 276 */         return map.getNamespaceURI();
/*     */       }
/*     */     }
/* 279 */     return null;
/*     */   }
/*     */ 
/*     */   public void dump(String dumpPrefix)
/*     */   {
/* 288 */     for (int cursor = this.top; cursor > 0; cursor--) {
/* 289 */       Mapping map = this.stack[cursor];
/*     */ 
/* 291 */       if (map == null)
/* 292 */         log.trace(dumpPrefix + Messages.getMessage("stackFrame00"));
/*     */       else
/* 294 */         log.trace(dumpPrefix + map.getNamespaceURI() + " -> " + map.getPrefix());
/*     */     }
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.utils.NSStack
 * JD-Core Version:    0.6.0
 */