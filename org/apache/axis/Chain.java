package org.apache.axis;

public abstract interface Chain extends Handler
{
  public abstract void addHandler(Handler paramHandler);

  public abstract boolean contains(Handler paramHandler);

  public abstract Handler[] getHandlers();
}

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.Chain
 * JD-Core Version:    0.6.0
 */