defaults = "src/main/resources/assets/emi/recipe/defaults/create.json"
lines = []
file = open(defaults)
for line in file:
    lines.append(line)
lines.sort()
out = open(defaults + ".new", "a")
out.writelines(lines)
