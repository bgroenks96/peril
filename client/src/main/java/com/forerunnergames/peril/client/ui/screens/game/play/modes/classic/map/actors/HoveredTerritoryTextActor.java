package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.actors;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

import com.forerunnergames.peril.client.input.MouseInput;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.images.CountryImageState;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.input.PlayMapInputDetection;
import com.forerunnergames.peril.core.model.map.continent.ContinentName;
import com.forerunnergames.peril.core.model.map.country.CountryName;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.LetterCase;
import com.forerunnergames.tools.common.Strings;

public final class HoveredTerritoryTextActor extends Actor
{
  private static final Vector2 TEXT_OFFSET = new Vector2 (35, -17);
  private final PlayMapInputDetection playMapInputDetection;
  private final MouseInput mouseInput;
  private final BitmapFont font;
  private final Vector2 mousePosition = new Vector2 ();
  private final Vector2 textPosition = new Vector2 ();
  private String text = "";
  private PlayMapActor playMapActor;
  private CountryImageState countryImageState;

  public HoveredTerritoryTextActor (final PlayMapInputDetection playMapInputDetection, final MouseInput mouseInput)
  {
    Arguments.checkIsNotNull (playMapInputDetection, "playMapInputDetection");
    Arguments.checkIsNotNull (mouseInput, "mouseInput");

    this.playMapInputDetection = playMapInputDetection;
    this.mouseInput = mouseInput;
    font = new BitmapFont ();
  }

  @Override
  public void draw (final Batch batch, final float parentAlpha)
  {
    font.draw (batch, text, textPosition.x, textPosition.y);
  }

  @Override
  public void act (float delta)
  {
    super.act (delta);

    mousePosition.set (mouseInput.position ());

    final CountryName countryName = playMapInputDetection.getCountryNameAt (mousePosition);
    final ContinentName continentName = playMapInputDetection.getContinentNameAt (mousePosition);

    if (playMapActor != null) countryImageState = playMapActor.getCurrentImageStateOf (countryName);

    text = Strings.toStringList (", ", LetterCase.PROPER, false, countryName.asString (), continentName.asString (),
                                 countryImageState != null ? countryImageState.toString () : "");

    screenToLocalCoordinates (mousePosition);

    textPosition.set (mousePosition.x + TEXT_OFFSET.x, mousePosition.y + TEXT_OFFSET.y);
  }

  public void setPlayMapActor (final PlayMapActor playMapActor)
  {
    Arguments.checkIsNotNull (playMapActor, "playMapActor");

    this.playMapActor = playMapActor;
  }
}
