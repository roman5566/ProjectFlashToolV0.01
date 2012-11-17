package org.apache.axis.management.jmx;

import org.apache.axis.AxisFault;
import org.apache.axis.ConfigurationException;
import org.apache.axis.deployment.wsdd.WSDDGlobalConfiguration;
import org.apache.axis.deployment.wsdd.WSDDHandler;
import org.apache.axis.deployment.wsdd.WSDDService;
import org.apache.axis.deployment.wsdd.WSDDTransport;

public abstract interface DeploymentQueryMBean
{
  public abstract WSDDGlobalConfiguration findGlobalConfig();

  public abstract WSDDHandler findHandler(String paramString);

  public abstract WSDDHandler[] findHandlers();

  public abstract WSDDService findService(String paramString);

  public abstract WSDDService[] findServices();

  public abstract WSDDTransport findTransport(String paramString);

  public abstract WSDDTransport[] findTransports();

  public abstract String[] listServices()
    throws AxisFault, ConfigurationException;
}

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.management.jmx.DeploymentQueryMBean
 * JD-Core Version:    0.6.0
 */