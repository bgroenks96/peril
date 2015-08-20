package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.widgets;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;

import com.forerunnergames.peril.client.settings.AssetSettings;
import com.forerunnergames.peril.client.settings.GraphicsSettings;
import com.forerunnergames.peril.client.settings.PlayMapSettings;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.actors.CountryActor;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.actors.CountryArmyTextActor;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.actors.DefaultCountryArmyTextActor;
import com.forerunnergames.peril.client.ui.widgets.CellPadding;
import com.forerunnergames.peril.client.ui.widgets.Widgets;
import com.forerunnergames.peril.client.ui.widgets.popup.PopupListener;
import com.forerunnergames.peril.client.events.DefaultStatusMessageEvent;
import com.forerunnergames.peril.core.shared.net.events.server.notification.CountryArmiesChangedEvent;
import com.forerunnergames.peril.client.messages.DefaultStatusMessage;
import com.forerunnergames.peril.core.shared.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.common.Strings;

import com.google.common.collect.ImmutableSet;

import javax.annotation.Nullable;

import net.engio.mbassy.bus.MBassador;

public class MandatoryOccupationPopup extends Dialog
{
  private static final float COUNTRY_BOX_WIDTH = 400;
  private static final float COUNTRY_BOX_HEIGHT = 200;
  private static final float COUNTRY_BOX_INNER_PADDING = 3;
  private static final float SOURCE_COUNTRY_ARROW_WIDTH = 56;
  private static final float DESTINATION_COUNTRY_ARROW_WIDTH = 64;
  private static final float SOURCE_COUNTRY_BOX_DESTINATION_COUNTRY_BOX_SPACING = 12;
  private static final Vector2 FOREGROUND_ARROW_TEXT_BOTTOM_LEFT_POPUP_REFERENCE_SPACE = new Vector2 (368, 255);
  private static final Vector2 FOREGROUND_ARROW_TEXT_SIZE_POPUP_REFERENCE_SPACE = new Vector2 (94, 14);
  private static final String WINDOW_STYLE_NAME_JSON = "dialog";
  private static final Vector2 POSITION_UPPER_LEFT_REFERENCE_SCREEN_SPACE = new Vector2 (494, 172);
  private static final Vector2 SIZE_REFERENCE_SCREEN_SPACE = new Vector2 (836, 468);
  private static final int BORDER_THICKNESS_PIXELS = 12;
  private static final boolean IS_RESIZABLE = false;
  private static final boolean IS_MOVABLE = true;
  private static final boolean IS_SLIDER_VERTICAL = false;
  private static final int SLIDER_STEP_SIZE = 1;
  private static final float INITIAL_BUTTON_REPEAT_DELAY_SECONDS = 0.5f;
  private static final float BUTTON_REPEAT_RATE_SECONDS = 0.05f;
  private final Vector2 tempPosition = new Vector2 ();
  private final Vector2 tempScaling = new Vector2 ();
  private final Vector2 tempSize = new Vector2 ();
  private final Color tempColor = new Color ();
  private final BitmapFont countryArmyTextFont = new BitmapFont ();
  private final CountryArmyTextActor sourceCountryArmyTextActor = new DefaultCountryArmyTextActor (countryArmyTextFont);
  private final CountryArmyTextActor destinationCountryArmyTextActor = new DefaultCountryArmyTextActor (
          countryArmyTextFont);
  private final Stage stage;
  private final AssetManager assetManager;
  private final MBassador <Event> eventBus;
  private final PopupListener listener;
  private Image title;
  private Drawable foregroundArrow;
  private Drawable foregroundArrowText;
  private Label sourceCountryNameLabel;
  private Label destinationCountryNameLabel;
  private ImageButton minusButton;
  private ImageButton plusButton;
  private ImageButton minButton;
  private ImageButton maxButton;
  private TextButton okButton;
  private Cell <Stack> sourceCountryStackCell;
  private Cell <Stack> destinationCountryStackCell;
  private float minusButtonPressTimeSeconds = 0.0f;
  private float plusButtonPressTimeSeconds = 0.0f;
  private float minusButtonRepeatDeltaSeconds = 0.0f;
  private float plusButtonRepeatDeltaSeconds = 0.0f;
  private boolean isShown = false;
  private int totalArmies = 0;
  private Slider slider;
  private Stack sourceCountryStack;
  private Stack destinationCountryStack;

