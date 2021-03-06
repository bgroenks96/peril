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

package com.forerunnergames.peril.common.playmap;

public final class PlayMapLoadingException extends RuntimeException
{
  public PlayMapLoadingException (final String message)
  {
    super (message);
  }

  public PlayMapLoadingException (final String message, final Throwable cause)
  {
    super (message, cause);
  }

  public PlayMapLoadingException (final Throwable cause)
  {
    super (cause);
  }
}
