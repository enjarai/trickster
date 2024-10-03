```json
{
  "title": "Macro Ring",
  "icon": "trickster:macro_ring",
  "category": "trickster:basics"
}
```

Macros allow you to create your own Revisions to aid with spell scribing. 
With a Macro Ring equipped, drawing a pattern that is a key in the ring will instead evaluate the spell associated with the key.

;;;;;

Macros take in the part of the spell that the pattern was drawn as the first argument. 
The macro must then return a spell. This spell replaces the spell the pattern was drawn in.

//TODO: add recipe

;;;;;

<|glyph@trickster:templates|trick-id=trickster:write_macro_ring,title=Macro Ploy|>

{spell: spell} -> bool

---

Writes the given map into the macro ring being worn. Returns whether this was successful.

;;;;;

<|glyph@trickster:templates|trick-id=trickster:read_macro_ring,title=Macro Delusion|>

-> {spell: spell} | void

---

Retrieves the map contained in the macro ring being worn. Returns void if the caster is not wearing a macro ring.
