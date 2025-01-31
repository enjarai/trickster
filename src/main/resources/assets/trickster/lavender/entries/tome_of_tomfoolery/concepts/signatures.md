```json
{
  "title": "Trick Signatures",
  "icon": "minecraft:writable_book",
  "category": "trickster:concepts"
}
```

The current format for signatures is rather inconsistent and subject to change.
The base structure is similar everywhere though, being:

> input1, input2 -\> output

input1/2 are inputs given by subcircles,
and output is the value outputted to this circle's parent.

;;;;;

Values on either side of the arrow can be absent to 
indicate that the trick does not need inputs or outputs.


Inputs are usually denoted using their Fragment Type, as many
tricks require specific types of values to operate.
Some examples include:

- number
- vector
- boolean
- void

;;;;;

The any type is a special case that accepts anything.


Certain modifier symbols may be applied to these type names
to further clarify exact behaviour.
These may look like the following:


__[typename]__

This input is optional and may be left out.


__typename[]__

This trick expects a list of fragments instead of just one.

;;;;;

__typename...__

This trick will accept any amount of additional inputs of the same type after this one.


__typename1 | typename2__

When used in inputs, this trick may accept either type1 or type2 in this position.
When used in output, this trick may either return type1 or type2 depending on the circumstances.

;;;;;

__{typename1: typename2}__

Represents a map containing keys of only type1 mapped to values of only type2.