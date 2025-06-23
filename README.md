# 🛠️ MiniLang Compiler

A simple compiler implementation for the **MiniLang** programming language, built using **Java**. This project supports basic programming constructs and demonstrates the four key phases of compilation.

---

## 📌 Features

MiniLang supports:

- ✅ Variable Declarations: `int x;`
- ✅ Assignments: `x = 5;`
- ✅ Arithmetic Expressions: `a = b + c * 2;`
- ✅ Conditional Statements: `if (a > 0) { ... } else { ... }`
- ✅ Loops: `while (a < 10) { ... }`
- ✅ Print Statements: `print(a);`

---

## 🧠 Compiler Phases Implemented

| Phase                 | Description                                 |
|-----------------------|---------------------------------------------|
| **Lexical Analysis**  | Tokenizes source code using regex           |
| **Syntax Analysis**   | Parses tokens based on MiniLang grammar     |
| **Semantic Analysis** | Detects undeclared/redeclared variables     |
| **Code Generation**   | Produces 3-address intermediate code        |
| **Symbol Table**      | Tracks and prints declared variables        |
| **Error Handling**    | Friendly messages for syntax/semantic issues|

---

## 🗃️ Project Structure
```bash
MiniLangCompiler/
├── src/
│ └── compiler/
│ ├── SimpleLexer.java
│ ├── SimpleParser.java
│ └── input.minilang
├── README.md
├── Final_Report.docx (or PDF)
```

## 📥 How to Run

### ✔️ Prerequisites:
- Java JDK 17+
- IDE: IntelliJ IDEA (or any Java IDE)

### ▶️ Compile & Run:
From IntelliJ Terminal or Command Line:

```bash
javac src/compiler/*.java
java compiler.SimpleLexer
```
### 📄 Sample Input (input.minilang)

```bash
int x;
x = 5 + 10;
print(x);

if (x > 5) {
    x = x - 1;
} else {
    print(x);
}

while (x > 0) {
    x = x - 1;
}
```
### ✅ Sample Output

```bash
✅ Lexical Tokens:
(KEYWORD, int)
(IDENTIFIER, x)
(SEMICOLON, ;)
(IDENTIFIER, x)
(ASSIGN_OP, =)
(NUMBER, 5)
(OPERATOR, +)
(NUMBER, 10)
(SEMICOLON, ;)
(KEYWORD, print)
(LEFT_PAREN, ()
(IDENTIFIER, x)
(RIGHT_PAREN, ))
(SEMICOLON, ;)
(KEYWORD, if)
(LEFT_PAREN, ()
(IDENTIFIER, x)
(COMPARATOR, >)
(NUMBER, 5)
(RIGHT_PAREN, ))
(LEFT_BRACE, {)
(IDENTIFIER, x)
(ASSIGN_OP, =)
(IDENTIFIER, x)
(OPERATOR, -)
(NUMBER, 1)
(SEMICOLON, ;)
(RIGHT_BRACE, })
(KEYWORD, else)
(LEFT_BRACE, {)
(KEYWORD, print)
(LEFT_PAREN, ()
(IDENTIFIER, x)
(RIGHT_PAREN, ))
(SEMICOLON, ;)
(RIGHT_BRACE, })
(KEYWORD, while)
(LEFT_PAREN, ()
(IDENTIFIER, x)
(COMPARATOR, >)
(NUMBER, 0)
(RIGHT_PAREN, ))
(LEFT_BRACE, {)
(IDENTIFIER, x)
(ASSIGN_OP, =)
(IDENTIFIER, x)
(OPERATOR, -)
(NUMBER, 1)
(SEMICOLON, ;)
(RIGHT_BRACE, })
✅ Syntax Analysis: Passed.
Generated 3-Address Code:
t0 = 5 + 10
x = t0
t1 = x - 1
x = t1
t2 = x - 1
x = t2
Declared Variables (Symbol Table):
- x
```
## 👨‍💻 Author
- **Name:** Dasun Navindu
- **Index Number:** PS/2020/169
- **Course:** Theory of Compilers – Final Assignment

---

## 📚 License
This project is submitted as part of academic coursework and is intended for educational use only.

```bash

You can copy this entire block and paste it into your README.md file. The formatting includes:
- Proper headings and subheadings
- Code blocks with syntax highlighting
- Tables for the compiler phases
- Lists for features and structure
- Section dividers
- Placeholders for your personal information

Let me know if you'd like any modifications to this template!
```
