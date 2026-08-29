#!/usr/bin/env python3
"""OpenMoji-SVG -> Android Vector Drawable.

Bewusst klein gehalten und genau auf die Struktur der OpenMoji-Dateien zugeschnitten
(72x72-Sicht, Gruppen "color" und "line", nur Grundformen). Der Konverter ist ein
Werkzeug, kein Teil der App — er lief einmal und liegt zur Nachvollziehbarkeit hier.
"""
import math
import re
import sys
import xml.etree.ElementTree as ET

NS = "{http://www.w3.org/2000/svg}"
ERBT = ("fill", "stroke", "stroke-width", "stroke-linecap", "stroke-linejoin", "opacity")


def zahlen(text):
    return [float(z) for z in re.findall(r"-?\d*\.?\d+(?:e-?\d+)?", text)]


def matrix_von(transform):
    """Nur die Formen, die in OpenMoji vorkommen: translate+rotate und matrix."""
    a, b, c, d, e, f = 1.0, 0.0, 0.0, 1.0, 0.0, 0.0
    for name, argtext in re.findall(r"(translate|rotate|matrix)\(([^)]*)\)", transform):
        w = zahlen(argtext)
        if name == "translate":
            m = (1, 0, 0, 1, w[0], w[1] if len(w) > 1 else 0)
        elif name == "rotate":
            r = math.radians(w[0])
            m = (math.cos(r), math.sin(r), -math.sin(r), math.cos(r), 0, 0)
        else:
            m = tuple(w[:6])
        # Verkettung: erst a..f, dann m
        a, b, c, d, e, f = (
            a * m[0] + c * m[1], b * m[0] + d * m[1],
            a * m[2] + c * m[3], b * m[2] + d * m[3],
            a * m[4] + c * m[5] + e, b * m[4] + d * m[5] + f,
        )
    return (a, b, c, d, e, f)


def punkt(m, x, y):
    return (m[0] * x + m[2] * y + m[4], m[1] * x + m[3] * y + m[5])


def wandle_pfad(d, m):
    """Wendet eine Matrix auf Pfaddaten an — nur absolute Koordinaten sind nötig,
    die vier betroffenen OpenMoji-Pfade nutzen ausschließlich M/L/C/Z absolut."""
    if m is None:
        return d
    teile = re.findall(r"([MmLlHhVvCcSsQqTtAaZz])([^MmLlHhVvCcSsQqTtAaZz]*)", d)
    raus = []
    for befehl, argtext in teile:
        if befehl in "Zz":
            raus.append("Z")
            continue
        if befehl.islower() or befehl in "HhVvAa":
            # Kommt in den vier Fällen nicht vor; lieber laut scheitern als still falsch.
            raise SystemExit(f"Pfadbefehl {befehl} unter transform wird nicht unterstützt")
        w = zahlen(argtext)
        neu = []
        for i in range(0, len(w) - 1, 2):
            x, y = punkt(m, w[i], w[i + 1])
            neu += [f"{x:.3f}", f"{y:.3f}"]
        raus.append(befehl + " " + " ".join(neu))
    return " ".join(raus)


