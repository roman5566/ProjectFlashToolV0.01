package org.apache.axis.transport.http;

import org.apache.axis.AxisFault;
import org.apache.axis.MessageContext;

public abstract interface QSHandler
{
  public abstract void invoke(MessageContext paramMessageContext)
    throws AxisFault;
}

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.transport.http.QSHandler
 * JD-Core Version:    0.6.0
 */