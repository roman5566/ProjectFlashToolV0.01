package org.apache.axis.message;

public abstract interface IDResolver
{
  public abstract Object getReferencedObject(String paramString);

  public abstract void addReferencedObject(String paramString, Object paramObject);
}

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.message.IDResolver
 * JD-Core Version:    0.6.0
 */