package com.forerunnergames.peril.android;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import com.forerunnergames.peril.client.application.ClientApplicationProperties;
import com.forerunnergames.peril.client.application.LibGdxGameFactory;

public final class AndroidLauncher extends AndroidApplication
{
  @Override
  protected void onCreate (final Bundle savedInstanceState)
  {
    super.onCreate (savedInstanceState);

    new ClientApplicationProperties ();

    initialize (LibGdxGameFactory.create (), new AndroidApplicationConfiguration ());
  }
}
