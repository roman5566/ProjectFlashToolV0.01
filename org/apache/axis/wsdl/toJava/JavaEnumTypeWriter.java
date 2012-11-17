/*     */ package org.apache.axis.wsdl.toJava;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.PrintWriter;
/*     */ import java.util.Vector;
/*     */ import javax.xml.namespace.QName;
/*     */ import org.apache.axis.utils.JavaUtils;
/*     */ import org.apache.axis.utils.Messages;
/*     */ import org.apache.axis.wsdl.symbolTable.TypeEntry;
/*     */ 
/*     */ public class JavaEnumTypeWriter extends JavaClassWriter
/*     */ {
/*     */   private Vector elements;
/*     */   private TypeEntry type;
/*     */ 
/*     */   protected JavaEnumTypeWriter(Emitter emitter, TypeEntry type, Vector elements)
/*     */   {
/*  47 */     super(emitter, type.getName(), "enumType");
/*     */ 
/*  49 */     this.elements = elements;
/*  50 */     this.type = type;
/*     */   }
/*     */ 
/*     */   protected String getImplementsText()
/*     */   {
/*  59 */     return "implements java.io.Serializable ";
/*     */   }
/*     */ 
/*     */   protected void writeFileBody(PrintWriter pw)
/*     */     throws IOException
/*     */   {
/*  73 */     String javaName = getClassName();
/*     */ 
/*  77 */     String baseType = ((TypeEntry)this.elements.get(0)).getName();
/*  78 */     String baseClass = baseType;
/*     */ 
/*  80 */     if (baseType.indexOf("int") == 0)
/*  81 */       baseClass = "java.lang.Integer";
/*  82 */     else if (baseType.indexOf("char") == 0)
/*  83 */       baseClass = "java.lang.Character";
/*  84 */     else if (baseType.indexOf("short") == 0)
/*  85 */       baseClass = "java.lang.Short";
/*  86 */     else if (baseType.indexOf("long") == 0)
/*  87 */       baseClass = "java.lang.Long";
/*  88 */     else if (baseType.indexOf("double") == 0)
/*  89 */       baseClass = "java.lang.Double";
/*  90 */     else if (baseType.indexOf("float") == 0)
/*  91 */       baseClass = "java.lang.Float";
/*  92 */     else if (baseType.indexOf("byte") == 0) {
/*  93 */       baseClass = "java.lang.Byte";
/*     */     }
/*     */ 
/*  97 */     Vector values = new Vector();
/*     */ 
/*  99 */     for (int i = 1; i < this.elements.size(); i++) {
/* 100 */       String value = (String)this.elements.get(i);
/*     */ 
/* 102 */       if (baseClass.equals("java.lang.String")) {
/* 103 */         value = "\"" + value + "\"";
/*     */       }
/* 105 */       else if (baseClass.equals("java.lang.Character")) {
/* 106 */         value = "'" + value + "'";
/* 107 */       } else if (baseClass.equals("java.lang.Float")) {
/* 108 */         if ((!value.endsWith("F")) && (!value.endsWith("f")))
/*     */         {
/* 111 */           value = value + "F";
/*     */         }
/* 113 */       } else if (baseClass.equals("java.lang.Long")) {
/* 114 */         if ((!value.endsWith("L")) && (!value.endsWith("l")))
/*     */         {
/* 117 */           value = value + "L";
/*     */         }
/* 119 */       } else if (baseClass.equals("javax.xml.namespace.QName")) {
/* 120 */         value = org.apache.axis.wsdl.symbolTable.Utils.getQNameFromPrefixedName(this.type.getNode(), value).toString();
/* 121 */         value = "javax.xml.namespace.QName.valueOf(\"" + value + "\")";
/* 122 */       } else if (baseClass.equals(baseType))
/*     */       {
/* 125 */         value = "new " + baseClass + "(\"" + value + "\")";
/*     */       }
/*     */ 
/* 128 */       values.add(value);
/*     */     }
/*     */ 
/* 132 */     Vector ids = getEnumValueIds(this.elements);
/*     */ 
/* 135 */     pw.println("    private " + baseType + " _value_;");
/*     */ 
/* 138 */     pw.println("    private static java.util.HashMap _table_ = new java.util.HashMap();");
/*     */ 
/* 140 */     pw.println("");
/*     */ 
/* 143 */     pw.println("    // " + Messages.getMessage("ctor00"));
/* 144 */     pw.println("    protected " + javaName + "(" + baseType + " value) {");
/* 145 */     pw.println("        _value_ = value;");
/*     */ 
/* 147 */     if ((baseClass.equals("java.lang.String")) || (baseClass.equals(baseType)))
/*     */     {
/* 149 */       pw.println("        _table_.put(_value_,this);");
/*     */     }
/* 151 */     else pw.println("        _table_.put(new " + baseClass + "(_value_),this);");
/*     */ 
/* 155 */     pw.println("    }");
/* 156 */     pw.println("");
/*     */ 
/* 160 */     for (int i = 0; i < ids.size(); i++)
/*     */     {
/* 163 */       if (baseType.equals("org.apache.axis.types.URI")) {
/* 164 */         pw.println("    public static final " + baseType + " _" + ids.get(i) + ";");
/* 165 */         pw.println("    static {");
/* 166 */         pw.println("    \ttry {");
/* 167 */         pw.println("            _" + ids.get(i) + " = " + values.get(i) + ";");
/* 168 */         pw.println("        }");
/* 169 */         pw.println("        catch (org.apache.axis.types.URI.MalformedURIException mue) {");
/* 170 */         pw.println("            throw new java.lang.RuntimeException(mue.toString());");
/* 171 */         pw.println("        }");
/* 172 */         pw.println("    }");
/* 173 */         pw.println("");
/*     */       }
/*     */       else {
/* 176 */         pw.println("    public static final " + baseType + " _" + ids.get(i) + " = " + values.get(i) + ";");
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 182 */     for (int i = 0; i < ids.size(); i++) {
/* 183 */       pw.println("    public static final " + javaName + " " + ids.get(i) + " = new " + javaName + "(_" + ids.get(i) + ");");
/*     */     }
/*     */ 
/* 188 */     pw.println("    public " + baseType + " getValue() { return _value_;}");
/*     */ 
/* 191 */     pw.println("    public static " + javaName + " fromValue(" + baseType + " value)");
/*     */ 
/* 193 */     pw.println("          throws java.lang.IllegalArgumentException {");
/* 194 */     pw.println("        " + javaName + " enumeration = (" + javaName + ")");
/*     */ 
/* 196 */     if ((baseClass.equals("java.lang.String")) || (baseClass.equals(baseType)))
/*     */     {
/* 198 */       pw.println("            _table_.get(value);");
/*     */     }
/* 200 */     else pw.println("            _table_.get(new " + baseClass + "(value));");
/*     */ 
/* 204 */     pw.println("        if (enumeration==null) throw new java.lang.IllegalArgumentException();");
/*     */ 
/* 206 */     pw.println("        return enumeration;");
/* 207 */     pw.println("    }");
/*     */ 
/* 210 */     pw.println("    public static " + javaName + " fromString(java.lang.String value)");
/*     */ 
/* 212 */     pw.println("          throws java.lang.IllegalArgumentException {");
/*     */ 
/* 214 */     if (baseClass.equals("java.lang.String")) {
/* 215 */       pw.println("        return fromValue(value);");
/* 216 */     } else if (baseClass.equals("javax.xml.namespace.QName")) {
/* 217 */       pw.println("        try {");
/* 218 */       pw.println("            return fromValue(javax.xml.namespace.QName.valueOf(value));");
/*     */ 
/* 220 */       pw.println("        } catch (Exception e) {");
/* 221 */       pw.println("            throw new java.lang.IllegalArgumentException();");
/*     */ 
/* 223 */       pw.println("        }");
/* 224 */     } else if (baseClass.equals(baseType)) {
/* 225 */       pw.println("        try {");
/* 226 */       pw.println("            return fromValue(new " + baseClass + "(value));");
/*     */ 
/* 228 */       pw.println("        } catch (Exception e) {");
/* 229 */       pw.println("            throw new java.lang.IllegalArgumentException();");
/*     */ 
/* 231 */       pw.println("        }");
/* 232 */     } else if (baseClass.equals("java.lang.Character")) {
/* 233 */       pw.println("        if (value != null && value.length() == 1);");
/* 234 */       pw.println("            return fromValue(value.charAt(0));");
/* 235 */       pw.println("        throw new java.lang.IllegalArgumentException();");
/*     */     }
/* 237 */     else if (baseClass.equals("java.lang.Integer")) {
/* 238 */       pw.println("        try {");
/* 239 */       pw.println("            return fromValue(java.lang.Integer.parseInt(value));");
/*     */ 
/* 241 */       pw.println("        } catch (Exception e) {");
/* 242 */       pw.println("            throw new java.lang.IllegalArgumentException();");
/*     */ 
/* 244 */       pw.println("        }");
/*     */     } else {
/* 246 */       String parse = "parse" + baseClass.substring(baseClass.lastIndexOf(".") + 1);
/*     */ 
/* 250 */       pw.println("        try {");
/* 251 */       pw.println("            return fromValue(" + baseClass + "." + parse + "(value));");
/*     */ 
/* 253 */       pw.println("        } catch (Exception e) {");
/* 254 */       pw.println("            throw new java.lang.IllegalArgumentException();");
/*     */ 
/* 256 */       pw.println("        }");
/*     */     }
/*     */ 
/* 259 */     pw.println("    }");
/*     */ 
/* 263 */     pw.println("    public boolean equals(java.lang.Object obj) {return (obj == this);}");
/*     */ 
/* 267 */     pw.println("    public int hashCode() { return toString().hashCode();}");
/*     */ 
/* 271 */     if (baseClass.equals("java.lang.String")) {
/* 272 */       pw.println("    public java.lang.String toString() { return _value_;}");
/*     */     }
/* 274 */     else if (baseClass.equals(baseType)) {
/* 275 */       pw.println("    public java.lang.String toString() { return _value_.toString();}");
/*     */     }
/*     */     else {
/* 278 */       pw.println("    public java.lang.String toString() { return java.lang.String.valueOf(_value_);}");
/*     */     }
/*     */ 
/* 282 */     pw.println("    public java.lang.Object readResolve() throws java.io.ObjectStreamException { return fromValue(_value_);}");
/*     */ 
/* 284 */     pw.println("    public static org.apache.axis.encoding.Serializer getSerializer(");
/*     */ 
/* 286 */     pw.println("           java.lang.String mechType, ");
/* 287 */     pw.println("           java.lang.Class _javaType,  ");
/* 288 */     pw.println("           javax.xml.namespace.QName _xmlType) {");
/* 289 */     pw.println("        return ");
/* 290 */     pw.println("          new org.apache.axis.encoding.ser.EnumSerializer(");
/*     */ 
/* 292 */     pw.println("            _javaType, _xmlType);");
/* 293 */     pw.println("    }");
/* 294 */     pw.println("    public static org.apache.axis.encoding.Deserializer getDeserializer(");
/*     */ 
/* 296 */     pw.println("           java.lang.String mechType, ");
/* 297 */     pw.println("           java.lang.Class _javaType,  ");
/* 298 */     pw.println("           javax.xml.namespace.QName _xmlType) {");
/* 299 */     pw.println("        return ");
/* 300 */     pw.println("          new org.apache.axis.encoding.ser.EnumDeserializer(");
/*     */ 
/* 302 */     pw.println("            _javaType, _xmlType);");
/* 303 */     pw.println("    }");
/* 304 */     pw.println("    // " + Messages.getMessage("typeMeta"));
/* 305 */     pw.println("    private static org.apache.axis.description.TypeDesc typeDesc =");
/*     */ 
/* 307 */     pw.println("        new org.apache.axis.description.TypeDesc(" + Utils.getJavaLocalName(this.type.getName()) + ".class);");
/*     */ 
/* 309 */     pw.println();
/* 310 */     pw.println("    static {");
/* 311 */     pw.println("        typeDesc.setXmlType(" + Utils.getNewQName(this.type.getQName()) + ");");
/*     */ 
/* 313 */     pw.println("    }");
/* 314 */     pw.println("    /**");
/* 315 */     pw.println("     * " + Messages.getMessage("returnTypeMeta"));
/* 316 */     pw.println("     */");
/* 317 */     pw.println("    public static org.apache.axis.description.TypeDesc getTypeDesc() {");
/*     */ 
/* 319 */     pw.println("        return typeDesc;");
/* 320 */     pw.println("    }");
/* 321 */     pw.println();
/*     */   }
/*     */ 
/*     */   public static Vector getEnumValueIds(Vector bv)
/*     */   {
/* 334 */     boolean validJava = true;
/*     */ 
/* 337 */     for (int i = 1; (i < bv.size()) && (validJava); i++) {
/* 338 */       String value = (String)bv.get(i);
/*     */ 
/* 340 */       if (!JavaUtils.isJavaId(value)) {
/* 341 */         validJava = false;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 346 */     Vector ids = new Vector();
/*     */ 
/* 348 */     for (int i = 1; i < bv.size(); i++)
/*     */     {
/* 352 */       if (!validJava)
/* 353 */         ids.add("value" + i);
/*     */       else {
/* 355 */         ids.add((String)bv.get(i));
/*     */       }
/*     */     }
/*     */ 
/* 359 */     return ids;
/*     */   }
/*     */ 
/*     */   public void generate()
/*     */     throws IOException
/*     */   {
/* 366 */     String fqcn = getPackage() + "." + getClassName();
/* 367 */     if (this.emitter.isDeploy()) {
/* 368 */       if (!this.emitter.doesExist(fqcn))
/* 369 */         super.generate();
/*     */     }
/*     */     else
/* 372 */       super.generate();
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.wsdl.toJava.JavaEnumTypeWriter
 * JD-Core Version:    0.6.0
 */