package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.widgets;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Scaling;

import com.forerunnergames.peril.client.settings.PlayMapSettings;
import com.forerunnergames.peril.client.settings.ScreenSettings;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.actors.CountryActor;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.actors.CountryArmyTextActor;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.actors.DefaultCountryArmyTextActor;
import com.forerunnergames.peril.client.ui.widgets.CellPadding;
import com.forerunnergames.peril.client.ui.widgets.Widgets;
import com.forerunnergames.peril.client.ui.widgets.popup.OkPopup;
import com.forerunnergames.peril.client.ui.widgets.popup.PopupListener;
import com.forerunnergames.peril.client.ui.widgets.popup.PopupStyle;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;

import net.engio.mbassy.bus.MBassador;

public abstract class AbstractArmyMovementPopup extends OkPopup
{
  private static final float COUNTRY_NAME_BOX_WIDTH = 400;
  private static final float COUNTRY_NAME_BOX_HEIGHT = 28;
  private static final float COUNTRY_BOX_INNER_PADDING = 3;
  private static final float COUNTRY_BOX_WIDTH = 400 - COUNTRY_BOX_INNER_PADDING * 2;
  private static final float COUNTRY_BOX_HEIGHT = 200 - COUNTRY_BOX_INNER_PADDING * 2;
  private static final float SOURCE_COUNTRY_ARROW_WIDTH = 74;
  private static final float DESTINATION_COUNTRY_ARROW_WIDTH = 74;
  private static final float SOURCE_DESTINATION_COUNTRY_BOX_SPACING = 2;
  // private static final Vector2 FOREGROUND_ARROW_TEXT_BOTTOM_LEFT_POPUP_REFERENCE_SPACE = new Vector2 (368, 255);
  // private static final Vector2 FOREGROUND_ARROW_TEXT_SIZE_POPUP_REFERENCE_SPACE = new Vector2 (94, 14);
  private static final int SLIDER_STEP_SIZE = 1;
  private static final float INITIAL_BUTTON_REPEAT_DELAY_SECONDS = 0.5f;
  private static final float BUTTON_REPEAT_RATE_SECONDS = 0.05f;
  private final ClassicModePlayScreenWidgetFactory widgetFactory;
  private final Vector2 tempPosition = new Vector2 ();
  private final Vector2 tempScaling = new Vector2 ();
  private final Vector2 tempSize = new Vector2 ();
  // private final Color tempColor = new Color ();
  private final BitmapFont countryArmyTextFont = new BitmapFont ();
  private final CountryArmyTextActor sourceCountryArmyTextActor = new DefaultCountryArmyTextActor (countryArmyTextFont);
  private final CountryArmyTextActor destinationCountryArmyTextActor = new DefaultCountryArmyTextActor (
          countryArmyTextFont);
  private final Label sourceCountryNameLabel;
  private final Label destinationCountryNameLabel;
  private final ImageButton minusButton;
  private final ImageButton plusButton;
  private final ImageButton minButton;
  private final ImageButton maxButton;
  private final Cell <Stack> sourceCountryStackCell;
  private final Cell <Stack> destinationCountryStackCell;
  private final Slider slider;
  private final Stack sourceCountryStack;
  private final Stack destinationCountryStack;
  private float minusButtonPressTimeSeconds = 0.0f;
  private float plusButtonPressTimeSeconds = 0.0f;
  private float minusButtonRepeatDeltaSeconds = 0.0f;
  private float plusButtonRepeatDeltaSeconds = 0.0f;
  private int totalArmies = 0;

  protected AbstractArmyMovementPopup (final ClassicModePlayScreenWidgetFactory widgetFactory,
                                       final String title,
                                       final Stage stage,
                                       final PopupListener listener,
                                       final MBassador <Event> eventBus)

