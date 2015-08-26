package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Stage;

import com.forerunnergames.peril.client.settings.AssetSettings;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.actors.PlayMapActorFactory;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.widgets.MandatoryOccupationPopup;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.widgets.SideBar;
import com.forerunnergames.peril.client.ui.widgets.WidgetFactory;
import com.forerunnergames.peril.client.ui.widgets.popup.PopupListener;
import com.forerunnergames.peril.common.map.MapMetadata;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;

import net.engio.mbassy.bus.MBassador;

public final class ClassicModePlayScreenWidgetFactory extends WidgetFactory
{
  private final PlayMapActorFactory playMapActorFactory;

  public ClassicModePlayScreenWidgetFactory (final AssetManager assetManager,
                                             final PlayMapActorFactory playMapActorFactory)
  {
    super (assetManager);

    Arguments.checkIsNotNull (playMapActorFactory, "playMapActorFactory");

    this.playMapActorFactory = playMapActorFactory;
  }

  public void destroyPlayMapActor (final MapMetadata mapMetadata)
  {
    Arguments.checkIsNotNull (mapMetadata, "mapMetadata");

    playMapActorFactory.destroy (mapMetadata);
  }

  public Actor createSideBar (final MBassador <Event> eventBus)
  {
    Arguments.checkIsNotNull (eventBus, "eventBus");

    return new SideBar (this, eventBus);
  }

  public Actor createSideBarIcon (final SideBar.IconType iconType, final EventListener listener)
  {
    Arguments.checkIsNotNull (iconType, "iconType");
    Arguments.checkIsNotNull (listener, "listener");

    return createImageButton (iconType.getStyleName (), listener);
  }

  public MandatoryOccupationPopup createMandatoryOccupationPopup (final Stage stage,
                                                                  final MBassador <Event> eventBus,
                                                                  final PopupListener listener)
  {
    Arguments.checkIsNotNull (stage, "stage");
    Arguments.checkIsNotNull (eventBus, "eventBus");
    Arguments.checkIsNotNull (listener, "listener");

    return new MandatoryOccupationPopup (getSkin (), stage, getAssetManager (), eventBus, listener);
  }

  public Texture createBackground ()
  {
    return getAsset (AssetSettings.CLASSIC_MODE_PLAY_SCREEN_BACKGROUND_ASSET_DESCRIPTOR);
  }
}
