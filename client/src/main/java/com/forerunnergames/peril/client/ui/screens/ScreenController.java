package com.forerunnergames.peril.client.ui.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;

import com.forerunnergames.peril.client.ui.music.MusicChanger;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.common.controllers.ControllerAdapter;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ScreenController extends ControllerAdapter implements ScreenChanger
{
  private static final Logger log = LoggerFactory.getLogger (ScreenController.class);
  private static final int SCREEN_HISTORY_DEPTH = 10;
  private final Deque <ScreenId> screenIdHistory = new ArrayDeque <> (SCREEN_HISTORY_DEPTH);
  private final BiMap <ScreenId, Screen> screens = HashBiMap.create (ScreenId.values ().length);
  private final Game game;
  private final MusicChanger musicChanger;
  private final ScreenFactoryCreator screenFactoryCreator;
  private ScreenFactory screenFactory;

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
  public void toPreviousScreenOrSkipping (final ScreenId defaultScreenId, final ScreenId... skipScreenIds)
  {
    Arguments.checkIsNotNull (defaultScreenId, "defaultScreenId");
    Arguments.checkIsNotNull (skipScreenIds, "skipScreenIds");
    Arguments.checkHasNoNullElements (skipScreenIds, "skipScreenIds");

    final ImmutableCollection <ScreenId> skipScreenIdsCopy = ImmutableList.copyOf (skipScreenIds);

    log.debug ("Attempting to go to previous screen from [{}], while avoiding the following screens: {}...",
               getCurrentScreenId (), skipScreenIdsCopy);

    final Iterator <ScreenId> screenIdHistoryIterator = screenIdHistory.descendingIterator ();

    int historyDepth = 1;

    while (screenIdHistoryIterator.hasNext ())
    {
      final ScreenId previousScreenId = screenIdHistoryIterator.next ();

      if (!skipScreenIdsCopy.contains (previousScreenId))
      {
        log.debug ("Success, {} back is [{}].", Strings.pluralizeS (historyDepth, "screen"), previousScreenId);
        toScreen (previousScreenId);
        return;
      }

      log.debug ("Failed, {} back is [{}].", Strings.pluralizeS (historyDepth, "screen"), previousScreenId);

      ++historyDepth;
    }

    log.debug ("Going to default screen [{}] from [{}]", defaultScreenId, getCurrentScreenId ());

    toScreen (defaultScreenId);
  }

  @Override
  public void toScreen (final ScreenId id)
  {
    Arguments.checkIsNotNull (id, "id");

    if (id == getCurrentScreenId ()) return;
    if (!screens.containsKey (id)) screens.put (id, screenFactory.create (id));
    if (screenIdHistory.size () == SCREEN_HISTORY_DEPTH) screenIdHistory.remove ();

    final ScreenId previousScreenId = getCurrentScreenId ();
    screenIdHistory.add (previousScreenId);
    game.setScreen (screens.get (id));
    musicChanger.changeMusic (previousScreenId, getCurrentScreenId ());

    log.info ("Changed from {} [{}] to {} [{}].", Screen.class.getSimpleName (), previousScreenId,
              Screen.class.getSimpleName (), id);
  }

  private ScreenId getCurrentScreenId ()
  {
    final Screen currentScreen = game.getScreen ();

    return currentScreen != null ? screens.inverse ().get (currentScreen) : ScreenId.NONE;
  }
}
