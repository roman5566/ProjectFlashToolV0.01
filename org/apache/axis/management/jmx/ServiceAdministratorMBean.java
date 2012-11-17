package org.apache.axis.management.jmx;

import org.apache.axis.AxisFault;
import org.apache.axis.ConfigurationException;

public abstract interface ServiceAdministratorMBean
{
  public abstract String getVersion();

  public abstract void start();

  public abstract void stop();

  public abstract void restart();

  public abstract void startService(String paramString)
    throws AxisFault, ConfigurationException;

  public abstract void stopService(String paramString)
    throws AxisFault, ConfigurationException;
}

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.management.jmx.ServiceAdministratorMBean
 * JD-Core Version:    0.6.0
 */