package org.apache.axis;

public abstract interface HandlerIterationStrategy
{
  public abstract void visit(Handler paramHandler, MessageContext paramMessageContext)
    throws AxisFault;
}

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.HandlerIterationStrategy
 * JD-Core Version:    0.6.0
 */