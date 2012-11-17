/*    */ package org.apache.axis.wsdl.toJava;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.PrintWriter;
/*    */ import org.apache.axis.wsdl.symbolTable.CollectionType;
/*    */ import org.apache.axis.wsdl.symbolTable.TypeEntry;
/*    */ 
/*    */ public class JavaHolderWriter extends JavaClassWriter
/*    */ {
/*    */   private TypeEntry type;
/*    */ 
/*    */   protected JavaHolderWriter(Emitter emitter, TypeEntry type)
/*    */   {
/* 41 */     super(emitter, Utils.holder(type, emitter), "holder");
/*    */ 
/* 43 */     this.type = type;
/*    */   }
/*    */ 
/*    */   protected String getClassModifiers()
/*    */   {
/* 52 */     return super.getClassModifiers() + "final ";
/*    */   }
/*    */ 
/*    */   protected String getImplementsText()
/*    */   {
/* 61 */     return "implements javax.xml.rpc.holders.Holder ";
/*    */   }
/*    */ 
/*    */   protected void writeFileBody(PrintWriter pw)
/*    */     throws IOException
/*    */   {
/* 72 */     String holderType = this.type.getName();
/* 73 */     if ((((this.type instanceof CollectionType)) && (((CollectionType)this.type).isWrapped())) || (this.type.getUnderlTypeNillable()))
/*    */     {
/* 81 */       holderType = Utils.getWrapperType(this.type);
/*    */     }
/* 83 */     pw.println("    public " + holderType + " value;");
/* 84 */     pw.println();
/* 85 */     pw.println("    public " + this.className + "() {");
/* 86 */     pw.println("    }");
/* 87 */     pw.println();
/* 88 */     pw.println("    public " + this.className + "(" + holderType + " value) {");
/* 89 */     pw.println("        this.value = value;");
/* 90 */     pw.println("    }");
/* 91 */     pw.println();
/*    */   }
/*    */ 
/*    */   public void generate()
/*    */     throws IOException
/*    */   {
/* 98 */     String fqcn = getPackage() + "." + getClassName();
/* 99 */     if (this.emitter.isDeploy()) {
/* 100 */       if (!this.emitter.doesExist(fqcn))
/* 101 */         super.generate();
/*    */     }
/*    */     else
/* 104 */       super.generate();
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.wsdl.toJava.JavaHolderWriter
 * JD-Core Version:    0.6.0
 */