package org.apache.axis.wsdl.symbolTable;

import java.io.IOException;

public abstract interface Undefined
{
  public abstract void register(TypeEntry paramTypeEntry);

  public abstract void update(TypeEntry paramTypeEntry)
    throws IOException;
}

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.wsdl.symbolTable.Undefined
 * JD-Core Version:    0.6.0
 */