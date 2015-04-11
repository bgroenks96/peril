package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.widgets;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
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

import com.forerunnergames.peril.client.settings.GraphicsSettings;
import com.forerunnergames.peril.client.settings.PlayMapSettings;
import com.forerunnergames.peril.client.ui.Assets;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.actors.CountryActor;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.actors.CountryArmyTextActor;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.tools.CoordinateSpaces;
import com.forerunnergames.peril.client.ui.widgets.CellPadding;
import com.forerunnergames.peril.client.ui.widgets.Widgets;
import com.forerunnergames.peril.core.shared.net.events.defaults.DefaultStatusMessageEvent;
import com.forerunnergames.peril.core.shared.net.events.notification.CountryArmiesChangedEvent;
import com.forerunnergames.peril.core.shared.net.messages.DefaultStatusMessage;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.common.geometry.Geometry;
import com.forerunnergames.tools.common.geometry.Point2D;
import com.forerunnergames.tools.common.geometry.Scaling2D;
import com.forerunnergames.tools.common.geometry.Size2D;
import com.forerunnergames.tools.common.geometry.Translation2D;

import javax.annotation.Nullable;

import net.engio.mbassy.bus.MBassador;

public final class MandatoryOccupationPopup extends Dialog
{
  private static final float COUNTRY_BOX_WIDTH = 400;
  private static final float COUNTRY_BOX_HEIGHT = 200;
  private static final float COUNTRY_BOX_INNER_PADDING = 3;
  private static final float SOURCE_COUNTRY_ARROW_WIDTH = 56;
  private static final float DESTINATION_COUNTRY_ARROW_WIDTH = 64;
  private static final float SOURCE_COUNTRY_BOX_DESTINATION_COUNTRY_BOX_SPACING = 12;
  private static final Point2D FOREGROUND_ARROW_TEXT_BOTTOM_LEFT_POPUP_REFERENCE_SPACE = new Point2D (368, 255);
  private static final Size2D FOREGROUND_ARROW_TEXT_SIZE_POPUP_REFERENCE_SPACE = new Size2D (94, 14);
  private static final String WINDOW_STYLE_NAME_JSON = "dialog";
  private static final Point2D POSITION_UPPER_LEFT_REFERENCE_SCREEN_SPACE = new Point2D (494, 172);
  private static final Size2D SIZE_REFERENCE_SCREEN_SPACE = new Size2D (836, 468);
  private static final int BORDER_THICKNESS_PIXELS = 12;
  private static final boolean IS_RESIZABLE = false;
  private static final boolean IS_MOVABLE = true;
  private static final boolean IS_SLIDER_VERTICAL = false;
  private static final int SLIDER_STEP_SIZE = 1;
  private static final float INITIAL_BUTTON_REPEAT_DELAY_SECONDS = 0.5f;
  private static final float BUTTON_REPEAT_RATE_SECONDS = 0.05f;
  private final Vector2 tempSize = new Vector2 ();
  private final Color tempColor = new Color ();
  private final CountryArmyTextActor sourceCountryArmyTextActor = new CountryArmyTextActor ();
  private final CountryArmyTextActor destinationCountryArmyTextActor = new CountryArmyTextActor ();
  private final Stage stage;
  private final MBassador <Event> eventBus;
  private Image title;
  private Drawable foregroundArrow;
  private Drawable foregroundArrowText;
  private Label sourceCountryNameLabel;
  private Label destinationCountryNameLabel;
  private TextButton minusButton;
  private TextButton plusButton;
  private TextButton resetButton;
  private TextButton minButton;
  private TextButton maxButton;
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

  public MandatoryOccupationPopup (final Skin skin, final Stage stage, final MBassador <Event> eventBus)
  {
    super ("", skin, WINDOW_STYLE_NAME_JSON);

    Arguments.checkIsNotNull (skin, "skin");
    Arguments.checkIsNotNull (stage, "stage");
    Arguments.checkIsNotNull (eventBus, "eventBus");

    this.stage = stage;
    this.eventBus = eventBus;

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
  }

  @Override
  public void hide ()
  {
    if (!isShown) return;

    super.hide (null);

    isShown = false;
  }

