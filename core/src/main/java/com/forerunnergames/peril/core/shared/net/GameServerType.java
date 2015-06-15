package com.forerunnergames.peril.core.shared.net;

import com.forerunnergames.tools.common.Arguments;

public enum GameServerType
{
  DEDICATED,
  HOST_AND_PLAY;

  public boolean is (final GameServerType type)
  {
    Arguments.checkIsNotNull (type, "type");

    return this == type;
  }

  public boolean isNot (final GameServerType type)
  {
    return !is (type);
  }
}
