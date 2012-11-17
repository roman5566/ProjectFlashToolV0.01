package org.apache.axis.session;

import java.util.Enumeration;

public abstract interface Session
{
  public abstract Object get(String paramString);

  public abstract void set(String paramString, Object paramObject);

  public abstract void remove(String paramString);

  public abstract Enumeration getKeys();

  public abstract void setTimeout(int paramInt);

  public abstract int getTimeout();

  public abstract void touch();

  public abstract void invalidate();

  public abstract Object getLockObject();
}

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.session.Session
 * JD-Core Version:    0.6.0
 */