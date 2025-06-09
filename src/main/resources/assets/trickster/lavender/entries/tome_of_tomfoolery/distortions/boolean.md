```json
{
  "title": "Boolean Logic",
  "icon": "minecraft:comparator",
  "category": "trickster:distortions",
  "additional_search_terms": [
    "Decision Distortion",
    "Parity Stratagem",
    "Disparity Stratagem",
    "Stratagem Bar None",
    "Stratagem In General",
    "Stratagem In Absence",
    "Lesser Distortion",
    "Greater Distortion"
  ]
}
```

This chapter describes a few patterns that can be used to perform boolean logic operations.


While glyphs here may indicate they require a boolean input, 
it is worth noting that any fragment will be automatically coerced into a boolean value when required.

;;;;;

<|trick@trickster:templates|trick-id=trickster:if_else|>

Returns one of two provided options based on a boolean value. 
If true, the first option is returned. Otherwise, the second.

;;;;;

<|trick@trickster:templates|trick-id=trickster:equals|>

Checks for equality between many inputs. Will only return true if all inputs are equal.

;;;;;

<|trick@trickster:templates|trick-id=trickster:not_equals|>

Checks for inequality between many inputs. Will return false if any input is equal to any other.

;;;;;

<|trick@trickster:templates|trick-id=trickster:all|>

Will only return true if all inputs are true.

;;;;;

<|trick@trickster:templates|trick-id=trickster:any|>

Will return true if any provided input is true.

;;;;;

<|trick@trickster:templates|trick-id=trickster:none|>

Will return true if none of the provided inputs are true.

;;;;;

<|trick@trickster:templates|trick-id=trickster:lesser_than|>

Returns whether the first number is lesser than the second.

;;;;;

<|trick@trickster:templates|trick-id=trickster:greater_than|>

Returns whether the first number is greater than the second.
