package org.apache.axis.server;

import java.util.Map;
import org.apache.axis.AxisFault;

public abstract interface AxisServerFactory
{
  public abstract AxisServer getServer(Map paramMap)
    throws AxisFault;
}

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.server.AxisServerFactory
 * JD-Core Version:    0.6.0
 */