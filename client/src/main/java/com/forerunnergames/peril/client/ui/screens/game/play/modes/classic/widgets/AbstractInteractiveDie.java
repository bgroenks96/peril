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

import java.util.ArrayList;
import java.util.List;

import javax.annotation.OverridingMethodsMustInvokeSuper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractInteractiveDie implements Die
{
  private static final Logger log = LoggerFactory.getLogger (AbstractInteractiveDie.class);
  private final int index;
  private final DieFaceValue defaultFaceValue;
  private final Button button;
  private DieFaceValue currentFaceValue;
  private boolean isBeingActivated = false;
  private boolean isBeingDeactivated = false;
  private List <DieListener> listeners = new ArrayList <> ();

  public AbstractInteractiveDie (final int index, final DieFaceValue defaultFaceValue, final Button button)
  {
    Arguments.checkIsNotNull (button, "button");
    Arguments.checkIsNotNull (defaultFaceValue, "defaultFaceValue");

    this.index = index;
    this.defaultFaceValue = defaultFaceValue;
    this.button = button;

    button.addListener (new ClickListener (Input.Buttons.LEFT)
    {
      @Override
      public void enter (final InputEvent event, final float x, final float y, final int pointer, final Actor fromActor)
      {
        super.enter (event, x, y, pointer, fromActor);

        if (!button.isTouchable ()) return;
        if (isBeingActivated || isBeingDeactivated) return;

        isBeingActivated = button.isDisabled ();
        isBeingDeactivated = !button.isDisabled ();

        // We must temporarily enable to be able to change button styles to + and -.
        button.setDisabled (false);

        refreshAssets ();
      }

      @Override
      public void exit (final InputEvent event, final float x, final float y, final int pointer, final Actor toActor)
      {
        super.exit (event, x, y, pointer, toActor);

        if (!button.isTouchable ()) return;

        if (isBeingActivated) button.setDisabled (true);
        if (isBeingDeactivated) button.setDisabled (false);

        isBeingActivated = false;
        isBeingDeactivated = false;

        refreshAssets ();
      }

      @Override
      public void clicked (final InputEvent event, final float x, final float y)
      {
        if (!button.isTouchable ()) return;

        if (isBeingActivated) activate ();
        if (isBeingDeactivated) deactivate ();

        isBeingActivated = false;
        isBeingDeactivated = false;

        refreshAssets ();
      }
    });

    reset ();
  }

  @Override
  public int getIndex ()
  {
    return index;
  }

  @Override
  public boolean isActive ()
  {
    // We could just be temporarily enabled while being activated.
    // In that case, no, we aren't really active yet.
    return !button.isDisabled () && !isBeingActivated;
  }

  @Override
  public void roll (final DieFaceValue faceValue)
  {
    Arguments.checkIsNotNull (faceValue, "faceValue");

    if (!isActive ()) return;

    setFaceValue (faceValue);
  }

  @Override
  public void activate ()
  {
    if (isActive ())
    {
      log.trace ("Ignoring activation of die because it is already active [{}]...", this);
      return;
    }

    log.trace ("Activating die [{}]...", this);

    setFaceValue (defaultFaceValue);

    button.setDisabled (false);

    for (final DieListener listener : listeners)
    {
      listener.onActivate (this);
    }

    log.trace ("Activated die [{}].", this);
  }

  @Override
  public void deactivate ()
  {
    if (isInactive ())
    {
      log.trace ("Ignoring deactivation of die because it is not active [{}]...", this);
      return;
    }

    log.trace ("Deactivating die [{}]...", this);

    button.setDisabled (true);

    for (final DieListener listener : listeners)
    {
      listener.onDeactivate (this);
    }

    log.trace ("Deactivated die [{}].", this);
  }

  @Override
  public void setTouchable (final boolean isTouchable)
  {
    button.setTouchable (isTouchable ? Touchable.enabled : Touchable.disabled);
  }

  @Override
  public void addListener (final DieListener listener)
  {
    Arguments.checkIsNotNull (listener, "listener");

    listeners.add (listener);
  }

  @Override
  public void reset ()
  {
    listeners.clear ();
    button.setDisabled (false);
    button.setTouchable (Touchable.enabled);
    isBeingActivated = false;
    isBeingDeactivated = false;
    currentFaceValue = defaultFaceValue;
    refreshAssets ();
  }

  @Override
  @OverridingMethodsMustInvokeSuper
  public void refreshAssets ()
  {
    if (isBeingActivated)
    {
      button.setStyle (createActivateDieButtonStyle ());
    }
    else if (isBeingDeactivated)
    {
      button.setStyle (createDeactivateDieButtonStyle ());
    }
    else
    {
      button.setStyle (createDieFaceButtonStyle (currentFaceValue));
    }
  }

  @Override
  public Actor asActor ()
  {
    return button;
  }

  protected abstract Button.ButtonStyle createDieFaceButtonStyle (final DieFaceValue currentFaceValue);

  protected abstract Button.ButtonStyle createActivateDieButtonStyle ();

  protected abstract Button.ButtonStyle createDeactivateDieButtonStyle ();

  private boolean isInactive ()
  {
    return button.isDisabled ();
  }

  private void setFaceValue (final DieFaceValue faceValue)
  {
    if (currentFaceValue == faceValue) return;

    currentFaceValue = faceValue;

    refreshAssets ();
  }

  @Override
  public int compareTo (final Die o)
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
  public boolean equals (final Object obj)
  {
    if (this == obj) return true;
    if (obj == null || getClass () != obj.getClass ()) return false;

    return index == ((AbstractInteractiveDie) obj).index;
  }

  @Override
  public String toString ()
  {
    return Strings.format (
                           "{}: Index: {} | Current Face Value: {} | Active: {} | Touchable: {} | Default Face Value: {}",
                           getClass ().getSimpleName (), index, currentFaceValue, isActive (), button.isTouchable (),
                           defaultFaceValue);
  }
}
