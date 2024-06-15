```json
{
  "title": "Spell-Scribing",
  "icon": "trickster:scroll_and_quill"
}
```

TODO

;;;;;

<|pattern@trickster:templates|pattern=0\,4\,8\,7,title=Add Subcircle|>

---

Returns one of two provided options based on a boolean value. 
If true, the first option is returned. Otherwise, the second.

;;;;;

<|pattern@trickster:templates|pattern=0\,4\,8\,5,title=Test|>

any... -> boolean

---

Checks for equality between many inputs. Will only return true if all inputs are equal.

;;;;;

<|pattern@trickster:templates|pattern=0\,4\,8,title=Test|>

any... -> boolean

---

Checks for inequality between many inputs. Will return false if any input is equal to any other.

;;;;;

<|pattern@trickster:templates|pattern=0\,4\,8\,5\,2\,1\,0\,3\,6\,7\,8,title=Test|>

boolean... -> boolean

---

Will only return true if all inputs are true.

;;;;;

<|pattern@trickster:templates|pattern=4\,0\,1\,4\,2\,1,title=Test|>

boolean... -> boolean

---

Will return true if any provided input is true.

;;;;;

<|pattern@trickster:templates|pattern=1\,2\,4\,1\,0\,4\,7,title=Test|>

;;;;;

<|pattern@trickster:templates|pattern=4\,3\,0\,4\,5\,2\,4\,1,title=Test|>

boolean... -> boolean

---

Will return true if none of the provided inputs are true.
