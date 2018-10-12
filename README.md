#### Note, as the language goal is to provide a danish programming language, everything is subject to change.

# DanskDiego

DanskDiego is an imperative, procedural language, providing the world with a way to program in danish.

## Overview
 
DanskDiego provides a small, and simple language, writeable in the mother tongue of Hans Christian Andersen. 
Coincidentally, the software was written in the birth city of the famed writer. 
 
## Data types
 
The primitive data types available in DanskDiego consists are `int`, `bool`, and `char`. Defining a variable is done using
the `var` keyword, e.g.:

```
var a: int;
```

As seen above, a statement is terminated with a `;`.

Multiple variables can be instantiated on the same line, by separating using a comma, as follows:

```
var a: int, b: bool, c: char;
```

## Collections

DanskDiego supports `arrays`, and `records`. To define an array, prefix the type with `array of`, e.g.

```
var a: array of int, b: array of bool, c: array of array of char;
```

Where `c` is a two-dimensional array.

As the code reads, no size is defined for the arrays. This is done by using the `allocate` keyword. Using the `allocate` 
keyword requires use of a size, given as an integer, e.g.:

```
allocate b of length 5;
```

Which in conjunction with the previous snippet allocate `b` as an array of booleans of size 5.

Accessing values in an array are done using brackets. Accessing the 0th index in an array would be written as `arr[0]`. 

A record is similar to a C-struct, and uses the keywords `record of` to define. An example:

```
var rec: record of { a: int, b: bool };
```

Which creates a structure with the children being accessible through `.`, e.g. `rec.a` or `rec.b`.

Like arrays, records will need to be allocated, which is done similarly to arrays, but without the size:

```
var rec: record of { a: int, b: bool };
allocate rec;
```

## Functions

Defining a function is done using the `func` keyword. A function must have a name and a return type, and be terminated 
by `end` followed by the function name.

A function returning an integer would be defined like this:

```
func returnInteger(a: int): int
    return a;
end returnInteger
```

which defines the function `returnMe` which takes a single argument, an integer.

Calling the function is done like most C-like programming languages:

```
returnMe(5);
```

## TODO - HELLO WORLD

## TODO - GRAMMAR
