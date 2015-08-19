package com.forerunnergames.peril.server.main;

import com.beust.jcommander.IStringConverter;

import com.forerunnergames.peril.core.shared.net.GameServerType;

public final class ServerTypeParameterConverter implements IStringConverter <GameServerType>
{
  @Override
  public GameServerType convert (final String value)
  {
    return GameServerType.valueOf (value.replace ('-', '_').toUpperCase ());
  }
}
