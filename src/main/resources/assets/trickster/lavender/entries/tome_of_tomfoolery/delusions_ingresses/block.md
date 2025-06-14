```json
{
  "title": "Block Querying",
  "icon": "minecraft:white_wool",
  "category": "trickster:delusions_ingresses",
  "additional_search_terms": [
    "Ingress of Validation",
    "Ingress of Hardness",
    "Ingress of Suitability",
    "Ingress of Logic",
    "Ingress of Resonance",
    "Ingress of Luminance"
  ]
}
```

Via these ingresses, one can query blocks in the world and their properties.


These tricks will blunder if their target is not being observed.

;;;;;

<|trick@trickster:templates|trick-id=trickster:check_block|>

Gives the block type at the given position.

;;;;;

<|trick@trickster:templates|trick-id=trickster:get_block_hardness|>

Gives the hardness of the block at the given position.

;;;;;

<|trick@trickster:templates|trick-id=trickster:can_place_block|>

Gives whether the given block can be placed at the given position. If no block is given, gives whether the position is generally replaceable.

;;;;;

<|trick@trickster:templates|trick-id=trickster:get_redstone_power|>

Gives the redstone power level inputted into the given block position.

;;;;;

<|trick@trickster:templates|trick-id=trickster:check_resonator|>

Gives the power level of the [Spell Resonator](^trickster:items/spell_resonator) at the given position.
Blunders if no resonator is present.

;;;;;

<|trick@trickster:templates|trick-id=trickster:light_level|>

Gives the light level at the given position. 
A boolean can be specified to check only sky light (if true) or block light (if false).
