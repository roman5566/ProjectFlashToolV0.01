package org.apache.axis.components.image;

import java.awt.Image;
import java.io.InputStream;
import java.io.OutputStream;

public abstract interface ImageIO
{
  public abstract void saveImage(String paramString, Image paramImage, OutputStream paramOutputStream)
    throws Exception;

  public abstract Image loadImage(InputStream paramInputStream)
    throws Exception;
}

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.components.image.ImageIO
 * JD-Core Version:    0.6.0
 */