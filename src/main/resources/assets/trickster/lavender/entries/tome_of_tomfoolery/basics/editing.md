```json
{
  "title": "Basic Spell-Scribing",
  "icon": "trickster:scroll_and_quill",
  "category": "trickster:basics"
}
```

Right-clicking a [Scroll and Quill](^trickster:basics/scroll_and_quill) will open the spell-scribing interface.
Spells consist of a tree-like structure of intersecting circles, and each circle contains a center glyph to denote its function.


When first opening a new scroll, you will see just one circle. This is the **root node**. 
Every other circle in your spell originates from it.

;;;;;

To begin writing a spell, so-called scribing patterns or revisions can be used add, remove, and move around circles.


The following page contains the most basic revision, with all others coming later in this chapter.

;;;;;

<|pattern@trickster:templates|pattern=0\,4\,8\,7,title=Extensive Revision|>

{gray}(Scribing pattern){}

---

Can be used to add a new subcircle to any circle. 
Being a Scribing pattern, this pattern acts instantly when drawn.

;;;;;

![](trickster:textures/gui/img/extension_revision.png,fit)

When Extensive Revision is drawn in the blue circle, the green circle will be created.

;;;;;

Most patterns will not activate instantly like this.
Instead, they will stay in their circle and, 
when the spell is cast, take their inputs from connected subcircles, perform an operation, and/or output to a parent circle.


Think of a spell like a tree with many splitting branches. 
First, the leaves of the tree (the most deeply nested circles) create or read values from the world.
These can be constants or, for example, your position.

;;;;;

At each point where the branches of the tree split sits a Glyph (Usually a pattern).
This glyph can then read the values coming in from its child branches, and perform an operation on them, outputting a new value to its parent. 


Some glyphs may not return a value, these are often called [Ploys](^trickster:ploys). 
These glyphs will have an effect in the world, which is usually the end goal of the entire spell.

;;;;;

The final ploy of a spell may be its root node (the trunk of the tree), but it doesn't have to be.
This is because while empty circles perform no operation on their inputs, they still ensure all their children get evaluated.


As such, a spell with multiple effects may use an empty root node to evaluate multiple trees of logic.

;;;;;

The following pages will list some potentially useful scribing patterns.

;;;;;

<|pattern@trickster:templates|pattern=0\,4\,8\,5,title=Inner Revision|>

{gray}(Scribing pattern){}

---

Adds a new inner circle to an existing circle.
Inner circles act like glyphs, and can be activated as such.
See [Spell Fragments](^trickster:distortions/functions).

;;;;;

![](trickster:textures/gui/img/inner_revision.png,fit)

When Inner Revision is drawn in the outer blue circle, the green circle will be created.

;;;;;

As explained briefly on the last page, an inner circle acts like a glyph, 
either returning its value, or being executed with arguments.


When no subcircles are connected to it, 
the circle containing the inner circle will return the inner circle as a spell fragment.
This can be used for meta-programming, recursion, and permanent storage of dynamic spells, among other things.

;;;;;

When the circle *does* have connected subcircles, 
it executes the inner circle directly as if it was called by a [Grand Stratagem](^trickster:distortions/functions),
using the results from the subcircles connected to the outer circle as parameters.


This can be very useful when needing to use one value in multiple places, 
as inner circles and spell fragments are the only way to move fragments back to the leaves of a tree.

;;;;;

<|pattern@trickster:templates|pattern=6\,3\,0\,4\,8,title=Devotion Revision|>

{gray}(Scribing pattern){}

---

Adds a new subcircle to the outer circle.

;;;;;

TODO: image

;;;;;

<|pattern@trickster:templates|pattern=3\,0\,4\,8,title=Split Revision|>

{gray}(Scribing pattern){}

---

Replaces the circle it is drawn in with a new circle, with the old circle as a subcircle.

;;;;;

![](trickster:textures/gui/img/split_revision.png,fit)

When Split Revision is drawn in the blue circle, it adds it as a subcircle to the newly created green circle.

;;;;;

<|pattern@trickster:templates|pattern=1\,0\,4\,8,title=Growth Revision|>

{gray}(Scribing pattern){}

---

Nests the circle it is drawn in inside another circle as its inner circle.

;;;;;

![](trickster:textures/gui/img/growth_revision.png,fit)

When Growth is drawn in the blue circle, it adds it as an inner circle to the newly created green circle.

;;;;;

<|pattern@trickster:templates|pattern=0\,4\,8,title=Grafting Revision|>

{gray}(Scribing pattern){}

---

Removes the circle it is drawn in. Will Replace it with the original circle's first subcircle if available.

;;;;;

![](trickster:textures/gui/img/grafting_revision.png,fit)

When Grafting is drawn in the yellow circle, it and the red circle are deleted and replaced by the green circle.

;;;;;

<|pattern@trickster:templates|pattern=0\,4\,8\,5\,2\,1\,0\,3\,6\,7\,8,title=Pruning Revision|>

{gray}(Scribing pattern){}

---

Removes the circle it is drawn in and any attached subcircles.

;;;;;

![](trickster:textures/gui/img/pruning_revision.png,fit)

When Pruning Revision is drawn in the yellow circle, it and the red circles are deleted.

;;;;;

<|pattern@trickster:templates|pattern=1\,2\,4\,6,title=Ascension Revision|>

{gray}(Scribing pattern){}

---

Expands the circle it is drawn in to replace its outer circle.

;;;;;

![](trickster:textures/gui/img/ascension_revision.png,fit)

When Ascension Revision is drawn in the blue circle, it deletes and replaces the red circle.

;;;;;

<|pattern@trickster:templates|pattern=1\,2\,5,title=Shifting Revision|>

{gray}(Scribing pattern){}

---

Shifts the subcircles of the circle it is drawn in, clockwise, so that the last subcircle is now the first.

;;;;;

<|pattern@trickster:templates|pattern=1\,0\,3,title=Reverse Shifting Revision|>

{gray}(Scribing pattern){}

---

Accomplishes the opposite of the Shifting Revision, moving subcircles counter-clockwise.

;;;;;

<|pattern@trickster:templates|pattern=2\,4\,3,title=Shuffling Revision|>

{gray}(Scribing pattern){}

---

Swaps the first subcircle with the second subcircle.

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
