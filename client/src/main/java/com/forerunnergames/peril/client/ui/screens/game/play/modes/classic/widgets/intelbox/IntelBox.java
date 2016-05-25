package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.widgets.intelbox;

import com.badlogic.gdx.scenes.scene2d.Actor;

import com.forerunnergames.peril.common.map.MapMetadata;
import com.forerunnergames.peril.common.net.GameServerConfiguration;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.tools.net.client.configuration.ClientConfiguration;

import com.google.common.collect.ImmutableSet;

public interface IntelBox
{
  void setPlayer (final PlayerPacket player);

  void setGameServerConfiguration (final GameServerConfiguration config);

  void setClientConfiguration (final ClientConfiguration config);

  void setMapMetadata (final MapMetadata mapMetadata);

  void setGamePhaseName (final String phaseName);

  void setOwnedCountries (final ImmutableSet <CountryPacket> ownedCountries);

  void clear ();

  Actor asActor ();

  void refreshAssets ();
}
