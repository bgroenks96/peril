package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.images;

import com.forerunnergames.tools.common.enums.IterableEnum;

public interface CountryImageState <E extends Enum <E> & CountryImageState <E>> extends IterableEnum <E>
{
  E asEnum ();

  String getEnumName ();
}
