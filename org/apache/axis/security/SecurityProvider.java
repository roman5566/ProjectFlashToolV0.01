package org.apache.axis.security;

import org.apache.axis.MessageContext;

public abstract interface SecurityProvider
{
  public abstract AuthenticatedUser authenticate(MessageContext paramMessageContext);

  public abstract boolean userMatches(AuthenticatedUser paramAuthenticatedUser, String paramString);
}

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.security.SecurityProvider
 * JD-Core Version:    0.6.0
 */