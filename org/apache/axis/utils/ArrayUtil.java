/*     */ package org.apache.axis.utils;
/*     */ 
/*     */ import java.lang.reflect.Array;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ 
/*     */ public class ArrayUtil
/*     */ {
/*  34 */   public static final NonConvertable NON_CONVERTABLE = new NonConvertable();
/*     */ 
/*     */   public static Object convertObjectToArray(Object obj, Class arrayType)
/*     */   {
/*     */     try
/*     */     {
/*  46 */       ArrayInfo arri = new ArrayInfo(null);
/*  47 */       boolean rc = internalIsConvertable(obj.getClass(), arri, arrayType);
/*  48 */       if (!rc) {
/*  49 */         return obj;
/*     */       }
/*     */ 
/*  52 */       BeanPropertyDescriptor pd = null;
/*  53 */       pd = getArrayComponentPD(obj.getClass());
/*  54 */       if (pd == null) {
/*  55 */         return NON_CONVERTABLE;
/*     */       }
/*  57 */       Object comp = pd.get(obj);
/*  58 */       if (comp == null) {
/*  59 */         return null;
/*     */       }
/*  61 */       int arraylen = 0;
/*  62 */       if (comp.getClass().isArray())
/*  63 */         arraylen = Array.getLength(comp);
/*     */       else {
/*  65 */         return comp;
/*     */       }
/*     */ 
/*  68 */       int[] dims = new int[arri.dimension];
/*  69 */       dims[0] = arraylen;
/*  70 */       Object targetArray = Array.newInstance(arri.componentType, dims);
/*     */ 
/*  72 */       for (int i = 0; i < arraylen; i++) {
/*  73 */         Object subarray = Array.get(comp, i);
/*  74 */         Class subarrayClass = arrayType.getComponentType();
/*  75 */         Array.set(targetArray, i, convertObjectToArray(subarray, subarrayClass));
/*     */       }
/*  77 */       return targetArray;
/*     */     } catch (InvocationTargetException e) {
/*  79 */       e.printStackTrace();
/*     */     } catch (IllegalAccessException e) {
/*  81 */       e.printStackTrace();
/*     */     }
/*     */ 
/*  84 */     return null;
/*     */   }
/*     */ 
/*     */   public static boolean isConvertable(Class clazz, Class arrayType)
/*     */   {
/*  95 */     ArrayInfo arrInfo = new ArrayInfo(null);
/*  96 */     return internalIsConvertable(clazz, arrInfo, arrayType);
/*     */   }
/*     */ 
/*     */   private static boolean internalIsConvertable(Class clazz, ArrayInfo arri, Class arrayType)
/*     */   {
/* 107 */     BeanPropertyDescriptor pd = null; BeanPropertyDescriptor oldPd = null;
/* 108 */     if (!arrayType.isArray()) {
/* 109 */       return false;
/*     */     }
/* 111 */     Class destArrCompType = arrayType.getComponentType();
/* 112 */     Class src = clazz;
/* 113 */     int depth = 0;
/*     */     while (true)
/*     */     {
/* 116 */       pd = getArrayComponentPD(src);
/* 117 */       if (pd == null)
/*     */         break;
/* 119 */       depth++;
/* 120 */       src = pd.getType();
/* 121 */       oldPd = pd;
/* 122 */       if (destArrCompType.isAssignableFrom(src)) {
/* 123 */         break;
/*     */       }
/*     */     }
/* 126 */     if ((depth == 0) || (oldPd.getType() == null)) {
/* 127 */       return false;
/*     */     }
/*     */ 
/* 130 */     arri.componentType = oldPd.getType();
/* 131 */     arri.dimension = depth;
/*     */ 
/* 133 */     Class componentType = oldPd.getType();
/* 134 */     int[] dims = new int[depth];
/* 135 */     Object array = Array.newInstance(componentType, dims);
/* 136 */     arri.arrayType = array.getClass();
/*     */ 
/* 139 */     return arrayType.isAssignableFrom(arri.arrayType);
/*     */   }
/*     */ 
/*     */   private static BeanPropertyDescriptor getArrayComponentPD(Class clazz)
/*     */   {
/* 150 */     BeanPropertyDescriptor bpd = null;
/* 151 */     int count = 0;
/* 152 */     Class cls = clazz;
/* 153 */     while ((cls != null) && (cls.getName() != null) && (!cls.getName().equals("java.lang.Object"))) {
/* 154 */       BeanPropertyDescriptor[] bpds = BeanUtils.getPd(clazz);
/* 155 */       for (int i = 0; i < bpds.length; i++) {
/* 156 */         BeanPropertyDescriptor pd = bpds[i];
/* 157 */         if ((pd.isReadable()) && (pd.isWriteable()) && (pd.isIndexed())) {
/* 158 */           count++;
/* 159 */           if (count >= 2) {
/* 160 */             return null;
/*     */           }
/* 162 */           bpd = pd;
/*     */         }
/*     */       }
/* 165 */       cls = cls.getSuperclass();
/*     */     }
/*     */ 
/* 168 */     if (count == 1) {
/* 169 */       return bpd;
/*     */     }
/*     */ 
/* 172 */     return null;
/*     */   }
/*     */ 
/*     */   public static int getArrayDimension(Class arrayType)
/*     */   {
/* 181 */     if (!arrayType.isArray())
/* 182 */       return 0;
/* 183 */     int dim = 0;
/* 184 */     Class compType = arrayType;
/*     */     do {
/* 186 */       dim++;
/* 187 */       arrayType = compType;
/* 188 */       compType = arrayType.getComponentType();
/* 189 */     }while (compType.isArray());
/*     */ 
/* 191 */     return dim;
/*     */   }
/*     */ 
/*     */   private static Object createNewInstance(Class cls) throws InstantiationException, IllegalAccessException {
/* 195 */     Object obj = null;
/* 196 */     if (!cls.isPrimitive()) {
/* 197 */       obj = cls.newInstance();
/*     */     }
/* 199 */     else if (Boolean.TYPE.isAssignableFrom(cls))
/* 200 */       obj = new Boolean(false);
/* 201 */     else if (Byte.TYPE.isAssignableFrom(cls))
/* 202 */       obj = new Byte(0);
/* 203 */     else if (Character.TYPE.isAssignableFrom(cls))
/* 204 */       obj = new Character('\000');
/* 205 */     else if (Short.TYPE.isAssignableFrom(cls))
/* 206 */       obj = new Short(0);
/* 207 */     else if (Integer.TYPE.isAssignableFrom(cls))
/* 208 */       obj = new Integer(0);
/* 209 */     else if (Long.TYPE.isAssignableFrom(cls))
/* 210 */       obj = new Long(0L);
/* 211 */     else if (Float.TYPE.isAssignableFrom(cls))
/* 212 */       obj = new Float(0.0F);
/* 213 */     else if (Double.TYPE.isAssignableFrom(cls)) {
/* 214 */       obj = new Double(0.0D);
/*     */     }
/*     */ 
/* 217 */     return obj;
/*     */   }
/*     */ 
/*     */   public static Object convertArrayToObject(Object array, Class destClass)
/*     */   {
/* 227 */     int dim = getArrayDimension(array.getClass());
/* 228 */     if (dim == 0) {
/* 229 */       return null;
/*     */     }
/*     */ 
/* 232 */     Object dest = null;
/*     */     try
/*     */     {
/* 236 */       int arraylen = Array.getLength(array);
/* 237 */       Object destArray = null;
/* 238 */       Class destComp = null;
/* 239 */       if (!destClass.isArray()) {
/* 240 */         dest = destClass.newInstance();
/* 241 */         BeanPropertyDescriptor pd = getArrayComponentPD(destClass);
/* 242 */         if (pd == null) {
/* 243 */           return null;
/*     */         }
/* 245 */         destComp = pd.getType();
/* 246 */         destArray = Array.newInstance(destComp, arraylen);
/* 247 */         pd.set(dest, destArray);
/*     */       } else {
/* 249 */         destComp = destClass.getComponentType();
/* 250 */         dest = Array.newInstance(destComp, arraylen);
/* 251 */         destArray = dest;
/*     */       }
/*     */ 
/* 255 */       for (int i = 0; i < arraylen; i++) {
/* 256 */         Array.set(destArray, i, createNewInstance(destComp));
/*     */       }
/*     */ 
/* 260 */       for (int i = 0; i < arraylen; i++) {
/* 261 */         Object comp = Array.get(array, i);
/*     */ 
/* 263 */         if (comp == null) {
/*     */           continue;
/*     */         }
/* 266 */         if (comp.getClass().isArray()) {
/* 267 */           Class cls = Array.get(destArray, i).getClass();
/* 268 */           Array.set(destArray, i, convertArrayToObject(comp, cls));
/*     */         }
/*     */         else {
/* 271 */           Array.set(destArray, i, comp);
/*     */         }
/*     */       }
/*     */     } catch (IllegalAccessException ignore) {
/* 275 */       return null;
/*     */     } catch (InvocationTargetException ignore) {
/* 277 */       return null;
/*     */     } catch (InstantiationException ignore) {
/* 279 */       return null;
/*     */     }
/*     */ 
/* 282 */     return dest;
/*     */   }
/*     */ 
/*     */   public static class NonConvertable
/*     */   {
/*     */   }
/*     */ 
/*     */   private static class ArrayInfo
/*     */   {
/*     */     public Class componentType;
/*     */     public Class arrayType;
/*     */     public int dimension;
/*     */ 
/*     */     private ArrayInfo()
/*     */     {
/*     */     }
/*     */ 
/*     */     ArrayInfo(ArrayUtil.1 x0)
/*     */     {
/*  23 */       this();
/*     */     }
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.utils.ArrayUtil
 * JD-Core Version:    0.6.0
 */