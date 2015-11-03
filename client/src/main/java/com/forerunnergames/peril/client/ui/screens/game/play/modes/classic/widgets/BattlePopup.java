package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.widgets;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Scaling;

import com.forerunnergames.peril.client.settings.PlayMapSettings;
import com.forerunnergames.peril.client.settings.ScreenSettings;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.actors.CountryActor;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.actors.CountryArmyTextActor;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.actors.DefaultCountryArmyTextActor;
import com.forerunnergames.peril.client.ui.widgets.popup.OkPopup;
import com.forerunnergames.peril.client.ui.widgets.popup.PopupListener;
import com.forerunnergames.peril.client.ui.widgets.popup.PopupStyle;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;

import net.engio.mbassy.bus.MBassador;

public final class BattlePopup extends OkPopup
{
  private static final boolean DEBUG = false;
  private static final float COUNTRY_NAME_BOX_WIDTH = 400;
  private static final float COUNTRY_NAME_BOX_HEIGHT = 28;
  private static final float COUNTRY_BOX_INNER_PADDING = 3;
  private static final float COUNTRY_BOX_WIDTH = 400 - COUNTRY_BOX_INNER_PADDING * 2;
  private static final float COUNTRY_BOX_HEIGHT = 200 - COUNTRY_BOX_INNER_PADDING * 2;
  private static final float INTER_COUNTRY_BOX_SPACING = 130;
  // private static final Vector2 FOREGROUND_ARROW_TEXT_BOTTOM_LEFT_POPUP_REFERENCE_SPACE = new Vector2 (368, 255);
  // private static final Vector2 FOREGROUND_ARROW_TEXT_SIZE_POPUP_REFERENCE_SPACE = new Vector2 (94, 14);
  private final ClassicModePlayScreenWidgetFactory widgetFactory;
  private final Vector2 tempPosition = new Vector2 ();
  private final Vector2 tempScaling = new Vector2 ();
  private final Vector2 tempSize = new Vector2 ();
  // private final Color tempColor = new Color ();
  private final BitmapFont countryArmyTextFont = new BitmapFont ();
  private final CountryArmyTextActor attackingCountryArmyTextActor = new DefaultCountryArmyTextActor (
          countryArmyTextFont);
  private final CountryArmyTextActor defendingCountryArmyTextActor = new DefaultCountryArmyTextActor (
          countryArmyTextFont);
  private final Label attackingCountryNameLabel;
  private final Label defendingCountryNameLabel;
  private final Cell <Stack> attackingCountryStackCell;
  private final Cell <Stack> defendingCountryStackCell;
  private final Stack attackingCountryStack;
  private final Stack defendingCountryStack;

  public BattlePopup (final ClassicModePlayScreenWidgetFactory widgetFactory,
                      final String title,
                      final Stage stage,
                      final PopupListener listener,
                      final MBassador <Event> eventBus)

  {
    // @formatter:off
    super (widgetFactory,
           PopupStyle.builder ()
                   .windowStyle ("battle")
                   .resizable (true)
                   .movable (true)
                   .size (990, 432)
                   .position (405, ScreenSettings.REFERENCE_SCREEN_HEIGHT - 178)
                   .title (title)
                   .titleHeight (58)
                   .messageBox (false)
                   .border (28)
                   .buttonSize (90, 32)
                   .textButtonStyle ("popup")
                   .debug (DEBUG)
                   .build (),
           stage, listener);
    // @formatter:on

    Arguments.checkIsNotNull (widgetFactory, "widgetFactory");
    Arguments.checkIsNotNull (stage, "stage");
    Arguments.checkIsNotNull (listener, "listener");
    Arguments.checkIsNotNull (eventBus, "eventBus");

    this.widgetFactory = widgetFactory;

    attackingCountryNameLabel = widgetFactory.createBattlePopupCountryNameLabel ();
    defendingCountryNameLabel = widgetFactory.createBattlePopupCountryNameLabel ();

    attackingCountryStack = new Stack ();
    defendingCountryStack = new Stack ();

    final Table attackingCountryStackTable = new Table ();
    attackingCountryStackCell = attackingCountryStackTable.add (attackingCountryStack);
    attackingCountryStackTable.setDebug (DEBUG, true);

    final Table defendingCountryStackTable = new Table ();
    defendingCountryStackCell = defendingCountryStackTable.add (defendingCountryStack);
    defendingCountryStackTable.setDebug (DEBUG, true);

    final Table attackingCountryTable = new Table ();
    attackingCountryTable.add (attackingCountryStackTable);
    attackingCountryTable.setClip (true);
    attackingCountryTable.setDebug (DEBUG, true);

    final Table defendingCountryTable = new Table ();
    defendingCountryTable.add (defendingCountryStackTable);
    defendingCountryTable.setClip (true);
    defendingCountryTable.setDebug (DEBUG, true);

    final Table countryTable = new Table ().center ();
    countryTable.add (attackingCountryTable).width (COUNTRY_BOX_WIDTH).maxHeight (COUNTRY_BOX_HEIGHT)
            .spaceRight (INTER_COUNTRY_BOX_SPACING).padLeft (COUNTRY_BOX_INNER_PADDING)
            .padRight (COUNTRY_BOX_INNER_PADDING);
    countryTable.add (defendingCountryTable).width (COUNTRY_BOX_WIDTH).maxHeight (COUNTRY_BOX_HEIGHT)
            .spaceLeft (INTER_COUNTRY_BOX_SPACING).padLeft (COUNTRY_BOX_INNER_PADDING)
            .padRight (COUNTRY_BOX_INNER_PADDING);
    countryTable.setDebug (DEBUG, true);

    getContentTable ().defaults ().space (0).pad (0);
    getContentTable ().top ();
    getContentTable ().row ().size (COUNTRY_NAME_BOX_WIDTH, COUNTRY_NAME_BOX_HEIGHT).spaceBottom (1);
    getContentTable ().add (attackingCountryNameLabel).spaceRight (INTER_COUNTRY_BOX_SPACING);
    getContentTable ().add (defendingCountryNameLabel).spaceLeft (INTER_COUNTRY_BOX_SPACING);
    getContentTable ().row ().colspan (2).height (COUNTRY_BOX_HEIGHT).spaceTop (1);
    getContentTable ().add (countryTable).padLeft (2).padRight (2).padTop (COUNTRY_BOX_INNER_PADDING - 2)
            .padBottom (COUNTRY_BOX_INNER_PADDING);
    getContentTable ().row ().colspan (2).top ().padTop (29);
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

    attackingCountryNameLabel.setStyle (widgetFactory.createBattlePopupCountryNameLabelStyle ());
    defendingCountryNameLabel.setStyle (widgetFactory.createBattlePopupCountryNameLabelStyle ());
  }

