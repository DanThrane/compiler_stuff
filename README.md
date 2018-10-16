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

## Type

The keyword `type` can be used to define a type. E.g. `type id = int;` would alias `id` to the `int` data type.

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

Records can be defined using the `type` keyword, e.g. for creation of binary trees:

```
type Node = record { leftChild: Node, rightChild: Node, key: int };
```

Which defines a node of a binary tree.

## Functions

Defining a function is done using the `func` keyword. A function must have a name and a return type, and be terminated 
by `end` followed by the function name.

A function returning an integer would be defined like this:

```
func returnInteger(a: int): int
    return a;
end returnInteger
```

which defines the function `returnInteger` which takes a single argument, an integer.

Calling the function is done like most C-like programming languages:

```
returnInteger(5);
```

## Array length and absolute value

To retrieve the length of an array, the pipe operator can be used:

```
var length: int, arr: array of int;
allocate arr of length 5;
length = |arr|;
write length; // 5
```

Additionally, the pipe operator can also be used on an integer to get the absolute value, e.g:

```
var a: int;
a = 0-5;
write |a|; // 5
```

## Comparisons

DanskDiego has `true` and `false` as boolean constants, and `null` for usage with records or arrays.

Equality operators in Diego are similar to most other imperative languages, being `==` for equality, `!=` for inequality.
Additionally, the language has `<`, `>`, `<=`, `>=` for equality checks.

## Control flow

### If-statements

If-statements in DanskDiego can be followed a else-statement, or have the else omitted. The predicate must be enclosed by
a parenthesis. Predicates can be chained using the `&&` or the `||` operators.

```
if (i == 0) write 1;
else write 0; // Can be omitted
```

If-statements can contain either a single statement or statement list (enclosed by curly braces).

### While loops

While loops syntax require a predicate, similar to if statements, but the predicate is followed by the keyword `do`, e.g.:

```
var i: int;
i = 5;
while (i < 5) do write i;
```

The body of a while statement can either be a single statement, or list of statements.

## Hello World

The Hello World-program in DanskDiego is done using the primitive `write`, and can be done at the top level:

```
write "Hello, World!";
```

## Binary tree traversal

```
type Node = record of { leftChild: Node, rightChild: Node, key: int };
func initNode(key: int, leftChild: Node, rightChild: Node): Node
    var n: Node;
    n.key = key;
    n.leftChild = leftChild;
    n.rightChild = rightChild;
    return n;
end initNode

var leftChild: Node, rightChild: Node, root: Node;
leftChild = initNode(1, null, null);
rightChild = initNode(2, null, null);
root = initNode(0, leftChild, rightChild);

func sumKeys(node: Node): int
    var keyValue: int;
    keyValue = 0;
    if (node.leftChild != null) then keyValue = keyValue + sumKeys(node.leftChild);
    if (node.rightChild != null) then keyValue = keyValue + sumKeys(node.rightChild);
    return keyValue + node.key;
end sumKeys

write sumKeys(root); // 3
```

