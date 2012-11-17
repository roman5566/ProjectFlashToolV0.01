package org.apache.axis;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.List;
import javax.xml.namespace.QName;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public abstract interface Handler extends Serializable
{
  public abstract void init();

  public abstract void cleanup();

  public abstract void invoke(MessageContext paramMessageContext)
    throws AxisFault;

  public abstract void onFault(MessageContext paramMessageContext);

  public abstract boolean canHandleBlock(QName paramQName);

  public abstract List getUnderstoodHeaders();

  public abstract void setOption(String paramString, Object paramObject);

  public abstract Object getOption(String paramString);

  public abstract void setName(String paramString);

  public abstract String getName();

  public abstract Hashtable getOptions();

  public abstract void setOptions(Hashtable paramHashtable);

  public abstract Element getDeploymentData(Document paramDocument);

  public abstract void generateWSDL(MessageContext paramMessageContext)
    throws AxisFault;
}

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.Handler
 * JD-Core Version:    0.6.0
 */