```json
{
  "title": "Wards",
  "icon": "minecraft:shield"
}
```

Wards are defensive spells that are cast when you are the target of a ploy. 
Your warding handler receives the glyph that is targeting you and a list containing the inputs the caster is passing to the glyph. 
The expected signature for a warding handler is the following: 

---

spell, any[] -> any[]

;;;;;

The return value of the warding handler is a list containing the new inputs for the given glyph. 
If the original inputs are returned, the player incurs no mana cost from the ward. 
If any entity fragment representing the player is removed or modified when returning the new inputs, 
the ward blunders. If any input in the list is of an invalid type, the ward blunders.

;;;;;

If the ward blunders, the spell targeting you moves forward with the old inputs, and a mana cost is still incurred by the ward. 
Wards which incur a mana cost will take 14 kilogandalfs of mana. 
Wards which successfully modify a glyph's inputs will incur a cost of 9 kilogandalfs for the caster.

;;;;;

To create a personal ward, you can take any charm-type accessory, commonly a [Warding Charm](^trickster:basics/warding_charm),
and inscribe a spell onto it.
While such an accessory is equipped, it will act as a ward for the player wearing it.