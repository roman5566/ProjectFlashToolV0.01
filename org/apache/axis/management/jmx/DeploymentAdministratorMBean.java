package org.apache.axis.management.jmx;

import org.apache.axis.deployment.wsdd.WSDDGlobalConfiguration;
import org.apache.axis.deployment.wsdd.WSDDHandler;

public abstract interface DeploymentAdministratorMBean
{
  public abstract void saveConfiguration();

  public abstract void configureGlobalConfig(WSDDGlobalConfiguration paramWSDDGlobalConfiguration);

  public abstract void deployHandler(WSDDHandler paramWSDDHandler);

  public abstract void deployService(WSDDServiceWrapper paramWSDDServiceWrapper);

  public abstract void deployTransport(WSDDTransportWrapper paramWSDDTransportWrapper);

  public abstract void undeployHandler(String paramString);

  public abstract void undeployService(String paramString);

  public abstract void undeployTransport(String paramString);
}

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.management.jmx.DeploymentAdministratorMBean
 * JD-Core Version:    0.6.0
 */