/*
 * Copyright © 2011 - 2013 Aaron Mahan.
 * Copyright © 2013 - 2016 Forerunner Games, LLC.
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

package com.forerunnergames.peril.client.input;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;

import com.forerunnergames.tools.common.Arguments;

public final class LibGdxMouseInput implements MouseInput
{
  private final Input input;
  private final Vector2 temp = new Vector2 ();

  public LibGdxMouseInput (final Input input)
  {
    Arguments.checkIsNotNull (input, "input");

    this.input = input;
  }

  @Override
  public int x ()
  {
    return input.getX ();
  }

  @Override
  public int y ()
  {
    return input.getY ();
  }

  @Override
  public Vector2 position ()
  {
    return temp.set (input.getX (), input.getY ());
  }
}
