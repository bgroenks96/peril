package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;

import com.forerunnergames.peril.client.settings.GraphicsSettings;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.data.CountrySpriteDataRepository;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.tools.CoordinateSpaces;
import com.forerunnergames.peril.core.model.map.country.CountryName;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.geometry.Geometry;
import com.forerunnergames.tools.common.geometry.Point2D;
import com.forerunnergames.tools.common.geometry.Scaling2D;
import com.forerunnergames.tools.common.geometry.Size2D;

import com.google.common.math.IntMath;

import java.util.HashMap;
import java.util.Map;

public final class ArmyTextActor extends Actor
{
  private static final int MIN_COUNTRY_ARMY_COUNT = 0;
  private static final int MAX_COUNTRY_ARMY_COUNT = 99;
  private final Map <CountryName, Integer> countryNamesToArmyCounts = new HashMap <> ();
  private final CountrySpriteDataRepository countrySpriteDataRepository;
  private final BitmapFont font;
  private String countryArmyText;
  private Point2D countryCenterPlayMapReferenceSpace;
  private Point2D countryCenterReferenceScreenSpace;
  private Size2D screenSize;
  private Scaling2D scaling;
  private float x;
  private float y;

  public ArmyTextActor (final CountrySpriteDataRepository repository)
  {
    Arguments.checkIsNotNull (repository, "repository");

    countrySpriteDataRepository = repository;

    font = new BitmapFont ();

    reset ();
  }

  @Override
  public void draw (final Batch batch, final float parentAlpha)
  {
    for (final Map.Entry <CountryName, Integer> entry : countryNamesToArmyCounts.entrySet ())
    {
      countryArmyText = String.valueOf (entry.getValue ());
      countryCenterPlayMapReferenceSpace = countrySpriteDataRepository.get (entry.getKey ())
              .getCenterPlayMapReferenceSpace ();
      countryCenterReferenceScreenSpace = CoordinateSpaces
              .referencePlayMapSpaceToReferenceScreenSpace (countryCenterPlayMapReferenceSpace);

      x = countryCenterReferenceScreenSpace.getX ();
      y = GraphicsSettings.REFERENCE_SCREEN_HEIGHT - countryCenterReferenceScreenSpace.getY ();

      font.draw (batch, countryArmyText, x, y);
    }
  }

  @Override
  public void act (float delta)
  {
    super.act (delta);

    if (shouldUpdateScreenSize ()) updateScreenSize ();
  }

  // TODO Production: Remove
  public boolean touchDown (final CountryName countryName, final int button)
  {
    switch (button)
    {
      case Input.Buttons.LEFT:
      {
        incrementArmyCount (countryName);

        return true;
      }
      case Input.Buttons.RIGHT:
      {
        decrementArmyCount (countryName);

        return true;
      }
      default:
      {
        return false;
      }
    }
  }

  public void reset ()
  {
    setAllCountryArmyCountsTo (0);
  }

  public void setAllCountryArmyCountsTo (final int armyCount)
  {
    for (final CountryName countryName : countrySpriteDataRepository.getCountryNames ())
    {
      setCountryArmyCountTo (armyCount, countryName);
    }
  }

  public void incrementArmyCount (final CountryName countryName)
  {
    Arguments.checkIsNotNull (countryName, "countryName");

    changeArmyCountBy (1, countryName);
  }

  public void decrementArmyCount (final CountryName countryName)
  {
    Arguments.checkIsNotNull (countryName, "countryName");

    changeArmyCountBy (-1, countryName);
  }

  public void setCountryArmyCountTo (final int armyCount, final CountryName countryName)
  {
    Arguments.checkIsNotNull (countryName, "countryName");
    Arguments.checkIsNotNegative (armyCount, "armyCount");

    changeArmyCountBy (getCountryArmyCountDelta (countryName, armyCount), countryName);
  }

  public void changeArmyCountBy (final int armyCountyDelta, final CountryName countryName)
  {
    Arguments.checkIsNotNull (countryName, "countryName");

    if (countrySpriteDataRepository.doesNotHave (countryName)) return;

    final int oldCountryArmyCount = getCountryArmyCount (countryName);
    final int newCountryArmyCount = IntMath.checkedAdd (oldCountryArmyCount, armyCountyDelta);

    if (newCountryArmyCount < MIN_COUNTRY_ARMY_COUNT || newCountryArmyCount > MAX_COUNTRY_ARMY_COUNT) return;

    countryNamesToArmyCounts.put (countryName, newCountryArmyCount);
  }

  private int getCountryArmyCountDelta (final CountryName countryName, final int desiredArmyCount)
  {
    return IntMath.checkedSubtract (desiredArmyCount, getCountryArmyCount (countryName));
  }

  private int getCountryArmyCount (final CountryName countryName)
  {
    final Integer countryArmyCount = countryNamesToArmyCounts.get (countryName);

    return countryArmyCount == null ? 0 : countryArmyCount;
  }

  private boolean shouldUpdateScreenSize ()
  {
    return screenSize == null || scaling == null
            || screenSize.isNot (Gdx.graphics.getWidth (), Gdx.graphics.getHeight ());
  }

  private void updateScreenSize ()
  {
    screenSize = new Size2D (Gdx.graphics.getWidth (), Gdx.graphics.getHeight ());
    scaling = Geometry.divide (GraphicsSettings.REFERENCE_SCREEN_SIZE, screenSize);
    font.getData ().setScale (scaling.getX (), scaling.getY ());
  }
}
