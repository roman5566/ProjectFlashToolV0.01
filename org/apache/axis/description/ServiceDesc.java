package org.apache.axis.description;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.namespace.QName;
import org.apache.axis.constants.Style;
import org.apache.axis.constants.Use;
import org.apache.axis.encoding.TypeMapping;
import org.apache.axis.encoding.TypeMappingRegistry;

public abstract interface ServiceDesc extends Serializable
{
  public abstract Style getStyle();

  public abstract void setStyle(Style paramStyle);

  public abstract Use getUse();

  public abstract void setUse(Use paramUse);

  public abstract String getWSDLFile();

  public abstract void setWSDLFile(String paramString);

  public abstract List getAllowedMethods();

  public abstract void setAllowedMethods(List paramList);

  public abstract TypeMapping getTypeMapping();

  public abstract void setTypeMapping(TypeMapping paramTypeMapping);

  public abstract String getName();

  public abstract void setName(String paramString);

  public abstract String getDocumentation();

  public abstract void setDocumentation(String paramString);

  public abstract void removeOperationDesc(OperationDesc paramOperationDesc);

  public abstract void addOperationDesc(OperationDesc paramOperationDesc);

  public abstract ArrayList getOperations();

  public abstract OperationDesc[] getOperationsByName(String paramString);

  public abstract OperationDesc getOperationByName(String paramString);

  public abstract OperationDesc getOperationByElementQName(QName paramQName);

  public abstract OperationDesc[] getOperationsByQName(QName paramQName);

  public abstract void setNamespaceMappings(List paramList);

  public abstract String getDefaultNamespace();

  public abstract void setDefaultNamespace(String paramString);

  public abstract void setProperty(String paramString, Object paramObject);

  public abstract Object getProperty(String paramString);

  public abstract String getEndpointURL();

  public abstract void setEndpointURL(String paramString);

  public abstract TypeMappingRegistry getTypeMappingRegistry();

  public abstract void setTypeMappingRegistry(TypeMappingRegistry paramTypeMappingRegistry);

  public abstract boolean isInitialized();

  public abstract boolean isWrapped();

  public abstract List getDisallowedMethods();

  public abstract void setDisallowedMethods(List paramList);
}

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.description.ServiceDesc
 * JD-Core Version:    0.6.0
 */