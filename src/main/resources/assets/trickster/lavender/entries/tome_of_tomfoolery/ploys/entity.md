```json
{
  "title": "Entity Ploys",
  "icon": "minecraft:sheep_spawn_egg",
  "category": "trickster:ploys",
  "additional_search_terms": [
    "Kinetic Ploy",
    "Ploy of Featherweight",
    "Ploy of the Usurper",
    "Polymorph Ploy",
    "Dispel Polymorph Ploy",
    "Containment Ploy",
    "Extrication Ploy",
    "Ploy of Occupation"
  ]
}
```

Various tricks related to manipulating entities.

;;;;;

<|ploy@trickster:templates|trick-id=trickster:add_velocity,cost=3kG + length^3 * 2kG|>

Applies the given vector as velocity to the given entity.

;;;;;

<|ploy@trickster:templates|trick-id=trickster:change_weight,cost=60kG * (1 - multiplier)|>

Given a number between zero and one, multiplies the given entity's effective gravity by that number for one second, provided it is alive.

;;;;;

<|ploy@trickster:templates|trick-id=trickster:displace,cost=20kG + 1.35^length|>

Displaces the given entity by the given vector after two seconds.

;;;;;

<|ploy@trickster:templates|trick-id=trickster:polymorph,cost=8000kG|>

Polymorphs the first entity to appear to be the second in every way. **Currently only works with players.**

;;;;;

<|ploy@trickster:templates|trick-id=trickster:dispel_polymorph,cost=1000kG|>

Dispels any polymorph on the given entity.

;;;;;

<|ploy@trickster:templates|trick-id=trickster:store_entity,cost=2000kG + distance ^ (distance / 5kG)|>

Stores the given entity in the caster's offhand item. 
The item must support entity storage, and the entity must not be a player.

;;;;;

<|ploy@trickster:templates|trick-id=trickster:release_entity,cost=2000kG + distance ^ (distance / 5kG)|>

Releases the entity stored in the caster's offhand item to the given position, returning it. 
Returns void if there is no entity.

;;;;;

<|ploy@trickster:templates|trick-id=trickster:set_scale,cost=abs(currentScale - newScale)^2 * 100kG + newScale * 50kG|>

Changes the scale of the given entity. Entities cannot be scaled below 0.0625 or above 8 times their usual size.
