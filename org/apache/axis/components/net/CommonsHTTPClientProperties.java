package org.apache.axis.components.net;

public abstract interface CommonsHTTPClientProperties
{
  public abstract int getMaximumTotalConnections();

  public abstract int getMaximumConnectionsPerHost();

  public abstract int getConnectionPoolTimeout();

  public abstract int getDefaultConnectionTimeout();

  public abstract int getDefaultSoTimeout();
}

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.components.net.CommonsHTTPClientProperties
 * JD-Core Version:    0.6.0
 */