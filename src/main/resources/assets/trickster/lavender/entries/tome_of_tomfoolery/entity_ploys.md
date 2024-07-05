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

---

Applies the given vector as velocity to the given entity.

;;;;;

<|glyph@trickster:templates|trick-id=trickster:polymorph,title=Polymorph Ploy|>

entity, entity ->

---

Polymorphs the first entity to appear to be the second in every way. Only works on players.

;;;;;

<|glyph@trickster:templates|trick-id=trickster:dispel_polymorph,title=Dispel Polymorph Ploy|>

entity ->

---

Dispels any polymorph on the given entity.

;;;;;

<|page-title@lavender:book_components|title=Note: Mana Leeching|>The Conduit's Ploy is a powerful glyph that allows 
you to exploit the strength of other creatures, channeling their mana through you and into your spells. 
Each creature you link will stay linked only for the duration of the spell, 
and only until the link has expended its permitted amount of mana. 


;;;;;

<|glyph@trickster:templates|trick-id=trickster:leech_mana,title=Conduit's Ploy|>

entity, number ->

---

Links the given entity to provide up to the given number's worth of mana towards this spell.