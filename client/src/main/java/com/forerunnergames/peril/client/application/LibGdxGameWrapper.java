/*
 * Copyright © 2013 - 2017 Forerunner Games, LLC.
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

package com.forerunnergames.peril.client.application;

import com.badlogic.gdx.Game;

import com.forerunnergames.tools.common.Application;
import com.forerunnergames.tools.common.Arguments;

/**
 * Wraps the actual {@link Application} instance inside of a {@link Game}, which implements
 * {@link com.badlogic.gdx.ApplicationListener} because all of the executable sub-projects (android, desktop, & ios)
 * must be passed an {@link com.badlogic.gdx.ApplicationListener} instance.
 * <p/>
 * In other words, LibGDX demands ultimate control over the client application, so the best way to deal with that is to
 * wrap & delegate to the actual {@link Application}.
 */
public final class LibGdxGameWrapper extends Game
{
  private final Application application;

  LibGdxGameWrapper (final Application application)
  {
    Arguments.checkIsNotNull (application, "application");

    this.application = application;
  }

  @Override
  public void create ()
  {
    application.initialize ();
  }

  @Override
  public void dispose ()
  {
    super.dispose ();

    application.shutDown ();
  }

  @Override
  public void render ()
  {
    super.render ();

    application.update ();
  }
}
