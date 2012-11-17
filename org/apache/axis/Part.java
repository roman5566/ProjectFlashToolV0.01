package org.apache.axis;

import java.io.Serializable;
import java.util.Iterator;

public abstract interface Part extends Serializable
{
  public abstract String[] getMimeHeader(String paramString);

  public abstract void addMimeHeader(String paramString1, String paramString2);

  public abstract String getContentLocation();

  public abstract void setContentLocation(String paramString);

  public abstract void setContentId(String paramString);

  public abstract String getContentId();

  public abstract Iterator getMatchingMimeHeaders(String[] paramArrayOfString);

  public abstract Iterator getNonMatchingMimeHeaders(String[] paramArrayOfString);

  public abstract String getContentType();

  public abstract String getContentIdRef();
}

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.Part
 * JD-Core Version:    0.6.0
 */