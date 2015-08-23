package com.forerunnergames.peril.common.map;

import com.forerunnergames.peril.common.game.GameMode;

final class NullMapMetadata extends AbstractMapMetadata
{
  @Override
  public String getName ()
  {
    return "";
  }

  @Override
  public MapType getType ()
  {
    return MapType.STOCK;
  }

  @Override
  public GameMode getMode ()
  {
    return GameMode.CLASSIC;
  }
}