  {
    // @formatter:off
    super (widgetFactory,
           PopupStyle.builder ()
                   .windowStyle ("army-movement-popup")
                   .modal (false)
                   .movable (true)
                   .size (862, 484)
                   .position (481, ScreenSettings.REFERENCE_SCREEN_HEIGHT - 164)
                   .title (title)
                   .titleHeight (58)
                   .messageBox (false)
                   .border (28)
                   .buttonSize (90, 32)
                   .build (),
           stage, listener);
    // @formatter:on

    Arguments.checkIsNotNull (widgetFactory, "widgetFactory");
    Arguments.checkIsNotNull (stage, "stage");
    Arguments.checkIsNotNull (listener, "listener");
    Arguments.checkIsNotNull (eventBus, "eventBus");

    this.widgetFactory = widgetFactory;

    sourceCountryNameLabel = widgetFactory.createArmyMovementPopupCountryNameLabel ();
    destinationCountryNameLabel = widgetFactory.createArmyMovementPopupCountryNameLabel ();

    slider = widgetFactory.createArmyMovementPopupSlider (new ChangeListener ()
    {
      @Override
      public void changed (final ChangeEvent event, final Actor actor)
      {
        updateCountryArmies ();
      }
    });

    minButton = widgetFactory.createArmyMovementPopupMinButton (new ClickListener (Input.Buttons.LEFT)
    {
      @Override
      public void clicked (final InputEvent event, final float x, final float y)
      {
        setSliderToMinValue ();
      }
    });

    minusButton = widgetFactory.createArmyMovementPopupMinusButton (new ClickListener (Input.Buttons.LEFT)
    {
      @Override
      public void clicked (final InputEvent event, final float x, final float y)
      {
        decrementSlider ();
      }
    });

    plusButton = widgetFactory.createArmyMovementPopupPlusButton (new ClickListener (Input.Buttons.LEFT)
    {
      @Override
      public void clicked (final InputEvent event, final float x, final float y)
      {
        incrementSlider ();
      }
    });

    maxButton = widgetFactory.createArmyMovementPopupMaxButton (new ClickListener (Input.Buttons.LEFT)
    {
      @Override
      public void clicked (final InputEvent event, final float x, final float y)
      {
        setSliderToMaxValue ();
      }
    });

    sourceCountryStack = new Stack ();
    destinationCountryStack = new Stack ();

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

    final Table countryTable = new Table ().center ();
    countryTable.add (sourceCountryTable).width (COUNTRY_BOX_WIDTH).maxHeight (COUNTRY_BOX_HEIGHT)
            .spaceRight (SOURCE_DESTINATION_COUNTRY_BOX_SPACING).padLeft (COUNTRY_BOX_INNER_PADDING)
            .padRight (COUNTRY_BOX_INNER_PADDING);
    countryTable.add (destinationCountryTable).width (COUNTRY_BOX_WIDTH).maxHeight (COUNTRY_BOX_HEIGHT)
            .spaceLeft (SOURCE_DESTINATION_COUNTRY_BOX_SPACING).padLeft (COUNTRY_BOX_INNER_PADDING)
            .padRight (COUNTRY_BOX_INNER_PADDING);

    final Table sliderTable = new Table ();
    sliderTable.add (minButton).width (20).spaceRight (7);
    sliderTable.add (minusButton).width (30).spaceLeft (7).spaceRight (10);
    sliderTable.add (slider).width (382).height (12).spaceLeft (10).spaceRight (10);
    sliderTable.add (plusButton).width (30).spaceLeft (10).spaceRight (7);
    sliderTable.add (maxButton).width (20).spaceLeft (7);

    getContentTable ().defaults ().space (0).pad (0);
    getContentTable ().top ();
    getContentTable ().row ().size (COUNTRY_NAME_BOX_WIDTH, COUNTRY_NAME_BOX_HEIGHT).spaceBottom (1);
    getContentTable ().add (sourceCountryNameLabel);
    getContentTable ().add (destinationCountryNameLabel);
    getContentTable ().row ().colspan (2).height (COUNTRY_BOX_HEIGHT).spaceTop (1);
    getContentTable ().add (countryTable).padLeft (2).padRight (2).padTop (COUNTRY_BOX_INNER_PADDING - 2)
            .padBottom (COUNTRY_BOX_INNER_PADDING);
    getContentTable ().row ().colspan (2).top ().padTop (29);
    getContentTable ().add (sliderTable);

    addListener (new InputListener ()
    {
      @Override
      public boolean keyDown (final InputEvent event, final int keycode)
      {
        if (!isShown ()) return false;

        switch (keycode)
        {
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

  @Override
  public void update (final float delta)
  {
    super.update (delta);

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
  //
  // @Override
  // public void draw (final Batch batch, final float parentAlpha)
  // {
  // super.draw (batch, parentAlpha);
  //
  // stageToLocalCoordinates (tempSize.set (getWidth (), getHeight ()));
  // tempColor.set (getColor ());
  // batch.setColor (tempColor.r, tempColor.g, tempColor.b, tempColor.a * parentAlpha);
  // foregroundArrow.draw (batch, getX (), getY (), getX () + tempSize.x, getY () + tempSize.y);
  // foregroundArrowText.draw (batch, getX () + FOREGROUND_ARROW_TEXT_BOTTOM_LEFT_POPUP_REFERENCE_SPACE.x,
  // getY () + FOREGROUND_ARROW_TEXT_BOTTOM_LEFT_POPUP_REFERENCE_SPACE.y,
  // FOREGROUND_ARROW_TEXT_SIZE_POPUP_REFERENCE_SPACE.x,
  // FOREGROUND_ARROW_TEXT_SIZE_POPUP_REFERENCE_SPACE.y);
  // }

  @Override
  public void refreshAssets ()
  {
    super.refreshAssets ();

    sourceCountryNameLabel.setStyle (widgetFactory.createArmyMovementPopupCountryNameLabelStyle ());
    destinationCountryNameLabel.setStyle (widgetFactory.createArmyMovementPopupCountryNameLabelStyle ());
    slider.setStyle (widgetFactory.createArmyMovementPopupSliderStyle ());
    minButton.setStyle (widgetFactory.createArmyMovementPopupMinButtonStyle ());
    minusButton.setStyle (widgetFactory.createArmyMovementPopupMinusButtonStyle ());
    plusButton.setStyle (widgetFactory.createArmyMovementPopupPlusButtonStyle ());
    maxButton.setStyle (widgetFactory.createArmyMovementPopupMaxButtonStyle ());
  }

  public void show (final int minDestinationArmies,
                    final int maxDestinationArmies,
                    final CountryActor sourceCountryActor,
                    final CountryActor destinationCountryActor,
                    final int totalArmies)
  {
    // @formatter:on
    Arguments.checkIsNotNegative (minDestinationArmies, "minDestinationArmies");
    Arguments.checkIsNotNegative (maxDestinationArmies, "maxDestinationArmies");
    Arguments.checkIsNotNull (sourceCountryActor, "sourceCountryActor");
    Arguments.checkIsNotNull (destinationCountryActor, "destinationCountryActor");
    Arguments.checkIsNotNegative (totalArmies, "totalArmies");
    Arguments.checkUpperInclusiveBound (minDestinationArmies, maxDestinationArmies, "minDestinationArmies", "maxDestinationArmies");
    Arguments.checkUpperInclusiveBound (minDestinationArmies, totalArmies, "minDestinationArmies", "totalArmies");
    Arguments.checkUpperInclusiveBound (maxDestinationArmies, totalArmies, "maxDestinationArmies", "totalArmies");
    // @formatter:on

    if (isShown ()) return;

    this.totalArmies = totalArmies;

    setSliderRange (minDestinationArmies, maxDestinationArmies);
    setSliderToMinValue ();
    setCountries (sourceCountryActor, destinationCountryActor);
    show ();
  }

  public void keyDownRepeating (final int keyCode)
  {
    if (!isShown ()) return;

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

  public String getSourceCountryName ()
  {
    return sourceCountryNameLabel.getText ().toString ();
  }

  public String getDestinationCountryName ()
  {
    return destinationCountryNameLabel.getText ().toString ();
  }

  public int getDeltaArmies ()
  {
    return getSliderValue ();
  }

  private static float calculateCountryImagePadding (final Image countryImagePostLayout, final float arrowWidth)
  {
    return Math.max (0.0f, Math.min (arrowWidth, arrowWidth
            - (COUNTRY_BOX_WIDTH - COUNTRY_BOX_INNER_PADDING * 2.0f - countryImagePostLayout.getImageWidth ())));
  }

  private static Image asImage (final CountryActor countryActor)
  {
    return new Image (countryActor.getCurrentPrimaryDrawable (), Scaling.none);
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
    sourceCountryArmyTextActor.changeArmiesTo (armies);
  }

  private void updateDestinationCountryArmies ()
  {
    setDestinationCountryArmies (getSliderValue ());
  }

  private void setDestinationCountryArmies (final int armies)
  {
    destinationCountryArmyTextActor.changeArmiesTo (armies);
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
