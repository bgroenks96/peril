package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.widgets;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import com.forerunnergames.peril.common.game.DieFaceValue;
import com.forerunnergames.peril.common.game.DieOutcome;
import com.forerunnergames.peril.common.settings.GameSettings;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import java.util.ArrayList;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract class AbstractDie implements Die
{
  // @formatter:off
  private static final Logger log = LoggerFactory.getLogger (AbstractDie.class);
  private final DieFaceValue defaultFaceValue;
  private final ImageButton button;
  private final int index;
  private final Table <DieState, DieStateTransition, DieStateTransitionAction> transitionActionsTable = HashBasedTable.create ();
  private final Collection <DieListener> listeners = new ArrayList <> ();
  private DieFaceValue currentFaceValue = GameSettings.DEFAULT_DIE_FACE_VALUE;
  private DieFaceValue spinningFaceValue = GameSettings.DEFAULT_DIE_FACE_VALUE;
  private DieState currentState = Die.DEFAULT_STATE;
  private DieOutcome currentOutcome = Die.DEFAULT_OUTCOME;
  private boolean isSpinning = true;
  private float spinThresholdTimeSeconds = GameSettings.DICE_SPINNING_INTERVAL_SECONDS;
  private float currentSpinTimeSeconds = 0.0f;
  // @formatter:on

  protected AbstractDie (final int index, final DieFaceValue defaultFaceValue, final ImageButton button)
  {
    Arguments.checkIsNotNull (button, "button");
    Arguments.checkIsNotNull (defaultFaceValue, "defaultFaceValue");
    Arguments.checkIsNotNull (button, "button");

    this.index = index;
    this.defaultFaceValue = defaultFaceValue;
    this.button = button;

    // @formatter:off

    registerActionOnTransitionFrom (DieState.ENABLED, DieStateTransition.ON_HOVER_START, new DieStateTransitionAction ()
    {
      @Override
      public void onTransition (final DieState toState)
      {
        stopSpinning ();
        refreshAssets (toState);
      }
    });

    registerActionOnTransitionFrom (DieState.DISABLED, DieStateTransition.ON_HOVER_START, new DieStateTransitionAction ()
    {
      @Override
      public void onTransition (final DieState toState)
      {
        stopSpinning ();
        refreshAssets (toState);
      }
    });

    registerActionOnTransitionFrom (DieState.ENABLING, DieStateTransition.ON_HOVER_END, new DieStateTransitionAction ()
    {
      @Override
      public void onTransition (final DieState toState)
      {
        startSpinning ();
        refreshAssets (toState);
      }
    });

    registerActionOnTransitionFrom (DieState.DISABLING, DieStateTransition.ON_HOVER_END, new DieStateTransitionAction ()
    {
      @Override
      public void onTransition (final DieState toState)
      {
        startSpinning ();
        refreshAssets (toState);
      }
    });

    registerActionOnTransitionFrom (DieState.ENABLING, DieStateTransition.ON_CLICK, new DieStateTransitionAction ()
    {
      @Override
      public void onTransition (final DieState toState)
      {
        enable ();
      }
    });

    registerActionOnTransitionFrom (DieState.DISABLING, DieStateTransition.ON_CLICK, new DieStateTransitionAction ()
    {
      @Override
      public void onTransition (final DieState toState)
      {
        disable ();
      }
    });

    // @formatter:on

    button.addListener (new ClickListener (Input.Buttons.LEFT)
    {
      @Override
      public void enter (final InputEvent event, final float x, final float y, final int pointer, final Actor fromActor)
      {
        super.enter (event, x, y, pointer, fromActor);

        currentState = currentState.transition (DieStateTransition.ON_HOVER_START, transitionActionsTable);
      }

      @Override
      public void exit (final InputEvent event, final float x, final float y, final int pointer, final Actor toActor)
      {
        super.exit (event, x, y, pointer, toActor);

        currentState = currentState.transition (DieStateTransition.ON_HOVER_END, transitionActionsTable);
      }

      @Override
      public void clicked (final InputEvent event, final float x, final float y)
      {
        currentState = currentState.transition (DieStateTransition.ON_CLICK, transitionActionsTable);
      }
    });

    resetAll ();
  }

  @Override
  public final int getIndex ()
  {
    return index;
  }

  @Override
  public final void roll (final DieFaceValue faceValue)
  {
    Arguments.checkIsNotNull (faceValue, "faceValue");

    if (!currentState.isRollable ()) return;

    currentFaceValue = faceValue;

    stopSpinning ();
    refreshAssets ();

    log.trace ("Rolled die [{}].", this);
  }

  @Override
  public final void setOutcomeAgainst (final DieFaceValue competingFaceValue)
  {
    Arguments.checkIsNotNull (competingFaceValue, "competingFaceValue");

    if (!currentState.isOutcomeable ()) return;

    currentOutcome = determineOutcome (currentFaceValue, competingFaceValue);

    stopSpinning ();
    refreshAssets ();

    log.trace ("Set outcome of die with face value [{}] to [{}] against die with face value [{}].", currentFaceValue,
               currentOutcome, competingFaceValue);
  }

  @Override
  public void setOutcome (final DieOutcome outcome)
  {
    Arguments.checkIsNotNull (outcome, "outcome");

    if (!currentState.isOutcomeable ()) return;

    currentOutcome = outcome;

    stopSpinning ();
    refreshAssets ();

    log.trace ("Set outcome of die with face value [{}] to [{}].", currentFaceValue, currentOutcome);
  }

  @Override
  public final DieOutcome getOutcome ()
  {
    return currentOutcome;
  }

  @Override
  public final boolean hasWinOutcome ()
  {
    return currentOutcome == DieOutcome.WIN;
  }

  @Override
  public final boolean hasLoseOutcome ()
  {
    return currentOutcome == DieOutcome.LOSE;
  }

  @Override
  public final void enable ()
  {
    currentState = DieState.ENABLED;
    currentFaceValue = defaultFaceValue;
    currentOutcome = DieOutcome.NONE;

    startSpinning ();
    refreshAssets ();

    for (final DieListener listener : listeners)
    {
      listener.onEnable (this);
    }

    log.trace ("Enabled die [{}].", this);
  }

  @Override
  public final void disable ()
  {
    currentState = DieState.DISABLED;
    currentFaceValue = defaultFaceValue;
    currentOutcome = DieOutcome.NONE;

    stopSpinning ();
    refreshAssets ();

    for (final DieListener listener : listeners)
    {
      listener.onDisable (this);
    }

    log.trace ("Disabled die [{}].", this);
  }

  @Override
  public final void setTouchable (final boolean isTouchable)
  {
    button.setTouchable (isTouchable ? Touchable.enabled : Touchable.disabled);
  }

  @Override
  public void resetSpinning ()
  {
    currentSpinTimeSeconds = 0.0f;
    spinningFaceValue = GameSettings.DEFAULT_DIE_FACE_VALUE;
    isSpinning = true;
    refreshAssets ();
  }

  @Override
  public final void addListener (final DieListener listener)
  {
    Arguments.checkIsNotNull (listener, "listener");

    listeners.add (listener);
  }

  @Override
  public final void resetState ()
  {
    currentState = Die.DEFAULT_STATE;

    refreshAssets ();
  }

  @Override
  public final void resetFaceValue ()
  {
    currentFaceValue = defaultFaceValue;
    refreshAssets ();
  }

  @Override
  public final void resetOutcome ()
  {
    currentOutcome = Die.DEFAULT_OUTCOME;
    refreshAssets ();
  }

  @Override
  public final void resetAll ()
  {
    currentState = Die.DEFAULT_STATE;
    currentFaceValue = defaultFaceValue;
    currentOutcome = Die.DEFAULT_OUTCOME;
    button.setTouchable (Die.DEFAULT_TOUCHABLE);
    currentSpinTimeSeconds = 0.0f;
    spinningFaceValue = GameSettings.DEFAULT_DIE_FACE_VALUE;
    isSpinning = true;
    refreshAssets ();
  }

  @Override
  public final void refreshAssets ()
  {
    refreshAssets (currentState);
  }

  @Override
  public void update (final float delta)
  {
    currentSpinTimeSeconds += delta;

    if (currentSpinTimeSeconds < spinThresholdTimeSeconds) return;

    spinningFaceValue = spinningFaceValue.hasNext () ? spinningFaceValue.next () : spinningFaceValue.first ();
    currentSpinTimeSeconds = 0.0f;

    if (isSpinning) refreshAssets ();
  }

  @Override
  public final float getWidth ()
  {
    return button.getWidth ();
  }

  @Override
  public final float getHeight ()
  {
    return button.getHeight ();
  }

  @Override
  public final Actor asActor ()
  {
    return button;
  }

  protected abstract DieOutcome determineOutcome (final DieFaceValue thisFaceValue, final DieFaceValue thatFaceValue);

  protected abstract ImageButton.ImageButtonStyle createDieImageButtonStyle (final DieState state,
                                                                             final DieFaceValue faceValue,
                                                                             final DieOutcome outcome);

  private void startSpinning ()
  {
    isSpinning = true;
  }

  private void stopSpinning ()
  {
    isSpinning = false;
  }

  private void refreshAssets (final DieState state)
  {
    // @formatter:off
    button.setStyle (createDieImageButtonStyle (state, isSpinning ? spinningFaceValue : currentFaceValue, currentOutcome));
    button.getImageCell ().expand ().fill ();
    button.invalidate ();
    // @formatter:on
  }

  private void registerActionOnTransitionFrom (final DieState fromState,
                                               final DieStateTransition transition,
                                               final DieStateTransitionAction actionOnFromStateTransition)
  {
    if (transitionActionsTable.contains (fromState, transition))
    {
      throw new IllegalStateException (
              Strings.format ("Already registered a {} from {}: [{}] on {}: [{}].",
                              DieStateTransitionAction.class.getSimpleName (), DieState.class.getSimpleName (),
                              fromState, DieStateTransition.class.getSimpleName (), transition));
    }

    transitionActionsTable.put (fromState, transition, actionOnFromStateTransition);
  }

  @Override
  public final int compareTo (final Die o)
  {
    if (index == o.getIndex ()) return 0;

    return index < o.getIndex () ? -1 : 1;
  }

  @Override
  public int hashCode ()
  {
    return index;
  }

  @Override
  public final boolean equals (final Object obj)
  {
    if (this == obj) return true;
    if (obj == null || getClass () != obj.getClass ()) return false;

    return index == ((AbstractDie) obj).index;
  }

  @Override
  public String toString ()
  {
    return Strings.format (
                           "{}: Index: {} | Current Face Value: {} | Current State: {}"
                                   + " | Current Outcome: {} | Touchable: {} | Spinning: {} | Default Face Value: {} | Default State: {}",
                           getClass ().getSimpleName (), index, currentFaceValue, currentState, currentOutcome,
                           button.isTouchable (), isSpinning, defaultFaceValue, Die.DEFAULT_STATE);
  }
}
