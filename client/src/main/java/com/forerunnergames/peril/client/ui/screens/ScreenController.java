package com.forerunnergames.peril.client.ui.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;

import com.forerunnergames.peril.client.ui.music.MusicChanger;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.controllers.ControllerAdapter;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ScreenController extends ControllerAdapter implements ScreenChanger
{
  private static final Logger log = LoggerFactory.getLogger (ScreenController.class);
  private final BiMap <ScreenId, Screen> screens = HashBiMap.create (ScreenId.values ().length);
  private final Game game;
  private final MusicChanger musicChanger;
  private final ScreenFactoryCreator screenFactoryCreator;
  private ScreenFactory screenFactory;
  @Nullable
  private ScreenId previousPreviousScreenId = null;
  @Nullable
  private ScreenId previousScreenId = null;

  public ScreenController (final Game game,
                           final MusicChanger musicChanger,
                           final ScreenFactoryCreator screenFactoryCreator)
  {
    Arguments.checkIsNotNull (game, "game");
    Arguments.checkIsNotNull (musicChanger, "musicChanger");
    Arguments.checkIsNotNull (screenFactoryCreator, "screenFactoryCreator");

    this.game = game;
    this.musicChanger = musicChanger;
    this.screenFactoryCreator = screenFactoryCreator;
  }

  @Override
  public void initialize ()
  {
    screenFactory = screenFactoryCreator.create (this);
    toScreen (ScreenId.SPLASH);
  }

  @Override
  public void shutDown ()
  {
    for (final Screen screen : screens.values ())
    {
      screen.dispose ();
    }

    screens.clear ();
  }

  @Override
  public void toPreviousScreenOr (final ScreenId defaultScreenId)
  {
    Arguments.checkIsNotNull (defaultScreenId, "defaultScreenId");

    toScreen (previousScreenId != null ? previousScreenId : defaultScreenId);
  }

  @Override
  public void toPreviousScreenSkippingOr (final ScreenId skipScreenId, final ScreenId defaultScreenId)
  {
    Arguments.checkIsNotNull (skipScreenId, "skipScreenId");
    Arguments.checkIsNotNull (defaultScreenId, "defaultScreenId");

    if (previousScreenId != null && previousScreenId != skipScreenId)
    {
      toScreen (previousScreenId);
      return;
    }

    if (previousPreviousScreenId != null && previousPreviousScreenId != skipScreenId)
    {
      toScreen (previousPreviousScreenId);
      return;
    }

    toScreen (defaultScreenId);
  }

  @Override
  public void toScreen (final ScreenId id)
  {
    Arguments.checkIsNotNull (id, "id");

    if (id == getCurrentScreenId ()) return;

    previousPreviousScreenId = previousScreenId;
    previousScreenId = getCurrentScreenId ();

    if (!screens.containsKey (id)) screens.put (id, screenFactory.create (id));

    game.setScreen (screens.get (id));
    musicChanger.changeMusic (previousScreenId, getCurrentScreenId ());

    log.info ("Changed from {} [{}] to {} [{}].", Screen.class.getSimpleName (), previousScreenId,
              Screen.class.getSimpleName (), id);
  }

  private ScreenId getCurrentScreenId ()
  {
    return screens.inverse ().get (game.getScreen ());
  }
}
