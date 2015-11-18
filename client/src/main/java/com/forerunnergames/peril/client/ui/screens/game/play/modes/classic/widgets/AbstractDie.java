package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.widgets;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import com.forerunnergames.peril.common.game.DieFaceValue;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import java.util.ArrayList;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractDie implements Die
{
  // @formatter:off
  private static final Logger log = LoggerFactory.getLogger (AbstractDie.class);
  private static final DieState DEFAULT_STATE = DieState.ENABLED;
  private final DieFaceValue defaultFaceValue;
  private final Button button;
  private final int index;
  private final Table <DieState, DieStateTransition, DieStateTransitionAction> transitionActionsTable = HashBasedTable.create ();
  private final Collection <DieListener> listeners = new ArrayList <> ();
  private DieFaceValue currentFaceValue;
  private DieState currentState;
  // @formatter:on

  protected AbstractDie (final int index, final DieFaceValue defaultFaceValue, final Button button)
  {
    Arguments.checkIsNotNull (button, "button");
    Arguments.checkIsNotNull (defaultFaceValue, "defaultFaceValue");

    this.index = index;
    this.defaultFaceValue = defaultFaceValue;
    this.button = button;

    // @formatter:off

    registerActionOnTransitionFrom (DieState.ENABLED, DieStateTransition.ON_HOVER_START, new DieStateTransitionAction ()
    {
      @Override
      public void onTransition (final DieState toState)
      {
        refreshAssets (toState);
      }
    });

    registerActionOnTransitionFrom (DieState.DISABLED, DieStateTransition.ON_HOVER_START, new DieStateTransitionAction ()
    {
      @Override
      public void onTransition (final DieState toState)
      {
        refreshAssets (toState);
      }
    });

    registerActionOnTransitionFrom (DieState.ENABLING, DieStateTransition.ON_HOVER_END, new DieStateTransitionAction ()
    {
      @Override
      public void onTransition (final DieState toState)
      {
        refreshAssets (toState);
      }
    });

    registerActionOnTransitionFrom (DieState.DISABLING, DieStateTransition.ON_HOVER_END, new DieStateTransitionAction ()
    {
      @Override
      public void onTransition (final DieState toState)
      {
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

    reset ();
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

    refreshAssets ();
  }

  @Override
  public final void enable ()
  {
    log.trace ("Enabling die [{}]...", this);

    currentState = DieState.ENABLED;
    currentFaceValue = defaultFaceValue;

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
    log.trace ("Disabling die [{}]...", this);

    currentState = DieState.DISABLED;
    currentFaceValue = defaultFaceValue;

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
  public final void addListener (final DieListener listener)
  {
    Arguments.checkIsNotNull (listener, "listener");

    listeners.add (listener);
  }

  @Override
  public final void reset ()
  {
    currentState = DEFAULT_STATE;
    currentFaceValue = defaultFaceValue;
    button.setTouchable (Touchable.disabled);
    refreshAssets ();
  }

  @Override
  public final void refreshAssets ()
  {
    refreshAssets (currentState);
  }

  @Override
  public final Actor asActor ()
  {
    return button;
  }

  protected abstract Button.ButtonStyle createDieButtonStyle (final DieState state, final DieFaceValue faceValue);

  private void refreshAssets (final DieState state)
  {
    button.setStyle (createDieButtonStyle (state, currentFaceValue));
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
  public final int hashCode ()
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
  public final String toString ()
  {
    return Strings.format (
                           "{}: Index: {} | Current Face Value: {} | Current State: {} | Touchable: {}"
                                   + " | Default Face Value: {} | Default State: {}",
                           getClass ().getSimpleName (), index, currentFaceValue, currentState, button.isTouchable (),
                           defaultFaceValue, DEFAULT_STATE);
  }
}
