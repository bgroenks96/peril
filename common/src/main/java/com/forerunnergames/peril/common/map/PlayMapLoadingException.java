package com.forerunnergames.peril.common.map;

public final class PlayMapLoadingException extends RuntimeException
{
  public PlayMapLoadingException (final String message)
  {
    super (message);
  }

  public PlayMapLoadingException (final String message, final Throwable cause)
  {
    super (message, cause);
  }

  public PlayMapLoadingException (final Throwable cause)
  {
    super (cause);
  }
}
