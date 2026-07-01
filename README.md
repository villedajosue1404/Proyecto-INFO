# Formato de salida de transiciones (AFD minimo)

## Estructura general

```
<alfabeto>
<numEstados>
<finales>
<transiciones_por_simbolo>
```

Cada linea termina con un salto de linea (`\n`), y cada linea de transicion termina con un punto (`.`) seguido de salto de linea.

---

## Linea 1 — Alfabeto

Los simbolos del alfabeto separados por coma, sin espacios.

```
a,b,c
```
Si el alfabeto esta vacio (regex `ε`), esta linea queda vacia.

---

## Linea 2 — Numero de estados

Cantidad total de estados, incluyendo el estado de error si existe.

```
4
```

Los estados se numeran de `0` a `numEstados - 1` en el orden en que aparecen en las lineas de transicion.

---

## Linea 3 — Estados finales

IDs de los estados finales separados por coma. Los IDs corresponden a la posicion del estado en la lista (remeapeo), no al ID original del automata.

```
2
```
```
1,3
```

Si hay un estado final pero no aparece en la lista de estados (porque su ID original no esta en `transiciones.keySet()`), no se imprime.

---

## Lineas 4+ — Transiciones por simbolo

Una linea por cada simbolo del alfabeto, en el mismo orden en que aparecen en la Linea 1.

Cada linea tiene un valor por cada estado (en orden de `0` a `numEstados - 1`), separados por coma, y termina con un punto.

El valor puede ser:
- **Un numero**: el estado destino (ID remapeado) cuando la transicion existe.
- **`E`**: transicion al estado de error (no definida).

```
1,E,E.
E,2,E,E.
```

### Ejemplo

Para esta salida:
```
a,b
4
2
1,1,1,E.
E,2,2,E.
```

- `a,b` → alfabeto: `{a, b}`
- `4` → 4 estados: `0, 1, 2, 3`
- `2` → el estado final es el `2`
- Linea `a`: estado `0→1`, `1→1`, `2→1`, `3→E`
- Linea `b`: estado `0→E`, `1→2`, `2→2`, `3→E`

---

## Estado de error

El estado de error (representado como `E`) es un estado especial que:

- **Siempre es el ultimo** en la lista de estados (indice mas alto).
- **No es un estado final** nunca.
- **Todas sus transiciones van a `E`** (se queda en el mismo estado de error para cualquier simbolo).
- Cuando un estado no tiene transicion definida para un simbolo, la entrada correspondiente se marca como `E`.

Si todos los estados tienen todas las transiciones definidas, el estado de error no se incluye.

---

## Formato de archivo `.afd` (lectura/escritura)

```
S= {a,b}
4
2
0-1,1-1,2-1,3-0
0-0,1-2,2-3,3-0
```

- `S= {a,b}` → alfabeto
- `4` → cantidad de estados
- `2` → IDs de estados finales
- Cada linea de transicion usa el formato `origen-destino` separado por coma
- Transiciones faltantes se representan como `N---` (origen sin destino)
