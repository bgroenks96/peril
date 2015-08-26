package com.forerunnergames.peril.client.ui.screens.menus.multiplayer.modes.classic.loading;

import com.forerunnergames.peril.common.net.GameServerConfiguration;

public interface CreateGameServerListener extends JoinGameServerListener
{
  void onCreateStart (final GameServerConfiguration configuration, final String playerName);

  void onCreateFinish (final GameServerConfiguration configuration);

  void onCreateFailure (final GameServerConfiguration configuration, final String reason);
}
