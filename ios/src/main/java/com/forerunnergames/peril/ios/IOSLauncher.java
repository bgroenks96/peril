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

package com.forerunnergames.peril.ios;

import com.badlogic.gdx.backends.iosrobovm.IOSApplication;
import com.badlogic.gdx.backends.iosrobovm.IOSApplicationConfiguration;

import com.forerunnergames.peril.client.application.ClientApplicationProperties;
import com.forerunnergames.peril.client.application.LibGdxGameFactory;

import org.robovm.apple.foundation.NSAutoreleasePool;
import org.robovm.apple.uikit.UIApplication;

public final class IOSLauncher extends IOSApplication.Delegate
{
  @Override
  protected IOSApplication createApplication ()
  {
    ClientApplicationProperties.set ();

    return new IOSApplication (LibGdxGameFactory.create (), new IOSApplicationConfiguration ());
  }

  public static void main (final String... args)
  {
    final NSAutoreleasePool pool = new NSAutoreleasePool ();
    UIApplication.main (args, null, IOSLauncher.class);
    pool.close ();
  }
}
