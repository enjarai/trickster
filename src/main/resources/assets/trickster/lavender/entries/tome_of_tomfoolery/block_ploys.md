```json
{
  "title": "Block Interaction",
  "icon": "minecraft:string",
  "category": "trickster:tricks"
}
```

This entry contains tricks that operate directly on blocks in the world.

;;;;;

<|glyph@trickster:templates|trick-id=trickster:break_block,title=Ploy of Destruction|>

vector -> 

---

Breaks the block at the given position. 

;;;;;

<|glyph@trickster:templates|trick-id=trickster:swap_block,title=Ploy of Exchange|>

vector, vector ->

---

Exchanges the blocks at two positions in the world. Neither of the positions can be empty.

;;;;;

<|glyph@trickster:templates|trick-id=trickster:conjure_flower,title=Floral Ploy|>

vector ->

---

Conjures a random flower at the given position.
The block underneath must have a solid top face.

;;;;;

<|glyph@trickster:templates|trick-id=trickster:conjure_water,title=Aquatic Ploy|>

vector ->

---

Conjures a small splash of water at the given position.

;;;;;

<|glyph@trickster:templates|trick-id=trickster:destabilize_block,title=Earthquake Ploy|>

vector ->

---

Makes the target block temporarily affected by gravity as if it were sand.

;;;;;

<|page-title@lavender:book_components|title=Note: Shadow Blocks|>Shadow blocks, 
sometimes also referred to as echoes or echo blocks, 
are illusions superimposed onto a material block. 
They are dispelled when the state of the material block changes, 
but can also be removed using a Revelation Ploy.

;;;;;

<|glyph@trickster:templates|trick-id=trickster:disguise_block,title=Shadow Ploy|>

vector, block -> boolean

---

Places a shadow of the given block at the given position and returns whether there was any change.

;;;;;

<|glyph@trickster:templates|trick-id=trickster:dispel_block_disguise,title=Revelation Ploy|>

vector -> boolean

---

Dispels any shadow block at the given position and returns whether there was one initially.

;;;;;

<|glyph@trickster:templates|trick-id=trickster:power_resonator,title=Resonance Ploy|>

vector, number -> boolean

---

Powers the [Spell Resonator](^trickster:spell_resonator) at the given position with the given power level, between 0 and 15.
