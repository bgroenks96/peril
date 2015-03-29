package com.forerunnergames.peril.core.shared.net;

import com.forerunnergames.tools.common.Result;

public interface GameServerCreator
{
  Result <String> create (final GameServerConfiguration config);

  void destroy ();
}
