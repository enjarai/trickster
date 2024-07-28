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

<|cost-rule@trickster:templates|formula=length^2 * 2kG|>

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