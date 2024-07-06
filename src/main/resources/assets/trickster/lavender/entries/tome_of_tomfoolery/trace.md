```json
{
  "title": "Stack Traces",
  "icon": "minecraft:tripwire_hook",
  "category": "trickster:basics"
}
```

Spell failures are referred to as *blunders*. When a spell blunders, a stack trace is printed out, 
signifying *where* in the spell did the failure occur. Stack traces are colon-separated lists of characters. 
They come in three kinds: # (pound/hashtag), > (chevron/angle bracket), and numbers.

;;;;;

The numbers are input indexes, the chevrons indicate a change of context into a spell fragment provided by the current spell, 
and the pounds indicate a change of context into a spell fragment provided by an exterior source.

;;;;;

<|page-title@lavender:book_components|title=Note: Indexes|>Each circle in a spell has a number, an *index*, 
that states its position relative to its parent. The purple pin on the parent circle is always counter-clockwise of the first subcircle, 
which has an index of zero. Each subcircle clockwise of the first subcircle has an index one greater than the one before it.