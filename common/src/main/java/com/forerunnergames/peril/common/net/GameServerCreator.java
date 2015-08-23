package com.forerunnergames.peril.common.net;

import com.forerunnergames.tools.common.Result;

public interface GameServerCreator
{
  Result <String> create (final GameServerConfiguration config);

  void destroy ();

  boolean isCreated ();
}
