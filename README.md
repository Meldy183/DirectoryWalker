# DirectoryWalker
File system program that handles files and directories. A directory can contain files or subdirectories, which can in turn contain more files or subdirectories. In process of implementation the following design patterns are used: Flyweight, Iterator, Visitor.


Full description of a task:
1. The goal is to create a class hierarchy for that system that applies at least three design patterns:
Flyweight – share recurring file-property data (e.g., extensions, permissions, owner/group).
Iterator – traverse and display the tree in a Linux-style tree view.
Visitor – compute the total size of any folder subtree.
2. System Architecture

Main classes
Node represents a base class for a file system node.
Directory represents a non-terminal node holding a list of children.
File represents a terminal node holding properties of a single file.

Flyweight
A single FilePropertiesFactory ensures that identical property sets share one FileProperties instance, minimizing memory usage.

Iterator
Clients request an iterator from any Directory to walk the subtree; the iterator prints a Linux-style tree (i.e., using ├──, └──, |).

Example:

.
├── tbzxhxll
│   └── rnboyqtu.log (336KB)
├── ypyzxdi
│   ├── juyt
│   └── ttplw.txt (809.8KB)
├── oou.cpp (666KB)
└── fhop

Visitor
The SizeVisitor traverses the Directory and sums individual File sizes; directories simply forward the visitor to children.

3. Design Pattern Requirements

3.1 Flyweight Pattern

Intent: Share immutable file-property objects across many nodes.
Tasks:
Implement FileProperties with fields for extension, read-only flag, owner, and group.
Implement FilePropertiesFactory that caches and returns shared FileProperties.
3.2 Iterator Pattern

Intent: Provide a uniform way to traverse any Directory subtree.
Tasks:
Define an Iterator<Directory> interface.
Each Directory should have createIterator() returning an iterator over the children nodes (in a depth-first order).
Write a utility to "pretty-print" the tree with indentation and branch characters.
3.3 Visitor Pattern

Intent: Separate an algorithm (size calculation) from the object structure.
Tasks:
Define a Visitor interface with methods to visit each node.
Implement SizeVisitor that accumulates sizes in visit(Node).
In each node's accept(Visitor v), call v.visit(this) and forward to children if any.
Input
Your code should use standard input and output.

The first line contains an integer N, the number of commands.
N lines follow, each containing a command line.
Valid commands are FILE and DIR.
DIR lines:
Format: DIR <id> <parent_id> <name>
The root directory has a unique id 0
Example: DIR 1234 3456 sus
FILE lines:

Format: FILE <id> <r> <owner> <group> <size> <name>.<ext>
id is the parent directory ID.
r is the permission type: T if readOnly, otherwise F.
owner and group are strings. They are used only in the Flyweight pattern.
size is a floating-point number in KB.
Example: FILE 1234 T root docker 12 malware.sh
Output
Output tree must follow the same input order.

Example of input:
5
FILE 0 T root docker 12 note.txt
DIR 1234 project
DIR 4567 1234 src
FILE 4567 T root docker 25 code.cpp
FILE 0 T root docker 50.4 image.png

Example of output:
total: 87.4KB
.
├── note.txt (12KB)
├── project
│   └── src
│       └── code.cpp (25KB)
└── image.png (50.4KB)


