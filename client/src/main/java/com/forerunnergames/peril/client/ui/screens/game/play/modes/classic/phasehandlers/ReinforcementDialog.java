/*
 * Copyright © 2013 - 2017 Forerunner Games, LLC.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.phasehandlers;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import com.forerunnergames.peril.client.settings.StyleSettings;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.ClassicModePlayScreenWidgetFactory;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.dialogs.NonPlayMapBlockingDialog;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.personbox.PersonBox;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.actors.Country;
import com.forerunnergames.peril.client.ui.widgets.dialogs.CancellableDialogListener;
import com.forerunnergames.peril.client.ui.widgets.dialogs.DialogStyle;
import com.forerunnergames.peril.client.ui.widgets.dialogs.OkCancelDialog;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;

public final class ReinforcementDialog extends OkCancelDialog implements NonPlayMapBlockingDialog
{
  private static final boolean DEBUG = false;
  private static final float INITIAL_BUTTON_REPEAT_DELAY_SECONDS = 0.5f;
  private static final float BUTTON_REPEAT_RATE_SECONDS = 0.05f;
  private static final Vector2 MENU_POSITION_OFFSET = new Vector2 (24, -24);
  private final ClassicModePlayScreenWidgetFactory widgetFactory;
  private final Stage stage;
  private final PersonBox personBox;
  private final ImageButton minusButton;
  private final ImageButton plusButton;
  private final ImageButton minButton;
  private final ImageButton maxButton;
  private final Slider slider;
  private final Vector2 tempPosition = new Vector2 ();
  private float minusButtonPressTimeSeconds = 0.0f;
  private float plusButtonPressTimeSeconds = 0.0f;
  private float minusButtonRepeatDeltaSeconds = 0.0f;
  private float plusButtonRepeatDeltaSeconds = 0.0f;
  private Country country = Country.NULL;
  private int originalCountryArmyCount;
  private int minReinforcements;
  private String playerName = "";

  public ReinforcementDialog (final ClassicModePlayScreenWidgetFactory widgetFactory,
                              final Stage stage,
                              final PersonBox personBox,
                              final CancellableDialogListener listener)

  {
    // @formatter:off
    super (widgetFactory,
            DialogStyle.builder ()
                    .modal (false)
                    .windowStyle (StyleSettings.REINFORCEMENT_DIALOG_WINDOW_STYLE)
                    .buttonTextPaddingVertical (0)
                    .buttonTextPaddingHorizontal (10)
                    .border (10)
                    .messageBox (false)
                    .debug (DEBUG)
                    .build (),
            stage, listener);
    // @formatter:on

    Arguments.checkIsNotNull (widgetFactory, "widgetFactory");
    Arguments.checkIsNotNull (stage, "stage");
    Arguments.checkIsNotNull (personBox, "personBox");

    this.widgetFactory = widgetFactory;
    this.stage = stage;
    this.personBox = personBox;

    slider = widgetFactory.createArmyMovementDialogSlider (new ChangeListener ()
    {
      @Override
      public void changed (final ChangeEvent event, final Actor actor)
      {
        updateArmies ();
        updateSubmitability ();
      }
    });

    minButton = widgetFactory.createArmyMovementDialogMinButton (new ClickListener (Input.Buttons.LEFT)
    {
      @Override
      public void clicked (final InputEvent event, final float x, final float y)
      {
        setSliderToMinValue ();
      }
    });

    minusButton = widgetFactory.createArmyMovementDialogMinusButton (new ClickListener (Input.Buttons.LEFT)
    {
      @Override
      public void clicked (final InputEvent event, final float x, final float y)
      {
        decrementSlider ();
      }
    });

    plusButton = widgetFactory.createArmyMovementDialogPlusButton (new ClickListener (Input.Buttons.LEFT)
    {
      @Override
      public void clicked (final InputEvent event, final float x, final float y)
      {
        incrementSlider ();
      }
    });

    maxButton = widgetFactory.createArmyMovementDialogMaxButton (new ClickListener (Input.Buttons.LEFT)
    {
      @Override
      public void clicked (final InputEvent event, final float x, final float y)
      {
        setSliderToMaxValue ();
      }
    });

    final Table sliderTable = new Table ();
    sliderTable.add (minButton).width (20).spaceRight (7);
    sliderTable.add (minusButton).width (30).spaceLeft (7).spaceRight (10);
    sliderTable.add (slider).width (191).height (12).spaceLeft (10).spaceRight (10);
    sliderTable.add (plusButton).width (30).spaceLeft (10).spaceRight (7);
    sliderTable.add (maxButton).width (20).spaceLeft (7);
    sliderTable.setDebug (DEBUG, true);

    getContentTable ().defaults ().space (0).pad (0);
    getContentTable ().top ();
    getContentTable ().add (sliderTable).padBottom (10);

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
  public void enableSubmission ()
  {
    if (!isSubmissionDisabled ()) return;

    super.enableSubmission ();

    enableTextButton ("OK");
  }

  @Override
  public void disableSubmission ()
  {
    if (isSubmissionDisabled ()) return;

    super.disableSubmission ();

    disableTextButton ("OK");
  }

  @Override
  public void setSubmissionDisabled (final boolean isDisabled)
  {
    if (isSubmissionDisabled () == isDisabled) return;

    super.setSubmissionDisabled (isDisabled);

    setTextButtonDisabled ("OK", isDisabled);
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

  @Override
  public void refreshAssets ()
  {
    super.refreshAssets ();

    slider.setStyle (widgetFactory.createArmyMovementDialogSliderStyle ());
    minButton.setStyle (widgetFactory.createArmyMovementDialogMinButtonStyle ());
    minusButton.setStyle (widgetFactory.createArmyMovementDialogMinusButtonStyle ());
    plusButton.setStyle (widgetFactory.createArmyMovementDialogPlusButtonStyle ());
    maxButton.setStyle (widgetFactory.createArmyMovementDialogMaxButtonStyle ());
  }

  @Override
  public void onKeyDownRepeating (final int keyCode)
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

  public void set (final int minReinforcements,
                   final int maxReinforcements,
                   final Country country,
                   final float screenX,
                   final float screenY,
                   final String playerName)
  {
    // @formatter:off
    Arguments.checkIsNotNegative (minReinforcements, "minReinforcements");
    Arguments.checkIsNotNegative (maxReinforcements, "maxReinforcements");
    Arguments.checkIsNotNull (country, "country");
    Arguments.checkUpperInclusiveBound (minReinforcements, maxReinforcements, "minReinforcements", "maxReinforcements");
    Arguments.checkIsNotNull (playerName, "playerName");
    // @formatter:on

    this.country = country;
    this.playerName = playerName;
    originalCountryArmyCount = country.getArmies ();
    this.minReinforcements = minReinforcements;

    setSliderRange (0, maxReinforcements);
    setSliderToMinValue ();
    updateSlidability ();
    updateSubmitability ();
    updateArmies ();
    updateTitle ();
    updatePosition (screenX, screenY);
  }

  public void show (final int minReinforcements,
                    final int maxReinforcements,
                    final Country country,
                    final float screenX,
                    final float screenY,
                    final String playerName)
  {
    set (minReinforcements, maxReinforcements, country, screenX, screenY, playerName);
    show ();
  }

  public void rollbackAnyPreemptiveUpdates ()
  {
    personBox.resetDisplayedArmiesInHand (playerName);
    country.setArmies (originalCountryArmyCount);
  }

  public String getCountryName ()
  {
    return country.getName ();
  }

  public int getCountryArmyCount ()
  {
    return country.getArmies ();
  }

  public int getReinforcements ()
  {
    return getSliderValue ();
  }

  public int getActualPlayerArmiesInHand ()
  {
    return personBox.getActualArmiesInHand (playerName);
  }

  public int getDisplayedArmiesInHand ()
  {
    return personBox.getDisplayedArmiesInHand (playerName);
  }

  private void updateArmies ()
  {
    updateDisplayedArmiesInHand ();
    updateCountryArmies ();
  }

  private void updateDisplayedArmiesInHand ()
  {
    personBox.setDisplayedArmiesInHandToDeltaFromActual (-getReinforcements (), playerName);
  }

  private void updateCountryArmies ()
  {
    country.setArmies (originalCountryArmyCount + getSliderValue ());
  }

  private int getSliderValue ()
  {
    return Math.round (slider.getValue ());
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

  private void setSliderRange (final int minValue, final int maxValue)
  {
    slider.setRange (minValue, maxValue);
  }

  private void updateSlidability ()
  {
    if (Math.round (slider.getMinValue ()) == Math.round (slider.getMaxValue ()))
    {
      slider.setDisabled (true);
      minButton.setDisabled (true);
      maxButton.setDisabled (true);
      minusButton.setDisabled (true);
      plusButton.setDisabled (true);
    }
    else
    {
      slider.setDisabled (false);
      minButton.setDisabled (false);
      maxButton.setDisabled (false);
      minusButton.setDisabled (false);
      plusButton.setDisabled (false);
    }
  }

  private void updateSubmitability ()
  {
    setSubmissionDisabled (getSliderValue () < minReinforcements);
  }

  private void updateTitle ()
  {
    assert country != null;
    setTitle (Strings.format ("Reinforcing:  {}", country.getName ()));
  }

  private void updatePosition (final float screenX, final float screenY)
  {
    stage.screenToStageCoordinates (tempPosition.set (screenX, screenY));
    setPosition (Math.round (tempPosition.x + MENU_POSITION_OFFSET.x),
                 Math.round (tempPosition.y + MENU_POSITION_OFFSET.y));
  }
}
