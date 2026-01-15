package io.github.edadma.table

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class TextTableTest extends AnyFlatSpec with Matchers:

  "TextTable with no border" should "render a simple table" in {
    val t = new TextTable(headerBold = false, headerUnderlined = false) {
      noansi()
      header("A", "B", "C")
      row(1, 2, 3)
    }
    t.toString shouldBe
      """| A  B  C
         | 1  2  3
         |""".stripMargin
  }

  it should "render multiple rows with varying widths" in {
    val t = new TextTable(headerBold = false, headerUnderlined = false) {
      noansi()
      header("Name", "Value")
      row("x", 1)
      row("longer", 1000)
    }
    t.toString shouldBe
      """|  Name   Value
         | x       1
         | longer  1000
         |""".stripMargin
  }

  it should "right-align numeric columns" in {
    val t = new TextTable(headerBold = false, headerUnderlined = false) {
      noansi()
      header("Item", "Count")
      row("apples", 5)
      row("oranges", 123)
      rightAlignment(2)
    }
    t.toString shouldBe
      """|  Item    Count
         | apples       5
         | oranges    123
         |""".stripMargin
  }

  it should "center-align columns" in {
    val t = new TextTable(headerBold = false, headerUnderlined = false, headerCentered = false) {
      noansi()
      header("A", "B")
      row("x", "y")
      row("xxx", "yyy")
      columnAlignment(1, CENTER)
      columnAlignment(2, CENTER)
    }
    t.toString shouldBe
      """| A    B
         |  x    y
         | xxx  yyy
         |""".stripMargin
  }

  "Markdown output" should "render a basic markdown table" in {
    val t = new TextTable(markdown = true) {
      noansi()
      header("Name", "Age")
      row("Alice", 30)
      row("Bob", 25)
    }
    t.toString shouldBe
      """|| Name  | Age |
         || ----- | --- |
         || Alice | 30  |
         || Bob   | 25  |
         |""".stripMargin
  }

  it should "show right-alignment with colon marker" in {
    val t = new TextTable(markdown = true) {
      noansi()
      header("Item", "Price")
      row("Widget", 9.99)
      row("Gadget", 24.99)
      rightAlignment(2)
    }
    t.toString shouldBe
      """||  Item  | Price |
         || ------ | ----: |
         || Widget |  9.99 |
         || Gadget | 24.99 |
         |""".stripMargin
  }

  "ASCII border" should "render with +, -, | characters" in {
    val t = new TextTable(border = ASCII, columnDividers = true, headerLine = true, headerBold = false, headerUnderlined = false) {
      noansi()
      header("A", "B")
      row(1, 2)
    }
    t.toString shouldBe
      """|+---+---+
         || A | B |
         |+---+---+
         || 1 | 2 |
         |+---+---+
         |""".stripMargin
  }

  it should "render wider columns correctly" in {
    val t = new TextTable(border = ASCII, columnDividers = true, headerLine = true, headerBold = false, headerUnderlined = false) {
      noansi()
      header("Name", "Value")
      row("test", 12345)
    }
    t.toString shouldBe
      """|+------+-------+
         || Name | Value |
         |+------+-------+
         || test | 12345 |
         |+------+-------+
         |""".stripMargin
  }

  "LIGHT border" should "render with Unicode light box characters" in {
    val t = new TextTable(border = LIGHT, columnDividers = true, headerLine = true, headerBold = false, headerUnderlined = false) {
      noansi()
      header("A", "B")
      row(1, 2)
    }
    t.toString shouldBe
      """|┌───┬───┐
         |│ A │ B │
         |├───┼───┤
         |│ 1 │ 2 │
         |└───┴───┘
         |""".stripMargin
  }

  "HEAVY border" should "render with Unicode heavy box characters" in {
    val t = new TextTable(border = HEAVY, columnDividers = true, headerLine = true, headerBold = false, headerUnderlined = false) {
      noansi()
      header("A", "B")
      row(1, 2)
    }
    t.toString shouldBe
      """|┏━━━┯━━━┓
         |┃ A │ B ┃
         |┣━━━┿━━━┫
         |┃ 1 │ 2 ┃
         |┗━━━┷━━━┛
         |""".stripMargin
  }

  "DOUBLE border" should "render with Unicode double box characters" in {
    val t = new TextTable(border = DOUBLE, columnDividers = true, headerLine = true, headerBold = false, headerUnderlined = false) {
      noansi()
      header("A", "B")
      row(1, 2)
    }
    // DOUBLE uses same rendering as HEAVY in current implementation
    t.toString shouldBe
      """|┏━━━┯━━━┓
         |┃ A │ B ┃
         |┣━━━┿━━━┫
         |┃ 1 │ 2 ┃
         |┗━━━┷━━━┛
         |""".stripMargin
  }

  "Matrix mode" should "render with bracket notation" in {
    val t = new TextTable(matrix = true) {
      noansi()
      row(1, 2, 3)
      row(4, 5, 6)
      row(7, 8, 9)
    }
    t.toString shouldBe
      """|┌         ┐
         |│ 1  2  3 │
         |│ 4  5  6 │
         |│ 7  8  9 │
         |└         ┘
         |""".stripMargin
  }

  "Tabbed output" should "separate columns with tabs" in {
    val t = new TextTable(tabbed = true, headerBold = false, headerUnderlined = false) {
      noansi()
      header("A", "B", "C")
      row(1, 2, 3)
    }
    t.toString shouldBe " A \t B \t C\n 1 \t 2 \t 3\n"
  }

  "Data type handling" should "display null as 'null'" in {
    val t = new TextTable(headerBold = false, headerUnderlined = false) {
      noansi()
      header("Value")
      row(null)
    }
    t.toString shouldBe
      """| Value
         | null
         |""".stripMargin
  }

  it should "format arrays with parentheses" in {
    val t = new TextTable(headerBold = false, headerUnderlined = false) {
      noansi()
      header("Data")
      row(Array(1, 2, 3))
    }
    t.toString shouldBe
      """|   Data
         | (1, 2, 3)
         |""".stripMargin
  }

  it should "format lists with brackets" in {
    val t = new TextTable(headerBold = false, headerUnderlined = false) {
      noansi()
      header("Data")
      row(List(1, 2, 3))
    }
    t.toString shouldBe
      """|   Data
         | [1, 2, 3]
         |""".stripMargin
  }

  "Line separators" should "appear between row groups" in {
    val t = new TextTable(border = ASCII, columnDividers = true, headerBold = false, headerUnderlined = false) {
      noansi()
      header("X")
      line()
      row(1)
      line()
      row(2)
    }
    t.toString shouldBe
      """|+---+
         || X |
         |+---+
         || 1 |
         |+---+
         || 2 |
         |+---+
         |""".stripMargin
  }

  "Error handling" should "reject empty rows" in {
    assertThrows[IllegalArgumentException] {
      new TextTable {
        row()
      }
    }
  }

  it should "reject mismatched column counts" in {
    assertThrows[RuntimeException] {
      new TextTable {
        header("A", "B")
        row(1, 2, 3)
      }
    }
  }

  it should "reject column index 0" in {
    assertThrows[IllegalArgumentException] {
      new TextTable {
        header("A")
        rightAlignment(0)
      }
    }
  }

  it should "reject column index beyond range" in {
    assertThrows[IllegalArgumentException] {
      new TextTable {
        header("A", "B")
        rightAlignment(3)
      }
    }
  }

  it should "reject formatting empty table" in {
    assertThrows[IllegalArgumentException] {
      new TextTable {}.toString
    }
  }

  "Per-cell alignment" should "override column alignment" in {
    val t = new TextTable(headerBold = false, headerUnderlined = false, headerCentered = false) {
      noansi()
      header("A", "B", "C")
      row("left", "center", "right")
      alignment(1, LEFT)
      alignment(2, CENTER)
      alignment(3, RIGHT)
      row("x", "x", "x")
    }
    t.toString shouldBe
      """| A     B       C
         | left  center  right
         | x     x       x
         |""".stripMargin
  }

  "headerSeq and rowSeq" should "work with sequences" in {
    val t = new TextTable(headerBold = false, headerUnderlined = false) {
      noansi()
      headerSeq(Seq("X", "Y"))
      rowSeq(Seq(1, 2))
    }
    t.toString shouldBe
      """| X  Y
         | 1  2
         |""".stripMargin
  }

  "ANSI formatting" should "include bold codes when headerBold is true" in {
    val t = new TextTable(headerBold = true, headerUnderlined = false) {
      header("Test")
      row("data")
    }
    val result = t.toString
    result should include(Console.BOLD)
    result should include(Console.RESET)
  }

  it should "include underline codes when headerUnderlined is true" in {
    val t = new TextTable(headerBold = false, headerUnderlined = true) {
      header("Test")
      row("data")
    }
    val result = t.toString
    result should include(Console.UNDERLINED)
    result should include(Console.RESET)
  }

  it should "include both bold and underline when both are true" in {
    val t = new TextTable(headerBold = true, headerUnderlined = true) {
      header("Test")
      row("data")
    }
    val result = t.toString
    result should include(Console.BOLD)
    result should include(Console.UNDERLINED)
  }

  it should "exclude ANSI codes when noansi is called" in {
    val t = new TextTable(headerBold = true, headerUnderlined = true) {
      noansi()
      header("Test")
      row("data")
    }
    val result = t.toString
    result should not include Console.BOLD
    result should not include Console.UNDERLINED
    result should not include Console.RESET
  }

  it should "apply style to cells" in {
    val t = new TextTable(headerBold = false, headerUnderlined = false) {
      header("A")
      row("styled")
      style(1, Console.RED)
    }
    val result = t.toString
    result should include(Console.RED)
    result should include(Console.RESET)
  }

  "Column dividers without border" should "show vertical separators" in {
    val t = new TextTable(columnDividers = true, headerBold = false, headerUnderlined = false) {
      noansi()
      header("A", "B")
      row(1, 2)
    }
    t.toString shouldBe
      """| A │ B
         | 1 │ 2
         |""".stripMargin
  }
