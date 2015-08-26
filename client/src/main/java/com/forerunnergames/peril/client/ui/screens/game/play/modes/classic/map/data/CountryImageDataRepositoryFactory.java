package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.data;

import com.forerunnergames.peril.common.map.MapMetadata;

public interface CountryImageDataRepositoryFactory
{
  CountryImageDataRepository create (final MapMetadata mapMetadata);
}
