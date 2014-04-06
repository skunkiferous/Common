WARNING:
This project has been superseded by Util, which is GWT-compatible:
@see https://github.com/skunkiferous/Util


This project unites several utility projects together:

Common
======

Common functionality, shared by most on BlockWithMe projects.

TODO: Test!

Base40
======

Base-40 implementation in Java.
Useful for packing a readable small string in a Java long.

The problem: How can I assing human-readable names/IDs to things, while not having to pay the overhead of a String object?

The solution: Limit the character set of the characters of the name to the minimum, and pack the letters in a long. So, the first obvious possibility, is to use Base36, which is a well-known encoding. Unfortunatly, Base-36 makes poor use of the full range of a long. It allows for identifiers of lenght 13 (12 character can use the full character set range). So how do we make better use of the full range of the 64 bit longs? By growing the character set, until we cannot store 12 full-range characters in it anymore. That gives us an improved character set of 40 characters. That is a 1/9 improvemnet. Since there is no unique set of 4 additional characters that will fit all use-cases, those are configurable.


Primitiveable
=============

Interfaces used to define converter objects, that can turn some object to and from a Java primitive value.

