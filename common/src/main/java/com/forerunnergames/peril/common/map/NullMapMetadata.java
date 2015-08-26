package com.forerunnergames.peril.common.map;

import com.forerunnergames.peril.common.game.GameMode;
import com.forerunnergames.tools.common.Strings;

final class NullMapMetadata implements MapMetadata
{
  private static final String NAME = "";
  private static final MapType TYPE = MapType.STOCK;
  private static final GameMode MODE = GameMode.CLASSIC;

  @Override
  public String getName ()
  {
    return NAME;
  }

  @Override
  public MapType getType ()
  {
    return TYPE;
  }

  @Override
  public GameMode getMode ()
  {
    return MODE;
  }

  @Override
  public int hashCode ()
  {
    int result = NAME.hashCode ();
    result = 31 * result + TYPE.hashCode ();
    result = 31 * result + MODE.hashCode ();
    return result;
  }

  @Override
  public boolean equals (final Object obj)
  {
    if (this == obj) return true;
    if (obj == null || getClass () != obj.getClass ()) return false;

    final MapMetadata mapMetadata = (MapMetadata) obj;

    return NAME.equals (mapMetadata.getName ()) && TYPE == mapMetadata.getType () && MODE == mapMetadata.getMode ();
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: Name: {} | Type: {} | Mode: {}", getClass ().getSimpleName (), NAME, TYPE, MODE);
  }
}
