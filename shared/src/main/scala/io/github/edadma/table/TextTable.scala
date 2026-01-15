package io.github.edadma.table

import collection.mutable.ArrayBuffer
import java.sql.ResultSet
import java.sql.Types.{DOUBLE as DBL, *}
import scala.collection.immutable.ArraySeq
import scala.collection.mutable
import scala.compiletime.uninitialized

trait Alignment
case object LEFT   extends Alignment
case object RIGHT  extends Alignment
case object CENTER extends Alignment

trait Border
case object NONE   extends Border
case object LIGHT  extends Border
case object HEAVY  extends Border
case object DOUBLE extends Border
case object ASCII  extends Border

object TextTable {

  private object boxes {
    val `BOX DRAWINGS LIGHT ARC DOWN AND RIGHT` = '\u256D'
    val `BOX DRAWINGS LIGHT ARC DOWN AND LEFT`  = '\u256E'
    val `BOX DRAWINGS LIGHT ARC UP AND LEFT`    = '\u256F'
    val `BOX DRAWINGS LIGHT ARC UP AND RIGHT`   = '\u2570'

    val `BOX DRAWINGS LIGHT DOWN AND RIGHT`          = '\u250C'
    val `BOX DRAWINGS LIGHT VERTICAL`                = '\u2502'
    val `BOX DRAWINGS LIGHT DOWN AND LEFT`           = '\u2510'
    val `BOX DRAWINGS LIGHT UP AND RIGHT`            = '\u2514'
    val `BOX DRAWINGS LIGHT UP AND LEFT`             = '\u2518'
    val `BOX DRAWINGS LIGHT HORIZONTAL`              = '\u2500'
    val `BOX DRAWINGS LIGHT VERTICAL AND RIGHT`      = '\u251C'
    val `BOX DRAWINGS LIGHT VERTICAL AND LEFT`       = '\u2524'
    val `BOX DRAWINGS LIGHT DOWN AND HORIZONTAL`     = '\u252C'
    val `BOX DRAWINGS LIGHT UP AND HORIZONTAL`       = '\u2534'
    val `BOX DRAWINGS LIGHT VERTICAL AND HORIZONTAL` = '\u253C'

    val `BOX DRAWINGS HEAVY HORIZONTAL`                    = '\u2501'
    val `BOX DRAWINGS HEAVY VERTICAL`                      = '\u2503'
    val `BOX DRAWINGS HEAVY DOWN AND RIGHT`                = '\u250F'
    val `BOX DRAWINGS HEAVY VERTICAL AND RIGHT`            = '\u2523'
    val `BOX DRAWINGS HEAVY UP AND RIGHT`                  = '\u2517'
    val `BOX DRAWINGS HEAVY DOWN AND LEFT`                 = '\u2513'
    val `BOX DRAWINGS HEAVY UP AND LEFT`                   = '\u251B'
    val `BOX DRAWINGS HEAVY VERTICAL AND LEFT`             = '\u252B'
    val `BOX DRAWINGS DOWN LIGHT AND HORIZONTAL HEAVY`     = '\u252F'
    val `BOX DRAWINGS VERTICAL LIGHT AND HORIZONTAL HEAVY` = '\u253F'
    val `BOX DRAWINGS UP LIGHT AND HORIZONTAL HEAVY`       = '\u2537'
  }

  def apply(result: ResultSet): TextTable = {
    val metadata = result.getMetaData
    val cols     = metadata.getColumnCount

    new TextTable {
      headerSeq(for (i <- 1 to cols) yield metadata.getColumnName(i))

      for (i <- 1 to cols)
        metadata.getColumnType(i) match {
          case BIGINT | DECIMAL | DBL | FLOAT | INTEGER | NUMERIC | REAL | SMALLINT | TINYINT =>
            rightAlignment(i)
          case _ =>
        }

      while (result.next) rowSeq(
        for (i <- 1 to cols)
          yield result.getObject(i) match {
            case s: String => s""""$s""""
            case a         => a
          },
      )
    }
  }
}

