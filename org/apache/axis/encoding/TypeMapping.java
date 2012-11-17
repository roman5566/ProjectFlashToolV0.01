package org.apache.axis.encoding;

import java.io.Serializable;
import javax.xml.namespace.QName;
import javax.xml.rpc.JAXRPCException;
import javax.xml.rpc.encoding.DeserializerFactory;
import javax.xml.rpc.encoding.SerializerFactory;

public abstract interface TypeMapping extends javax.xml.rpc.encoding.TypeMapping, Serializable
{
  public abstract SerializerFactory getSerializer(Class paramClass)
    throws JAXRPCException;

  public abstract DeserializerFactory getDeserializer(QName paramQName)
    throws JAXRPCException;

  public abstract QName getTypeQName(Class paramClass);

  public abstract QName getTypeQNameExact(Class paramClass);

  public abstract Class getClassForQName(QName paramQName);

  public abstract Class getClassForQName(QName paramQName, Class paramClass);

  public abstract Class[] getAllClasses();

  public abstract QName getXMLType(Class paramClass, QName paramQName, boolean paramBoolean)
    throws JAXRPCException;
}

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.encoding.TypeMapping
 * JD-Core Version:    0.6.0
 */