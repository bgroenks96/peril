package com.forerunnergames.peril.client;

import com.forerunnergames.peril.core.shared.net.GameServerConfiguration;
import com.forerunnergames.tools.common.Result;

public interface GameServerCreator
{
  Result <String> create (final GameServerConfiguration config);

  void destroy ();
}
