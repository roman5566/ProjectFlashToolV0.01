/*     */ package org.apache.axis.transport.http;
/*     */ 
/*     */ import java.util.Enumeration;
/*     */ import javax.servlet.http.HttpServletRequest;
/*     */ import javax.servlet.http.HttpSession;
/*     */ import org.apache.axis.session.Session;
/*     */ 
/*     */ public class AxisHttpSession
/*     */   implements Session
/*     */ {
/*     */   public static final String AXIS_SESSION_MARKER = "axis.isAxisSession";
/*     */   private HttpSession rep;
/*     */   private HttpServletRequest req;
/*     */ 
/*     */   public AxisHttpSession(HttpServletRequest realRequest)
/*     */   {
/*  39 */     this.req = realRequest;
/*     */   }
/*     */ 
/*     */   public AxisHttpSession(HttpSession realSession)
/*     */   {
/*  44 */     if (realSession != null)
/*  45 */       setRep(realSession);
/*     */   }
/*     */ 
/*     */   public HttpSession getRep()
/*     */   {
/*  52 */     ensureSession();
/*  53 */     return this.rep;
/*     */   }
/*     */ 
/*     */   private void setRep(HttpSession realSession)
/*     */   {
/*  62 */     this.rep = realSession;
/*  63 */     this.rep.setAttribute("axis.isAxisSession", Boolean.TRUE);
/*     */   }
/*     */ 
/*     */   public Object get(String key)
/*     */   {
/*  72 */     ensureSession();
/*  73 */     return this.rep.getAttribute(key);
/*     */   }
/*     */ 
/*     */   public void set(String key, Object value)
/*     */   {
/*  83 */     ensureSession();
/*  84 */     this.rep.setAttribute(key, value);
/*     */   }
/*     */ 
/*     */   public void remove(String key)
/*     */   {
/*  93 */     ensureSession();
/*  94 */     this.rep.removeAttribute(key);
/*     */   }
/*     */ 
/*     */   public Enumeration getKeys()
/*     */   {
/* 101 */     ensureSession();
/* 102 */     return this.rep.getAttributeNames();
/*     */   }
/*     */ 
/*     */   public void setTimeout(int timeout)
/*     */   {
/* 113 */     ensureSession();
/* 114 */     this.rep.setMaxInactiveInterval(timeout);
/*     */   }
/*     */ 
/*     */   public int getTimeout()
/*     */   {
/* 123 */     ensureSession();
/* 124 */     return this.rep.getMaxInactiveInterval();
/*     */   }
/*     */ 
/*     */   public void touch()
/*     */   {
/*     */   }
/*     */ 
/*     */   public void invalidate()
/*     */   {
/* 138 */     this.rep.invalidate();
/*     */   }
/*     */ 
/*     */   protected void ensureSession() {
/* 142 */     if (this.rep == null)
/* 143 */       setRep(this.req.getSession());
/*     */   }
/*     */ 
/*     */   public Object getLockObject()
/*     */   {
/* 156 */     ensureSession();
/* 157 */     return this.rep;
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.transport.http.AxisHttpSession
 * JD-Core Version:    0.6.0
 */