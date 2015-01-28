package com.forerunnergames.peril.client.ui.screens.game.play.map.actors;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;

import com.forerunnergames.peril.client.ui.screens.game.play.map.input.PlayMapInputDetection;
import com.forerunnergames.peril.core.model.map.country.CountryName;
import com.forerunnergames.peril.core.model.people.player.PlayerColor;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Randomness;
import com.forerunnergames.tools.common.geometry.Point2D;
import com.forerunnergames.tools.common.geometry.Size2D;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;

public final class PlayMapActor extends Actor
{
  private final ImmutableMap <CountryName, CountryActor> countryNamesToActors;
  private final PlayMapInputDetection inputDetection;
  private CountryActor hoveredCountryActor;
  private CountryActor touchedCountryActor;
  private CountryActor countryActor;

  public PlayMapActor (final ImmutableMap <CountryName, CountryActor> countryNamesToActors,
                       final PlayMapInputDetection inputDetection)
  {
    Arguments.checkIsNotNull (countryNamesToActors, "countryNamesToActors");
    Arguments.checkHasNoNullKeysOrValues (countryNamesToActors, "countryNamesToActors");
    Arguments.checkIsNotNull (inputDetection, "inputDetection");

    this.countryNamesToActors = countryNamesToActors;
    this.inputDetection = inputDetection;
  }

  @Override
  public void draw (final Batch batch, final float parentAlpha)
  {
    for (final CountryActor actor : countryNamesToActors.values ())
    {
      actor.draw (batch, parentAlpha);
    }
  }

  public boolean existsCountryActorAt (final Point2D inputCoordinate, final Size2D screenSize)
  {
    Arguments.checkIsNotNull (inputCoordinate, "inputCoordinate");
    Arguments.checkIsNotNull (screenSize, "screenSize");

    return countryNamesToActors.containsKey (inputDetection.getCountryNameAt (inputCoordinate, screenSize));
  }

  public CountryActor getCountryActorAt (final Point2D inputCoordinate, final Size2D screenSize)
  {
    Arguments.checkIsNotNull (inputCoordinate, "inputCoordinate");
    Arguments.checkIsNotNull (screenSize, "screenSize");

    countryActor = countryNamesToActors.get (inputDetection.getCountryNameAt (inputCoordinate, screenSize));

    if (countryActor == null)
    {
      throw new IllegalStateException ("Cannot find " + CountryActor.class.getSimpleName () + " at " + inputCoordinate
                      + ".");

    }

    return countryActor;
  }

  public PlayerColor getHoveredCountryColor ()
  {
    return hoveredCountryActor != null ? hoveredCountryActor.getCurrentColor () : PlayerColor.UNKNOWN;
  }

  public boolean mouseMoved (final Point2D mouseCoordinate, final Size2D screenSize)
  {
    Arguments.checkIsNotNull (mouseCoordinate, "mouseCoordinate");
    Arguments.checkIsNotNull (screenSize, "screenSize");

    if (!existsCountryActorAt (mouseCoordinate, screenSize))
    {
      if (touchedCountryActor != null)
      {
        touchedCountryActor.onTouchUp ();
        touchedCountryActor = null;
      }

      if (hoveredCountryActor != null)
      {
        hoveredCountryActor.onHoverEnd ();
        hoveredCountryActor = null;
      }

      return false;
    }

    final CountryActor hoveredCountryActor = getCountryActorAt (mouseCoordinate, screenSize);
    hoveredCountryActor.onHoverStart ();

    if (this.hoveredCountryActor != null
                    && !this.hoveredCountryActor.getName ().equals (hoveredCountryActor.getName ()))
    {
      this.hoveredCountryActor.onHoverEnd ();
    }

    this.hoveredCountryActor = hoveredCountryActor;

    return true;
  }

