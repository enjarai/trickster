```json
{
  "title": "Basic Spell-Scribing",
  "icon": "trickster:scroll_and_quill",
  "category": "trickster:basics"
}
```

Right-clicking a [Scroll and Quill](^trickster:scroll_and_quill) will open the spell-scribing interface.
Spells consist of a tree-like structure of intersecting circles, and each circle contains a center glyph to denote its function.


When first opening a new scroll, you will see just one circle. This is the **root node**.

;;;;;

<|pattern@trickster:templates|pattern=0\,4\,8\,7,title=Extensive Revision|>

[{gray}(Scribing pattern){}](^trickster:scribing_patterns)

---

The pattern above can be used to add a new subcircle to any circle. 
Being a Scribing pattern, this pattern acts instantly when drawn.

;;;;;

Most patterns will not activate instantly like this.
Instead, when the spell is cast, they take their inputs from connected subcircles, perform an operation, and/or output to a parent circle.


Think of a spell like a tree with many splitting branches. 
First, the leaves of the tree (the most deeply nested circles) create or read values from the world.
These can be constants or, for example, your position.

;;;;;

At each point where the branches of the tree split sits a [Glyph](^trickster:glyphs) (Usually a pattern).
This glyph can then read the values coming in from its child branches, and perform an operation on them, outputting a new value to its parent. 


Some glyphs may not return a value, these are often called [Ploys](^trickster:tricks). 
These glyphs will have an effect in the world, which is usually the end goal of the entire spell.

;;;;;

The final ploy of a spell may be its root node (the trunk of the tree), but it doesn't have to be.
This is because while empty circles perform no operation on their inputs, they still ensure all their children get evaluated.


As such, a spell with multiple effects may use an empty root node to evaluate multiple trees of logic.


The following pages will list some potentially useful scribing patterns.

;;;;;

<|pattern@trickster:templates|pattern=0\,4\,8\,5,title=Inner Revision|>

{gray}(Scribing pattern){}

---

Adds a new inner circle to an existing circle.
Inner circles act like glyphs, and can be activated as such.
See [Spell Fragments](^trickster:functions).

;;;;;

<|pattern@trickster:templates|pattern=3\,0\,4\,8,title=Split Revision|>

{gray}(Scribing pattern){}

---

Replaces the circle it is drawn in with a new circle, with the old circle as a subcircle.

;;;;;

<|pattern@trickster:templates|pattern=1\,0\,4\,8,title=Growth Revision|>

{gray}(Scribing pattern){}

---

Nests the circle it is drawn in inside another circle as its inner circle.

;;;;;

<|pattern@trickster:templates|pattern=0\,4\,8,title=Grafting Revision|>

{gray}(Scribing pattern){}

---

Removes the circle it is drawn in. Will Replace it with the original circle's first subcircle if available.

;;;;;

<|pattern@trickster:templates|pattern=0\,4\,8\,5\,2\,1\,0\,3\,6\,7\,8,title=Pruning Revision|>

{gray}(Scribing pattern){}

---

Removes the circle it is drawn in and any attached subcircles.

;;;;;

<|pattern@trickster:templates|pattern=4\,0\,1\,4\,2\,1,title=Notulist's Revision|>

{gray}(Scribing pattern){}

---

Reads a spell from the user's offhand and grafts it onto the spell currently being edited,
replacing the circle it is drawn in.

;;;;;

<|pattern@trickster:templates|pattern=1\,2\,4\,1\,0\,4\,7,title=Inner Notulist's Revision|>

{gray}(Scribing pattern){}

---

Reads a spell from the user's offhand and places it as a glyph in the center of the circle it is drawn in.

;;;;;

<|pattern@trickster:templates|pattern=4\,3\,0\,4\,5\,2\,4\,1,title=Grand Revision|>

{gray}(Scribing pattern){}

---

Reads a spell from the user's offhand, casts it, 
and puts the result as a glyph in the center of the circle it is drawn in.
