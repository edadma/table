# table

[![Maven Central](https://img.shields.io/maven-central/v/io.github.edadma/table_3.svg)](https://maven-badges.herokuapp.com/maven-central/io.github.edadma/table_3)

A Scala 3 library for rendering text tables with support for multiple output formats and border styles.

## Overview

`table` provides a simple, fluent API for creating formatted text tables. It supports:

- Multiple border styles: ASCII, Unicode (light/heavy), or no borders
- Output formats: plain text, Markdown, tab-separated values
- Column alignment: left, right, or center
- ANSI styling: bold and underlined headers
- JDBC integration: create tables directly from `ResultSet`
- Cross-platform: works on JVM, Scala.js, and Scala Native

## Installation

Add to your `build.sbt`:

```scala
libraryDependencies += "io.github.edadma" %%% "table" % "0.0.2"
```

## Basic Usage

```scala
import io.github.edadma.table.*

val t = new TextTable {
  header("Name", "Age", "City")
  row("Alice", 30, "New York")
  row("Bob", 25, "London")
  row("Charlie", 35, "Tokyo")
}

println(t)
```

Output:
```
 Name     Age  City
 Alice    30   New York
 Bob      25   London
 Charlie  35   Tokyo
```

## Configuration Options

`TextTable` accepts the following constructor parameters:

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `border` | `Border` | `NONE` | Border style: `NONE`, `ASCII`, `LIGHT`, `HEAVY`, `DOUBLE` |
| `columnDividers` | `Boolean` | `false` | Show vertical dividers between columns |
| `headerBold` | `Boolean` | `true` | Render header text in bold (ANSI) |
| `headerLine` | `Boolean` | `false` | Draw a line under the header |
| `headerUnderlined` | `Boolean` | `true` | Underline header text (ANSI) |
| `headerCentered` | `Boolean` | `true` | Center-align header text |
| `matrix` | `Boolean` | `false` | Matrix notation style (brackets) |
| `markdown` | `Boolean` | `false` | Output as Markdown table |
| `tabbed` | `Boolean` | `false` | Output as tab-separated values |

## Examples

### Markdown Output

```scala
val t = new TextTable(markdown = true) {
  header("Product", "Price", "Stock")
  row("Widget", 9.99, 100)
  row("Gadget", 24.99, 50)
  2 to 3 foreach rightAlignment
}
println(t)
```

Output:
```
| Product | Price | Stock |
| ------- | ----: | ----: |
| Widget  |  9.99 |   100 |
| Gadget  | 24.99 |    50 |
```

### ASCII Borders

```scala
val t = new TextTable(border = ASCII, columnDividers = true, headerLine = true, headerUnderlined = false) {
  header("ID", "Name", "Status")
  row(1, "Task A", "Done")
  row(2, "Task B", "Pending")
}
println(t)
```

Output:
```
+----+--------+---------+
| ID |  Name  | Status  |
+----+--------+---------+
|  1 | Task A | Done    |
|  2 | Task B | Pending |
+----+--------+---------+
```

### Unicode Box Drawing (Light)

```scala
val t = new TextTable(border = LIGHT, columnDividers = true, headerLine = true, headerUnderlined = false) {
  header("Col 1", "Col 2", "Col 3")
  row("A", "B", "C")
  row("D", "E", "F")
}
println(t)
```

Output:
```
┌───────┬───────┬───────┐
│ Col 1 │ Col 2 │ Col 3 │
├───────┼───────┼───────┤
│ A     │ B     │ C     │
│ D     │ E     │ F     │
└───────┴───────┴───────┘
```

### Unicode Box Drawing (Heavy)

```scala
val t = new TextTable(border = HEAVY, columnDividers = true, headerLine = true, headerUnderlined = false) {
  header("X", "Y", "Z")
  row(1, 2, 3)
  row(4, 5, 6)
}
println(t)
```

Output:
```
┏━━━┯━━━┯━━━┓
┃ X │ Y │ Z ┃
┠───┼───┼───┨
┃ 1 │ 2 │ 3 ┃
┃ 4 │ 5 │ 6 ┃
┗━━━┷━━━┷━━━┛
```

### Matrix Style

```scala
val t = new TextTable(matrix = true) {
  row(1, 0, 0)
  row(0, 1, 0)
  row(0, 0, 1)
  1 to 3 foreach rightAlignment
}
println(t)
```

Output:
```
┌         ┐
│ 1  0  0 │
│ 0  1  0 │
│ 0  0  1 │
└         ┘
```

### Tab-Separated Values

```scala
val t = new TextTable(tabbed = true) {
  header("A", "B", "C")
  row(1, 2, 3)
}
println(t)
```

### Column Alignment

```scala
val t = new TextTable {
  header("Left", "Center", "Right")
  row("text", "text", "text")
  row("longer text", "longer text", "longer text")
  columnAlignment(2, CENTER)
  columnAlignment(3, RIGHT)
}
println(t)
```

### Adding Separator Lines

```scala
val t = new TextTable(columnDividers = true) {
  header("Category", "Item", "Value")
  row("Fruits", "Apple", 1.50)
  row("Fruits", "Banana", 0.75)
  line()
  row("Vegetables", "Carrot", 0.50)
  row("Vegetables", "Potato", 0.30)
}
println(t)
```

### From JDBC ResultSet

```scala
import java.sql.DriverManager

val conn = DriverManager.getConnection("jdbc:h2:mem:test")
val stmt = conn.createStatement()
stmt.execute("CREATE TABLE users (id INT, name VARCHAR, age INT)")
stmt.execute("INSERT INTO users VALUES (1, 'Alice', 30), (2, 'Bob', 25)")

val rs = stmt.executeQuery("SELECT * FROM users")
val t = TextTable(rs)
println(t)
```

Numeric columns are automatically right-aligned.

## API Reference

### TextTable Class

#### Methods

| Method | Description |
|--------|-------------|
| `header(s: String*)` | Add a header row |
| `headerSeq(s: Seq[Any])` | Add a header row from a sequence |
| `row(s: Any*)` | Add a data row |
| `rowSeq(s: Seq[Any])` | Add a data row from a sequence |
| `line()` | Add a horizontal separator line |
| `rightAlignment(col: Int)` | Set column to right alignment (1-indexed) |
| `columnAlignment(col: Int, align: Alignment)` | Set column alignment |
| `alignment(col: Int, align: Alignment)` | Set alignment for current row's cell |
| `style(col: Int, style: String)` | Apply ANSI style to current row's cell |
| `noansi()` | Disable ANSI escape codes in output |
| `toString` | Render the table as a string |

### Alignment Enum

- `LEFT` - Left-align content (default)
- `RIGHT` - Right-align content
- `CENTER` - Center content

### Border Enum

- `NONE` - No border
- `ASCII` - ASCII characters (`+`, `-`, `|`)
- `LIGHT` - Unicode light box drawing characters
- `HEAVY` - Unicode heavy box drawing characters
- `DOUBLE` - Unicode double box drawing characters

## Tests

Run tests with:

```
sbt test
```

## License

ISC License - see [LICENSE](LICENSE) for details.