  public boolean touchDown (final Point2D touchDownCoordinate, final int button, final Size2D screenSize)
  {
    Arguments.checkIsNotNull (touchDownCoordinate, "touchDownCoordinate");
    Arguments.checkIsNotNull (screenSize, "screenSize");

    if (!existsCountryActorAt (touchDownCoordinate, screenSize))
    {
      if (touchedCountryActor != null)
      {
        touchedCountryActor.onTouchUp ();
        touchedCountryActor = null;
      }

      return false;
    }

    final CountryActor touchedDownCountryActor = getCountryActorAt (touchDownCoordinate, screenSize);

    if (button == Input.Buttons.RIGHT)
    {
      touchedDownCountryActor.nextColor ();

      return true;
    }

    touchedDownCountryActor.onTouchDown ();
    touchedDownCountryActor.changeColorRandomly ();

    if (touchedCountryActor != null && !touchedCountryActor.getName ().equals (touchedDownCountryActor.getName ()))
    {
      touchedCountryActor.onTouchUp ();
    }

    touchedCountryActor = touchedDownCountryActor;

    return true;
  }

  public boolean touchUp (final Point2D touchUpCoordinate, final int button, final Size2D screenSize)
  {
    Arguments.checkIsNotNull (touchUpCoordinate, "touchUpCoordinate");
    Arguments.checkIsNotNull (screenSize, "screenSize");

    if (!existsCountryActorAt (touchUpCoordinate, screenSize))
    {
      if (touchedCountryActor != null)
      {
        touchedCountryActor.onTouchUp ();
        touchedCountryActor = null;
      }

      if (hoveredCountryActor != null)
      {
        hoveredCountryActor.onHoverEnd ();
        hoveredCountryActor = null;
      }

      return false;
    }

    final CountryActor touchedUpCountryActor = getCountryActorAt (touchUpCoordinate, screenSize);
    touchedUpCountryActor.onTouchUp ();

    hoveredCountryActor = touchedUpCountryActor;
    hoveredCountryActor.onHoverStart ();

    if (touchedCountryActor != null && !touchedCountryActor.getName ().equals (touchedUpCountryActor.getName ()))
    {
      touchedCountryActor.onTouchUp ();
    }

    touchedCountryActor = null;

    return true;
  }

  public void setClassicCountryColors ()
  {
    // North America
    setCountryColor ("Alaska", PlayerColor.GOLD);
    setCountryColor ("Northwest Territory", PlayerColor.GOLD);
    setCountryColor ("Greenland", PlayerColor.GOLD);
    setCountryColor ("Alberta", PlayerColor.GOLD);
    setCountryColor ("Ontario", PlayerColor.GOLD);
    setCountryColor ("Quebec", PlayerColor.GOLD);
    setCountryColor ("Western United States", PlayerColor.GOLD);
    setCountryColor ("Eastern United States", PlayerColor.GOLD);
    setCountryColor ("Central America", PlayerColor.GOLD);

    // South America
    setCountryColor ("Venezuela", PlayerColor.RED);
    setCountryColor ("Peru", PlayerColor.RED);
    setCountryColor ("Brazil", PlayerColor.RED);
    setCountryColor ("Argentina", PlayerColor.RED);

    // Europe
    setCountryColor ("Iceland", PlayerColor.BLUE);
    setCountryColor ("Scandinavia", PlayerColor.BLUE);
    setCountryColor ("Great Britain", PlayerColor.BLUE);
    setCountryColor ("Northern Europe", PlayerColor.BLUE);
    setCountryColor ("Ukraine", PlayerColor.BLUE);
    setCountryColor ("Western Europe", PlayerColor.BLUE);
    setCountryColor ("Southern Europe", PlayerColor.BLUE);

    // Asia
    setCountryColor ("Ural", PlayerColor.GREEN);
    setCountryColor ("Siberia", PlayerColor.GREEN);
    setCountryColor ("Yakutsk", PlayerColor.GREEN);
    setCountryColor ("Kamchatka", PlayerColor.GREEN);
    setCountryColor ("Afghanistan", PlayerColor.GREEN);
    setCountryColor ("Irkutsk", PlayerColor.GREEN);
    setCountryColor ("Mongolia", PlayerColor.GREEN);
    setCountryColor ("Japan", PlayerColor.GREEN);
    setCountryColor ("Middle East", PlayerColor.GREEN);
    setCountryColor ("India", PlayerColor.GREEN);
    setCountryColor ("China", PlayerColor.GREEN);
    setCountryColor ("Siam", PlayerColor.GREEN);

    // Africa
    setCountryColor ("North Africa", PlayerColor.BROWN);
    setCountryColor ("Egypt", PlayerColor.BROWN);
    setCountryColor ("Congo", PlayerColor.BROWN);
    setCountryColor ("East Africa", PlayerColor.BROWN);
    setCountryColor ("South Africa", PlayerColor.BROWN);
    setCountryColor ("Madagascar", PlayerColor.BROWN);

    // Australia
    setCountryColor ("Indonesia", PlayerColor.PINK);
    setCountryColor ("New Guinea", PlayerColor.PINK);
    setCountryColor ("Western Australia", PlayerColor.PINK);
    setCountryColor ("Eastern Australia", PlayerColor.PINK);

    clearCountryColor ("Hawaii");
    clearCountryColor ("Caribbean Islands");
    clearCountryColor ("Falkland Islands");
    clearCountryColor ("Svalbard");
    clearCountryColor ("Philippines");
    clearCountryColor ("New Zealand");
    clearCountryColor ("Antarctica");
  }

