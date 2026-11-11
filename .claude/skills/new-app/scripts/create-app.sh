#!/usr/bin/env bash
# Creates apps/<app> from apps/_template and registers it in settings.gradle.kts.
# Usage: .claude/skills/new-app/scripts/create-app.sh <app> "<Display Name>"
#   <app>          ^[a-z][a-z0-9]*$, max 20 chars (docs/NAMING.md §1)
#   <Display Name> shown as "Void <Display Name>"
# Run from the repository root. Works in Git Bash (Windows), Linux and macOS.
set -euo pipefail

app="${1:-}"
display="${2:-}"

fail() { echo "error: $*" >&2; exit 1; }

[ -f settings.gradle.kts ] && [ -d apps/_template ] || fail "run from the repository root"
[ -n "$app" ] && [ -n "$display" ] || fail "usage: $0 <app> \"<Display Name>\""
[[ "$app" =~ ^[a-z][a-z0-9]*$ ]] || fail "'$app' must match ^[a-z][a-z0-9]*\$"
[ "${#app}" -le 20 ] || fail "'$app' is longer than 20 characters"
case "$app" in
  *void*) fail "'$app' must not contain 'void'" ;;
  template|abstract|as|assert|boolean|break|byte|case|catch|char|class|const|continue|default|do|double|else|enum|extends|false|final|finally|float|for|fun|goto|if|implements|import|in|instanceof|int|interface|is|long|native|new|null|object|package|private|protected|public|return|short|static|super|switch|synchronized|this|throw|throws|transient|true|try|typealias|typeof|val|var|void|volatile|when|while)
    fail "'$app' is reserved" ;;
esac
[ ! -e "apps/$app" ] || fail "apps/$app already exists"

name="$(tr '[:lower:]' '[:upper:]' <<< "${app:0:1}")${app:1}"   # PascalCase of <app>
pkg_old="io.github.romanfabio.voidapps.template"
pkg_new="io.github.romanfabio.voidapps.$app"
dest="apps/$app"

echo "Creating $dest (package $pkg_new, class prefix $name, display 'Void $display')"

mkdir -p "$dest"
# Copy everything except build outputs and generated Room schemas.
(cd apps/_template && find . -mindepth 1 -maxdepth 1 ! -name build ! -name schemas -exec cp -R {} "../$app/" \;)

# Move package directories.
for set in main test androidTest; do
  old="$dest/src/$set/java/io/github/romanfabio/voidapps/template"
  if [ -d "$old" ]; then
    mv "$old" "$dest/src/$set/java/io/github/romanfabio/voidapps/$app"
  fi
done

# Rename files whose name starts with Template.
find "$dest/src" -type f -name 'Template*' | while read -r f; do
  mv "$f" "$(dirname "$f")/${name}$(basename "$f" | sed 's/^Template//')"
done

# Replace identifiers in every text file.
find "$dest" -type f \( -name '*.kt' -o -name '*.kts' -o -name '*.xml' -o -name '*.md' -o -name '*.pro' -o -name '*.properties' \) -print0 |
  while IFS= read -r -d '' f; do
    sed -i.bak \
      -e "s/${pkg_old//./\\.}/$pkg_new/g" \
      -e "s/TemplateApplication/${name}Application/g" \
      -e "s/TemplateApp/${name}App/g" \
      -e "s/TemplateNavHost/${name}NavHost/g" \
      -e "s/TemplateDatabase/${name}Database/g" \
      -e "s/template\.db/$app.db/g" \
      -e "s#apps/_template/schemas#apps/$app/schemas#g" \
      -e "s/Void Template/Void $display/g" \
      "$f"
    rm -f "$f.bak"
  done

printf 'version=0.1.0\n' > "$dest/version.properties"

# Register the module (alphabetical order is restored by the skill if needed).
printf 'include(":apps:%s")\n' "$app" >> settings.gradle.kts

echo "Done. Next steps (new-app skill):"
echo "  1. Rewrite $dest/README.md from docs/README_TEMPLATE.md"
echo "  2. Run: ./gradlew :apps:$app:check :apps:$app:assembleDebug"
