package com.forerunnergames.peril.common.io;

import com.google.common.collect.ImmutableBiMap;

public abstract class AbstractBiMapDataLoader <T, U> extends AbstractDataLoader <ImmutableBiMap <T, U>>
        implements BiMapDataLoader <T, U>
{
}