  public MandatoryOccupationPopup (final Skin skin,
                                   final Stage stage,
                                   final AssetManager assetManager,
                                   final MBassador <Event> eventBus,
                                   final PopupListener listener)
  {
    super ("", skin, WINDOW_STYLE_NAME_JSON);

    Arguments.checkIsNotNull (skin, "skin");
    Arguments.checkIsNotNull (stage, "stage");
    Arguments.checkIsNotNull (assetManager, "assetManager");
    Arguments.checkIsNotNull (eventBus, "eventBus");
    Arguments.checkIsNotNull (listener, "listener");

    this.stage = stage;
    this.assetManager = assetManager;
    this.eventBus = eventBus;
    this.listener = listener;

    createWidgets (skin);
    configureWindow ();
    configureInput ();
    addBackgroundImage ();
    addTitleImage ();
    addCountryNameContainers ();
    addCountryImageContainers ();
    addButtons ();
  }

  @Override
  public void hide (final Action action)
  {
    if (!isShown) return;

    super.hide (action);

    isShown = false;

    listener.onHide ();
  }

  @Override
  public void hide ()
  {
    if (!isShown) return;

    super.hide (null);

    isShown = false;

    listener.onHide ();
  }

  @Override
  protected void result (@Nullable final Object object)
  {
    final int deltaArmies = getSliderValue ();
    final String sourceCountryName = getSourceCountryName ();
    final String destinationCountryName = getDestinationCountryName ();

    // TODO Production: Remove
    eventBus.publish (new DefaultStatusMessageEvent (
            new DefaultStatusMessage ("You occupied " + destinationCountryName + " with "
                    + Strings.pluralize (deltaArmies, "army", "armies") + " from " + sourceCountryName + "."),
            ImmutableSet.<PlayerPacket> of ()));

    // TODO Production: Remove
    eventBus.publish (new CountryArmiesChangedEvent (sourceCountryName, -deltaArmies));

    // TODO Production: Remove
    eventBus.publish (new CountryArmiesChangedEvent (destinationCountryName, deltaArmies));

    // TODO: Production: Publish event (OccupyCountryRequestEvent?)

    listener.onSubmit ();
  }

  @Override
  public void act (final float delta)
  {
    super.act (delta);

    minusButtonPressTimeSeconds = minusButton.isPressed () ? minusButtonPressTimeSeconds + delta : 0.0f;
    plusButtonPressTimeSeconds = plusButton.isPressed () ? plusButtonPressTimeSeconds + delta : 0.0f;

    // @formatter:off
    minusButtonRepeatDeltaSeconds = minusButtonPressTimeSeconds >= INITIAL_BUTTON_REPEAT_DELAY_SECONDS ? minusButtonRepeatDeltaSeconds + delta : 0.0f;
    plusButtonRepeatDeltaSeconds = plusButtonPressTimeSeconds >= INITIAL_BUTTON_REPEAT_DELAY_SECONDS ? plusButtonRepeatDeltaSeconds + delta : 0.0f;
    // @formatter:on

    if (minusButtonRepeatDeltaSeconds >= BUTTON_REPEAT_RATE_SECONDS)
    {
      decrementSlider ();
      minusButtonRepeatDeltaSeconds = 0.0f;
    }

    if (plusButtonRepeatDeltaSeconds >= BUTTON_REPEAT_RATE_SECONDS)
    {
      incrementSlider ();
      plusButtonRepeatDeltaSeconds = 0.0f;
    }
  }

