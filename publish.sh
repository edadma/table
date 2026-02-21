#!/bin/bash
set -e

echo "==> Building fullLinkJS..."
sbt tableJS/fullLinkJS

echo "==> Copying artifacts to npm/..."
cp js/target/scala-3.7.4/table-opt/main.js npm/main.js

echo "==> Package contents:"
cd npm
npm pack --dry-run

if [ "$1" = "--publish" ]; then
  echo "==> Publishing to npm..."
  npm publish --access public
else
  echo ""
  echo "Dry run complete. Run with --publish to publish to npm."
fi
