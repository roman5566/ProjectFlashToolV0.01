package org.apache.axis.encoding;

import org.apache.axis.message.MessageElement;

public abstract interface AnyContentType
{
  public abstract MessageElement[] get_any();

  public abstract void set_any(MessageElement[] paramArrayOfMessageElement);
}

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.encoding.AnyContentType
 * JD-Core Version:    0.6.0
 */