  @Override
  public void draw (final Batch batch, final float parentAlpha)
  {
    super.draw (batch, parentAlpha);

    stageToLocalCoordinates (tempSize.set (getWidth (), getHeight ()));
    tempColor.set (getColor ());
    batch.setColor (tempColor.r, tempColor.g, tempColor.b, tempColor.a * parentAlpha);
    foregroundArrow.draw (batch, getX (), getY (), getX () + tempSize.x, getY () + tempSize.y);
    foregroundArrowText.draw (batch, getX () + FOREGROUND_ARROW_TEXT_BOTTOM_LEFT_POPUP_REFERENCE_SPACE.x,
                              getY () + FOREGROUND_ARROW_TEXT_BOTTOM_LEFT_POPUP_REFERENCE_SPACE.y,
                              FOREGROUND_ARROW_TEXT_SIZE_POPUP_REFERENCE_SPACE.x,
                              FOREGROUND_ARROW_TEXT_SIZE_POPUP_REFERENCE_SPACE.y);
  }

  public void show (final int minDestinationArmies,
                    final int maxDestinationArmies,
                    final CountryActor sourceCountryActor,
                    final CountryActor destinationCountryActor,
                    final int totalArmies)
  {
    Arguments.checkIsNotNegative (minDestinationArmies, "minDestinationArmies");
    Arguments.checkIsNotNegative (maxDestinationArmies, "maxDestinationArmies");
    Arguments.checkIsNotNull (sourceCountryActor, "sourceCountryActor");
    Arguments.checkIsNotNull (destinationCountryActor, "destinationCountryActor");
    Arguments.checkIsNotNegative (totalArmies, "totalArmies");
    Arguments.checkUpperInclusiveBound (minDestinationArmies, maxDestinationArmies, "minDestinationArmies",
                                        "maxDestinationArmies");
    Arguments.checkUpperInclusiveBound (minDestinationArmies, totalArmies, "minDestinationArmies", "totalArmies");
    Arguments.checkUpperInclusiveBound (maxDestinationArmies, totalArmies, "maxDestinationArmies", "totalArmies");

    if (isShown) return;

    this.totalArmies = totalArmies;

    setSliderRange (minDestinationArmies, maxDestinationArmies);
    setSliderToMinValue ();
    setCountries (sourceCountryActor, destinationCountryActor);
    setPosition (POSITION_UPPER_LEFT_REFERENCE_SCREEN_SPACE.x, GraphicsSettings.REFERENCE_SCREEN_HEIGHT - getHeight ()
            - POSITION_UPPER_LEFT_REFERENCE_SCREEN_SPACE.y);
    show (stage, null);

    isShown = true;

    listener.onShow ();
  }

  public void keyDownRepeating (final int keyCode)
  {
    if (!isShown) return;

    switch (keyCode)
    {
      case Input.Keys.LEFT:
      {
        decrementSlider ();

        break;
      }
      case Input.Keys.RIGHT:
      {
        incrementSlider ();

        break;
      }
    }
  }

  private static float calculateCountryImagePadding (final Image countryImagePostLayout, final float arrowWidth)
  {
    return Math.max (0.0f, Math.min (arrowWidth, arrowWidth
            - (COUNTRY_BOX_WIDTH - (COUNTRY_BOX_INNER_PADDING * 2.0f) - countryImagePostLayout.getImageWidth ())));
  }

  private static Image asImage (final CountryActor countryActor)
  {
    return new Image (countryActor.getCurrentPrimaryDrawable (), Scaling.none);
  }

  private void createWidgets (final Skin skin)
  {
    title = new Image (
            assetManager.get (AssetSettings.CLASSIC_MODE_PLAY_SCREEN_ARMY_MOVEMENT_OCCUPATION_TITLE_ASSET_DESCRIPTOR));

    foregroundArrow = new TextureRegionDrawable (new TextureRegion (
            assetManager.get (AssetSettings.CLASSIC_MODE_PLAY_SCREEN_ARMY_MOVEMENT_POPUP_FOREGROUND_ASSET_DESCRIPTOR)));

    foregroundArrowText = new TextureRegionDrawable (new TextureRegion (assetManager
            .get (AssetSettings.CLASSIC_MODE_PLAY_SCREEN_ARMY_MOVEMENT_FOREGROUND_ARROW_TEXT_ASSET_DESCRIPTOR)));

    slider = new Slider (0, 0, SLIDER_STEP_SIZE, IS_SLIDER_VERTICAL, skin);
    sourceCountryNameLabel = new Label (null, skin);
    destinationCountryNameLabel = new Label (null, skin);
    okButton = new TextButton ("OK", skin.get ("popup", TextButton.TextButtonStyle.class));
    minButton = new ImageButton (skin.get ("min", ImageButton.ImageButtonStyle.class));
    minusButton = new ImageButton (skin.get ("minus", ImageButton.ImageButtonStyle.class));
    plusButton = new ImageButton (skin.get ("plus", ImageButton.ImageButtonStyle.class));
    maxButton = new ImageButton (skin.get ("max", ImageButton.ImageButtonStyle.class));
    sourceCountryStack = new Stack ();
    destinationCountryStack = new Stack ();
  }

