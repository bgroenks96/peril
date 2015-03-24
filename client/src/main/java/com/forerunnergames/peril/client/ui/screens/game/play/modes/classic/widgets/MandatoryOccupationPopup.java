package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.widgets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;

import com.forerunnergames.peril.client.settings.GraphicsSettings;
import com.forerunnergames.peril.client.ui.Assets;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.actors.CountryActor;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.tools.CoordinateSpaces;
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

import java.util.HashMap;
import java.util.Map;

import net.engio.mbassy.bus.MBassador;

public final class MandatoryOccupationPopup extends Dialog
{
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
  private static final Texture.TextureFilter COUNTRY_MINIFICATION_FILTER = Texture.TextureFilter.Nearest;
  private static final Texture.TextureFilter COUNTRY_MAGNIFICATION_FILTER = Texture.TextureFilter.Nearest;
  private final Map <String, Texture> countrySpriteTextureCopies = new HashMap <> ();
  private final ArmyTextActor sourceCountryArmyTextActor = new ArmyTextActor ();
  private final ArmyTextActor destinationCountryArmyTextActor = new ArmyTextActor ();
  private final Stage stage;
  private final MBassador <Event> eventBus;
  private final Label sourceCountryNameLabel;
  private final Label destinationCountryNameLabel;
  private final TextButton minusButton;
  private final TextButton plusButton;
  private final Table sourceCountryStackTable;
  private final Table destinationCountryStackTable;
  private float minusButtonPressTimeSeconds = 0.0f;
  private float plusButtonPressTimeSeconds = 0.0f;
  private float minusButtonRepeatDeltaSeconds = 0.0f;
  private float plusButtonRepeatDeltaSeconds = 0.0f;
  private boolean isShown = false;
  private int totalArmies = 0;
  private Slider slider;
  private Stack sourceCountryStack;
  private Stack destinationCountryStack;
  private Cell <Stack> sourceCountryStackCell;
  private Cell <Stack> destinationCountryStackCell;

  public MandatoryOccupationPopup (final Skin skin, final Stage stage, final MBassador <Event> eventBus)
  {
    super ("", skin, WINDOW_STYLE_NAME_JSON);

    Arguments.checkIsNotNull (skin, "skin");
    Arguments.checkIsNotNull (stage, "stage");
    Arguments.checkIsNotNull (eventBus, "eventBus");

    this.stage = stage;
    this.eventBus = eventBus;

    slider = new Slider (0, 0, SLIDER_STEP_SIZE, IS_SLIDER_VERTICAL, skin);
    slider.addListener (new ChangeListener ()
    {
      @Override
      public void changed (final ChangeEvent event, final Actor actor)
      {
        updateCountryArmyCounts ();
      }
    });

    sourceCountryNameLabel = new Label (null, skin);
    sourceCountryNameLabel.setAlignment (Align.center);

    destinationCountryNameLabel = new Label (null, skin);
    destinationCountryNameLabel.setAlignment (Align.center);

    sourceCountryStack = new Stack ();
    destinationCountryStack = new Stack ();

    setResizable (IS_RESIZABLE);
    setMovable (IS_MOVABLE);
    setSize (SIZE_REFERENCE_SCREEN_SPACE.getWidth (), SIZE_REFERENCE_SCREEN_SPACE.getHeight ());
    setBackground (new TextureRegionDrawable (new TextureRegion (Assets.armyMovementBackground)));
    pad (BORDER_THICKNESS_PIXELS);

    final Table buttonTable = getButtonTable ();
    final Table contentTable = getContentTable ();

    getCell (contentTable).space (12);
    getCell (buttonTable).width (812).height (138).space (12);

    final TextButton okButton = new TextButton ("OK", skin.get (TextButton.TextButtonStyle.class));
    final TextButton resetButton = new TextButton ("RESET", skin.get (TextButton.TextButtonStyle.class));
    final TextButton minButton = new TextButton ("|<", skin.get (TextButton.TextButtonStyle.class));
    minusButton = new TextButton ("-", skin.get (TextButton.TextButtonStyle.class));
    plusButton = new TextButton ("+", skin.get (TextButton.TextButtonStyle.class));
    final TextButton maxButton = new TextButton (">|", skin.get (TextButton.TextButtonStyle.class));

    setObject (okButton, null);

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

    buttonTable.add (minButton).size (22, 34).space (8);
    buttonTable.add (minusButton).size (34, 34).space (8);
    buttonTable.add (slider).width (624).height (12).space (8);
    buttonTable.add (plusButton).size (34, 34).space (8);
    buttonTable.add (maxButton).size (22, 34).space (8);
    buttonTable.row ().spaceTop (22);
    buttonTable.add ().colspan (2);
    buttonTable.add (resetButton).size (94, 38);
    buttonTable.add (okButton).size (55, 38).colspan (2).right ();

    contentTable.top ();
    contentTable.row ().colspan (2).height (44);

    final Image title = new Image (Assets.armyMovementOccupationTitle);
    title.setScaling (Scaling.none);

    contentTable.add (title);
    contentTable.row ().spaceTop (12).spaceBottom (4).spaceLeft (12).spaceRight (12).height (34);
    contentTable.add (sourceCountryNameLabel).width (400);
    contentTable.add (destinationCountryNameLabel).width (400);
    contentTable.row ().spaceTop (4).height (200);

    sourceCountryStackTable = new Table ();
    sourceCountryStackCell = sourceCountryStackTable.add (sourceCountryStack);

    destinationCountryStackTable = new Table ();
    destinationCountryStackCell = destinationCountryStackTable.add (destinationCountryStack);

    final Table countryTable = new Table ();
    countryTable.add (sourceCountryStackTable).width (344).maxHeight (180).padRight (62);
    countryTable.add (destinationCountryStackTable).width (336).maxHeight (180).padLeft (72);

    contentTable.add (countryTable).colspan (2);
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
  public void hide ()
  {
    if (!isShown) return;

    super.hide (null);

    isShown = false;
  }

  @Override
  protected void result (final Object object)
  {
    final int deltaArmyCount = getSliderValue ();
    final String sourceCountryName = getSourceCountryName ();
    final String destinationCountryName = getDestinationCountryName ();

    // TODO Production: Remove
    eventBus.publish (new DefaultStatusMessageEvent (new DefaultStatusMessage ("You occupied " + destinationCountryName
            + " with " + Strings.pluralize (deltaArmyCount, "army", "armies") + " from " + sourceCountryName + ".")));

    // TODO Production: Remove
    eventBus.publish (new CountryArmiesChangedEvent (sourceCountryName, -deltaArmyCount));

    // TODO Production: Remove
    eventBus.publish (new CountryArmiesChangedEvent (destinationCountryName, deltaArmyCount));

    // TODO: Production: Publish event (OccupyCountryRequestEvent?)
  }

  // @formatter:off
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
    show (stage, null);
    setPosition (POSITION_UPPER_LEFT_REFERENCE_SCREEN_SPACE.getX (), GraphicsSettings.REFERENCE_SCREEN_HEIGHT - getHeight () - POSITION_UPPER_LEFT_REFERENCE_SCREEN_SPACE.getY ());

    isShown = true;
  }
  // @formatter:on

