package com.forerunnergames.peril.server.main.args;

import com.beust.jcommander.IStringConverter;

import com.forerunnergames.peril.common.net.GameServerType;

public final class ServerTypeParameterConverter implements IStringConverter <GameServerType>
{
  @Override
  public GameServerType convert (final String value)
  {
    return GameServerType.valueOf (value.replace ('-', '_').toUpperCase ());
  }
}
