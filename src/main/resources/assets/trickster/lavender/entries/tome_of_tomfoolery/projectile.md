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

vector -> entity

---

Summons a fireball at the given position, returning it. 
Requires a fire charge in the caster's inventory. 

