package com.forerunnergames.peril.client.ui.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;

import com.forerunnergames.peril.client.settings.ScreenSettings;
import com.forerunnergames.peril.client.ui.music.MusicChanger;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.common.controllers.ControllerAdapter;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import javax.annotation.Nullable;

import net.engio.mbassy.bus.MBassador;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ScreenController extends ControllerAdapter implements ScreenChanger
{
  private static final Logger log = LoggerFactory.getLogger (ScreenController.class);
  private final Game game;
  private final MusicChanger musicChanger;
  private final AssetManager assetManager;
  private final MBassador <Event> eventBus;
  private final BiMap <ScreenId, Screen> screens = HashBiMap.create (ScreenId.values ().length);
  @Nullable
  private ScreenId previousScreenId = null;

  public ScreenController (final Game game,
                           final MusicChanger musicChanger,
                           final AssetManager assetManager,
                           final MBassador <Event> eventBus)
  {
    Arguments.checkIsNotNull (game, "game");
    Arguments.checkIsNotNull (musicChanger, "musicChanger");
    Arguments.checkIsNotNull (assetManager, "assetManager");
    Arguments.checkIsNotNull (eventBus, "eventBus");

    this.game = game;
    this.musicChanger = musicChanger;
    this.assetManager = assetManager;
    this.eventBus = eventBus;
  }

  @Override
  public void initialize ()
  {
    final ScreenFactory screenFactory = ScreenFactoryCreator.create (this, assetManager, eventBus);

    for (final ScreenId screenId : ScreenId.values ())
    {
      screens.put (screenId, screenFactory.create (screenId));
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

    if (id == getCurrentScreenId ()) return;

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
