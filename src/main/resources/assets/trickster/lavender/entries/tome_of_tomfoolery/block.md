```json
{
  "title": "Block Querying",
  "icon": "minecraft:white_wool",
  "category": "trickster:tricks"
}
```

This entry contains tricks that query or inspect blocks in the world.

;;;;;

<|glyph@trickster:templates|trick-id=trickster:check_block,title=Distortion of Validation|>

vector -> block

---

Returns the block type at the given position.

;;;;;

<|glyph@trickster:templates|trick-id=trickster:get_block_hardness,title=Distortion of Hardness|>

vector -> number

---

Returns the hardness of the block at the given position.

;;;;;

<|glyph@trickster:templates|trick-id=trickster:can_place_block,title=Distortion of Suitability|>

vector, block -> boolean |
vector -> boolean

---

Returns whether the given block can be placed at the given position. 

;;;;;

If no block is given, returns whether the given position is empty. 