  private void configureWindow ()
  {
    setResizable (IS_RESIZABLE);
    setMovable (IS_MOVABLE);
    setSize (SIZE_REFERENCE_SCREEN_SPACE.x, SIZE_REFERENCE_SCREEN_SPACE.y);
    pad (BORDER_THICKNESS_PIXELS);
  }

  private void addBackgroundImage ()
  {
    setBackground (new TextureRegionDrawable (new TextureRegion (assetManager
            .get (AssetSettings.CLASSIC_MODE_PLAY_SCREEN_ARMY_MOVEMENT_POPUP_BACKGROUND_ASSET_DESCRIPTOR))));
  }

  private void addTitleImage ()
  {
    title.setScaling (Scaling.none);
    getCell (getContentTable ()).space (12);
    getContentTable ().bottom ().left ();
    getContentTable ().row ().colspan (2).height (44);
    getContentTable ().add (title);
  }

  private void addCountryNameContainers ()
  {
    sourceCountryNameLabel.setAlignment (Align.center);
    destinationCountryNameLabel.setAlignment (Align.center);
    getContentTable ().row ().spaceTop (12).spaceBottom (4).spaceLeft (12).spaceRight (12).height (34);
    getContentTable ().add (sourceCountryNameLabel).width (400);
    getContentTable ().add (destinationCountryNameLabel).width (400);
  }

  private void addCountryImageContainers ()
  {
    getContentTable ().row ().spaceTop (4).height (200);

    final Table sourceCountryStackTable = new Table ();
    sourceCountryStackCell = sourceCountryStackTable.add (sourceCountryStack).padRight (SOURCE_COUNTRY_ARROW_WIDTH);

    final Table destinationCountryStackTable = new Table ();
    destinationCountryStackCell = destinationCountryStackTable.add (destinationCountryStack)
            .padLeft (DESTINATION_COUNTRY_ARROW_WIDTH);

    final Table sourceCountryTable = new Table ();
    sourceCountryTable.add (sourceCountryStackTable);
    sourceCountryTable.setClip (true);

    final Table destinationCountryTable = new Table ();
    destinationCountryTable.add (destinationCountryStackTable);
    destinationCountryTable.setClip (true);

    final Table countryTable = new Table ();
    countryTable.add (sourceCountryTable).width (COUNTRY_BOX_WIDTH).maxHeight (COUNTRY_BOX_HEIGHT)
            .spaceRight (SOURCE_COUNTRY_BOX_DESTINATION_COUNTRY_BOX_SPACING).pad (COUNTRY_BOX_INNER_PADDING);
    countryTable.add (destinationCountryTable).width (COUNTRY_BOX_WIDTH).maxHeight (COUNTRY_BOX_HEIGHT)
            .spaceLeft (SOURCE_COUNTRY_BOX_DESTINATION_COUNTRY_BOX_SPACING).pad (COUNTRY_BOX_INNER_PADDING);

    getContentTable ().add (countryTable).colspan (2);
  }

  private void addButtons ()
  {
    final Table buttonTable = getButtonTable ();

    getCell (buttonTable).height (140).space (12).expandX ().fill ();

    buttonTable.defaults ().space (0).pad (0);
    buttonTable.center ();
    buttonTable.add (minButton).width (20).spaceRight (7);
    buttonTable.add (minusButton).width (30).spaceLeft (7).spaceRight (10);
    buttonTable.add (slider).width (382).height (12).spaceLeft (10).spaceRight (10);
    buttonTable.add (plusButton).width (30).spaceLeft (10).spaceRight (7);
    buttonTable.add (maxButton).width (20).spaceLeft (7);
    buttonTable.row ().spaceTop (40);
    buttonTable.add ().colspan (2);
    buttonTable.add (okButton).width (44);
  }