  public boolean isShown ()
  {
    return isShown;
  }

  public void dispose ()
  {
    for (final Texture texture : countrySpriteTextureCopies.values ())
    {
      texture.dispose ();
    }
  }

  // @formatter:off
  private void setCountries (final CountryActor sourceCountryActor, final CountryActor destinationCountryActor)
  {
    setCountryNames (sourceCountryActor.getName (), destinationCountryActor.getName ());

    final Image sourceCountryImage = asImage (sourceCountryActor);
    final Size2D sourceCountryImageOriginalSize = new Size2D (sourceCountryImage.getWidth(), sourceCountryImage.getHeight());

    sourceCountryStack.clear();
    sourceCountryStack.add (sourceCountryImage);
    sourceCountryStack.add (sourceCountryArmyTextActor);

    getContentTable ().layout ();

    sourceCountryArmyTextActor.setTopLeft (
                    calculateCountryArmyTextTopLeft (
                                    sourceCountryActor,
                                    sourceCountryImageOriginalSize,
                                    sourceCountryImage,
                                    sourceCountryStackCell,
                                    sourceCountryStackTable));

    final Image destinationCountryImage = asImage (destinationCountryActor);
    final Size2D destinationCountryImageOriginalSize = new Size2D (destinationCountryImage.getWidth(), destinationCountryImage.getHeight());

    destinationCountryStack.clear ();
    destinationCountryStack.add (destinationCountryImage);
    destinationCountryStack.add (destinationCountryArmyTextActor);

    getContentTable ().layout ();

    destinationCountryArmyTextActor.setTopLeft (
                    calculateCountryArmyTextTopLeft (
                                    destinationCountryActor,
                                    destinationCountryImageOriginalSize,
                                    destinationCountryImage,
                                    destinationCountryStackCell,
                                    destinationCountryStackTable));
    updateCountryArmyCounts ();
  }
  // @formatter:on

  private Image asImage (final CountryActor countryActor)
  {
    final String countrySpriteName = countryActor.getCurrentCountrySpriteName ();

    final Sprite countrySprite = new Sprite (countryActor.getCurrentSprite ());
    final Texture countrySpriteTextureCopy = new Texture (countrySprite.getTexture ().getTextureData ());
    countrySpriteTextureCopy.setFilter (COUNTRY_MINIFICATION_FILTER, COUNTRY_MAGNIFICATION_FILTER);
    countrySprite.setTexture (countrySpriteTextureCopy);

    final Image countryImage = new Image (new SpriteDrawable (countrySprite), Scaling.fit);

    countrySpriteTextureCopies.put (countrySpriteName, countrySpriteTextureCopy);

    return countryImage;
  }

