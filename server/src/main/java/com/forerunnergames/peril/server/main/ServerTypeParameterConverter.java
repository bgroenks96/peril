package com.forerunnergames.peril.server.main;

import com.forerunnergames.peril.core.shared.net.GameServerType;

import com.beust.jcommander.IStringConverter;

public class ServerTypeParameterConverter implements IStringConverter <GameServerType>
{
  @Override
  public GameServerType convert (final String value)
  {
    return GameServerType.valueOf (value.replace ('-', '_').toUpperCase ());
  }
}
