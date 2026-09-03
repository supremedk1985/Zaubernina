#!/usr/bin/env python3
"""Erzeugt data/Vokabeln.kt: der Wortschatz für Lesen (Deutsch, Silben) und Sprachen (EN/ES).
Bilder sind Emoji (Systemschrift, keine Berechtigung); Farben haben statt Emoji eine Farbfläche.
Aufruf: python3 werkzeuge/vokabeln_erzeugen.py
"""
import os
ZIEL = os.path.join(os.path.dirname(__file__), "..", "app", "src", "main", "java", "de", "korte_daniel", "zaubernina", "data", "Vokabeln.kt")

# (de mit Silben |, Artikel oder "", en, es, Emoji oder "", Farbe oder "")
THEMEN = [
 ("FARBEN", "Farben", "🎨", "", "It is {en}.", "Es {es}.", [
   ("rot","","red","rojo","","#E53935"),("blau","","blue","azul","","#1E88E5"),("gelb","","yellow","amarillo","","#FDD835"),
   ("grün","","green","verde","","#43A047"),("o|ran|ge","","orange","naranja","","#FB8C00"),("li|la","","purple","morado","","#8E24AA"),
   ("ro|sa","","pink","rosa","","#EC407A"),("schwarz","","black","negro","","#212121"),("weiß","","white","blanco","","#FAFAFA"),("braun","","brown","marrón","","#6D4C41")]),
 ("ZAHLEN", "Zahlen", "🔢", "", "I count to {en}.", "Cuento hasta {es}.", [
   ("eins","","one","uno","1️⃣",""),("zwei","","two","dos","2️⃣",""),("drei","","three","tres","3️⃣",""),("vier","","four","cuatro","4️⃣",""),
   ("fünf","","five","cinco","5️⃣",""),("sechs","","six","seis","6️⃣",""),("sie|ben","","seven","siete","7️⃣",""),("acht","","eight","ocho","8️⃣",""),
   ("neun","","nine","nueve","9️⃣",""),("zehn","","ten","diez","🔟","")]),
 ("TIERE", "Tiere", "🐾", "Da ist {akk}.", "I see a {en}.", "Veo un {es}.", [
   ("Hund","der","dog","perro","🐶",""),("Kat|ze","die","cat","gato","🐱",""),("Pferd","das","horse","caballo","🐴",""),("Kuh","die","cow","vaca","🐮",""),
   ("Schwein","das","pig","cerdo","🐷",""),("Maus","die","mouse","ratón","🐭",""),("Vo|gel","der","bird","pájaro","🐦",""),("Fisch","der","fish","pez","🐟",""),
   ("Pin|gu|in","der","penguin","pingüino","🐧",""),("Rob|be","die","seal","foca","🦭",""),("Frosch","der","frog","rana","🐸",""),("Bär","der","bear","oso","🐻","")]),
 ("ESSEN", "Essen", "🍎", "Da ist {akk}.", "I like {en}.", "Me gusta {es}.", [
   ("Ap|fel","der","apple","manzana","🍎",""),("Ba|na|ne","die","banana","plátano","🍌",""),("Brot","das","bread","pan","🍞",""),("Kä|se","der","cheese","queso","🧀",""),
   ("Milch","die","milk","leche","🥛",""),("Was|ser","das","water","agua","💧",""),("Ei","das","egg","huevo","🥚",""),("Ku|chen","der","cake","pastel","🍰",""),
   ("Eis","das","ice cream","helado","🍦",""),("Piz|za","die","pizza","pizza","🍕",""),("Ka|rot|te","die","carrot","zanahoria","🥕",""),("Erd|bee|re","die","strawberry","fresa","🍓","")]),
 ("FAMILIE", "Familie", "👨‍👩‍👧‍👧", "Da ist {akk}.", "This is my {en}.", "Es mi {es}.", [
   ("Ma|ma","die","mum","mamá","👩",""),("Pa|pa","der","dad","papá","👨",""),("Schwes|ter","die","sister","hermana","👧",""),("Bru|der","der","brother","hermano","👦",""),
   ("O|ma","die","grandma","abuela","👵",""),("O|pa","der","grandpa","abuelo","👴",""),("Ba|by","das","baby","bebé","👶",""),("Freund","der","friend","amigo","🧒","")]),
 ("KOERPER", "Körper", "🖐️", "Da ist {akk}.", "This is my {en}.", "Es mi {es}.", [
   ("Hand","die","hand","mano","🖐️",""),("Fuß","der","foot","pie","🦶",""),("Au|ge","das","eye","ojo","👁️",""),("Ohr","das","ear","oreja","👂",""),
   ("Na|se","die","nose","nariz","👃",""),("Mund","der","mouth","boca","👄",""),("Zahn","der","tooth","diente","🦷",""),("Haar","das","hair","pelo","💇","")]),
 ("SCHULE", "Schule", "🎒", "Da ist {akk}.", "I have a {en}.", "Tengo un {es}.", [
   ("Buch","das","book","libro","📖",""),("Stift","der","pen","bolígrafo","🖊️",""),("Sche|re","die","scissors","tijeras","✂️",""),("Ruck|sack","der","backpack","mochila","🎒",""),
   ("Tisch","der","table","mesa","🪑",""),("Uhr","die","clock","reloj","🕐",""),("Ball","der","ball","pelota","⚽",""),("Bild","das","picture","dibujo","🖼️","")]),
 ("WETTER", "Wetter", "🌤️", "Da ist {akk}.", "Today there is {en}.", "Hoy hay {es}.", [
   ("Son|ne","die","sun","sol","☀️",""),("Re|gen","der","rain","lluvia","🌧️",""),("Schnee","der","snow","nieve","❄️",""),("Wol|ke","die","cloud","nube","☁️",""),
   ("Wind","der","wind","viento","🌬️",""),("Re|gen|bo|gen","der","rainbow","arcoíris","🌈",""),("Mond","der","moon","luna","🌙",""),("Stern","der","star","estrella","⭐","")]),
]

