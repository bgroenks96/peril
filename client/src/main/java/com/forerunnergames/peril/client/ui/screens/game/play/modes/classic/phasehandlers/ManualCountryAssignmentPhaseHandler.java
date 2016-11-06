package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.phasehandlers;

import com.badlogic.gdx.Gdx;

import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.actors.PlayMap;
import com.forerunnergames.peril.common.game.PlayerColor;
import com.forerunnergames.peril.common.net.events.client.request.response.PlayerClaimCountryResponseRequestEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerClaimCountryResponseDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.request.PlayerClaimCountryRequestEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerClaimCountryResponseSuccessEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.common.Result;
import com.forerunnergames.tools.common.Strings;

import javax.annotation.Nullable;

import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.listener.Handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ManualCountryAssignmentPhaseHandler extends AbstractGamePhaseHandler
{
  private static final Logger log = LoggerFactory.getLogger (ManualCountryAssignmentPhaseHandler.class);
  @Nullable
  private PlayerClaimCountryRequestEvent request = null;

  public ManualCountryAssignmentPhaseHandler (final PlayMap playMap, final MBassador <Event> eventBus)
  {
    super (playMap, eventBus);
  }

  @Override
  void onCountryLeftClicked (final String countryName, final float x, final float y)
  {
    if (checkRequestExistsForCountryLeftClick (countryName).failed ()) return;
    if (checkLeftClickedCountryIsUnclaimed (countryName).failed ()) return;

    preemptivelyUpdatePlayMap (countryName);
    sendResponse (countryName);
    reset ();
  }

  @Override
  public void reset ()
  {
    super.reset ();
    request = null;
  }

  @Override
  public void execute ()
  {
    // Empty implementation.
  }

  @Handler
  void onEvent (final PlayerClaimCountryRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    request = event;

    listenForPlayMapCountryClicks ();
  }

  @Handler
  void onEvent (final PlayerClaimCountryResponseSuccessEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    if (isSelf (event.getPlayer ())) verifyPreemptivePlayMapUpdates (event);
  }

  @Handler
  void onEvent (final PlayerClaimCountryResponseDeniedEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);
    log.warn ("Error. Could not claim country [{}]. Reason: {}.", event.getCountryName (), event.getReason ());

    rollBackPreemptivePlayMapUpdates (event.getCountryName ());
    reset ();
  }

  private Result <String> checkRequestExistsForCountryLeftClick (final String countryName)
  {
    if (request == null)
    {
      // @formatter:off
      final String failureMessage =
              Strings.format ("Ignoring left-click on country [{}] because no prior corresponding [{}] was received.",
                              countryName, PlayerClaimCountryRequestEvent.class.getSimpleName ());
      // @formatter:on
      log.warn (failureMessage);
      return Result.failure (failureMessage);
    }

    return Result.success ();
  }

  private Result <String> checkLeftClickedCountryIsUnclaimed (final String countryName)
  {
    if (isClaimedCountry (countryName))
    {
      // @formatter:off
      final String failureMessage =
              Strings.format ("Ignoring left-click on country [{}] because not a valid response to [{}]. (Country " +
                              "is already claimed.)", countryName, PlayerClaimCountryRequestEvent.class.getSimpleName ());
      // @formatter:on
      log.warn (failureMessage);
      return Result.failure (failureMessage);
    }

    return Result.success ();
  }

  private boolean isClaimedCountry (final String countryName)
  {
    assert request != null;
    return request.isClaimedCountry (countryName);
  }

  private void sendResponse (final String countryName)
  {
    publish (new PlayerClaimCountryResponseRequestEvent (countryName));
  }

  private void preemptivelyUpdatePlayMap (final String countryName)
  {
    // PlayerClaimCountryRequestEvent field will be reset to null before the Runnable executes,
    // so make a defensive copy of the required event data.
    assert request != null;
    final PlayerColor playerColor = request.getPlayerColor ();

    Gdx.app.postRunnable (new Runnable ()
    {
      @Override
      public void run ()
      {
        setCountryOwner (playerColor, countryName);
        changeCountryArmiesBy (1, countryName);
      }
    });
  }

  private void verifyPreemptivePlayMapUpdates (final PlayerClaimCountryResponseSuccessEvent event)
  {
    Gdx.app.postRunnable (new Runnable ()
    {
      @Override
      public void run ()
      {
        assert playerOwnsCountry (event.getPlayerColor (), event.getCountryName ());
        assert countryArmyCountIs (event.getCountryArmyCount (), event.getCountryName ());
      }
    });
  }

  private void rollBackPreemptivePlayMapUpdates (final String countryName)
  {
    Gdx.app.postRunnable (new Runnable ()
    {
      @Override
      public void run ()
      {
        setCountryUnowned (countryName);
        changeCountryArmiesBy (-1, countryName);
      }
    });
  }
}
