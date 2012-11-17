package org.apache.axis;

public abstract interface TargetedChain extends Chain
{
  public abstract Handler getRequestHandler();

  public abstract Handler getPivotHandler();

  public abstract Handler getResponseHandler();
}

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.TargetedChain
 * JD-Core Version:    0.6.0
 */