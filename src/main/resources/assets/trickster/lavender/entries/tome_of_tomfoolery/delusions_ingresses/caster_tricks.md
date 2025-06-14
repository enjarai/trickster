```json
{
  "title": "Casting Context",
  "icon": "trickster:wand",
  "category": "trickster:delusions_ingresses",
  "additional_search_terms": [
    "Positioning Delusion",
    "Directional Delusion",
    "Reflection Delusion",
    "Dimensional Delusion",
    "Authority Delusion",
    "Crowning Delusion",
    "Delusion of Order",
    "Framed Delusion",
    "Macro Delusion"
  ]
}
```

<|trick@trickster:templates|trick-id=trickster:reflection|>

Gives the location the spell is being cast from.

;;;;;

<|trick@trickster:templates|trick-id=trickster:facing_reflection|>

Gives the direction the casting block or entity is facing as a unit vector.

;;;;;

<|trick@trickster:templates|trick-id=trickster:caster_reflection|>

Gives the entity casting the spell, if available.

;;;;;

<|trick@trickster:templates|trick-id=trickster:get_dimension|>

Gives the dimension where this spell is being cast.

;;;;;

<|trick@trickster:templates|trick-id=trickster:mana_reflection|>

Gives the amount of mana directly available to the spell.

;;;;;

This delusion counts the amount of mana in all mana-carrying items the caster is holding or wearing, 
including [Knots](^trickster:items/knots) and [Whorls](^trickster:items/amethyst_whorl).


When cast from a [Spell Construct](^trickster:items/spell_construct), only the mana in the Construct's one Knot slot is counted.

;;;;;

<|trick@trickster:templates|trick-id=trickster:max_mana_reflection|>

Gives the maximum amount of mana that the caster of the spell can store. Works similarly to the previous delusion.

;;;;;

<|trick@trickster:templates|trick-id=trickster:current_thread|>

Gives the spell slot running this spell, or void if this casting context does not use spell slots.

;;;;;

<|trick@trickster:templates|trick-id=trickster:read_macro_ring|>

Gives a map containing the combined maps of all rings worn, with any entries that aren't valid macros filtered out.

;;;;;

The result of this trick is equal to the map used when evaluating macros.


See the entry on [Macros](^trickster:concepts/macro) for more details.

;;;;;

<|trick@trickster:templates|trick-id=trickster:hotbar_reflection|>

Gives the selected hotbar slot of the caster, if available.
