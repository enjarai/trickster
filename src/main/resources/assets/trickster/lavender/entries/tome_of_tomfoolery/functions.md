```json
{
  "title": "Spell Fragments",
  "icon": "minecraft:paper",
  "category": "trickster:tricks"
}
```

Just as values can be created, passed around, and used by spells, so can parts of the spell itself.


When nesting one circle as a glyph inside another, 
but not immediately providing any subcircles to the upper circle, 
the upper circle will return the inner circle as a fragment.

;;;;;

This fragment can then be used in a number of ways, including being written to an item using [Notulist's Ploy](^trickster:basic_tricks), 
and being cast later or even multiple times within the same spell.


It is also very possible to pass a spell fragment inside of itself, and execute it again there, 
using recursion to create what is essentially a loop.

;;;;;

<|glyph@trickster:templates|trick-id=trickster:execute,title=Grand Stratagem|>

spell, any... -> any

---

A powerful trick indeed, it executes the passed in spell fragment, 
providing it with all other passed in fragments as arguments.

;;;;;

<|page-title@lavender:book_components|title=Note: Arguments|>
Fragments can be passed into executed spell fragments as arguments.


These fragments can be retrieved within the spell fragment using a set of specific glyphs.


At a maximum, eight arguments can be passed into a spell fragment.

;;;;;

<|glyph@trickster:templates|trick-id=trickster:load_argument_1,title=Primary Delusion|>

-> any

---

Returns the first argument passed, if available.

;;;;;

<|glyph@trickster:templates|trick-id=trickster:load_argument_2,title=Secondary Delusion|>

-> any

---

Returns the second argument passed, if available.

;;;;;

<|glyph@trickster:templates|trick-id=trickster:load_argument_3,title=Tertiary Delusion|>

-> any

---

Returns the third argument passed, if available.

;;;;;

<|glyph@trickster:templates|trick-id=trickster:load_argument_4,title=Quaternary Delusion|>

-> any

---

Returns the fourth argument passed, if available.

;;;;;

<|glyph@trickster:templates|trick-id=trickster:load_argument_5,title=Quinary Delusion|>

-> any

---

Returns the fifth argument passed, if available.

;;;;;

<|glyph@trickster:templates|trick-id=trickster:load_argument_6,title=Senary Delusion|>

-> any

---

Returns the sixth argument passed, if available.

;;;;;

<|glyph@trickster:templates|trick-id=trickster:load_argument_7,title=Septenary Delusion|>

-> any

---

Returns the seventh argument passed, if available.

;;;;;

<|glyph@trickster:templates|trick-id=trickster:load_argument_8,title=Octonary Delusion|>

-> any

---

Returns the eighth argument passed, if available.
