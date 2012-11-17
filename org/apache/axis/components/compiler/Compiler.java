package org.apache.axis.components.compiler;

import java.io.IOException;
import java.util.List;

public abstract interface Compiler
{
  public abstract void addFile(String paramString);

  public abstract void setSource(String paramString);

  public abstract void setDestination(String paramString);

  public abstract void setClasspath(String paramString);

  public abstract void setEncoding(String paramString);

  public abstract boolean compile()
    throws IOException;

  public abstract List getErrors()
    throws IOException;
}

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.components.compiler.Compiler
 * JD-Core Version:    0.6.0
 */