package org.apache.axis.attachments;

import java.io.OutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import javax.xml.soap.MimeHeaders;
import org.apache.axis.AxisFault;
import org.apache.axis.Part;

public abstract interface Attachments extends Serializable
{
  public static final int SEND_TYPE_NOTSET = 1;
  public static final int SEND_TYPE_MIME = 2;
  public static final int SEND_TYPE_DIME = 3;
  public static final int SEND_TYPE_MTOM = 4;
  public static final int SEND_TYPE_NONE = 5;
  public static final int SEND_TYPE_MAX = 5;
  public static final int SEND_TYPE_DEFAULT = 2;
  public static final String CIDprefix = "cid:";

  public abstract Part addAttachmentPart(Part paramPart)
    throws AxisFault;

  public abstract Part removeAttachmentPart(String paramString)
    throws AxisFault;

  public abstract void removeAllAttachments();

  public abstract Part getAttachmentByReference(String paramString)
    throws AxisFault;

  public abstract Collection getAttachments()
    throws AxisFault;

  public abstract Iterator getAttachments(MimeHeaders paramMimeHeaders);

  public abstract Part createAttachmentPart(Object paramObject)
    throws AxisFault;

  public abstract Part createAttachmentPart()
    throws AxisFault;

  public abstract void setAttachmentParts(Collection paramCollection)
    throws AxisFault;

  public abstract Part getRootPart();

  public abstract void setRootPart(Part paramPart);

  public abstract long getContentLength()
    throws AxisFault;

  public abstract void writeContentToStream(OutputStream paramOutputStream)
    throws AxisFault;

  public abstract String getContentType()
    throws AxisFault;

  public abstract int getAttachmentCount();

  public abstract boolean isAttachment(Object paramObject);

  public abstract void setSendType(int paramInt);

  public abstract int getSendType();

  public abstract void dispose();

  public abstract IncomingAttachmentStreams getIncomingAttachmentStreams();
}

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.attachments.Attachments
 * JD-Core Version:    0.6.0
 */