```json
{
  "title": "Block Interaction",
  "icon": "minecraft:string",
  "category": "trickster:ploys",
  "additional_search_terms": [
    "Ploy of Destruction",
    "Ploy of Creation",
    "Ploy of Exchange",
    "Ploy of Featherweight",
    "Ploy of Investiture",
    "Ploy of Divestiture",
    "Ploy of Attrition",
    "Floral Ploy",
    "Aquatic Ploy",
    "Illumination Ploy",
    "Resonance Ploy"
  ]
}
```

This entry contains tricks that operate directly on blocks in the world.

;;;;;

<|ploy@trickster:templates|trick-id=trickster:break_block,cost=max(hardness * 1G\, 8G)|>

Breaks the block at the given position. 

;;;;;

<|ploy@trickster:templates|trick-id=trickster:place_block,cost=max(distance * 1G\, 8G)|>

Places the given block at the given position.

;;;;;

Will consume its respective item. 
This ploy optionally takes two additional arguments. 

- The first defines the direction to place from.
- The second defines what side of an adjacent block is interacted with when placing.

Some blocks may change their facing or other properties based on these values.

;;;;;

<|ploy@trickster:templates|trick-id=trickster:change_weight,cost=60G * (1 - multiplier)|>

Given a number between zero and one, levitates the block at the given position, using the number as its gravity multiplier.

;;;;;

<|ploy@trickster:templates|trick-id=trickster:swap_block,cost=60G + distance * 1G|>

Exchanges the blocks at two positions in the world. Neither of the positions can be empty.

;;;;;

<|page-title@lavender:book_components|title=Note: Heating and Cooling|>By pushing or pulling a large quantity of mana into or from a block, it may be rapidly heated or cooled.


Given these extreme temperature changes, some blocks may change significantly in useful ways, 
though some collateral effects on surrounding blocks should be expected.

;;;;;

<|ploy@trickster:templates|trick-id=trickster:heat,cost=80G|>

Instantly heat up the given block significantly.


Heating a furnace like this would be quite effective.

;;;;;

<|ploy@trickster:templates|trick-id=trickster:cool,cost=80G|>

Instantly cools off the given block significantly.

;;;;;

<|ploy@trickster:templates|trick-id=trickster:erode,cost=80G|>

Wears down the block at the first given position, making use of the water at the second given position.

;;;;;

<|ploy@trickster:templates|trick-id=trickster:conjure_flower,cost=5G|>

Conjures a random flower at the given position.
The block underneath must have a solid top face.

;;;;;

<|ploy@trickster:templates|trick-id=trickster:conjure_water,cost=15G|>

Conjures a small splash of water at the given position.

;;;;;

<|ploy@trickster:templates|trick-id=trickster:conjure_light,cost=20G|>

Conjures a permanent light source at the given position.

;;;;;

<|ploy@trickster:templates|trick-id=trickster:power_resonator,cost=distance / 2G|>

Powers the [Spell Resonator](^trickster:items/spell_resonator) at the given position with the given power level, between 0 and 15.
