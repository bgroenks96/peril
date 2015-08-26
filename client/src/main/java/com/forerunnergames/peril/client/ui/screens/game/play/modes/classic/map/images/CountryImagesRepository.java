package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.images;

public interface CountryImagesRepository
{
  CountryImages <CountryPrimaryImageState, CountryPrimaryImage> getPrimary (final String countryName);

  CountryImages <CountrySecondaryImageState, CountrySecondaryImage> getSecondary (final String countryName);
}