  private void configureInput ()
  {
    configureButtonInput ();
    configureKeyboardInput ();
    makeOkButtonCallResultMethod ();
  }

  private void makeOkButtonCallResultMethod ()
  {
    setObject (okButton, null);
  }

  private void configureButtonInput ()
  {
    slider.addListener (new ChangeListener ()
    {
      @Override
      public void changed (final ChangeEvent event, final Actor actor)
      {
        updateCountryArmies ();
      }
    });

    minButton.addListener (new InputListener ()
    {
      @Override
      public boolean touchDown (final InputEvent event,
                                final float x,
                                final float y,
                                final int pointer,
                                final int button)
      {
        setSliderToMinValue ();
        return true;
      }
    });

    minusButton.addListener (new InputListener ()
    {
      @Override
      public boolean touchDown (final InputEvent event,
                                final float x,
                                final float y,
                                final int pointer,
                                final int button)
      {
        decrementSlider ();
        return true;
      }
    });

    plusButton.addListener (new InputListener ()
    {
      @Override
      public boolean touchDown (final InputEvent event,
                                final float x,
                                final float y,
                                final int pointer,
                                final int button)
      {
        incrementSlider ();
        return true;
      }
    });

    maxButton.addListener (new InputListener ()
    {
      @Override
      public boolean touchDown (final InputEvent event,
                                final float x,
                                final float y,
                                final int pointer,
                                final int button)
      {
        setSliderToMaxValue ();
        return true;
      }
    });
  }

  private void updateCountryArmies ()
  {
    updateSourceCountryArmies ();
    updateDestinationCountryArmies ();
  }

  private void updateSourceCountryArmies ()
  {
    setSourceCountryArmies (totalArmies - getSliderValue ());
  }

  private int getSliderValue ()
  {
    return Math.round (slider.getValue ());
  }

  private void setSourceCountryArmies (final int armies)
  {
    sourceCountryArmyTextActor.setArmies (armies);
  }

  private void updateDestinationCountryArmies ()
  {
    setDestinationCountryArmies (getSliderValue ());
  }

  private void setDestinationCountryArmies (final int armies)
  {
    destinationCountryArmyTextActor.setArmies (armies);
  }

  private void setSliderToMinValue ()
  {
    slider.setValue (slider.getMinValue ());
  }

  private void setSliderToMaxValue ()
  {
    slider.setValue (slider.getMaxValue ());
  }

  private void decrementSlider ()
  {
    slider.setValue (slider.getValue () - slider.getStepSize ());
  }

  private void incrementSlider ()
  {
    slider.setValue (slider.getValue () + slider.getStepSize ());
  }

  private void configureKeyboardInput ()
  {
    addListener (new InputListener ()
    {
      @Override
      public boolean keyDown (final InputEvent event, final int keycode)
      {
        if (!isShown) return false;

        switch (keycode)
        {
          case Input.Keys.ENTER:
          {
            hide (new Action ()
            {
              @Override
              public boolean act (final float delta)
              {
                result (null);

                return true;
              }
            });

            return true;
          }
          case Input.Keys.LEFT:
          {
            decrementSlider ();

            return true;
          }
          case Input.Keys.RIGHT:
          {
            incrementSlider ();

            return true;
          }
          default:
          {
            return false;
          }
        }
      }
    });
  }

  private String getSourceCountryName ()
  {
    return sourceCountryNameLabel.getText ().toString ();
  }

  private String getDestinationCountryName ()
  {
    return destinationCountryNameLabel.getText ().toString ();
  }

  private void setCountries (final CountryActor sourceCountryActor, final CountryActor destinationCountryActor)
  {
    setCountryNames (sourceCountryActor, destinationCountryActor);
    setCountryImages (sourceCountryActor, destinationCountryActor);
    updateCountryArmies ();
  }

  private void setCountryNames (final CountryActor sourceCountryActor, final CountryActor destinationCountryActor)
  {
    setCountryNames (sourceCountryActor.asActor ().getName (), destinationCountryActor.asActor ().getName ());
  }

