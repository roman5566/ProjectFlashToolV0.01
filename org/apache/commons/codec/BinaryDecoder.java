package org.apache.commons.codec;

public abstract interface BinaryDecoder extends Decoder
{
  public abstract byte[] decode(byte[] paramArrayOfByte)
    throws DecoderException;
}

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.commons.codec.BinaryDecoder
 * JD-Core Version:    0.6.0
 */