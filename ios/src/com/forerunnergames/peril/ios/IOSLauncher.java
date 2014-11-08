package com.forerunnergames.peril.ios;

import com.badlogic.gdx.backends.iosrobovm.IOSApplication;
import com.badlogic.gdx.backends.iosrobovm.IOSApplicationConfiguration;

import com.forerunnergames.peril.client.application.ClientApplicationFactory;

import org.robovm.apple.foundation.NSAutoreleasePool;
import org.robovm.apple.uikit.UIApplication;

public final class IOSLauncher extends IOSApplication.Delegate
{
  @Override
  protected IOSApplication createApplication()
  {
    return new IOSApplication (ClientApplicationFactory.create(), new IOSApplicationConfiguration());
  }

  public static void main (final String... args)
  {
    final NSAutoreleasePool pool = new NSAutoreleasePool();
    UIApplication.main (args, null, IOSLauncher.class);
    pool.close();
  }
}