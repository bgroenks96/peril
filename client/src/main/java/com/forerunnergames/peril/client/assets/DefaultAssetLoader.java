package com.forerunnergames.peril.client.assets;

import com.badlogic.gdx.assets.AssetManager;

import com.forerunnergames.peril.client.settings.AssetSettings;
import com.forerunnergames.tools.common.Arguments;

public final class DefaultAssetLoader implements AssetLoader
{
  private final AssetManager assetManager;

  public DefaultAssetLoader (final AssetManager assetManager)
  {
    Arguments.checkIsNotNull (assetManager, "assetManager");

    this.assetManager = assetManager;
  }

  @Override
  public void queueAssets ()
  {
    // @formatter:off

    // Shared
    // TODO Load during the initial loading screen.
    assetManager.load (AssetSettings.NORMAL_CURSOR_ASSET_DESCRIPTOR);
    assetManager.load (AssetSettings.QUIT_POPUP_BACKGROUND_ASSET_DESCRIPTOR);
    assetManager.load (AssetSettings.SKIN_JSON_ASSET_DESCRIPTOR);

    // Loading Screen
    assetManager.load (AssetSettings.LOADING_SCREEN_BACKGROUND_ASSET_DESCRIPTOR);

    // Menus
    // TODO Load during the initial loading screen.
    // TODO Unload during join/create game loading screen.
    assetManager.load (AssetSettings.MENU_ATLAS_ASSET_DESCRIPTOR);
    assetManager.load (AssetSettings.MENU_MUSIC_ASSET_DESCRIPTOR);

    // Play Screen
    // TODO Load during the join/create game loading screen.
    // TODO Unload during menu loading screen.
    assetManager.load (AssetSettings.PLAY_SCREEN_MUSIC_ASSET_DESCRIPTOR);

    // Classic Mode Play Screen
    // TODO Load during the join/create game loading screen.
    // TODO Unload during menu loading screen.
    assetManager.load (AssetSettings.CLASSIC_MODE_PLAY_SCREEN_BACKGROUND_ASSET_DESCRIPTOR);
    assetManager.load (AssetSettings.CLASSIC_MODE_PLAY_SCREEN_ARMY_MOVEMENT_POPUP_BACKGROUND_ASSET_DESCRIPTOR);
    assetManager.load (AssetSettings.CLASSIC_MODE_PLAY_SCREEN_ARMY_MOVEMENT_POPUP_FOREGROUND_ASSET_DESCRIPTOR);
    assetManager.load (AssetSettings.CLASSIC_MODE_PLAY_SCREEN_ARMY_MOVEMENT_FOREGROUND_ARROW_TEXT_ASSET_DESCRIPTOR);
    assetManager.load (AssetSettings.CLASSIC_MODE_PLAY_SCREEN_ARMY_MOVEMENT_OCCUPATION_TITLE_ASSET_DESCRIPTOR);

    // Peril Mode Play Screen
    // TODO Load during the join/create game loading screen.
    // TODO Unload during menu loading screen.
    assetManager.load (AssetSettings.PERIL_MODE_ATLAS_ASSET_DESCRIPTOR);

    // TODO Remove after implementing loading screens for asynchronous loading (to be able to show loading progress).
    assetManager.finishLoading ();

    // @formatter:on
  }

  @Override
  public void loadQueuedAssets ()
  {
    assetManager.update ();
  }

  @Override
  public void disposeAssets ()
  {
    assetManager.dispose ();
  }
}
