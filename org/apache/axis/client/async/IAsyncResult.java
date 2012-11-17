package org.apache.axis.client.async;

public abstract interface IAsyncResult
{
  public abstract void abort();

  public abstract Status getStatus();

  public abstract void waitFor(long paramLong)
    throws InterruptedException;

  public abstract Object getResponse();

  public abstract Throwable getException();
}

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.client.async.IAsyncResult
 * JD-Core Version:    0.6.0
 */