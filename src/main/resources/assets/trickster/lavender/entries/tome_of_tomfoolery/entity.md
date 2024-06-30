```json
{
  "title": "Entity Distortions",
  "icon": "minecraft:cow_spawn_egg",
  "category": "trickster:tricks"
}
```

Various tricks related to gathering data about entities.

;;;;;

<|glyph@trickster:templates|trick-id=trickster:get_position,title=Locational Distortion|>

entity -> vector

---

Given an entity, returns its position in the world.

;;;;;

<|glyph@trickster:templates|trick-id=trickster:get_facing,title=Directional Distortion|>

entity -> vector

---

Given an entity, returns its facing as a vector.

;;;;;

<|glyph@trickster:templates|trick-id=trickster:add_velocity,title=Impulse Ploy|>

entity, vector ->

---

Applies the given vector as velocity to the given entity.

;;;;;

<|glyph@trickster:templates|trick-id=trickster:height_reflection,title=Stature Distortion|>

entity -> number

---

Given an entity, returns its height in blocks.

;;;;;

<|glyph@trickster:templates|trick-id=trickster:sneaking_reflection,title=Alternative Distortion|>

entity -> number

---

Given an entity, returns whether the entity is crouching.

;;;;;

<|glyph@trickster:templates|trick-id=trickster:raycast,title=Scout's Distortion|>

entity -> vector

---

Given an entity, returns the block the entity is looking at.

;;;;;

<|glyph@trickster:templates|trick-id=trickster:raycast_entity,title=Archer's Distortion|>

entity -> entity

---

Given an entity, returns the entity this entity is looking at.