  @Override
  protected void result (@Nullable final Object object)
  {
    final int deltaArmies = getSliderValue ();
    final String sourceCountryName = getSourceCountryName ();
    final String destinationCountryName = getDestinationCountryName ();

    // TODO Production: Remove
    eventBus.publish (new DefaultStatusMessageEvent (new DefaultStatusMessage ("You occupied " + destinationCountryName
            + " with " + Strings.pluralize (deltaArmies, "army", "armies") + " from " + sourceCountryName + ".")));

    // TODO Production: Remove
    eventBus.publish (new CountryArmiesChangedEvent (sourceCountryName, -deltaArmies));

    // TODO Production: Remove
    eventBus.publish (new CountryArmiesChangedEvent (destinationCountryName, deltaArmies));

    // TODO: Production: Publish event (OccupyCountryRequestEvent?)
  }

  @Override
  public void act (float delta)
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
    foregroundArrowText.draw (batch, getX () + FOREGROUND_ARROW_TEXT_BOTTOM_LEFT_POPUP_REFERENCE_SPACE.getX (), getY ()
                                      + FOREGROUND_ARROW_TEXT_BOTTOM_LEFT_POPUP_REFERENCE_SPACE.getY (),
                              FOREGROUND_ARROW_TEXT_SIZE_POPUP_REFERENCE_SPACE.getWidth (),
                              FOREGROUND_ARROW_TEXT_SIZE_POPUP_REFERENCE_SPACE.getHeight ());
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
    setPosition (POSITION_UPPER_LEFT_REFERENCE_SCREEN_SPACE.getX (), GraphicsSettings.REFERENCE_SCREEN_HEIGHT
            - getHeight () - POSITION_UPPER_LEFT_REFERENCE_SCREEN_SPACE.getY ());
    show (stage, null);

    isShown = true;
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

  private void createWidgets (final Skin skin)
  {
    title = new Image (Assets.armyMovementOccupationTitle);
    foregroundArrow = new TextureRegionDrawable (new TextureRegion (Assets.armyMovementForegroundArrow));
    foregroundArrowText = new TextureRegionDrawable (new TextureRegion (Assets.armyMovementForegroundArrowText));
    slider = new Slider (0, 0, SLIDER_STEP_SIZE, IS_SLIDER_VERTICAL, skin);
    sourceCountryNameLabel = new Label (null, skin);
    destinationCountryNameLabel = new Label (null, skin);
    okButton = new TextButton ("OK", skin.get (TextButton.TextButtonStyle.class));
    resetButton = new TextButton ("RESET", skin.get (TextButton.TextButtonStyle.class));
    minButton = new TextButton ("|<", skin.get (TextButton.TextButtonStyle.class));
    minusButton = new TextButton ("-", skin.get (TextButton.TextButtonStyle.class));
    plusButton = new TextButton ("+", skin.get (TextButton.TextButtonStyle.class));
    maxButton = new TextButton (">|", skin.get (TextButton.TextButtonStyle.class));
    sourceCountryStack = new Stack ();
    destinationCountryStack = new Stack ();
  }

  private void configureWindow ()
  {
    setResizable (IS_RESIZABLE);
    setMovable (IS_MOVABLE);
    setSize (SIZE_REFERENCE_SCREEN_SPACE.getWidth (), SIZE_REFERENCE_SCREEN_SPACE.getHeight ());
    pad (BORDER_THICKNESS_PIXELS);
  }

