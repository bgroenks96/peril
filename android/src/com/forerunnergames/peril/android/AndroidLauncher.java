package com.forerunnergames.peril.android;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import com.forerunnergames.peril.client.application.LibGdxGameFactory;

import android.os.Bundle;

public final class AndroidLauncher extends AndroidApplication
{
	@Override
	protected void onCreate (final Bundle savedInstanceState)
  {
		super.onCreate (savedInstanceState);

		initialize (LibGdxGameFactory.create(), new AndroidApplicationConfiguration());
	}
}
