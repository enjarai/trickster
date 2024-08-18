```json
{
  "title": "Placing Spells",
  "icon": "minecraft:gunpowder",
  "category": "trickster:basics"
}
```

With some trickery and a large up-front cost, pre-designed spell circles can be placed directly in the world.
This lets them cast completely independently, and run indefinitely on an internal regenerating mana buffer.

;;;;;

Placed spell circles benefit from a sizable 450kG base mana buffer, with double the usual recharge rate.
While they can still leech from entities using [Conduit's Ploy](^trickster:ploys/mana#3), 
they cannot themselves be leeched from.


When the spell in a placed circle completes, the circle will dissipate by itself.
If the spell unexpectedly blunders, the circle will remain in a frozen state, 
and display its reason for failure to the caster.

;;;;;

<|glyph@trickster:templates|trick-id=trickster:create_spell_circle,title=Sisyphus Ploy|>

vector, vector, spell -> boolean

<|cost-rule@trickster:templates|formula=496kG|>

Places the latter spell at the position of the first vector, facing the second vector.

;;;;;

While placed spell circles can easily be broken by brute force, 
they can also be unravelled from a distance when required.

;;;;;

<|glyph@trickster:templates|trick-id=trickster:delete_spell_circle,title=Ploy of the Bigger Boulder|>

vector -> boolean

<|cost-rule@trickster:templates|formula=124kG|>

Destroys any spell circle placed at the given vector position.
Returns true if a spell circle was destroyed.
