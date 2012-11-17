package org.apache.commons.codec;

public abstract interface StringEncoder extends Encoder
{
  public abstract String encode(String paramString)
    throws EncoderException;
}

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.commons.codec.StringEncoder
 * JD-Core Version:    0.6.0
 */