class TextTable(
    border: Border = NONE,
    columnDividers: Boolean = false,
    headerBold: Boolean = true,
    headerLine: Boolean = false,
    headerUnderlined: Boolean = true,
    headerCentered: Boolean = true,
    matrix: Boolean = false,
    markdown: Boolean = false,
    tabbed: Boolean = false,
) {
  private class Box(val contents: String) {
    var alignment: Alignment = uninitialized
    var underlined: Boolean  = false
    var style: String        = uninitialized
//    var corner: Boolean = _
//    var cornerChar: Char = _
//    var topBorder: Border = _
//    var top: Boolean = _
//    var topChar: Char = _
//    var sideBorder: Border = _
//    var side: Boolean = _
//    var sideChar: Char = _
  }

  import TextTable.boxes._

  private val table = new ArrayBuffer[ArraySeq[Box]]
  private val lines = new mutable.HashSet[Int]

  private var ansi                        = true
  private var columns: Int                = uninitialized
  private var colaligns: Array[Alignment] = uninitialized
  private var widths: IndexedSeq[Int]     = uninitialized

  def noansi(): Unit = ansi = false

  def rightAlignment(col: Int): Unit = columnAlignment(col, RIGHT)

  def columnAlignment(col: Int, align: Alignment): Unit = {
    require(table.nonEmpty, "empty table")
    require(1 <= col && col <= columns, "column number out of range")

    colaligns(col - 1) = align
  }

  def header(s: String*): Unit = headerSeq(s)

  def headerSeq(s: collection.Seq[Any]): Unit = {
    rowSeq(s)

    for (i <- 1 to columns) {
      alignment(i, if (headerCentered) CENTER else LEFT)

      if (headerBold && !markdown && !tabbed)
        style(i, Console.BOLD)

      if (headerUnderlined && !markdown && !tabbed)
        cell(i).underlined = true
    }

    if ((headerLine || markdown) && !tabbed)
      line()
  }

  def row(s: Any*): Unit = rowSeq(s)

  def rowSeq(s: collection.Seq[Any]): Unit = {
    require(s.nonEmpty, "need at least one column")

    if (table.isEmpty) {
      columns = s.length
      colaligns = Array.fill[Alignment](columns)(LEFT)
    } else if (columns != s.length)
      sys.error(s"table is $columns column(s) wide")

    table +=
      (s map {
        case a: Array[_] => a.mkString("(", ", ", ")")
        case a: Seq[_]   => a.mkString("[", ", ", "]")
        case v           => String.valueOf(v)
      } map (new Box(_))) :+ new Box("") to ArraySeq
  }

  def alignment(col: Int, align: Alignment): Unit = {
    require(table.nonEmpty, "empty table")
    require(1 <= col && col <= columns, "column number out of range")

    table(table.size - 1)(col - 1).alignment = align
  }

  private def cell(col: Int) = {
    require(table.nonEmpty, "empty table")
    require(1 <= col && col <= columns, "column number out of range")

    table(table.size - 1)(col - 1)
  }

  def style(col: Int, style: String): Unit = {
    val c = cell(col)

    c.style =
      (c.style match {
        case null => ""
        case s    => s
      }) + style
  }

  def line(): Unit = lines += table.size

  def format(): Unit = {
    require(table.nonEmpty, "empty table")

    if (widths eq null) {
      table += ArraySeq.fill(columns + 1)(new Box(""))
      widths = table.transpose
        .map(_.map(_.contents.length).foldLeft(0)(_ max _))
        .toIndexedSeq
    }
  }

  override def toString: String = {
    format()

    val buf     = new StringBuilder
    var lineind = 0

    def drawLine(): Unit = {
      if (matrix || border != NONE || markdown)
        buf append
          (if (markdown)
             '|'
           else if (border == ASCII)
             '+'
           else if (lineind == 0) {
             if (matrix)
               `BOX DRAWINGS LIGHT DOWN AND RIGHT`
             else if (border == LIGHT)
               `BOX DRAWINGS LIGHT DOWN AND RIGHT` // could be rounded
             else
               `BOX DRAWINGS HEAVY DOWN AND RIGHT`
           } else if (lineind < table.size - 1) {
             if (border == LIGHT)
               `BOX DRAWINGS LIGHT VERTICAL AND RIGHT`
             else
               `BOX DRAWINGS HEAVY VERTICAL AND RIGHT`
           } else {
             if (matrix)
               `BOX DRAWINGS LIGHT UP AND RIGHT`
             else if (border == LIGHT)
               `BOX DRAWINGS LIGHT UP AND RIGHT` // could be rounded
             else
               `BOX DRAWINGS HEAVY UP AND RIGHT`
           })

      for (j <- 0 until columns) {
        buf append (
          if (markdown) {
            if (colaligns(j) == RIGHT)
              s" ${"-" * ((widths(j) - 1) max 1)}: "
            else
              s" ${"-" * widths(j)} "
          } else if (columnDividers || border != NONE || matrix)
            (if (border == ASCII) '-'
             else if (matrix) ' '
             else {
               if (border == LIGHT)
                 `BOX DRAWINGS LIGHT HORIZONTAL`
               else
                 `BOX DRAWINGS HEAVY HORIZONTAL`
             }).toString * (widths(j) + 2)
          else
            (if (j == 0) " " else "") + (if (border == ASCII) '-'
                                         else {
                                           if (border == LIGHT)
                                             `BOX DRAWINGS LIGHT HORIZONTAL`
                                           else
                                             `BOX DRAWINGS HEAVY HORIZONTAL`
                                         }).toString * (widths(j) + (if (j > 0 && j < columns - 1)
                                                                       2
                                                                     else
                                                                       1)) + (if (j == columns - 1)
                                                                                " "
                                                                              else
                                                                                "")
        )

        if (j == columns - 1) {
          if (matrix || border != NONE || markdown)
            buf append
              (if (markdown)
                 '|'
               else if (border == ASCII)
                 '+'
               else if (lineind == 0) {
                 if (matrix)
                   `BOX DRAWINGS LIGHT DOWN AND LEFT`
                 else if (border == LIGHT)
                   `BOX DRAWINGS LIGHT DOWN AND LEFT` // could be rounded
                 else
                   `BOX DRAWINGS HEAVY DOWN AND LEFT`
               } else if (lineind < table.size - 1) {
                 if (border == LIGHT)
                   `BOX DRAWINGS LIGHT VERTICAL AND LEFT`
                 else
                   `BOX DRAWINGS HEAVY VERTICAL AND LEFT`
               } else {
                 if (matrix)
                   `BOX DRAWINGS LIGHT UP AND LEFT`
                 else if (border == LIGHT)
                   `BOX DRAWINGS LIGHT UP AND LEFT` // could be rounded
                 else
                   `BOX DRAWINGS HEAVY UP AND LEFT`
               })
        } else if (columnDividers || markdown)
          buf append
            (if (markdown)
               '|'
             else if (border == ASCII)
               '+'
             else if (lineind == 0) {
               if (border == LIGHT)
                 `BOX DRAWINGS LIGHT DOWN AND HORIZONTAL`
               else
                 `BOX DRAWINGS DOWN LIGHT AND HORIZONTAL HEAVY`
             } else if (lineind < table.size - 1) {
               if (border == LIGHT)
                 `BOX DRAWINGS LIGHT VERTICAL AND HORIZONTAL`
               else
                 `BOX DRAWINGS VERTICAL LIGHT AND HORIZONTAL HEAVY`
             } else {
               if (border == LIGHT)
                 `BOX DRAWINGS LIGHT UP AND HORIZONTAL`
               else
                 `BOX DRAWINGS UP LIGHT AND HORIZONTAL HEAVY`
             })
      }

      buf append '\n'
    }

    if (matrix || border != NONE && !markdown && !tabbed)
      drawLine()

    for (i <- 0 until table.length - 1) {
      if (lines contains i)
        if (!tabbed)
          drawLine()

      for (j <- 0 until columns) {
        if (j == 0) {
          if (matrix || border != NONE || markdown)
            buf append (if (border == ASCII || markdown) '|'
                        else {
                          if (matrix || border == LIGHT)
                            `BOX DRAWINGS LIGHT VERTICAL`
                          else
                            `BOX DRAWINGS HEAVY VERTICAL`
                        })
        } else if (columnDividers || markdown)
          buf append (if (border == ASCII || markdown) '|'
                      else `BOX DRAWINGS LIGHT VERTICAL`)
        else if (tabbed)
          buf append '\t'

        buf append ' '

        val elem        = table(i)(j).contents
        val diff        = widths(j) - elem.length
        val (pre, post) =
          (table(i)(j).alignment match {
            case null => colaligns(j)
            case a    => a
          }) match {
            case LEFT   => (0, diff)
            case RIGHT  => (diff, 0)
            case CENTER =>
              val before = diff / 2

              (before, diff - before)
          }
        val (pres, posts) =
          table(i)(j).style match {
            case null          => ("", "")
            case style if ansi => (style, Console.RESET)
            case _             => ("", "")
          }
        val underlined = table(i)(j).underlined

        if (underlined && ansi)
          buf append Console.UNDERLINED

        buf append " " * pre
        buf append pres
        buf append elem

        if (underlined && ansi)
          buf append Console.RESET

        buf append posts

        if (underlined && ansi)
          buf append Console.UNDERLINED

        // Skip trailing padding for last column in borderless tables
        if (j < columns - 1 || matrix || border != NONE || markdown)
          buf append " " * post

        if (underlined && ansi)
          buf append Console.RESET

        if (j < columns - 1 || matrix || border != NONE || markdown)
          buf append ' '
      }

      if (matrix || border != NONE || markdown)
        buf append (if (border == ASCII || markdown) '|'
                    else {
                      if (matrix || border == LIGHT)
                        `BOX DRAWINGS LIGHT VERTICAL`
                      else
                        `BOX DRAWINGS HEAVY VERTICAL`
                    })

      buf append '\n'
      lineind += 1
    }

    if (matrix || border != NONE && !tabbed)
      drawLine()

    buf.toString
  }
}
