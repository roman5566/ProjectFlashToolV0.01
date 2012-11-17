package org.apache.axis.components.encoding;

import java.io.IOException;
import java.io.Writer;

public abstract interface XMLEncoder
{
  public abstract String getEncoding();

  public abstract String encode(String paramString);

  public abstract void writeEncoded(Writer paramWriter, String paramString)
    throws IOException;
}

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.components.encoding.XMLEncoder
 * JD-Core Version:    0.6.0
 */