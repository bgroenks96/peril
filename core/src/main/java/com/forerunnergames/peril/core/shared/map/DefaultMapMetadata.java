package com.forerunnergames.peril.core.shared.map;

import com.forerunnergames.peril.core.model.rules.GameMode;
import com.forerunnergames.peril.core.shared.settings.GameSettings;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Preconditions;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public final class DefaultMapMetadata extends AbstractMapMetadata
{
  private final String name;
  private final MapType type;
  private final GameMode mode;

  public DefaultMapMetadata (final String name, final MapType type, final GameMode mode)
  {
    Arguments.checkIsNotNull (name, "name");
    Arguments.checkIsNotNull (type, "type");
    Arguments.checkIsNotNull (mode, "mode");
    Preconditions.checkIsTrue (GameSettings.isValidMapName (name), Strings.format ("Invalid map name [{}].", name));

    this.name = name;
    this.type = type;
    this.mode = mode;
  }

  @Override
  public String getName ()
  {
    return name;
  }

  @Override
  public MapType getType ()
  {
    return type;
  }

  @Override
  public GameMode getMode ()
  {
    return mode;
  }

  @RequiredForNetworkSerialization
  private DefaultMapMetadata ()
  {
    name = null;
    type = null;
    mode = null;
  }
}
