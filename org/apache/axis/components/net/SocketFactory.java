package org.apache.axis.components.net;

import java.net.Socket;

public abstract interface SocketFactory
{
  public abstract Socket create(String paramString, int paramInt, StringBuffer paramStringBuffer, BooleanHolder paramBooleanHolder)
    throws Exception;
}

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.components.net.SocketFactory
 * JD-Core Version:    0.6.0
 */