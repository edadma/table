package io.github.edadma.table

import scala.scalajs.js
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}

@JSExportTopLevel("TextTable")
class TextTableJS(options: js.UndefOr[js.Dynamic] = js.undefined):

  private val opts = options.toOption

  private def optStr(name: String): Option[String] =
    opts.flatMap(o => o.selectDynamic(name).asInstanceOf[js.UndefOr[String]].toOption)

  private def optBool(name: String, default: Boolean): Boolean =
    opts.flatMap(o => o.selectDynamic(name).asInstanceOf[js.UndefOr[Boolean]].toOption).getOrElse(default)

  private val borderStyle: Border = optStr("border") match
    case Some("light")  => LIGHT
    case Some("heavy")  => HEAVY
    case Some("double") => DOUBLE
    case Some("ascii")  => ASCII
    case _              => NONE

  private val tt = new TextTable(
    border = borderStyle,
    columnDividers = optBool("columnDividers", false),
    headerBold = optBool("headerBold", true),
    headerLine = optBool("headerLine", false),
    headerUnderlined = optBool("headerUnderlined", true),
    headerCentered = optBool("headerCentered", true),
    matrix = optBool("matrix", false),
    matrixRounded = optBool("matrixRounded", false),
    markdown = optBool("markdown", false),
    tabbed = optBool("tabbed", false),
  )

  if optBool("noAnsi", false) then tt.noansi()

  @JSExport
  def header(cols: js.Array[String]): TextTableJS =
    tt.headerSeq(cols.toSeq)
    this

  @JSExport
  def row(cols: js.Array[js.Any]): TextTableJS =
    tt.rowSeq(cols.toSeq.map {
      case v if v == null || js.isUndefined(v) => null
      case v => v
    })
    this

  @JSExport
  def rightAlignment(col: Int): TextTableJS =
    tt.rightAlignment(col)
    this

  @JSExport
  def columnAlignment(col: Int, align: String): TextTableJS =
    val a = align.toLowerCase match
      case "right"  => RIGHT
      case "center" => CENTER
      case _        => LEFT
    tt.columnAlignment(col, a)
    this

  @JSExport
  def alignment(col: Int, align: String): TextTableJS =
    val a = align.toLowerCase match
      case "right"  => RIGHT
      case "center" => CENTER
      case _        => LEFT
    tt.alignment(col, a)
    this

  @JSExport
  def style(col: Int, ansiStyle: String): TextTableJS =
    tt.style(col, ansiStyle)
    this

  @JSExport
  def line(): TextTableJS =
    tt.line()
    this

  @JSExport
  def render(): String = tt.toString