def form_zu_pfad(el, m):
    tag = el.tag.replace(NS, "")
    if tag == "path":
        return wandle_pfad(el.get("d", ""), m)
    if tag == "line":
        x1, y1, x2, y2 = (float(el.get(k, 0)) for k in ("x1", "y1", "x2", "y2"))
        if m:
            (x1, y1), (x2, y2) = punkt(m, x1, y1), punkt(m, x2, y2)
        return f"M {x1:.3f} {y1:.3f} L {x2:.3f} {y2:.3f}"
    if tag in ("circle", "ellipse"):
        cx, cy = float(el.get("cx", 0)), float(el.get("cy", 0))
        if tag == "circle":
            rx = ry = float(el.get("r", 0))
        else:
            rx, ry = float(el.get("rx", 0)), float(el.get("ry", 0))
        if not m:
            # Zwei Halbbögen — so schreibt es auch das Vector Asset Studio.
            return (f"M {cx - rx:.3f} {cy:.3f} a {rx:.3f} {ry:.3f} 0 1 0 {2 * rx:.3f} 0 "
                    f"a {rx:.3f} {ry:.3f} 0 1 0 {-2 * rx:.3f} 0 Z")
        # Unter einer Drehung lässt sich ein Bogen nicht einfach mitdrehen: der Kreis
        # wird deshalb durch vier kubische Kurven ersetzt (Kappa), deren Kontrollpunkte
        # sich Punkt für Punkt transformieren lassen.
        k = 0.5522847498
        ecken = [(cx + rx, cy), (cx, cy + ry), (cx - rx, cy), (cx, cy - ry)]
        griffe = [
            ((cx + rx, cy + k * ry), (cx + k * rx, cy + ry)),
            ((cx - k * rx, cy + ry), (cx - rx, cy + k * ry)),
            ((cx - rx, cy - k * ry), (cx - k * rx, cy - ry)),
            ((cx + k * rx, cy - ry), (cx + rx, cy - k * ry)),
        ]
        x0, y0 = punkt(m, *ecken[0])
        d = f"M {x0:.3f} {y0:.3f}"
        for i in range(4):
            (g1, g2), ziel = griffe[i], ecken[(i + 1) % 4]
            for p in (g1, g2, ziel):
                px, py = punkt(m, *p)
                d += f" {'C' if p is g1 else ''} {px:.3f} {py:.3f}"
        return d + " Z"
    if tag in ("polyline", "polygon"):
        w = zahlen(el.get("points", ""))
        pkte = [(w[i], w[i + 1]) for i in range(0, len(w) - 1, 2)]
        if m:
            pkte = [punkt(m, x, y) for x, y in pkte]
        d = "M " + " L ".join(f"{x:.3f} {y:.3f}" for x, y in pkte)
        return d + (" Z" if tag == "polygon" else "")
    return None


def farbe(wert):
    if not wert or wert in ("none", "transparent"):
        return None
    w = wert.strip()
    if w.startswith("#") and len(w) == 4:          # #abc -> #aabbcc
        w = "#" + "".join(c * 2 for c in w[1:])
    return w.upper() if w.startswith("#") else w


def sammle(el, geerbt, m_eltern, raus):
    eigen = dict(geerbt)
    for k in ERBT:
        if el.get(k) is not None:
            eigen[k] = el.get(k)
    m = m_eltern
    if el.get("transform"):
        eigen_m = matrix_von(el.get("transform"))
        m = eigen_m if m is None else eigen_m  # keine Verschachtelung in OpenMoji
    tag = el.tag.replace(NS, "")
    if tag in ("path", "line", "circle", "ellipse", "polyline", "polygon"):
        d = form_zu_pfad(el, m)
        if d:
            raus.append((d, eigen))
    for kind in el:
        sammle(kind, eigen, m, raus)


def wandle(pfad_svg, pfad_xml, groesse_dp=48):
    baum = ET.parse(pfad_svg)
    wurzel = baum.getroot()
    sicht = zahlen(wurzel.get("viewBox", "0 0 72 72"))
    formen = []
    sammle(wurzel, {}, None, formen)

    zeilen = [
        '<?xml version="1.0" encoding="utf-8"?>',
        "<!-- OpenMoji (https://openmoji.org), CC BY-SA 4.0 — umgewandelt mit svg2vd.py -->",
        '<vector xmlns:android="http://schemas.android.com/apk/res/android"',
        f'    android:width="{groesse_dp}dp"',
        f'    android:height="{groesse_dp}dp"',
        f'    android:viewportWidth="{sicht[2]:g}"',
        f'    android:viewportHeight="{sicht[3]:g}">',
    ]
    for d, s in formen:
        f_farbe = farbe(s.get("fill", "#000000"))
        s_farbe = farbe(s.get("stroke"))
        if not f_farbe and not s_farbe:
            continue
        zeilen.append("    <path")
        zeilen.append(f'        android:pathData="{d}"')
        if f_farbe:
            zeilen.append(f'        android:fillColor="{f_farbe}"')
        if s_farbe:
            zeilen.append(f'        android:strokeColor="{s_farbe}"')
            zeilen.append(f'        android:strokeWidth="{s.get("stroke-width", "1")}"')
            if s.get("stroke-linecap"):
                zeilen.append(f'        android:strokeLineCap="{s["stroke-linecap"]}"')
            if s.get("stroke-linejoin"):
                zeilen.append(f'        android:strokeLineJoin="{s["stroke-linejoin"]}"')
        zeilen[-1] += "/>"
    zeilen.append("</vector>")
    open(pfad_xml, "w").write("\n".join(zeilen) + "\n")
    return len(formen)


if __name__ == "__main__":
    n = wandle(sys.argv[1], sys.argv[2])
    print(f"{sys.argv[2]}: {n} Formen")
