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

<|cost-rule@trickster:templates|formula=max(hardness * 1kG\, 8kG)|>

Breaks the block at the given position. 

;;;;;

<|glyph@trickster:templates|trick-id=trickster:place_block,title=Ploy of Creation|>

vector, slot |

vector, block ->

<|cost-rule@trickster:templates|formula=20kG + max((distance - 5kG) * 1.5\, 0kG)|>

Places the given block at the given position. Will consume its respective item. 

;;;;;

<|glyph@trickster:templates|trick-id=trickster:swap_block,title=Ploy of Exchange|>

vector, vector ->

<|cost-rule@trickster:templates|formula=60kG + distance * 1kG|>

Exchanges the blocks at two positions in the world. Neither of the positions can be empty.

;;;;;

<|glyph@trickster:templates|trick-id=trickster:conjure_flower,title=Floral Ploy|>

vector ->

<|cost-rule@trickster:templates|formula=5kG|>

Conjures a random flower at the given position.
The block underneath must have a solid top face.

;;;;;

<|glyph@trickster:templates|trick-id=trickster:conjure_water,title=Aquatic Ploy|>

vector ->

<|cost-rule@trickster:templates|formula=15kG|>

Conjures a small splash of water at the given position.

;;;;;

<|glyph@trickster:templates|trick-id=trickster:conjure_light,title=Illumination Ploy|>

vector ->

<|cost-rule@trickster:templates|formula=20kG|>

Conjures a permanent light source at the given position.

;;;;;

<|glyph@trickster:templates|trick-id=trickster:drain_fluid,title=Drought Ploy|>

vector ->

<|cost-rule@trickster:templates|formula=15kG|>

Drains any fluid at the given position.

;;;;;

<|glyph@trickster:templates|trick-id=trickster:destabilize_block,title=Earthquake Ploy|>

vector ->

<|cost-rule@trickster:templates|formula=10kG|>

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

<|cost-rule@trickster:templates|formula=20kG|>

Places a shadow of the given block at the given position and returns whether there was any change.

;;;;;

<|glyph@trickster:templates|trick-id=trickster:dispel_block_disguise,title=Revelation Ploy|>

vector -> boolean

<|cost-rule@trickster:templates|formula=10kG|>

Dispels any shadow block at the given position and returns whether there was one initially.

;;;;;

<|glyph@trickster:templates|trick-id=trickster:power_resonator,title=Resonance Ploy|>

vector, number -> boolean

<|cost-rule@trickster:templates|formula=distance / 2kG|>

Powers the [Spell Resonator](^trickster:spell_resonator) at the given position with the given power level, between 0 and 15.
