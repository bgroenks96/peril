package com.forerunnergames.peril.client.ui.screens.menus.multiplayer.modes.classic.creategame;

import com.forerunnergames.peril.common.game.GameConfiguration;

public interface CreateGameServerHandler
{
  void create (final String serverName,
               final GameConfiguration gameConfig,
               final String playerName,
               final CreateGameServerListener listener);
}
