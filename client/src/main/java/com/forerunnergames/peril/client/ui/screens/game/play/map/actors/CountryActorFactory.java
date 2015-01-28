package com.forerunnergames.peril.client.ui.screens.game.play.map.actors;

import com.forerunnergames.peril.client.ui.screens.game.play.map.data.CountrySpriteColorOrder;
import com.forerunnergames.peril.client.ui.screens.game.play.map.data.CountrySpriteData;
import com.forerunnergames.tools.common.Arguments;

public final class CountryActorFactory
{
  private final CountrySpriteColorOrder countrySpriteColorOrder;

  public CountryActorFactory (final CountrySpriteColorOrder countrySpriteColorOrder)
  {
    Arguments.checkIsNotNull (countrySpriteColorOrder, "countrySpriteColorOrder");

    this.countrySpriteColorOrder = countrySpriteColorOrder;
  }

  public CountryActor create (final CountrySpriteData countrySpriteData)
  {
    Arguments.checkIsNotNull (countrySpriteData, "countrySpriteData");

    return new CountryActor (countrySpriteData, countrySpriteColorOrder);
  }
}
