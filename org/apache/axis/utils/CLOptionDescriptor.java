/*     */ package org.apache.axis.utils;
/*     */ 
/*     */ public final class CLOptionDescriptor
/*     */ {
/*     */   public static final int ARGUMENT_REQUIRED = 2;
/*     */   public static final int ARGUMENT_OPTIONAL = 4;
/*     */   public static final int ARGUMENT_DISALLOWED = 8;
/*     */   public static final int ARGUMENTS_REQUIRED_2 = 16;
/*     */   public static final int DUPLICATES_ALLOWED = 32;
/*     */   private final int m_id;
/*     */   private final int m_flags;
/*     */   private final String m_name;
/*     */   private final String m_description;
/*     */   private final int[] m_incompatible;
/*     */ 
/*     */   public CLOptionDescriptor(String name, int flags, int id, String description)
/*     */   {
/*  60 */     this(name, flags, id, description, new int[] { (flags & 0x20) > 0 ? new int[0] : id });
/*     */   }
/*     */ 
/*     */   public CLOptionDescriptor(String name, int flags, int id, String description, int[] incompatable)
/*     */   {
/*  80 */     this.m_id = id;
/*  81 */     this.m_name = name;
/*  82 */     this.m_flags = flags;
/*  83 */     this.m_description = description;
/*  84 */     this.m_incompatible = incompatable;
/*     */   }
/*     */ 
/*     */   /** @deprecated */
/*     */   protected final int[] getIncompatble()
/*     */   {
/*  92 */     return getIncompatible();
/*     */   }
/*     */ 
/*     */   protected final int[] getIncompatible()
/*     */   {
/*  97 */     return this.m_incompatible;
/*     */   }
/*     */ 
/*     */   public final String getDescription()
/*     */   {
/* 107 */     return this.m_description;
/*     */   }
/*     */ 
/*     */   public final int getFlags()
/*     */   {
/* 118 */     return this.m_flags;
/*     */   }
/*     */ 
/*     */   public final int getId()
/*     */   {
/* 129 */     return this.m_id;
/*     */   }
/*     */ 
/*     */   public final String getName()
/*     */   {
/* 139 */     return this.m_name;
/*     */   }
/*     */ 
/*     */   public final String toString()
/*     */   {
/* 149 */     return "[OptionDescriptor " + this.m_name + ", " + this.m_id + ", " + this.m_flags + ", " + this.m_description + " ]";
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.utils.CLOptionDescriptor
 * JD-Core Version:    0.6.0
 */