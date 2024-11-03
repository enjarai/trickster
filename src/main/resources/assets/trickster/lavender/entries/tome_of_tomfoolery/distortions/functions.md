```json
{
  "title": "Spell Fragments",
  "icon": "minecraft:paper",
  "category": "trickster:distortions"
}
```

Just as values can be created, passed around, and used by spells, so can parts of the spell itself.


When nesting one circle as a glyph inside another, 
but not immediately providing any subcircles to the upper circle, 
the upper circle will return the inner circle as a fragment.

;;;;;

This fragment can then be used in a number of ways, including being written to an item using [Notulist's Ploy](^trickster:tricks/basic#4), 
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

<|glyph@trickster:templates|trick-id=trickster:execute_same_scope,title=Quiet Distortion|>

spell -> any

---

Executes the given spell with the current spell's arguments.

;;;;;

<|glyph@trickster:templates|trick-id=trickster:fork,title=Utensil Stratagem|>

spell, any... ->

---

An alternative to Grand Stratagem, this dispatches the given spell to a new spell slot, 
letting it run concurrently with this spell.

;;;;;

<|glyph@trickster:templates|trick-id=trickster:fold,title=Folding Distortion|>

spell, any[], any -> any[]

---

For each item in the given list, execute the given spell, passing the given fragment to the first iteration.

;;;;;

Each iteration receives four arguments:

---

any, any, number, any[]

---

Where the first argument is the result of the last iteration, the second is the current item, the third is its index in the given list, 
and the fourth is the given list.

;;;;;

The result of each execution is passed as the first argument to the next, where the last's result is the return of this trick.

;;;;;

<|glyph@trickster:templates|trick-id=trickster:try_catch,title=Cautious Stratagem|>

spell, spell, any... -> any

---

Attempts to execute the first spell. If it blunders, the second spell is run and the blunder is silenced. Excess values are arguments to both. 

;;;;;

<|glyph@trickster:templates|trick-id=trickster:supplier,title=Supply Distortion|>

any -> spell

---

Creates a new spell fragment which returns the previously provided fragment when executed.

;;;;;

<|glyph@trickster:templates|trick-id=trickster:closure,title=Closure Stratagem|>

spell, [spell, any]... -> spell
spell, {spell: any} -> spell

---

Replaces any occurrence of any of the latter spell's patterns inside the first spell
with the fragment immediately following them.

;;;;;

<|page-title@lavender:book_components|title=Note: Arguments|>Fragments can be passed into executed spell fragments as arguments.


See the chapter on [arguments](^trickster:delusions_ingresses/arguments) for more information.
