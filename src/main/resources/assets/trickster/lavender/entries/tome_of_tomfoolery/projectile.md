```json
{
  "title": "Projectiles",
  "icon": "minecraft:spectral_arrow",
  "category": "trickster:tricks"
}
```

Tricks for manipulating projectiles.

;;;;;

<|glyph@trickster:templates|trick-id=trickster:summon_arrow,title=Ballista's Ploy|>

vector, [slot] -> entity

<|cost-rule@trickster:templates|formula=20kG + max((distance - 5kG) * 1.5, 0kG)|>

Summons an arrow at the given position, returning it. 
Requires an arrow, from either the given slot or the caster's inventory.

;;;;;

<|glyph@trickster:templates|trick-id=trickster:summon_fireball,title=Pyromancer's Ploy|>

vector, [slot] -> entity

<|cost-rule@trickster:templates|formula=20kG + max((distance - 5kG) * 1.5, 0kG)|>

Summons a fireball at the given position, returning it. 
Requires a fire charge, from either the given slot or the caster's inventory.

;;;;;

<|glyph@trickster:templates|trick-id=trickster:summon_dragon_breath,title=Dragon's Ploy|>

vector, [slot] -> entity

<|cost-rule@trickster:templates|formula=20kG + max((distance - 5kG) * 1.5, 0kG)|>

Summons dragon's breath at the given position, returning it.
Requires a fire charge, from either the given slot or the caster's inventory.

;;;;;

<|glyph@trickster:templates|trick-id=trickster:summon_tnt,title=Demolitionist's Ploy|>

vector, [slot] -> entity

<|cost-rule@trickster:templates|formula=20kG + max((distance - 5kG) * 1.5, 0kG)|>

Summons lit TNT at the given position, returning it.
Requires a block of TNT, from either the given slot or the caster's inventory.