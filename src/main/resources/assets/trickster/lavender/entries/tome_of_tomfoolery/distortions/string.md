```json
{
  "title": "Strings",
  "icon": "minecraft:flower_banner_pattern",
  "category": "trickster:distortions",
  "additional_search_terms": [
    "Distortion of Composition",
    "Distortion of Deconstruction"
  ]
}
```

Most operations you would want to perform on strings can be carried out 
using [list manipulation](^trickster:distortions/list) patterns on lists of character fragments. (also referred to as chars)


However, to actually be able to use a list of character fragments *as* a string, it needs to be converted.
That's where these patterns come in.

;;;;;

<|glyph@trickster:templates|trick-id=trickster:compose_string,title=Distortion of Composition|>

char[]... | any... -> string

---

Converts an arbitrary amount of fragments into a single string fragment, with special handling for chars.

;;;;;

<|glyph@trickster:templates|trick-id=trickster:decompose_string,title=Distortion of Deconstruction|>

string | fragment -> char[]

---

Converts any fragment into a char list representation of its stringified form, with special handling for strings.
