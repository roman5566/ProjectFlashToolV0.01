/*     */ package org.apache.axis.management;
/*     */ 
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import org.apache.axis.components.logger.LogFactory;
/*     */ import org.apache.axis.i18n.Messages;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public class Registrar
/*     */ {
/*  35 */   protected static Log log = LogFactory.getLog(Registrar.class.getName());
/*     */ 
/*  83 */   private static ModelerBinding modelerBinding = null;
/*     */ 
/*     */   public static boolean register(Object objectToRegister, String name, String context)
/*     */   {
/*  47 */     if (isBound()) {
/*  48 */       if (log.isDebugEnabled()) {
/*  49 */         log.debug("Registering " + objectToRegister + " as " + name);
/*     */       }
/*     */ 
/*  52 */       return modelerBinding.register(objectToRegister, name, context);
/*     */     }
/*  54 */     return false;
/*     */   }
/*     */ 
/*     */   public static boolean isBound()
/*     */   {
/*  66 */     createModelerBinding();
/*  67 */     return modelerBinding.canBind();
/*     */   }
/*     */ 
/*     */   private static void createModelerBinding()
/*     */   {
/*  75 */     if (modelerBinding == null)
/*  76 */       modelerBinding = new ModelerBinding();  } 
/* 109 */   static class ModelerBinding { protected static Log log = LogFactory.getLog(ModelerBinding.class.getName());
/*     */     Object registry;
/*     */     Method registerComponent;
/*     */ 
/*  94 */     public ModelerBinding() { bindToModeler();
/*     */     }
/*     */ 
/*     */     public boolean canBind()
/*     */     {
/* 103 */       return this.registry != null;
/*     */     }
/*     */ 
/*     */     public boolean register(Object objectToRegister, String name, String context)
/*     */     {
/* 128 */       if (this.registry != null) {
/* 129 */         Object[] args = { objectToRegister, name, context };
/*     */         try {
/* 131 */           this.registerComponent.invoke(this.registry, args);
/* 132 */           if (log.isDebugEnabled())
/* 133 */             log.debug("Registered " + name + " in " + context);
/*     */         }
/*     */         catch (IllegalAccessException e) {
/* 136 */           log.error(e);
/* 137 */           return false;
/*     */         } catch (IllegalArgumentException e) {
/* 139 */           log.error(e);
/* 140 */           return false;
/*     */         } catch (InvocationTargetException e) {
/* 142 */           log.error(e);
/* 143 */           return false;
/*     */         }
/* 145 */         return true;
/*     */       }
/* 147 */       return false;
/*     */     }
/*     */ 
/*     */     private boolean bindToModeler()
/*     */     {
/* 157 */       Exception ex = null;
/*     */       try
/*     */       {
/* 160 */         clazz = Class.forName("org.apache.commons.modeler.Registry");
/*     */       }
/*     */       catch (ClassNotFoundException e)
/*     */       {
/*     */         Class clazz;
/* 163 */         this.registry = null;
/* 164 */         return false;
/*     */       }
/*     */       try
/*     */       {
/*     */         Class clazz;
/* 167 */         Class[] getRegistryArgs = { Object.class, Object.class };
/* 168 */         Method getRegistry = clazz.getMethod("getRegistry", getRegistryArgs);
/* 169 */         Object[] getRegistryOptions = { null, null };
/* 170 */         this.registry = getRegistry.invoke(null, getRegistryOptions);
/* 171 */         Class[] registerArgs = { Object.class, String.class, String.class };
/*     */ 
/* 174 */         this.registerComponent = clazz.getMethod("registerComponent", registerArgs);
/*     */       } catch (IllegalAccessException e) {
/* 176 */         ex = e;
/*     */       } catch (IllegalArgumentException e) {
/* 178 */         ex = e;
/*     */       } catch (InvocationTargetException e) {
/* 180 */         ex = e;
/*     */       } catch (NoSuchMethodException e) {
/* 182 */         ex = e;
/*     */       }
/*     */ 
/* 185 */       if (ex != null)
/*     */       {
/* 187 */         log.warn(Messages.getMessage("Registrar.cantregister"), ex);
/*     */ 
/* 189 */         this.registry = null;
/*     */ 
/* 191 */         return false;
/*     */       }
/*     */ 
/* 194 */       return true;
/*     */     }
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.management.Registrar
 * JD-Core Version:    0.6.0
 */