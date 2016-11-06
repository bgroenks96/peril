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

package com.forerunnergames.peril.common.application;

import com.forerunnergames.tools.common.Application;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.controllers.CompositeController;
import com.forerunnergames.tools.common.controllers.Controller;
import com.forerunnergames.tools.common.controllers.ControllerAdapter;

import com.google.common.collect.ImmutableCollection;

public class DefaultApplication extends ControllerAdapter implements Application
{
  private final CompositeController compositeController;

  public DefaultApplication (final Controller... controllers)
  {
    Arguments.checkIsNotNull (controllers, "controllers");
    Arguments.checkHasNoNullElements (controllers, "controllers");

    compositeController = new CompositeController (controllers);
  }

  @Override
  public void initialize ()
  {
    compositeController.initialize ();
  }

  @Override
  public void update ()
  {
    compositeController.update ();
  }

  @Override
  public boolean shouldShutDown ()
  {
    return compositeController.shouldShutDown ();
  }

  @Override
  public void shutDown ()
  {
    compositeController.shutDown ();
  }

  @Override
  public void add (final Controller controller)
  {
    Arguments.checkIsNotNull (controller, "controller");

    compositeController.add (controller);
  }

  @Override
  public void remove (final Controller controller)
  {
    Arguments.checkIsNotNull (controller, "controller");

    compositeController.remove (controller);
  }

  public void removeController (final String name)
  {
    Arguments.checkIsNotNull (name, "name");

    compositeController.remove (name);
  }

  public Controller getController (final String name)
  {
    Arguments.checkIsNotNull (name, "name");

    return compositeController.get (name);
  }

  public ImmutableCollection <Controller> getAllControllers ()
  {
    return compositeController.getAll ();
  }
}
