package org.apache.axis.wsdl.gen;

import javax.wsdl.Binding;
import javax.wsdl.Definition;
import javax.wsdl.Message;
import javax.wsdl.PortType;
import javax.wsdl.Service;
import org.apache.axis.wsdl.symbolTable.BaseTypeMapping;
import org.apache.axis.wsdl.symbolTable.SymbolTable;
import org.apache.axis.wsdl.symbolTable.TypeEntry;

public abstract interface GeneratorFactory
{
  public abstract void generatorPass(Definition paramDefinition, SymbolTable paramSymbolTable);

  public abstract Generator getGenerator(Message paramMessage, SymbolTable paramSymbolTable);

  public abstract Generator getGenerator(PortType paramPortType, SymbolTable paramSymbolTable);

  public abstract Generator getGenerator(Binding paramBinding, SymbolTable paramSymbolTable);

  public abstract Generator getGenerator(Service paramService, SymbolTable paramSymbolTable);

  public abstract Generator getGenerator(TypeEntry paramTypeEntry, SymbolTable paramSymbolTable);

  public abstract Generator getGenerator(Definition paramDefinition, SymbolTable paramSymbolTable);

  public abstract void setBaseTypeMapping(BaseTypeMapping paramBaseTypeMapping);

  public abstract BaseTypeMapping getBaseTypeMapping();
}

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.wsdl.gen.GeneratorFactory
 * JD-Core Version:    0.6.0
 */