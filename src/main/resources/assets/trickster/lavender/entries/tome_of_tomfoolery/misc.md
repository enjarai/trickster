```json
{
  "title": "Miscellaneous Tricks",
  "icon": "minecraft:iron_ingot",
  "category": "trickster:tricks"
}
```

A few miscellaneous tricks that don't fit into any other category.

;;;;;

<|glyph@trickster:templates|trick-id=trickster:type_fragment,title=Argumentative Distortion|>

any -> type

---

Returns the type of the given fragment. Can be used to validate inputs, among other things.

;;;;;

<|glyph@trickster:templates|trick-id=trickster:delay_execution,title=Ploy of Suspension|>

[number] ->

---

Delays the execution of the current spell by the given number of ticks, or until the next tick.

;;;;;

<|glyph@trickster:templates|trick-id=trickster:pin_chunk,title=Ploy of Celestial Pin|>

vector -> boolean

<|cost-rule@trickster:templates|formula=32kG|>

Fully loads the chunk containing the given position for exactly 4 seconds.

;;;;;

<|page-title@lavender:book_components|title=Note: Bars|>Spells can display arbitrary values on the caster's screen as bars.


Bars are identified by an id number and can be overwritten at any time by using the same id again.
Bars are randomly colored based on their id. The same id will always display as the same color.

;;;;;

A bar can either be given one number, which will be interpreted on a scale of 0 to 1, or two numbers, 
which means it will interpret the first as the current and the second as the maximum value.


It also always returns the given value to its parent circle when updated, allowing for easy chaining.

;;;;;

<|glyph@trickster:templates|trick-id=trickster:show_bar,title=Ploy of Clarity|>

number, number, [number] -> number

---

Shows a bar on the caster's screen identified by the first number displaying the second number.

;;;;;

<|glyph@trickster:templates|trick-id=trickster:clear_bar,title=Ploy of Obfuscation|>

number -> boolean

---

Immediately clears a bar from the caster's screen identified by the given number.
