package org.apache.axis.message;

import java.util.Hashtable;

public abstract interface MessageWithAttachments
{
  public abstract boolean hasAttachments();

  public abstract Hashtable getAttachments();

  public abstract Object getAttachment(String paramString);

  public abstract Object getAttachment(int paramInt);
}

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.message.MessageWithAttachments
 * JD-Core Version:    0.6.0
 */