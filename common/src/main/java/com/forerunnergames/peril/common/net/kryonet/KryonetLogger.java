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

package com.forerunnergames.peril.common.net.kryonet;

import com.esotericsoftware.minlog.Log;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SLF4J Logger for Kryonet Library
 */
final class KryonetLogger extends Log.Logger
{
  private static final Logger log = LoggerFactory.getLogger (KryonetLogger.class);

  KryonetLogger ()
  {
    Log.set (Log.LEVEL_TRACE);
  }

  @Override
  public void log (final int level, @Nullable final String category, final String message, @Nullable final Throwable ex)
  {
    switch (level)
    {
      case Log.LEVEL_ERROR:
      {
        log.error ("{}", message, ex);
        break;
      }
      case Log.LEVEL_WARN:
      {
        log.warn ("{}", message, ex);
        break;
      }
      case Log.LEVEL_INFO:
      {
        log.info ("{}", message, ex);
        break;
      }
      case Log.LEVEL_DEBUG:
      {
        log.debug ("{}", message, ex);
        break;
      }
      case Log.LEVEL_TRACE:
      {
        log.trace ("{}", message, ex);
        break;
      }
    }
  }
}