  private void addBackgroundImage ()
  {
    setBackground (new TextureRegionDrawable (new TextureRegion (Assets.armyMovementBackground)));
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

    getCell (buttonTable).width (812).height (138).space (12);

    buttonTable.add (minButton).size (22, 34).space (8);
    buttonTable.add (minusButton).size (34, 34).space (8);
    buttonTable.add (slider).width (624).height (12).space (8);
    buttonTable.add (plusButton).size (34, 34).space (8);
    buttonTable.add (maxButton).size (22, 34).space (8);
    buttonTable.row ().spaceTop (22);
    buttonTable.add ().colspan (2);
    buttonTable.add (resetButton).size (94, 38);
    buttonTable.add (okButton).size (55, 38).colspan (2).right ();
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

    resetButton.addListener (new InputListener ()
    {
      @Override
      public boolean touchDown (InputEvent event, float x, float y, int pointer, int button)
      {
        setSliderToMinValue ();
        return true;
      }
    });

    minButton.addListener (new InputListener ()
    {
      @Override
      public boolean touchDown (InputEvent event, float x, float y, int pointer, int button)
      {
        setSliderToMinValue ();
        return true;
      }
    });

    minusButton.addListener (new InputListener ()
    {
      @Override
      public boolean touchDown (InputEvent event, float x, float y, int pointer, int button)
      {
        decrementSlider ();
        return true;
      }
    });

    plusButton.addListener (new InputListener ()
    {
      @Override
      public boolean touchDown (InputEvent event, float x, float y, int pointer, int button)
      {
        incrementSlider ();
        return true;
      }
    });

    maxButton.addListener (new InputListener ()
    {
      @Override
      public boolean touchDown (InputEvent event, float x, float y, int pointer, int button)
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
    return (int) slider.getValue ();
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
      public boolean keyDown (InputEvent event, int keycode)
      {
        if (!isShown) return false;

        switch (keycode)
        {
          case Input.Keys.ENTER:
          {
            hide (new Action ()
            {
              @Override
              public boolean act (float delta)
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
    setCountryNames (sourceCountryActor.getName (), destinationCountryActor.getName ());
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
    countryStack.add (countryArmyTextActor);

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
    countryArmyTextActor.setCircleSize (calculateCountryArmyTextCircleSizeActualCountrySpace (countryActor,
                                                                                              countryImage));
  }

  private void setCountryArmyCirclePosition (final CountryArmyTextActor countryArmyTextActor,
                                             final CountryActor countryActor,
                                             final Image countryImage)
  {
    countryArmyTextActor.setCircleTopLeft (calculateCountryArmyTextCircleTopLeftActualCountrySpace (countryActor,
            countryImage));
  }

  private float calculateCountryImagePadding (final Image countryImagePostLayout, final float arrowWidth)
  {
    return Math.max (0.0f, Math.min (arrowWidth, arrowWidth
            - (COUNTRY_BOX_WIDTH - (COUNTRY_BOX_INNER_PADDING * 2.0f) - countryImagePostLayout.getImageWidth ())));
  }

  private Image asImage (final CountryActor countryActor)
  {
    return new Image (countryActor.getCurrentImage ().getDrawable (), Scaling.none);
  }

  private Vector2 calculateCountryArmyTextCircleTopLeftActualCountrySpace (final CountryActor countryActor,
                                                                           final Image countryImagePostLayout)
  {
    final Point2D countryOriginReferencePlayMapSpace = countryActor.getDestPlayMapReferenceSpace ();
    final Point2D countryArmyTextTopLeftReferencePlayMapSpace = countryActor.getCenterPlayMapReferenceSpace ();

    final Point2D countryArmyTextTopLeftReferenceCountrySpace = CoordinateSpaces
            .toReferenceCountrySpace (countryArmyTextTopLeftReferencePlayMapSpace, countryOriginReferencePlayMapSpace);

    final Point2D countryArmyTextTopLeftScaledCountrySpace = Geometry
            .scale (countryArmyTextTopLeftReferenceCountrySpace,
                    calculateCountryImageScaling (countryActor, countryImagePostLayout));

    final Point2D countryArmyTextTopLeftActualCountrySpace = Geometry
            .translate (countryArmyTextTopLeftScaledCountrySpace,
                        new Translation2D (countryImagePostLayout.getImageX (), countryImagePostLayout.getImageY ()));

    return new Vector2 (countryArmyTextTopLeftActualCountrySpace.getX (),
            countryArmyTextTopLeftActualCountrySpace.getY ());
  }

  private Size2D calculateCountryArmyTextCircleSizeActualCountrySpace (final CountryActor countryActor,
                                                                       final Image countryImagePostLayout)
  {
    return Geometry.scale (PlayMapSettings.COUNTRY_ARMY_CIRCLE_SIZE_REFERENCE_PLAY_MAP_SPACE,
                           calculateCountryImageScaling (countryActor, countryImagePostLayout));
  }

  private Scaling2D calculateCountryImageScaling (final CountryActor countryActor, final Image countryImagePostLayout)
  {
    return Geometry.divide (getSizeOfCountryImagePostLayout (countryImagePostLayout),
                            countryActor.getSizePlayMapReferenceSpace ());
  }

  private Size2D getSizeOfCountryImagePostLayout (final Image countryImagePostLayout)
  {
    return new Size2D (countryImagePostLayout.getImageWidth (), countryImagePostLayout.getImageHeight ());
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
