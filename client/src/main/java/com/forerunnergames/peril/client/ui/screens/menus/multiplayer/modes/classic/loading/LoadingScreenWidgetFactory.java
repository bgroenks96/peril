package com.forerunnergames.peril.client.ui.screens.menus.multiplayer.modes.classic.loading;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;

import com.forerunnergames.peril.client.settings.AssetSettings;
import com.forerunnergames.peril.client.ui.widgets.WidgetFactory;

public final class LoadingScreenWidgetFactory extends WidgetFactory
{
  public LoadingScreenWidgetFactory (final AssetManager assetManager)
  {
    super (assetManager);
  }

  public Image createBackground ()
  {
    return new Image (getAsset (AssetSettings.LOADING_SCREEN_BACKGROUND_ASSET_DESCRIPTOR));
  }

  public ProgressBar createProgressBar (final float stepSize)
  {
    return createHorizontalProgressBar (0.0f, 1.0f, stepSize, "default-horizontal");
  }
}