  public void clearCountryColor (final String countryName)
  {
    final CountryName name = new CountryName (countryName);

    if (countryNamesToActors.containsKey (name)) countryNamesToActors.get (name).clearColor ();
  }

  public void clearCountryColors ()
  {
    for (final CountryActor countryActor : getCountryActors ())
    {
      countryActor.clearColor ();
    }
  }

  public void setCountriesTo (final PlayerColor color)
  {
    Arguments.checkIsNotNull (color, "color");

    for (final CountryActor countryActor : getCountryActors ())
    {
      countryActor.changeColorTo (color);
    }
  }

  public void setCountryColor (final String countryName, final PlayerColor color)
  {
    final CountryName name = new CountryName (countryName);

    if (countryNamesToActors.containsKey (name)) countryNamesToActors.get (name).changeColorTo (color);
  }

  public void randomizeCountryColors ()
  {
    for (final CountryActor countryActor : getCountryActors ())
    {
      countryActor.changeColorRandomly ();
    }
  }

  public void randomizeCountryColorsUsingNRandomColors (final int n)
  {
    Arguments.checkLowerInclusiveBound (n, 1, "n");

    final Collection <PlayerColor> validColors = Collections2.filter (EnumSet.allOf (PlayerColor.class),
                    new Predicate <PlayerColor> ()
                    {
                      @Override
                      public boolean apply (final PlayerColor color)
                      {
                        return color.isNot (PlayerColor.UNKNOWN);
                      }
                    });

    Arguments.checkUpperInclusiveBound (n, validColors.size(), "n");

    final ImmutableSet.Builder <PlayerColor> nColorsBuilder = ImmutableSet.builder ();

    for (int i = 0; i < n; ++i)
    {
      final PlayerColor randomColor = Randomness.getRandomElementFrom (validColors);
      nColorsBuilder.add (randomColor);
      validColors.remove (randomColor);
    }

    randomizeCountryColorsUsingOnly (nColorsBuilder.build());
  }

  public void randomizeCountryColorsUsingOnly (final Collection <PlayerColor> colors)
  {
    Arguments.checkIsNotNullOrEmpty (colors, "colors");
    Arguments.checkHasNoNullElements (colors, "colors");

    PlayerColor randomColor;

    for (final CountryActor countryActor : getCountryActors ())
    {
      randomColor = Randomness.getRandomElementFrom (colors);

      countryActor.changeColorTo (randomColor);
    }
  }

  public void randomizeCountryColorsUsingOnly (final PlayerColor... colors)
  {
    randomizeCountryColorsUsingOnly (Arrays.asList (colors));
  }

  public void setCountryTextureFiltering (final Texture.TextureFilter minFilter, final Texture.TextureFilter magFilter)
  {
    Arguments.checkIsNotNull (minFilter, "minFilter");
    Arguments.checkIsNotNull (magFilter, "magFilter");

    for (final CountryActor countryActor : countryNamesToActors.values ())
    {
      countryActor.setTextureFiltering (minFilter, magFilter);
    }
  }

  private ImmutableCollection <CountryActor> getCountryActors ()
  {
    return countryNamesToActors.values ();
  }
}