  private void setCountryImages (final CountryActor sourceCountryActor, final CountryActor destinationCountryActor)
  {
    setCountryImage (sourceCountryActor, sourceCountryArmyTextActor, sourceCountryStack, sourceCountryStackCell,
                     SOURCE_COUNTRY_ARROW_WIDTH, CellPadding.LEFT);

    setCountryImage (destinationCountryActor, destinationCountryArmyTextActor, destinationCountryStack,
                     destinationCountryStackCell, DESTINATION_COUNTRY_ARROW_WIDTH, CellPadding.RIGHT);
  }

  private void setCountryImage (final CountryActor countryActor,
                                final CountryArmyTextActor countryArmyTextActor,
                                final Stack countryStack,
                                final Cell <Stack> countryStackCell,
                                final float countryArrowWidth,
                                final CellPadding paddingType)
  {
    final Image countryImage = asImage (countryActor);

    countryStack.clear ();
    countryStack.add (countryImage);
    countryStack.add (countryArmyTextActor.asActor ());

    getContentTable ().layout ();

    Widgets.padCell (countryStackCell, calculateCountryImagePadding (countryImage, countryArrowWidth), paddingType);

    countryStackCell.getTable ().invalidateHierarchy ();

    getContentTable ().layout ();

    updateCountryArmyCircle (countryArmyTextActor, countryActor, countryImage);
  }

  private void updateCountryArmyCircle (final CountryArmyTextActor countryArmyTextActor,
                                        final CountryActor countryActor,
                                        final Image countryImage)
  {
    setCountryArmyCircleSize (countryArmyTextActor, countryActor, countryImage);
    setCountryArmyCirclePosition (countryArmyTextActor, countryActor, countryImage);
  }

  private void setCountryArmyCircleSize (final CountryArmyTextActor countryArmyTextActor,
                                         final CountryActor countryActor,
                                         final Image countryImage)
  {
    countryArmyTextActor
            .setCircleSize (calculateCountryArmyTextCircleSizeActualCountrySpace (countryActor, countryImage));
  }

  private void setCountryArmyCirclePosition (final CountryArmyTextActor countryArmyTextActor,
                                             final CountryActor countryActor,
                                             final Image countryImage)
  {
    countryArmyTextActor
            .setCircleTopLeft (calculateCountryArmyTextCircleTopLeftActualCountrySpace (countryActor, countryImage));
  }

  private Vector2 calculateCountryArmyTextCircleTopLeftActualCountrySpace (final CountryActor countryActor,
                                                                           final Image countryImagePostLayout)
  {
    return tempPosition.set (countryActor.getReferenceTextUpperLeft ()).sub (countryActor.getReferenceDestination ())
            .set (Math.abs (tempPosition.x), Math.abs (tempPosition.y))
            .scl (calculateCountryImageScaling (countryActor, countryImagePostLayout))
            .add (countryImagePostLayout.getImageX (), countryImagePostLayout.getImageY ());
  }

  private Vector2 calculateCountryArmyTextCircleSizeActualCountrySpace (final CountryActor countryActor,
                                                                        final Image countryImagePostLayout)
  {
    return tempSize.set (PlayMapSettings.COUNTRY_ARMY_CIRCLE_SIZE_REFERENCE_PLAY_MAP_SPACE)
            .scl (calculateCountryImageScaling (countryActor, countryImagePostLayout));
  }

  private Vector2 calculateCountryImageScaling (final CountryActor countryActor, final Image countryImagePostLayout)
  {
    return tempScaling.set (countryImagePostLayout.getImageWidth () / countryActor.getReferenceWidth (),
                            countryImagePostLayout.getImageHeight () / countryActor.getReferenceHeight ());
  }

  private void setSliderRange (final int minValue, final int maxValue)
  {
    slider.setRange (minValue, maxValue);
  }

  private void setCountryNames (final String sourceCountryName, final String destinationCountryName)
  {
    sourceCountryNameLabel.setText (sourceCountryName);
    destinationCountryNameLabel.setText (destinationCountryName);
  }
}
