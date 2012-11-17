/*     */ package org.dyno.visual.swing.layouts;
/*     */ 
/*     */ import java.awt.Component;
/*     */ import java.awt.Container;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Insets;
/*     */ import java.awt.LayoutManager2;
/*     */ import java.awt.Rectangle;
/*     */ import java.io.Serializable;
/*     */ import java.util.HashMap;
/*     */ import javax.swing.JComponent;
/*     */ 
/*     */ public class GroupLayout
/*     */   implements LayoutManager2, Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private HashMap<Component, Constraints> constraints;
/*     */ 
/*     */   public GroupLayout()
/*     */   {
/*  40 */     this.constraints = new HashMap();
/*     */   }
/*     */ 
/*     */   public Constraints getConstraints(Component comp) {
/*  44 */     return (Constraints)this.constraints.get(comp);
/*     */   }
/*     */   public void setConstraints(Component comp, Constraints constraint) {
/*  47 */     this.constraints.put(comp, constraint);
/*     */   }
/*     */ 
/*     */   public void addLayoutComponent(Component comp, Object con) {
/*  51 */     assert ((con != null) && ((con instanceof Constraints)));
/*  52 */     Constraints constraints = (Constraints)con;
/*  53 */     checkPreferredSize(comp, constraints);
/*  54 */     this.constraints.put(comp, constraints);
/*     */   }
/*     */ 
/*     */   private void checkPreferredSize(Component comp, Constraints constraints) {
/*  58 */     Dimension prefs = comp.getPreferredSize();
/*  59 */     Alignment axis = constraints.getHorizontal();
/*  60 */     if ((axis instanceof Leading)) {
/*  61 */       Leading leading = (Leading)axis;
/*  62 */       int size = leading.getSize();
/*  63 */       if (size == prefs.width)
/*  64 */         leading.setSize(-1);
/*  65 */     } else if ((axis instanceof Trailing)) {
/*  66 */       Trailing trailing = (Trailing)axis;
/*  67 */       int size = trailing.getSize();
/*  68 */       if (size == prefs.width)
/*  69 */         trailing.setSize(-1);
/*  70 */     } else if ((axis instanceof Bilateral)) {
/*  71 */       Bilateral bilateral = (Bilateral)axis;
/*  72 */       int pref = bilateral.getSpring().getPreferred();
/*  73 */       if (pref == prefs.width)
/*  74 */         bilateral.getSpring().setPreferred(-1);
/*     */     }
/*  76 */     axis = constraints.getVertical();
/*  77 */     if ((axis instanceof Leading)) {
/*  78 */       Leading leading = (Leading)axis;
/*  79 */       int size = leading.getSize();
/*  80 */       if (size == prefs.height)
/*  81 */         leading.setSize(-1);
/*  82 */     } else if ((axis instanceof Trailing)) {
/*  83 */       Trailing trailing = (Trailing)axis;
/*  84 */       int size = trailing.getSize();
/*  85 */       if (size == prefs.height)
/*  86 */         trailing.setSize(-1);
/*  87 */     } else if ((axis instanceof Bilateral)) {
/*  88 */       Bilateral bilateral = (Bilateral)axis;
/*  89 */       int pref = bilateral.getSpring().getPreferred();
/*  90 */       if (pref == prefs.height)
/*  91 */         bilateral.getSpring().setPreferred(-1);
/*     */     }
/*     */   }
/*     */ 
/*     */   public float getLayoutAlignmentX(Container target)
/*     */   {
/*  97 */     return 0.0F;
/*     */   }
/*     */ 
/*     */   public float getLayoutAlignmentY(Container target)
/*     */   {
/* 102 */     return 0.0F;
/*     */   }
/*     */ 
/*     */   public void invalidateLayout(Container target)
/*     */   {
/*     */   }
/*     */ 
/*     */   public Dimension maximumLayoutSize(Container target)
/*     */   {
/* 111 */     return new Dimension(2147483647, 2147483647);
/*     */   }
/*     */ 
/*     */   public void addLayoutComponent(String name, Component comp)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void layoutContainer(Container parent)
/*     */   {
/* 120 */     int width = parent.getWidth();
/* 121 */     int height = parent.getHeight();
/* 122 */     Dimension min = minimumLayoutSize(parent);
/* 123 */     if (width < min.width)
/* 124 */       width = min.width;
/* 125 */     if (height < min.height)
/* 126 */       height = min.height;
/* 127 */     int count = parent.getComponentCount();
/* 128 */     Insets insets = parent.getInsets();
/* 129 */     for (int i = 0; i < count; i++) {
/* 130 */       Component comp = parent.getComponent(i);
/* 131 */       if (comp == null)
/*     */         continue;
/* 133 */       Constraints cons = (Constraints)this.constraints.get(comp);
/* 134 */       if (cons == null)
/* 135 */         cons = createConstraintsFor(parent, comp);
/* 136 */       Alignment horizontal = cons.getHorizontal();
/* 137 */       Alignment vertical = cons.getVertical();
/* 138 */       Rectangle bounds = comp.getBounds();
/* 139 */       Dimension prefs = comp.getPreferredSize();
/* 140 */       int x = bounds.x; int y = bounds.y; int w = bounds.width; int h = bounds.height;
/* 141 */       if ((horizontal instanceof Leading)) {
/* 142 */         Leading leading = (Leading)horizontal;
/* 143 */         x = insets.left + leading.getLeading();
/* 144 */         int size = leading.getSize();
/* 145 */         w = size == -1 ? prefs.width : size;
/* 146 */       } else if ((horizontal instanceof Bilateral)) {
/* 147 */         Bilateral bilateral = (Bilateral)horizontal;
/* 148 */         x = insets.left + bilateral.getLeading();
/* 149 */         w = width - x - bilateral.getTrailing() - insets.right;
/* 150 */       } else if ((horizontal instanceof Trailing)) {
/* 151 */         Trailing trailing = (Trailing)horizontal;
/* 152 */         int size = trailing.getSize();
/* 153 */         w = size == -1 ? prefs.width : size;
/* 154 */         x = width - trailing.getTrailing() - w - insets.right;
/*     */       }
/* 156 */       if ((vertical instanceof Leading)) {
/* 157 */         Leading leading = (Leading)vertical;
/* 158 */         y = insets.top + leading.getLeading();
/* 159 */         int size = leading.getSize();
/* 160 */         h = size == -1 ? prefs.height : size;
/* 161 */       } else if ((vertical instanceof Bilateral)) {
/* 162 */         Bilateral bilateral = (Bilateral)vertical;
/* 163 */         y = insets.top + bilateral.getLeading();
/* 164 */         h = height - y - bilateral.getTrailing() - insets.bottom;
/* 165 */       } else if ((vertical instanceof Trailing)) {
/* 166 */         Trailing trailing = (Trailing)vertical;
/* 167 */         int size = trailing.getSize();
/* 168 */         h = size == -1 ? prefs.height : size;
/* 169 */         y = height - trailing.getTrailing() - h - insets.bottom;
/*     */       }
/* 171 */       comp.setBounds(x, y, w, h);
/*     */     }
/*     */   }
/*     */ 
/*     */   private Constraints createConstraintsFor(Container parent, Component comp)
/*     */   {
/* 177 */     LayoutStyle style = LayoutStyle.getInstance();
/* 178 */     int gap = style.getContainerGap((JComponent)comp, 3, parent);
/* 179 */     Rectangle bounds = comp.getBounds();
/* 180 */     Spring spring = new Spring(gap, gap);
/* 181 */     Leading horizontal = new Leading(bounds.x, bounds.width, spring);
/* 182 */     gap = style.getContainerGap((JComponent)comp, 5, parent);
/* 183 */     spring = new Spring(gap, gap);
/* 184 */     Leading vertical = new Leading(bounds.y, bounds.height, spring);
/* 185 */     Constraints cons = new Constraints(horizontal, vertical);
/* 186 */     this.constraints.put(comp, cons);
/* 187 */     return cons;
/*     */   }
/*     */ 
/*     */   public Dimension minimumLayoutSize(Container parent)
/*     */   {
/* 192 */     int width = 0;
/* 193 */     int height = 0;
/* 194 */     int count = parent.getComponentCount();
/* 195 */     Insets insets = parent.getInsets();
/* 196 */     for (int i = 0; i < count; i++) {
/* 197 */       Component comp = parent.getComponent(i);
/* 198 */       if (comp == null)
/*     */         continue;
/* 200 */       Dimension prefs = comp.getPreferredSize();
/* 201 */       Constraints cons = (Constraints)this.constraints.get(comp);
/* 202 */       if (cons == null)
/* 203 */         cons = createConstraintsFor(parent, comp);
/* 204 */       Alignment horizontal = cons.getHorizontal();
/* 205 */       Alignment vertical = cons.getVertical();
/* 206 */       if ((horizontal instanceof Bilateral)) {
/* 207 */         Bilateral bialteral = (Bilateral)horizontal;
/* 208 */         if (bialteral.getLeading() + bialteral.getTrailing() + bialteral.getSpring().getMinimum() + insets.left + insets.right > width)
/* 209 */           width = bialteral.getLeading() + bialteral.getTrailing() + bialteral.getSpring().getMinimum() + insets.left + insets.right;
/* 210 */       } else if ((horizontal instanceof Leading)) {
/* 211 */         Leading leading = (Leading)horizontal;
/* 212 */         int size = leading.getSize() == -1 ? prefs.width : leading.getSize();
/* 213 */         if (leading.getLeading() + size + leading.getSpring().getMinimum() + insets.left + insets.right > width)
/* 214 */           width = leading.getLeading() + size + leading.getSpring().getMinimum() + insets.left + insets.right;
/* 215 */       } else if ((horizontal instanceof Trailing)) {
/* 216 */         Trailing trailing = (Trailing)horizontal;
/* 217 */         int size = trailing.getSize() == -1 ? prefs.width : trailing.getSize();
/* 218 */         if (trailing.getTrailing() + size + trailing.getSpring().getMinimum() + insets.left + insets.right > width)
/* 219 */           width = trailing.getTrailing() + size + trailing.getSpring().getMinimum() + insets.left + insets.right;
/*     */       }
/* 221 */       if ((vertical instanceof Bilateral)) {
/* 222 */         Bilateral bilateral = (Bilateral)vertical;
/* 223 */         if (bilateral.getLeading() + bilateral.getTrailing() + bilateral.getSpring().getMinimum() + insets.top + insets.bottom > height)
/* 224 */           height = bilateral.getLeading() + bilateral.getTrailing() + bilateral.getSpring().getMinimum() + insets.top + insets.bottom;
/* 225 */       } else if ((vertical instanceof Leading)) {
/* 226 */         Leading leading = (Leading)vertical;
/* 227 */         int size = leading.getSize() == -1 ? prefs.height : leading.getSize();
/* 228 */         if (leading.getLeading() + size + leading.getSpring().getMinimum() + insets.top + insets.bottom > height)
/* 229 */           height = leading.getLeading() + size + leading.getSpring().getMinimum() + insets.top + insets.bottom;
/* 230 */       } else if ((vertical instanceof Trailing)) {
/* 231 */         Trailing trailing = (Trailing)vertical;
/* 232 */         int size = trailing.getSize() == -1 ? prefs.height : trailing.getSize();
/* 233 */         if (trailing.getTrailing() + size + trailing.getSpring().getMinimum() + insets.top + insets.bottom > height)
/* 234 */           height = trailing.getTrailing() + size + trailing.getSpring().getMinimum() + insets.top + insets.bottom;
/*     */       }
/*     */     }
/* 237 */     return new Dimension(width, height);
/*     */   }
/*     */ 
/*     */   public Dimension preferredLayoutSize(Container parent)
/*     */   {
/* 242 */     int width = 0;
/* 243 */     int height = 0;
/* 244 */     int count = parent.getComponentCount();
/* 245 */     Insets insets = parent.getInsets();
/* 246 */     for (int i = 0; i < count; i++) {
/* 247 */       Component comp = parent.getComponent(i);
/* 248 */       if (comp == null)
/*     */         continue;
/* 250 */       Dimension prefs = comp.getPreferredSize();
/* 251 */       Constraints cons = (Constraints)this.constraints.get(comp);
/* 252 */       if (cons == null)
/* 253 */         cons = createConstraintsFor(parent, comp);
/* 254 */       Alignment horizontal = cons.getHorizontal();
/* 255 */       Alignment vertical = cons.getVertical();
/* 256 */       if ((horizontal instanceof Bilateral)) {
/* 257 */         Bilateral bilateral = (Bilateral)horizontal;
/* 258 */         int pref = bilateral.getSpring().getPreferred();
/* 259 */         pref = pref == -1 ? prefs.width : pref;
/* 260 */         if (bilateral.getLeading() + bilateral.getTrailing() + pref + insets.left + insets.right > width)
/* 261 */           width = bilateral.getLeading() + bilateral.getTrailing() + pref + insets.left + insets.right;
/* 262 */       } else if ((horizontal instanceof Leading)) {
/* 263 */         Leading leading = (Leading)horizontal;
/* 264 */         int size = leading.getSize() == -1 ? prefs.width : leading.getSize();
/* 265 */         if (leading.getLeading() + size + leading.getSpring().getPreferred() + insets.left + insets.right > width)
/* 266 */           width = leading.getLeading() + size + leading.getSpring().getPreferred() + insets.left + insets.right;
/* 267 */       } else if ((horizontal instanceof Trailing)) {
/* 268 */         Trailing trailing = (Trailing)horizontal;
/* 269 */         int size = trailing.getSize() == -1 ? prefs.width : trailing.getSize();
/* 270 */         if (trailing.getTrailing() + size + trailing.getSpring().getPreferred() + insets.left + insets.right > width)
/* 271 */           width = trailing.getTrailing() + size + trailing.getSpring().getPreferred() + insets.left + insets.right;
/*     */       }
/* 273 */       if ((vertical instanceof Bilateral)) {
/* 274 */         Bilateral bilateral = (Bilateral)vertical;
/* 275 */         int pref = bilateral.getSpring().getPreferred();
/* 276 */         pref = pref == -1 ? prefs.height : pref;
/* 277 */         if (bilateral.getLeading() + bilateral.getTrailing() + pref + insets.top + insets.bottom > height)
/* 278 */           height = bilateral.getLeading() + bilateral.getTrailing() + pref + insets.top + insets.bottom;
/* 279 */       } else if ((vertical instanceof Leading)) {
/* 280 */         Leading leading = (Leading)vertical;
/* 281 */         int size = leading.getSize() == -1 ? prefs.height : leading.getSize();
/* 282 */         if (leading.getLeading() + size + leading.getSpring().getPreferred() + insets.top + insets.bottom > height)
/* 283 */           height = leading.getLeading() + size + leading.getSpring().getPreferred() + insets.top + insets.bottom;
/* 284 */       } else if ((vertical instanceof Trailing)) {
/* 285 */         Trailing trailing = (Trailing)vertical;
/* 286 */         int size = trailing.getSize() == -1 ? prefs.height : trailing.getSize();
/* 287 */         if (trailing.getTrailing() + size + trailing.getSpring().getPreferred() + insets.top + insets.bottom > height)
/* 288 */           height = trailing.getTrailing() + size + trailing.getSpring().getPreferred() + insets.top + insets.bottom;
/*     */       }
/*     */     }
/* 291 */     return new Dimension(width, height);
/*     */   }
/*     */ 
/*     */   public void removeLayoutComponent(Component comp)
/*     */   {
/* 296 */     this.constraints.remove(comp);
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.dyno.visual.swing.layouts.GroupLayout
 * JD-Core Version:    0.6.0
 */