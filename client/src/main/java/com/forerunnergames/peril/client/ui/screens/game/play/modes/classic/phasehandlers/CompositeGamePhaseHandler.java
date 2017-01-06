/*
 * Copyright © 2016 Forerunner Games, LLC.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.phasehandlers;

import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.actors.PlayMap;
import com.forerunnergames.peril.common.game.GamePhase;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.common.Arguments;

import com.google.common.collect.ImmutableSet;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

public final class CompositeGamePhaseHandler implements GamePhaseHandler
{
  private Collection <GamePhaseHandler> handlers = new HashSet<> ();

  public CompositeGamePhaseHandler (final GamePhaseHandler... handlers)
  {
    Arguments.checkIsNotNull (handlers, "handlers");
    Arguments.checkHasNoNullElements (handlers, "handlers");

    this.handlers.addAll (Arrays.asList (handlers));
  }

  @Override
  public ImmutableSet <GamePhase> getPhases ()
  {
    final ImmutableSet.Builder <GamePhase> phases = ImmutableSet.builder ();

    for (final GamePhaseHandler handler : handlers)
    {
      phases.addAll (handler.getPhases ());
    }

    return phases.build ();
  }

  @Override
  public void activate ()
  {
    for (final GamePhaseHandler handler : handlers)
    {
      handler.activate ();
    }
  }

  @Override
  public void activate (final GamePhase currentPhase)
  {
    Arguments.checkIsNotNull (currentPhase, "phase");

    for (final GamePhaseHandler handler : handlers)
    {
      handler.activate (currentPhase);
    }
  }

  @Override
  public void activate (final PlayerPacket currentPlayer, final GamePhase currentPhase)
  {
    Arguments.checkIsNotNull (currentPlayer, "player");
    Arguments.checkIsNotNull (currentPhase, "phase");

    for (final GamePhaseHandler handler : handlers)
    {
      handler.activate (currentPlayer, currentPhase);
    }
  }

  @Override
  public void execute ()
  {
    for (final GamePhaseHandler handler : handlers)
    {
      handler.execute ();
    }
  }

  @Override
  public void cancel ()
  {
    for (final GamePhaseHandler handler : handlers)
    {
      handler.cancel ();
    }
  }

  @Override
  public void deactivate ()
  {
    for (final GamePhaseHandler handler : handlers)
    {
      handler.deactivate ();
    }
  }

  @Override
  public void deactivate (final GamePhase currentPhase)
  {
    Arguments.checkIsNotNull (currentPhase, "phase");

    for (final GamePhaseHandler handler : handlers)
    {
      handler.deactivate (currentPhase);
    }
  }

  @Override
  public void deactivate (final PlayerPacket currentPlayer, final GamePhase currentPhase)
  {
    Arguments.checkIsNotNull (currentPlayer, "player");
    Arguments.checkIsNotNull (currentPhase, "phase");

    for (final GamePhaseHandler handler : handlers)
    {
      handler.deactivate (currentPlayer, currentPhase);
    }
  }

  @Override
  public void setSelfPlayer (final PlayerPacket player)
  {
    Arguments.checkIsNotNull (player, "player");

    for (final GamePhaseHandler handler : handlers)
    {
      handler.setSelfPlayer (player);
    }
  }

  @Override
  public void updatePlayerForSelf (final PlayerPacket player)
  {
    Arguments.checkIsNotNull (player, "player");

    for (final GamePhaseHandler handler : handlers)
    {
      handler.updatePlayerForSelf (player);
    }
  }

  @Override
  public void reset ()
  {
    for (final GamePhaseHandler handler : handlers)
    {
      handler.reset ();
    }
  }

  @Override
  public void shutDown ()
  {
    for (final GamePhaseHandler handler : handlers)
    {
      handler.shutDown ();
    }
  }

  @Override
  public void setPlayMap (final PlayMap playMap)
  {
    Arguments.checkIsNotNull (playMap, "playMap");

    for (final GamePhaseHandler handler : handlers)
    {
      handler.setPlayMap (playMap);
    }
  }

  public void add (final GamePhaseHandler dialog)
  {
    Arguments.checkIsNotNull (dialog, "dialog");

    final Collection <GamePhaseHandler> handlersCopy = new HashSet<> (handlers);
    handlersCopy.add (dialog);
    handlers = handlersCopy;
  }

  public void add (final GamePhaseHandler... handlers)
  {
    Arguments.checkIsNotNull (handlers, "handlers");
    Arguments.checkHasNoNullElements (handlers, "handlers");

    final Collection <GamePhaseHandler> handlersCopy = new HashSet<> (this.handlers);
    handlersCopy.addAll (Arrays.asList (handlers));
    this.handlers = handlersCopy;
  }

  public void remove (final GamePhaseHandler dialog)
  {
    Arguments.checkIsNotNull (dialog, "dialog");

    final Collection <GamePhaseHandler> handlersCopy = new HashSet<> (handlers);
    handlersCopy.remove (dialog);
    handlers = handlersCopy;
  }

  public void remove (final GamePhaseHandler... handlers)
  {
    Arguments.checkIsNotNull (handlers, "handlers");
    Arguments.checkHasNoNullElements (handlers, "handlers");

    final Collection <GamePhaseHandler> handlersCopy = new HashSet<> (this.handlers);
    handlersCopy.removeAll (Arrays.asList (handlers));
    this.handlers = handlersCopy;
  }
}
