```json
{
  "title": "Entity Ploys",
  "icon": "minecraft:sheep_spawn_egg",
  "category": "trickster:tricks"
}
```

Various tricks related to manipulating entities.

;;;;;

<|glyph@trickster:templates|trick-id=trickster:add_velocity,title=Impulse Ploy|>

entity, vector ->

<|cost-rule@trickster:templates|formula=3kG + length^2 * 2kG|>

Applies the given vector as velocity to the given entity.

;;;;;

<|glyph@trickster:templates|trick-id=trickster:polymorph,title=Polymorph Ploy|>

entity, entity ->

<|cost-rule@trickster:templates|formula=480kG|>

Polymorphs the first entity to appear to be the second in every way. Only works with players.

;;;;;

<|glyph@trickster:templates|trick-id=trickster:dispel_polymorph,title=Dispel Polymorph Ploy|>

entity ->

<|cost-rule@trickster:templates|formula=70kG|>

Dispels any polymorph on the given entity.

;;;;;

<|glyph@trickster:templates|trick-id=trickster:store_entity,title=Containment Ploy|>

entity ->

<|cost-rule@trickster:templates|formula=60kG + distance ^ (distance / 5kG)|>

Stores the given entity in the caster's offhand item. 
The item must support entity storage, and the entity must not be a player.

;;;;;

<|glyph@trickster:templates|trick-id=trickster:release_entity,title=Extrication Ploy|>

vector -> entity | void

<|cost-rule@trickster:templates|formula=60kG + distance ^ (distance / 5kG)|>

Releases the entity stored in the caster's offhand item to the given position, returning it. 
Returns void if there is no entity.