```json
{
  "title": "Events and You",
  "icon": "minecraft:gunpowder",
  "category": "trickster:events"
}
```

Spell circles can be placed directly in the world when given an event pattern to react to.
This will let them be independently cast when the given event occurs within their range,
and return a value to dictate how to respond to it.


Many events can be directly cancelled by this method, 
but side effects and exceptions are very much supported.

;;;;;

<|glyph@trickster:templates|trick-id=trickster:create_spell_circle,title=Perceptive Ploy|>

vector, spell, spell ->

---

Places the latter spell at the given vector position and sets it 
to listen for the event referenced by the former spell.

;;;;;

A spell fragment references an event when its root node contains a 
pattern glyph corresponding to a defined event type, as listed in other entries in this category.


Different events may provide different arguments to an event listening spell,
these can be retrieved using the standard method as defined in [Spell Fragments](^trickster:functions).

;;;;;

Since it is very much possible an event listening circle makes its caster unable to move and/or break blocks,
there exists a utility pattern to easily destroy a spell circle given a specific block position.


See the next page.

;;;;;

<|glyph@trickster:templates|trick-id=trickster:delete_spell_circle,title=Cancellation Ploy|>

vector -> boolean

---

Destroys any spell circle placed at the given vector position.
Returns true if a spell circle was destroyed, or false if not.
