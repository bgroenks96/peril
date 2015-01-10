package com.forerunnergames.peril.core.model.people.person;

// @formatter:off
/**
 * The server sets this for each client, so that the client can know whether a player is owned / controlled by the local
 * machine / user, or belongs to someone else (remote machine / user).
 *
 * It has many uses, such as being able to refer to a specific player as "you" instead of by name, or giving the user
 * extra permissions for their own player.
 *
 * Usually, it is initialized to {@code UNKNOWN}, until the server is able to determine which client is associated with
 * which person.
 */
// @formatter:on
public enum PersonIdentity
{
  SELF,
  NON_SELF,
  UNKNOWN
}
