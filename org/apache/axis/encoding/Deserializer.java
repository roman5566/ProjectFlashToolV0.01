package org.apache.axis.encoding;

import java.util.Vector;
import javax.xml.namespace.QName;
import org.apache.axis.message.SOAPHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public abstract interface Deserializer extends javax.xml.rpc.encoding.Deserializer, Callback
{
  public abstract Object getValue();

  public abstract void setValue(Object paramObject);

  public abstract Object getValue(Object paramObject);

  public abstract void setChildValue(Object paramObject1, Object paramObject2)
    throws SAXException;

  public abstract void setDefaultType(QName paramQName);

  public abstract QName getDefaultType();

  public abstract void registerValueTarget(Target paramTarget);

  public abstract Vector getValueTargets();

  public abstract void removeValueTargets();

  public abstract void moveValueTargets(Deserializer paramDeserializer);

  public abstract boolean componentsReady();

  public abstract void valueComplete()
    throws SAXException;

  public abstract void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes, DeserializationContext paramDeserializationContext)
    throws SAXException;

  public abstract void onStartElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes, DeserializationContext paramDeserializationContext)
    throws SAXException;

  public abstract SOAPHandler onStartChild(String paramString1, String paramString2, String paramString3, Attributes paramAttributes, DeserializationContext paramDeserializationContext)
    throws SAXException;

  public abstract void endElement(String paramString1, String paramString2, DeserializationContext paramDeserializationContext)
    throws SAXException;

  public abstract void onEndElement(String paramString1, String paramString2, DeserializationContext paramDeserializationContext)
    throws SAXException;
}

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.encoding.Deserializer
 * JD-Core Version:    0.6.0
 */