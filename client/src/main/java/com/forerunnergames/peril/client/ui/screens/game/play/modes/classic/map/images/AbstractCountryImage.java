package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.images;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import com.forerunnergames.peril.core.model.map.country.CountryName;
import com.forerunnergames.tools.common.Arguments;

public abstract class AbstractCountryImage <E extends Enum <E> & CountryImageState <E>> extends Image implements
        CountryImage <E>
{
  private final CountryName countryName;
  private final CountryImageState <E> state;

  protected AbstractCountryImage (final Drawable drawable,
                                  final CountryName countryName,
                                  final CountryImageState <E> state)
  {
    super (drawable);

    Arguments.checkIsNotNull (countryName, "countryName");
    Arguments.checkIsNotNull (state, "state");

    this.countryName = countryName;
    this.state = state;

    setName (countryName.asString () + " " + state.toProperCase ());
  }

  @Override
  public CountryName getCountryName ()
  {
    return countryName;
  }

  @Override
  public CountryImageState <E> getState ()
  {
    return state;
  }

  @Override
  public void setPosition (final Vector2 position)
  {
    Arguments.checkIsNotNull (position, "position");

    setPosition (position.x, position.y);
  }

  @Override
  public void setScale (final Vector2 scaling)
  {
    Arguments.checkIsNotNull (scaling, "scaling");

    setScale (scaling.x, scaling.y);
  }

  @Override
  public final int hashCode ()
  {
    int result = countryName.hashCode ();
    result = 31 * result + state.hashCode ();
    return result;
  }

  @Override
  public final boolean equals (final Object o)
  {
    if (this == o) return true;
    if (o == null || getClass () != o.getClass ()) return false;
    final AbstractCountryImage that = (AbstractCountryImage) o;
    return countryName.equals (that.countryName) && state == that.state;
  }

  @Override
  public String toString ()
  {
    return String.format ("%1$s: Name: %2$s | State: %3$s | Drawable: %4$s", getClass ().getSimpleName (), countryName,
                          state, getDrawable ());
  }
}
