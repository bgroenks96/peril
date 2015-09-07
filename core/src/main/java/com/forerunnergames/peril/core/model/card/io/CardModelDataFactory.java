package com.forerunnergames.peril.core.model.card.io;

import com.forerunnergames.peril.common.map.MapMetadata;
import com.forerunnergames.peril.core.model.card.Card;

import com.google.common.collect.ImmutableSet;

public interface CardModelDataFactory
{
  ImmutableSet <Card> createCards (final MapMetadata mapMetadata);
}
