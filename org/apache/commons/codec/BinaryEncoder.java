package org.apache.commons.codec;

public abstract interface BinaryEncoder extends Encoder
{
  public abstract byte[] encode(byte[] paramArrayOfByte)
    throws EncoderException;
}

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.commons.codec.BinaryEncoder
 * JD-Core Version:    0.6.0
 */