package org.apache.axis.encoding;

import java.io.Serializable;

public abstract interface TypeMappingRegistry extends javax.xml.rpc.encoding.TypeMappingRegistry, Serializable
{
  public abstract void delegate(TypeMappingRegistry paramTypeMappingRegistry);

  public abstract TypeMapping getOrMakeTypeMapping(String paramString);
}

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.encoding.TypeMappingRegistry
 * JD-Core Version:    0.6.0
 */