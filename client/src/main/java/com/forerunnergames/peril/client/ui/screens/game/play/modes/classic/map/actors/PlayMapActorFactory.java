package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.actors;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import com.forerunnergames.peril.client.input.MouseInput;
import com.forerunnergames.peril.client.ui.screens.ScreenSize;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.data.CountryImageData;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.data.CountryImageDataRepository;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.data.CountryImageDataRepositoryFactory;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.input.PlayMapInputDetection;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.input.PlayMapInputDetectionFactory;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.io.CountryAtlasesLoader;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.io.CountryImageDataLoader;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.io.CountryImagesLoader;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.io.DefaultCountryAtlasesLoader;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.io.DefaultPlayMapBackgroundImageLoader;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.io.PlayMapBackgroundImageLoader;
import com.forerunnergames.peril.core.model.io.ExternalStreamParserFactory;
import com.forerunnergames.peril.core.model.map.country.CountryName;
import com.forerunnergames.peril.core.shared.io.DataLoader;
import com.forerunnergames.peril.core.shared.io.StreamParserFactory;
import com.forerunnergames.peril.core.shared.map.MapMetadata;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;

import com.google.common.collect.ImmutableMap;

import net.engio.mbassy.bus.MBassador;

public final class PlayMapActorFactory
{
  private final MouseInput mouseInput;
  private final MBassador <Event> eventBus;
  private final CountryAtlasesLoader countryAtlasesLoader;
  private final PlayMapInputDetectionFactory playMapInputDetectionFactory;
  private final PlayMapBackgroundImageLoader playMapBackgroundImageLoader;

  public PlayMapActorFactory (final AssetManager assetManager,
                              final ScreenSize screenSize,
                              final MouseInput mouseInput,
                              final MBassador <Event> eventBus)
  {
    Arguments.checkIsNotNull (assetManager, "assetManager");
    Arguments.checkIsNotNull (screenSize, "screenSize");
    Arguments.checkIsNotNull (mouseInput, "mouseInput");
    Arguments.checkIsNotNull (eventBus, "eventBus");

    this.mouseInput = mouseInput;
    this.eventBus = eventBus;
    countryAtlasesLoader = new DefaultCountryAtlasesLoader (assetManager);
    playMapInputDetectionFactory = new PlayMapInputDetectionFactory (assetManager, screenSize);
    playMapBackgroundImageLoader = new DefaultPlayMapBackgroundImageLoader (assetManager);
  }

  public PlayMapActor create (final MapMetadata mapMetadata)
  {
    Arguments.checkIsNotNull (mapMetadata, "mapMetadata");

    if (mapMetadata.equals (MapMetadata.NULL_MAP_METADATA)) return PlayMapActor.NULL_PLAY_MAP_ACTOR;

    // @formatter:off
    final StreamParserFactory streamParserFactory = new ExternalStreamParserFactory ();
    final DataLoader <CountryName, CountryImageData> countryImageDataLoader = new CountryImageDataLoader (streamParserFactory);
    final CountryImageDataRepositoryFactory countryImageDataRepositoryFactory = new CountryImageDataRepositoryFactory (countryImageDataLoader);
    final CountryImagesLoader countryImagesLoader = new CountryImagesLoader (mapMetadata, countryAtlasesLoader.load (mapMetadata));
    final CountryImageDataRepository countryImageDataRepository = countryImageDataRepositoryFactory.create (mapMetadata);
    final PlayMapInputDetection playMapInputDetection = playMapInputDetectionFactory.create (mapMetadata);
    final CountryActorFactory countryActorFactory = new CountryActorFactory (countryImagesLoader, countryImageDataRepository);
    final BitmapFont font = new BitmapFont ();
    final HoveredTerritoryTextActor hoveredTerritoryTextActor = new HoveredTerritoryTextActor (playMapInputDetection, mouseInput, font);
    final Image backgroundImage = playMapBackgroundImageLoader.load (mapMetadata);
    final ImmutableMap.Builder <CountryName, CountryActor> countryNamesToActorsBuilder = ImmutableMap.builder ();
    // @formatter:on

    for (final CountryName countryName : countryImageDataRepository.getCountryNames ())
    {
      countryNamesToActorsBuilder.put (countryName, countryActorFactory.create (countryName, font));
    }

    final PlayMapActor playMapActor = new DefaultPlayMapActor (countryNamesToActorsBuilder.build (),
            playMapInputDetection, hoveredTerritoryTextActor, backgroundImage, mapMetadata, eventBus);

    hoveredTerritoryTextActor.setPlayMapActor (playMapActor);

    return playMapActor;
  }

  public void destroy (final MapMetadata mapMetadata)
  {
    Arguments.checkIsNotNull (mapMetadata, "mapMetadata");

    if (mapMetadata.equals (MapMetadata.NULL_MAP_METADATA)) return;

    countryAtlasesLoader.unload (mapMetadata);
    playMapBackgroundImageLoader.unload (mapMetadata);
    playMapInputDetectionFactory.destroy (mapMetadata);
  }
}
