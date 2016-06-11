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

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.concurrent.ThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO This is a hack until the core battle API redesign is complete.
@ThreadSafe
public final class BattleResetState
{
  private static final Logger log = LoggerFactory.getLogger (BattleResetState.class);
  private final Lock lock = new ReentrantLock ();
  private final Condition condition = lock.newCondition ();
  private volatile boolean isResetting;

  public void waitForBattleResetToFinish () throws InterruptedException
  {
    lock.lock ();
    try
    {
      while (isResetting)
      {
        condition.await ();
      }
      log.trace ("Battle reset complete.");
    }
    finally
    {
      lock.unlock ();
    }
  }

  public void startReset ()
  {
    lock.lock ();
    try
    {
      isResetting = true;
      log.trace ("Starting battle reset...");
    }
    finally
    {
      lock.unlock ();
    }
  }

  public void finishReset ()
  {
    lock.lock ();
    try
    {
      isResetting = false;
      log.trace ("Finishing battle reset...");
      condition.signalAll ();
    }
    finally
    {
      lock.unlock ();
    }
  }

  public void cancelReset ()
  {
    lock.lock ();
    try
    {
      isResetting = false;
      log.trace ("Cancelling battle reset...");
      Thread.currentThread ().interrupt ();
    }
    finally
    {
      lock.unlock ();
    }
  }
}
