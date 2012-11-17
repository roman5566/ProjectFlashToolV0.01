/*    */ package org.dyno.visual.swing.layouts;
/*    */ 
/*    */ import java.awt.Container;
/*    */ import java.lang.reflect.Method;
/*    */ import javax.swing.JComponent;
/*    */ 
/*    */ public abstract class LayoutStyle
/*    */ {
/* 81 */   private static LayoutStyle singleton5 = new DefaultLayoutStyle(null);
/*    */   private static LayoutStyle singleton6;
/*    */ 
/*    */   public static LayoutStyle getInstance()
/*    */   {
/* 85 */     String version = System.getProperty("java.version");
/* 86 */     if (version.startsWith("1.6")) {
/* 87 */       if (singleton6 == null)
/* 88 */         singleton6 = new Java6LayoutStyle();
/* 89 */       return singleton6;
/*    */     }
/* 91 */     return singleton5; } 
/*    */   public abstract int getContainerGap(JComponent paramJComponent, int paramInt, Container paramContainer);
/*    */ 
/*    */   public abstract int getPreferredGap(JComponent paramJComponent1, JComponent paramJComponent2, ComponentPlacement paramComponentPlacement, int paramInt, Container paramContainer);
/*    */ 
/* 95 */   public static enum ComponentPlacement { RELATED, UNRELATED, INDENT;
/*    */   }
/*    */ 
/*    */   private static class DefaultLayoutStyle extends LayoutStyle
/*    */   {
/*    */     public int getContainerGap(JComponent component, int position, Container parent)
/*    */     {
/* 12 */       return 12;
/*    */     }
/*    */ 
/*    */     public int getPreferredGap(JComponent component1, JComponent component2, LayoutStyle.ComponentPlacement type, int position, Container parent)
/*    */     {
/* 17 */       switch (type) {
/*    */       case INDENT:
/* 19 */         return 6;
/*    */       case RELATED:
/* 21 */         return 12;
/*    */       case UNRELATED:
/* 23 */         return 12;
/*    */       }
/* 25 */       return 12;
/*    */     }
/*    */   }
/*    */   private static class Java6LayoutStyle extends LayoutStyle { private static Class java6LayoutStyleClass;
/*    */     private static Class java6ComponentPlacementClass;
/*    */     private Object style;
/*    */     private static Method getContainerGap;
/*    */     private static Method getPreferredGap;
/*    */     private static Method valueOf;
/*    */ 
/* 40 */     public Java6LayoutStyle() { if (java6LayoutStyleClass == null)
/*    */         try {
/* 42 */           java6LayoutStyleClass = Class.forName("javax.swing.LayoutStyle");
/* 43 */           java6ComponentPlacementClass = Class.forName("javax.swing.LayoutStyle$ComponentPlacement");
/* 44 */           getContainerGap = java6LayoutStyleClass.getMethod("getContainerGap", new Class[] { JComponent.class, Integer.TYPE, Container.class });
/* 45 */           getPreferredGap = java6LayoutStyleClass.getMethod("getPreferredGap", new Class[] { JComponent.class, JComponent.class, java6ComponentPlacementClass, Integer.TYPE, Container.class });
/* 46 */           valueOf = java6ComponentPlacementClass.getMethod("valueOf", new Class[] { String.class });
/*    */         } catch (Exception e) {
/* 48 */           e.printStackTrace();
/*    */         }
/*    */       try
/*    */       {
/* 52 */         Method method = java6LayoutStyleClass.getMethod("getInstance", new Class[0]);
/* 53 */         this.style = method.invoke(null, new Object[0]);
/*    */       } catch (Exception e) {
/* 55 */         e.printStackTrace();
/*    */       } }
/*    */ 
/*    */     public int getContainerGap(JComponent component, int position, Container parent)
/*    */     {
/*    */       try
/*    */       {
/* 62 */         return ((Integer)getContainerGap.invoke(this.style, new Object[] { component, Integer.valueOf(position), parent })).intValue();
/*    */       } catch (Exception e) {
/* 64 */         e.printStackTrace();
/*    */       }
/* 66 */       return 12;
/*    */     }
/*    */ 
/*    */     public int getPreferredGap(JComponent component1, JComponent component2, LayoutStyle.ComponentPlacement type, int position, Container parent)
/*    */     {
/*    */       try {
/* 72 */         Object enumType = valueOf.invoke(null, new Object[] { type.name() });
/* 73 */         return ((Integer)getPreferredGap.invoke(this.style, new Object[] { component1, component2, enumType, Integer.valueOf(position), parent })).intValue();
/*    */       } catch (Exception e) {
/* 75 */         e.printStackTrace();
/*    */       }
/* 77 */       return 12;
/*    */     }
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.dyno.visual.swing.layouts.LayoutStyle
 * JD-Core Version:    0.6.0
 */