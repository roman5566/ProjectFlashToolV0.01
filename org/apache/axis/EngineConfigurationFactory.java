package org.apache.axis;

public abstract interface EngineConfigurationFactory
{
  public static final String SYSTEM_PROPERTY_NAME = "axis.EngineConfigFactory";

  public abstract EngineConfiguration getClientEngineConfig();

  public abstract EngineConfiguration getServerEngineConfig();
}

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.EngineConfigurationFactory
 * JD-Core Version:    0.6.0
 */