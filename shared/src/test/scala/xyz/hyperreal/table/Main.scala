package xyz.hyperreal.table

object Main extends App {

  println
  println(
    new TextTable {
      header("one", "two", "three")
      row(null, "this is a string", List(1, 2, 3))
      row(12, 234, 3456)
      1 to 3 foreach rightAlignment
    }
  )

  println(
    new TextTable(markdown = true) {
      header("one", "two", "three")
      row(null, "this is a string", List(1, 2, 3))
      row(12, 234, 3456)
      1 to 3 foreach rightAlignment
    }
  )

  println(
    new TextTable(tabbed = true) {
      header("one", "two", "three")
      row(null, "this is a string", List(1, 2, 3))
      row(12, 234, 3456)
      1 to 3 foreach rightAlignment
    }
  )

  println(
    new TextTable(columnDividers = true, headerLine = true, headerUnderlined = false) {
      header("one", "two", "three")
      row(null, "this is a string", List(1, 2, 3))
      row(12, 234, 3456)
      1 to 3 foreach rightAlignment
    }
  )

  println(
    new TextTable(border = HEAVY, columnDividers = true, headerLine = true, headerUnderlined = false) {
      header("one", "two", "three")
      row(null, "this is a string", List(1, 2, 3))
      row(12, 234, 3456)
      1 to 3 foreach rightAlignment
    }
  )

  println(
    new TextTable(border = LIGHT, columnDividers = true, headerLine = true, headerUnderlined = false) {
      header("one", "two", "three")
      row(null, "this is a string", List(1, 2, 3))
      row(12, 234, 3456)
      1 to 3 foreach rightAlignment
    }
  )

  println(
    new TextTable(border = ASCII, columnDividers = true, headerBold = false, headerUnderlined = false) {
      header("one", "two", "three")
      line()
      row(null, "this is a string", List(1, 2, 3))
      row(12, 234, 3456)
      1 to 3 foreach rightAlignment
    }
  )

  println(
    new TextTable(border = ASCII, columnDividers = true, headerLine = true, headerUnderlined = false) {
      header("one", "two", "three")
      row(null, "this is a string", List(1, 2, 3))
      row(12, 234, 3456)
      1 to 3 foreach rightAlignment
    }
  )

  println(
    new TextTable(matrix = true) {
      row(1, 2, 3)
      row(4, 5, 6)
      row(7, 8, 9)
      1 to 3 foreach rightAlignment
    }
  )

}
