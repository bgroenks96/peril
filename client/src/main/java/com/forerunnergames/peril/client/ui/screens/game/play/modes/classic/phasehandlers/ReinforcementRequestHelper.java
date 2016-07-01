package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.phasehandlers;

import com.forerunnergames.peril.common.net.events.server.notify.direct.PlayerBeginCountryReinforcementEvent;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import javax.annotation.Nullable;

import net.engio.mbassy.bus.MBassador;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO This class is a hack until the core reinforcement event API can be fixed.
public final class ReinforcementRequestHelper
{
  private static final Logger log = LoggerFactory.getLogger (ReinforcementRequestHelper.class);
  @Nullable
  private PlayerBeginCountryReinforcementEvent regularRequest;

  public boolean isSet ()
  {
    return regularRequest != null;
  }

  void set (final Object request)
  {
    Arguments.checkIsNotNull (request, "request");

    if (request instanceof PlayerBeginCountryReinforcementEvent)
    {
      regularRequest = (PlayerBeginCountryReinforcementEvent) request;
      return;
    }

    log.warn ("Not setting unrecognized request object: {}.", request);
  }

  ImmutableSet <CountryPacket> getPlayerOwnedCountries ()
  {
    if (regularRequest != null) return regularRequest.getPlayerOwnedCountries ();

    log.warn ("No prior corresponding reinforcement request was received.");

    return ImmutableSet.of ();
  }

  boolean isPlayerOwnedCountry (final String countryName)
  {
    Arguments.checkIsNotNull (countryName, "countryName");

    if (regularRequest != null) return regularRequest.isPlayerOwnedCountry (countryName);

    log.warn ("No prior corresponding reinforcement request was received.");

    return false;
  }

  boolean isNotPlayerOwnedCountry (final String countryName)
  {
    Arguments.checkIsNotNull (countryName, "countryName");

    if (regularRequest != null) return regularRequest.isNotPlayerOwnedCountry (countryName);

    log.warn ("No prior corresponding reinforcement request was received.");

    return true;
  }

  boolean canAddArmiesToCountry (final String countryName)
  {
    Arguments.checkIsNotNull (countryName, "countryName");

    if (regularRequest != null) return regularRequest.canAddArmiesToCountry (countryName);

    log.warn ("No prior corresponding reinforcement request was received.");

    return false;
  }

  int getMaxArmiesPerCountry ()
  {
    if (regularRequest != null) return regularRequest.getMaxArmiesPerCountry ();

    log.warn ("No prior corresponding reinforcement request was received.");

    return 0;
  }

  int getTotalReinforcements ()
  {
    if (regularRequest != null) return regularRequest.getTotalReinforcements ();

    log.warn ("No prior corresponding reinforcement request was received.");

    return 0;
  }

  String getPlayerName ()
  {
    if (regularRequest != null) return regularRequest.getPlayerName ();

    log.warn ("No prior corresponding reinforcement request was received.");

    return "";
  }

  String getClassName ()
  {
    if (regularRequest != null) return regularRequest.getClass ().getSimpleName ();

    log.warn ("No prior corresponding reinforcement request was received.");

    return "";
  }

  void sendResponse (final ImmutableMap <String, Integer> countryNamesToReinforcements,
                     final MBassador <Event> eventBus)
  {
    if (regularRequest != null)
    {
      // FIXME
      // eventBus.publish (new PlayerReinforceCountryResponseRequestEvent (countryNamesToReinforcements));
      return;
    }

    log.warn ("No prior corresponding reinforcement request was received.");
  }

  void reset ()
  {
    regularRequest = null;
  }
}
