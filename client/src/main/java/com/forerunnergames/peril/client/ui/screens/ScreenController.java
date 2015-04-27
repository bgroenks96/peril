package com.forerunnergames.peril.client.ui.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;

import com.forerunnergames.peril.client.input.LibGdxMouseInput;
import com.forerunnergames.peril.client.settings.ScreenSettings;
import com.forerunnergames.peril.client.ui.Assets;
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
  private final Game game;
  private final ScreenFactory screenFactory;
  private final MusicChanger musicChanger;
  private BiMap <ScreenId, Screen> screens = HashBiMap.create (ScreenId.values ().length);
  @Nullable
  private ScreenId previousScreenId;

  public ScreenController (final Game game, final ScreenFactory screenFactory, final MusicChanger musicChanger)
  {
    Arguments.checkIsNotNull (game, "game");
    Arguments.checkIsNotNull (screenFactory, "screenFactory");
    Arguments.checkIsNotNull (musicChanger, "musicChanger");

    this.game = game;
    this.screenFactory = screenFactory;
    this.musicChanger = musicChanger;
  }

  @Override
  public void initialize ()
  {
    for (final ScreenId screenId : ScreenId.values ())
    {
      screens.put (screenId, screenFactory.create (screenId, this, new LibGdxScreenSize (Gdx.graphics),
                                                   new LibGdxMouseInput (Gdx.input), Assets.skin));
    }

    toScreen (ScreenSettings.START_SCREEN);
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
  public void toScreen (final ScreenId id)
  {
    Arguments.checkIsNotNull (id, "id");
    Arguments.checkIsTrue (screens.containsKey (id), "Cannot find " + Screen.class.getSimpleName () + " with "
            + id.getClass ().getSimpleName () + " [" + id + "].");

    if (id.equals (getCurrentScreenId ())) return;

    previousScreenId = getCurrentScreenId ();

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
