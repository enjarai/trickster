```json
{
  "title": "6. Spell Fragments",
  "icon": "minecraft:paper",
  "ordinal": 5,
  "category": "trickster:tutorials"
}
```

An important next step is learning how to work with **Spell Fragments**.
By using these, it becomes possible to have spells as *values* within another spell, 
and manipulate them in very interesting ways.


The easiest way to create a Spell Fragment to play with by adding an 
**Inner Circle** to your existing spell using [Inner Revision](^trickster:editing#3).

;;;;;

<|spell-preview-unloadable@trickster:templates|spell=YxEpKcpMzi4uSS2yKi5IzcmJL0gsKsEqKIgQLEgsAVJ5DGAAAK01BcpDAAAA|>

As you can see, this creates a circle nested inside the circle you drew it in!
But what does that do?

;;;;;

The logic here might seem a bit strange, but bear with me:


We know that the thing in the center of a circle determines the output of that circle, 
be that a literal value such as a number, or a trick that operates on other values.
Now then, what would happen if another spell was put in that position?

;;;;;

We can deduce two possible situations: 
Either the spell is a value and returns itself
or it operates on given inputs and produces a brand-new output.
But which one is it?


That's the fun part, its both!


If the circle that contains the inner circle has no subcircles of its own, 
the inner circle and all its subcircles are returned as a value.

;;;;;

However, if subcircles *are* added to the outer circle,
the results of those circles will be used as **Arguments** to the inner circle,
which will be cast and evaluated.


That's enough theory for now though,
lets ignore Arguments for now and look at two practical examples.
These use the former and latter types of inner circles respectively.


Examine the spells.

;;;;;

Try to figure out how one will behave differently from the other.


Spell A:
<|spell-preview@trickster:templates|spell=YxEpKcpMzi4uSS2yKi5IzcmJL0gsKhFECBYklgCpvA0+DAyMjMwMDAwgDGSSq08AoSSvNDcptciBAQ6YIAQA9gP08ZQAAAA=|>

;;;;;

Why does spell B showcase the number 2 twice, when Spell A doesn't at all?


Spell B:
<|spell-preview@trickster:templates|spell=YxEpKcpMzi4uSS2yKi5IzcmJL0gsKhFECBYklgCpvA0+DAyMjGwMDAzMQAxkkqSPGaFPAKEkrzQ3KbXIgQEOmBgQ6jCNQlbExAAA+4YzxbkAAAA=|>

;;;;;

Keep in mind that [Showcase Stratagem](^trickster:tricks/basic#7) 
passes its input value along as its output after showcasing it.

//TODO