package org.apache.axis.encoding;

import java.io.IOException;
import javax.xml.namespace.QName;
import org.apache.axis.wsdl.fromJava.Types;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;

public abstract interface Serializer extends javax.xml.rpc.encoding.Serializer
{
  public abstract void serialize(QName paramQName, Attributes paramAttributes, Object paramObject, SerializationContext paramSerializationContext)
    throws IOException;

  public abstract Element writeSchema(Class paramClass, Types paramTypes)
    throws Exception;
}

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.encoding.Serializer
 * JD-Core Version:    0.6.0
 */