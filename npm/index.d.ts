export type Alignment = 'left' | 'right' | 'center';

export interface TextTableOptions {
  /** Border style: 'light', 'heavy', 'double', 'ascii', or omit for no border */
  border?: 'light' | 'heavy' | 'double' | 'ascii';
  /** Show vertical column dividers */
  columnDividers?: boolean;
  /** Bold header text (ANSI) */
  headerBold?: boolean;
  /** Show horizontal line after header */
  headerLine?: boolean;
  /** Underline header text (ANSI) */
  headerUnderlined?: boolean;
  /** Center header text */
  headerCentered?: boolean;
  /** Matrix bracket mode */
  matrix?: boolean;
  /** Use rounded parentheses for matrix brackets */
  matrixRounded?: boolean;
  /** Render as Markdown table */
  markdown?: boolean;
  /** Tab-separated output */
  tabbed?: boolean;
  /** Disable ANSI escape codes */
  noAnsi?: boolean;
}

export class TextTable {
  constructor(options?: TextTableOptions);

  /** Set the header row */
  header(columns: string[]): this;

  /** Add a data row */
  row(columns: any[]): this;

  /** Right-align a column (1-based index) */
  rightAlignment(col: number): this;

  /** Set default alignment for a column (1-based index) */
  columnAlignment(col: number, align: Alignment): this;

  /** Set alignment for a cell in the last added row (1-based column index) */
  alignment(col: number, align: Alignment): this;

  /** Apply ANSI style to a cell in the last added row (1-based column index) */
  style(col: number, ansiStyle: string): this;

  /** Insert a horizontal separator line */
  line(): this;

  /** Render the table to a string */
  render(): string;
}
