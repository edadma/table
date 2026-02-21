Text table rendering with multiple border styles, column alignment, ANSI formatting, and Markdown output.

## Installation

```bash
npm install @edadma/table
```

## Quick Start

```javascript
import { TextTable } from '@edadma/table';

const t = new TextTable({ border: 'light', columnDividers: true, headerLine: true, headerUnderlined: false });
t.header(['name', 'department', 'salary']);
t.row(['Alice', 'Engineering', 95000]);
t.row(['Bob', 'Sales', 72000]);
t.row(['Charlie', 'Marketing', 88000]);
t.rightAlignment(3);
console.log(t.render());
```

Output:

```
┌─────────┬─────────────┬────────┐
│  name   │ department  │ salary │
├─────────┼─────────────┼────────┤
│ Alice   │ Engineering │  95000 │
│ Bob     │ Sales       │  72000 │
│ Charlie │ Marketing   │  88000 │
└─────────┴─────────────┴────────┘
```

## Border Styles

```javascript
new TextTable()                          // no border (default)
new TextTable({ border: 'light' })       // ┌─┬─┐ │ │ └─┴─┘
new TextTable({ border: 'heavy' })       // ┏━┳━┓ ┃ ┃ ┗━┻━┛
new TextTable({ border: 'double' })      // Unicode double lines
new TextTable({ border: 'ascii' })       // +-+-+ | | +-+-+
new TextTable({ markdown: true })        // | col | col |
new TextTable({ tabbed: true })          // tab-separated
new TextTable({ matrix: true })          // ⎡ ⎤ ⎢ ⎥ ⎣ ⎦
new TextTable({ matrix: true, matrixRounded: true })  // ⎛ ⎞ ⎜ ⎟ ⎝ ⎠
```

## Options

| Option | Type | Default | Description |
|--------|------|---------|-------------|
| `border` | `'light' \| 'heavy' \| 'double' \| 'ascii'` | none | Border style |
| `columnDividers` | `boolean` | `false` | Show vertical separators between columns |
| `headerBold` | `boolean` | `true` | Bold header text (ANSI) |
| `headerLine` | `boolean` | `false` | Horizontal line after header |
| `headerUnderlined` | `boolean` | `true` | Underline header text (ANSI) |
| `headerCentered` | `boolean` | `true` | Center header text |
| `matrix` | `boolean` | `false` | Matrix bracket mode |
| `matrixRounded` | `boolean` | `false` | Use rounded parentheses for matrix |
| `markdown` | `boolean` | `false` | Markdown table output |
| `tabbed` | `boolean` | `false` | Tab-separated output |
| `noAnsi` | `boolean` | `false` | Disable ANSI escape codes |

## API

All methods return `this` for chaining.

### `header(columns: string[])`

Set the header row.

### `row(columns: any[])`

Add a data row. Values are converted to strings. `null` and `undefined` render as `"null"`.

### `rightAlignment(col: number)`

Right-align a column (1-based index).

### `columnAlignment(col: number, align: Alignment)`

Set the default alignment for a column. `align` is `'left'`, `'right'`, or `'center'`.

### `alignment(col: number, align: Alignment)`

Override alignment for a specific cell in the last added row.

### `style(col: number, ansiStyle: string)`

Apply an ANSI style code to a cell in the last added row. Example: `'\x1b[31m'` for red.

### `line()`

Insert a horizontal separator line at the current position.

### `render(): string`

Render the table to a string.

## Examples

### Markdown

```javascript
const t = new TextTable({ markdown: true });
t.header(['Feature', 'Status']);
t.row(['Tables', 'Done']);
t.row(['Borders', 'Done']);
console.log(t.render());
// | Feature | Status |
// | ------- | ------ |
// | Tables  | Done   |
// | Borders | Done   |
```

### Chaining

```javascript
const output = new TextTable({ border: 'ascii', columnDividers: true, headerLine: true, headerUnderlined: false })
  .header(['x', 'x^2'])
  .row([1, 1])
  .row([2, 4])
  .row([3, 9])
  .rightAlignment(1)
  .rightAlignment(2)
  .render();
```

### Line Separators

```javascript
const t = new TextTable();
t.header(['Group', 'Value']);
t.row(['A', 1]);
t.row(['A', 2]);
t.line();
t.row(['B', 3]);
```

## License

[ISC](https://opensource.org/licenses/ISC)
