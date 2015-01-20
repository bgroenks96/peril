package com.forerunnergames.peril.core.shared.net;

import com.forerunnergames.tools.common.Result;

public interface GameServerCreator
{
  public Result <String> create (final GameServerConfiguration config);
  public void destroy();
}