  public void show (final CountryActor attackingCountryActor,
                    final CountryActor defendingCountryActor,
                    final int attackingCountryArmies,
                    final int defendingCountryArmies)
  {
    Arguments.checkIsNotNegative (attackingCountryArmies, "attackingCountryArmies");
    Arguments.checkIsNotNegative (defendingCountryArmies, "defendingCountryArmies");
    Arguments.checkIsNotNull (attackingCountryActor, "attackingCountryActor");
    Arguments.checkIsNotNull (defendingCountryActor, "defendingCountryActor");

    if (isShown ()) return;

    setCountryActors (attackingCountryActor, defendingCountryActor);
    setCountryArmies (attackingCountryArmies, defendingCountryArmies);

    show ();
  }

  public String getAttackingCountryName ()
  {
    return attackingCountryNameLabel.getText ().toString ();
  }

  public String getDefendingCountryName ()
  {
    return defendingCountryNameLabel.getText ().toString ();
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

  private void setCountryArmies (final int attackingCountryArmies, final int defendingCountryArmies)
  {
    setAttackingCountryArmies (attackingCountryArmies);
    setDefendingCountryArmies (defendingCountryArmies);
  }

  private void setAttackingCountryArmies (final int armies)
  {
    attackingCountryArmyTextActor.setArmies (armies);
  }

  private void setDefendingCountryArmies (final int armies)
  {
    defendingCountryArmyTextActor.setArmies (armies);
  }

  private void setCountryActors (final CountryActor attackingCountryActor, final CountryActor defendingCountryActor)
  {
    setCountryNames (attackingCountryActor, defendingCountryActor);
    setCountryImages (attackingCountryActor, defendingCountryActor);
  }

  private void setCountryNames (final CountryActor attackingCountryActor, final CountryActor defendingCountryActor)
  {
    setCountryNames (attackingCountryActor.asActor ().getName (), defendingCountryActor.asActor ().getName ());
  }

  private void setCountryImages (final CountryActor attackingCountryActor, final CountryActor defendingCountryActor)
  {
    setCountryImage (attackingCountryActor, attackingCountryArmyTextActor, attackingCountryStack,
                     attackingCountryStackCell);

    setCountryImage (defendingCountryActor, defendingCountryArmyTextActor, defendingCountryStack,
                     defendingCountryStackCell);
  }

  private void setCountryImage (final CountryActor countryActor,
                                final CountryArmyTextActor countryArmyTextActor,
                                final Stack countryStack,
                                final Cell <Stack> countryStackCell)
  {
    final Image countryImage = asImage (countryActor);

    countryStack.clear ();
    countryStack.add (countryImage);
    countryStack.add (countryArmyTextActor.asActor ());

    getContentTable ().layout ();

//    Widgets.padCell (countryStackCell, calculateCountryImagePadding (countryImage, countryArrowWidth), paddingType);

//    countryStackCell.getTable ().invalidateHierarchy ();

//    getContentTable ().layout ();

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

  private void setCountryNames (final String attackingCountryName, final String defendingCountryName)
  {
    attackingCountryNameLabel.setText (attackingCountryName);
    defendingCountryNameLabel.setText (defendingCountryName);
  }
}
