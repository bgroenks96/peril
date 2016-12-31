package com.forerunnergames.peril.server.controllers;

import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.server.remote.RemoteClient;

/**
 * Thrown to indicate that the player registered to a client no longer exists. This is an exceptional case (and likely
 * the result of a bug or state violation in core) so it needs to be handled by server appropriately.
 */
final class RegisteredClientPlayerNotFoundException extends Exception
{
  final String message;

  RegisteredClientPlayerNotFoundException (final String playerName, final RemoteClient client)
  {
    Arguments.checkIsNotNull (playerName, "playerName");
    Arguments.checkIsNotNull (client, "client");

    message = Strings.format ("Player [{}] not found for client [{}].", playerName, client);
  }
}