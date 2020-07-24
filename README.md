# Duyguji input module (client side)

This mod adds the ability to input arbitrary, predefined strings (so called "symbols") which would otherwise be hard to type without support by the operating system. For example, take the kaomoji `(★ω★)`. In order to type it, your keyboard layout would need to include both `ω` and `★` keys, which only leaves copy pasting from outside. Using this mod, you can define a shortcode for it, like so:

```
(★ω★)/star_face
```

Now you can type `:star_face:` instead (or just `:star_`), and have the kaomoji be automatically inserted on a button press:

![example0](https://i.imgur.com/X9zuagZ.png)

![example1](https://i.imgur.com/jFmqmtf.png)

# Usage

When entering text, type `:` plus one of the available shortcodes to start seeing suggestions. Press **UP** or **DOWN** to select a shortcode or continue typing until only one suggestion is left. To convert the shortcode into its symbol, press **TAB** or **ENTER**.

Note that in order for suggestions to show up, there must be spaces (or the beginning/end of the text field) around the shortcode. So typing for example

`some text:star|`

will not show suggestions (`|` being the text cursor here); same goes for

`some :star|text`

This however works:

`some :star| text`.

All available shortcodes can be found and edited in `.minecraft/config/duyguji/vanilla.txt`. This file is generated on first launch (or whenever it is deleted) from [this default](src/main/resources/assets/duyguji/client/input/vanilla.txt). Feel free to add your own definitions.

This mod is **client-side** only and the predefined symbols it ships with can be displayed on all vanilla clients, so your friends don't have to install it to see the black and white ❤s you'll hopefully send them!

Shortcode input currently works in:
* Chat
* Anvil name field

This mod works great in tandem with the [other duyguji modules](https://github.com/Sturmlilie/Duyguji).

# Supported Minecraft versions

* 1.16 (fabric)

# License

The source code of all duyguji components is licensed under LGPLv3.
