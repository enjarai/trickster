```json
{
  "title": "Arithmetic",
  "icon": "minecraft:copper_bulb",
  "category": "trickster:distortions"
}
```

The following patterns regard basic arithmetic and simple mathematical operations.


Many base arithmetical operations, though not all, will work on both single numbers and vectors.

;;;;;

<|glyph@trickster:templates|trick-id=trickster:two,title=Foundational Delusion|>

-> number (2)

---

Returns the number two, always.

;;;;;

<|glyph@trickster:templates|trick-id=trickster:add,title=Annexation Stratagem|>

number... | number[] -> number |
vec... | vec[] -> vec

---

Adds up an arbitrary sequence of numbers into a single value.
Works with vectors.

;;;;;

<|glyph@trickster:templates|trick-id=trickster:subtract,title=Desertion Stratagem|>

number... | number[] -> number |
vec... | vec[] -> vec

---

Sequentially subtracts an arbitrary sequence of numbers into a single value.
Works with vectors.

;;;;;

<|glyph@trickster:templates|trick-id=trickster:multiply,title=Domination Stratagem|>

(number | vec)... | (number | vec)[] -> (number | vec)

---

Multiplies many numbers or vectors into a single value.
A number and a vector will combine into a vector.

;;;;;

<|glyph@trickster:templates|trick-id=trickster:divide,title=Submission Stratagem|>

(number | vec)... | (number | vec)[] -> (number | vec)

---

Divides many numbers or vectors into a single value.
A number and a vector will combine into a vector.

;;;;;

<|glyph@trickster:templates|trick-id=trickster:modulo,title=Distortion of Wholes|>

number, number -> number

---

Returns the remainder of dividing the first number by the second number.

;;;;;

<|glyph@trickster:templates|trick-id=trickster:max,title=Noble Stratagem|>

number... | number[] -> number

---

Returns highest of many input values.

;;;;;

<|glyph@trickster:templates|trick-id=trickster:min,title=Insignificance Stratagem|>

number... | number[] -> number

---

Returns lowest of many input values.

;;;;;

<|glyph@trickster:templates|trick-id=trickster:ceil,title=Distortion of Grandeur|>

number -> number

---

Returns the value of the input rounded up.

;;;;;

<|glyph@trickster:templates|trick-id=trickster:floor,title=Distortion of Humility|>

number -> number

---

Returns value of the input rounded down.

;;;;;

<|glyph@trickster:templates|trick-id=trickster:round,title=Distortion of Objectivity|>

number -> number

---

Returns the rounded value of the input.

;;;;;

<|glyph@trickster:templates|trick-id=trickster:sqrt,title=Distortion of Decline|>

number -> number

---

Returns the square root of the input.

;;;;;

<|glyph@trickster:templates|trick-id=trickster:invert,title=Negation Distortion|>

number -> number | vec -> vec

---

Inverts the given number or vector.
