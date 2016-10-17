package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.intelbox;

import com.badlogic.gdx.scenes.scene2d.Actor;

import com.forerunnergames.peril.common.map.MapMetadata;
import com.forerunnergames.peril.common.net.GameServerConfiguration;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.net.client.configuration.ClientConfiguration;

import javax.annotation.Nullable;

public interface IntelBox
{
  void setSelfPlayer (final PlayerPacket player);

  void setGameServerConfiguration (final GameServerConfiguration config);

  void setClientConfiguration (final ClientConfiguration config);

  void setMapMetadata (final MapMetadata mapMetadata);

  void setGamePhaseName (final String phaseName);

  void setGameRound (final int round);

  void setOwnedCountriesForSelf (final int countries, @Nullable final PlayerPacket player);

  void addOwnedCountryForSelf (@Nullable final PlayerPacket player);

  void removeOwnedCountryForSelf (@Nullable final PlayerPacket player);

  void clear ();

  Actor asActor ();

  void refreshAssets ();
}
