```json
{
  "title": "Block Interaction",
  "icon": "minecraft:string",
  "category": "trickster:ploys"
}
```

This entry contains tricks that operate directly on blocks in the world.

;;;;;

<|glyph@trickster:templates|trick-id=trickster:break_block,title=Ploy of Destruction|>

vector -> vector

<|cost-rule@trickster:templates|formula=max(hardness * 1kG\, 8kG)|>

Breaks the block at the given position. 

;;;;;

<|glyph@trickster:templates|trick-id=trickster:place_block,title=Ploy of Creation|>

vector, slot |

vector, block -> vector

<|cost-rule@trickster:templates|formula=max(distance * 1kG\, 8kG)|>

Places the given block at the given position. Will consume its respective item. 

;;;;;

<|glyph@trickster:templates|trick-id=trickster:swap_block,title=Ploy of Exchange|>

vector, vector ->

<|cost-rule@trickster:templates|formula=60kG + distance * 1kG|>

Exchanges the blocks at two positions in the world. Neither of the positions can be empty.

;;;;;

<|glyph@trickster:templates|trick-id=trickster:conjure_flower,title=Floral Ploy|>

vector -> vector

<|cost-rule@trickster:templates|formula=5kG|>

Conjures a random flower at the given position.
The block underneath must have a solid top face.

;;;;;

<|glyph@trickster:templates|trick-id=trickster:conjure_water,title=Aquatic Ploy|>

vector -> vector

<|cost-rule@trickster:templates|formula=15kG|>

Conjures a small splash of water at the given position.

;;;;;

<|glyph@trickster:templates|trick-id=trickster:conjure_light,title=Illumination Ploy|>

vector -> vector

<|cost-rule@trickster:templates|formula=20kG|>

Conjures a permanent light source at the given position.

;;;;;

<|glyph@trickster:templates|trick-id=trickster:drain_fluid,title=Drought Ploy|>

vector -> vector

<|cost-rule@trickster:templates|formula=15kG|>

Drains any fluid at the given position.

;;;;;

<|glyph@trickster:templates|trick-id=trickster:power_resonator,title=Resonance Ploy|>

vector, number -> boolean

<|cost-rule@trickster:templates|formula=distance / 2kG|>

Powers the [Spell Resonator](^trickster:items/spell_resonator) at the given position with the given power level, between 0 and 15.
