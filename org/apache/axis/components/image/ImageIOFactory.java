/*    */ package org.apache.axis.components.image;
/*    */ 
/*    */ import org.apache.axis.AxisProperties;
/*    */ import org.apache.axis.components.logger.LogFactory;
/*    */ import org.apache.axis.utils.ClassUtils;
/*    */ import org.apache.commons.logging.Log;
/*    */ 
/*    */ public class ImageIOFactory
/*    */ {
/* 30 */   protected static Log log = LogFactory.getLog(ImageIOFactory.class.getName());
/*    */ 
/*    */   public static ImageIO getImageIO()
/*    */   {
/* 56 */     ImageIO imageIO = (ImageIO)AxisProperties.newInstance(ImageIO.class);
/*    */ 
/* 61 */     if (imageIO == null) {
/*    */       try {
/* 63 */         Class cls = ClassUtils.forName("org.apache.axis.components.image.JDK13IO");
/* 64 */         imageIO = (ImageIO)cls.newInstance();
/*    */       } catch (Exception e) {
/* 66 */         log.debug("ImageIOFactory: No matching ImageIO found", e);
/*    */       }
/*    */     }
/*    */ 
/* 70 */     log.debug("axis.ImageIO: " + imageIO.getClass().getName());
/* 71 */     return imageIO;
/*    */   }
/*    */ 
/*    */   static
/*    */   {
/* 34 */     AxisProperties.setClassOverrideProperty(ImageIO.class, "axis.ImageIO");
/*    */ 
/* 40 */     AxisProperties.setClassDefaults(class$org$apache$axis$components$image$ImageIO, new String[] { "org.apache.axis.components.image.MerlinIO", "org.apache.axis.components.image.JimiIO", "org.apache.axis.components.image.JDK13IO" });
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.components.image.ImageIOFactory
 * JD-Core Version:    0.6.0
 */