package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.data;

import com.badlogic.gdx.math.Vector2;

import com.forerunnergames.tools.common.Arguments;

public final class CountryImageData
{
  private final String countryName;
  private final Vector2 referenceDestination;
  private final Vector2 referenceTextUpperLeft;
  private final Vector2 referenceSize;

  public CountryImageData (final String countryName,
                           final Vector2 referenceDestination,
                           final Vector2 referenceTextUpperLeft,
                           final Vector2 referenceSize)
  {
    Arguments.checkIsNotNull (countryName, "countryName");
    Arguments.checkIsNotNull (referenceDestination, "referenceDestination");
    Arguments.checkIsNotNull (referenceTextUpperLeft, "referenceTextUpperLeft");
    Arguments.checkIsNotNull (referenceSize, "referenceSize");

    this.countryName = countryName;
    this.referenceDestination = referenceDestination;
    this.referenceTextUpperLeft = referenceTextUpperLeft;
    this.referenceSize = referenceSize;
  }

  public String getCountryName ()
  {
    return countryName;
  }

  public Vector2 getReferenceDestination ()
  {
    return referenceDestination;
  }

  public Vector2 getReferenceTextUpperLeft ()
  {
    return referenceTextUpperLeft;
  }

  public float getReferenceWidth ()
  {
    return referenceSize.x;
  }

  public float getReferenceHeight ()
  {
    return referenceSize.y;
  }

  @Override
  public String toString ()
  {
    return String.format (
                          "%1$s: Country Name: %2$s | Reference Destination: %3$s"
                                  + " | Reference Text Upper Left: %4$s | Reference Size: %5$s",
                          getClass ().getSimpleName (), countryName, referenceDestination, referenceTextUpperLeft,
                          referenceSize);
  }
}