def k(s): return '"' + s.replace("\\", "\\\\").replace('"', '\\"').replace("$", "\\$") + '"'

KOPF = '''package de.korte_daniel.zaubernina.data

// ACHTUNG: erzeugte Datei — Texte stehen in werkzeuge/vokabeln_erzeugen.py, dort ändern.

/** Ein Wort in drei Sprachen. [deSilben] mit Trennern für das Lesen-Spiel, [bild] Emoji oder leer, [farbe] Hex oder leer. */
data class Vokabel(
    val deSilben: String,
    val artikel: String,
    val en: String,
    val es: String,
    val bild: String,
    val farbe: String,
) {
    val de: String get() = deSilben.replace("|", "")
    /** Kennung für die Fehlerstatistik: das deutsche Wort. */
    val kennung: String get() = de
    /** „einen Hund", „eine Katze", „ein Buch" — für Lesesätze. Leer ohne Artikel. */
    val akkusativ: String get() = when (artikel) {
        "der" -> "einen $de"
        "die" -> "eine $de"
        "das" -> "ein $de"
        else -> de
    }
}

/** Ein Wortfeld mit Satzmustern je Sprache ({akk} = Akkusativ deutsch, {en}/{es} = Wort). Leer = keine Sätze in dieser Sprache. */
data class Wortthema(
    val kennung: String,
    val name: String,
    val symbol: String,
    val satzDe: String,
    val satzEn: String,
    val satzEs: String,
    val woerter: List<Vokabel>,
)

val WORTTHEMEN: List<Wortthema> = listOf(
'''
teile = []
for kennung, name, symbol, satzDe, satzEn, satzEs, ws in THEMEN:
    zeilen = ",\n".join(f"            Vokabel({k(a)}, {k(b)}, {k(c)}, {k(d)}, {k(e)}, {k(f)})" for a,b,c,d,e,f in ws)
    teile.append(f"    Wortthema(\n        kennung = {k(kennung)},\n        name = {k(name)},\n        symbol = {k(symbol)},\n        satzDe = {k(satzDe)},\n        satzEn = {k(satzEn)},\n        satzEs = {k(satzEs)},\n        woerter = listOf(\n{zeilen},\n        ),\n    ),")
with open(ZIEL, "w", encoding="utf-8") as f:
    f.write(KOPF + "\n".join(teile) + "\n)\n")
print("Themen", len(THEMEN), "Wörter", sum(len(t[6]) for t in THEMEN))
