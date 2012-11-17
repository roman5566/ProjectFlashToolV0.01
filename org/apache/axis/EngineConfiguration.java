package org.apache.axis;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import javax.xml.namespace.QName;
import org.apache.axis.encoding.TypeMappingRegistry;
import org.apache.axis.handlers.soap.SOAPService;

public abstract interface EngineConfiguration
{
  public static final String PROPERTY_NAME = "engineConfig";

  public abstract void configureEngine(AxisEngine paramAxisEngine)
    throws ConfigurationException;

  public abstract void writeEngineConfig(AxisEngine paramAxisEngine)
    throws ConfigurationException;

  public abstract Handler getHandler(QName paramQName)
    throws ConfigurationException;

  public abstract SOAPService getService(QName paramQName)
    throws ConfigurationException;

  public abstract SOAPService getServiceByNamespaceURI(String paramString)
    throws ConfigurationException;

  public abstract Handler getTransport(QName paramQName)
    throws ConfigurationException;

  public abstract TypeMappingRegistry getTypeMappingRegistry()
    throws ConfigurationException;

  public abstract Handler getGlobalRequest()
    throws ConfigurationException;

  public abstract Handler getGlobalResponse()
    throws ConfigurationException;

  public abstract Hashtable getGlobalOptions()
    throws ConfigurationException;

  public abstract Iterator getDeployedServices()
    throws ConfigurationException;

  public abstract List getRoles();
}

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.EngineConfiguration
 * JD-Core Version:    0.6.0
 */