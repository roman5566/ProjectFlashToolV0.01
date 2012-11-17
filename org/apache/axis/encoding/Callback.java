package org.apache.axis.encoding;

import org.xml.sax.SAXException;

public abstract interface Callback
{
  public abstract void setValue(Object paramObject1, Object paramObject2)
    throws SAXException;
}

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.encoding.Callback
 * JD-Core Version:    0.6.0
 */