  // @formatter:off
  private Point2D calculateCountryArmyTextTopLeft (final CountryActor countryActor,
                                                   final Size2D countryImageSizePreLayout,
                                                   final Image countryImagePostLayout,
                                                   final Cell <Stack> countryStackCell,
                                                   final Table countryStackTable)
  {
    final float countryImageScalingX = countryImagePostLayout.getImageWidth() / countryImageSizePreLayout.getWidth();
    final float countryImageScalingY = countryImagePostLayout.getImageHeight() / countryImageSizePreLayout.getHeight();
    final float countryX = getPopupPaddingLeft () + countryStackTable.getX () + countryStackCell.getActorX() + countryImagePostLayout.getImageX();
    final float countryY = getPopupPaddingBottom () + getButtonTableHeight () +  getButtonTableContentTableSpacing () + countryStackTable.getY() + countryStackCell.getActorY() + countryImagePostLayout.getImageY();
    final Point2D countryOriginReferencePopupSpace = new Point2D (countryX, countryY);
    final Point2D countryOriginReferencePlayMapSpace = countryActor.getDestPlayMapReferenceSpace ();
    final Point2D countryCenterReferencePlayMapSpace = Geometry.translate (countryActor.getCenterPlayMapReferenceSpace (), new Translation2D (9, 9));
    final Point2D countryCenterReferenceCountrySpace = CoordinateSpaces.toReferenceCountrySpace (countryCenterReferencePlayMapSpace, countryOriginReferencePlayMapSpace);
    final Point2D countryCenterScaledCountrySpace = Geometry.scale (countryCenterReferenceCountrySpace, new Scaling2D (countryImageScalingX, countryImageScalingY));

    return CoordinateSpaces.fromReferenceCountrySpace (countryCenterScaledCountrySpace, countryOriginReferencePopupSpace);
  }
  // @formatter:on

  private float getPopupPaddingLeft ()
  {
    return getPadLeft ();
  }

  private float getPopupPaddingBottom ()
  {
    return getPadBottom ();
  }

  private float getButtonTableHeight ()
  {
    return getCell (getButtonTable ()).getPrefHeight ();
  }

  private float getButtonTableContentTableSpacing ()
  {
    return getCell (getContentTable ()).getSpaceBottom ();
  }

  private void setSliderRange (final int minValue, final int maxValue)
  {
    slider.setRange (minValue, maxValue);
  }

  private void updateCountryArmyCounts ()
  {
    setSourceCountryArmyCount (totalArmies - getSliderValue ());
    setDestinationCountryArmyCount (getSliderValue ());
  }

  private int getSliderValue ()
  {
    return (int) slider.getValue ();
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

  private String getSourceCountryName ()
  {
    return sourceCountryNameLabel.getText ().toString ();
  }

  private void setCountryNames (final String sourceCountryName, final String destinationCountryName)
  {
    sourceCountryNameLabel.setText (sourceCountryName);
    destinationCountryNameLabel.setText (destinationCountryName);
  }

  private String getDestinationCountryName ()
  {
    return destinationCountryNameLabel.getText ().toString ();
  }

  private void setSourceCountryArmyCount (final int armyCount)
  {
    sourceCountryArmyTextActor.setArmies (armyCount);
  }

  private void setDestinationCountryArmyCount (final int armyCount)
  {
    destinationCountryArmyTextActor.setArmies (armyCount);
  }

  private final class ArmyTextActor extends Actor
  {
    private final BitmapFont font = Assets.aurulentSans16;
    private final GlyphLayout glyphLayout = new GlyphLayout ();
    private String text = "";
    private Point2D topLeft = new Point2D (0, 0);
    private Size2D screenSize;
    private Scaling2D scaling;

    public void setArmies (final int armies)
    {
      changeText (String.valueOf (armies));
    }

    private void changeText (final String text)
    {
      this.text = text;
      glyphLayout.setText (font, text);
      updateSize ();
    }

    public void setTopLeft (final Point2D topLeft)
    {
      Arguments.checkIsNotNull (topLeft, "topLeft");

      this.topLeft = topLeft;
    }

    @Override
    public void draw (final Batch batch, final float parentAlpha)
    {
      font.draw (batch, text, topLeft.getX () + (13 - glyphLayout.width) / 2.0f, topLeft.getY ());
    }

    @Override
    public void act (float delta)
    {
      super.act (delta);

      if (!shouldUpdateScreenSize ()) return;

      updateScreenSize ();
      updateScaling ();
      updateSize ();
    }

    private boolean shouldUpdateScreenSize ()
    {
      return screenSize == null || scaling == null
              || screenSize.isNot (Gdx.graphics.getWidth (), Gdx.graphics.getHeight ());
    }

    private void updateScreenSize ()
    {
      screenSize = new Size2D (Gdx.graphics.getWidth (), Gdx.graphics.getHeight ());
    }

    private void updateScaling ()
    {
      scaling = Geometry.divide (GraphicsSettings.REFERENCE_SCREEN_SIZE, screenSize);
      font.getData ().setScale (scaling.getX (), scaling.getY ());
    }

    private void updateSize ()
    {
      setSize (glyphLayout.width, glyphLayout.height);
    }
  }
}
